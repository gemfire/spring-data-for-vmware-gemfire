/* Copyright 2026 Broadcom. SPDX-License-Identifier: Apache-2.0 */
import com.sun.source.tree.*;
import com.sun.source.util.*;
import javax.lang.model.element.Modifier;
import javax.tools.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;

/** Parse, never execute, project sources. javac handles comments, escapes and multiline declarations. */
public class SourceFacts {
    private static final Pattern NATIVE = Pattern.compile(
        "(?:org[./]apache[./]geode|com[./]gemstone[./]gemfire|com[./]vmware[./]gemfire[./](?!driver\\b)[\\w$]+)(?:[./][\\w$*]+)*");
    private static final Set<String> IMMUTABLE = Set.of("boolean", "byte", "short", "int", "long", "char",
        "float", "double", "Boolean", "Byte", "Short", "Integer", "Long", "Character", "Float", "Double",
        "String", "BigInteger", "BigDecimal", "UUID", "Duration", "Instant");

    public static void main(String[] args) throws Exception {
        Path root = Path.of(args[0]).toAbsolutePath().normalize();
        List<Path> files;
        try (var lines = Files.lines(Path.of(args[1]))) {
            files = lines.filter(s -> !s.isBlank()).map(root::resolve).toList();
        }
        var compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) throw new IllegalStateException("A JDK 17+ is required");
        var diagnostics = new DiagnosticCollector<JavaFileObject>();
        try (var manager = compiler.getStandardFileManager(diagnostics, null, null)) {
            var task = (JavacTask) compiler.getTask(null, manager, diagnostics,
                List.of("-proc:none", "--release", "17"), null, manager.getJavaFileObjectsFromPaths(files));
            var trees = Trees.instance(task);
            var units = new ArrayList<CompilationUnitTree>();
            task.parse().forEach(units::add);
            if (diagnostics.getDiagnostics().stream().anyMatch(d -> d.getKind() == Diagnostic.Kind.ERROR)) {
                throw new IllegalStateException("Cannot parse source: " + diagnostics.getDiagnostics());
            }
            // Discover immutable records across units rather than trusting a class's name.
            Set<String> immutableValues = new HashSet<>();
            boolean changed;
            do {
                int previous = immutableValues.size();
                for (var unit : units) {
                    var probe = new Visitor(root, unit, trees, immutableValues);
                    for (var declaration : unit.getTypeDecls()) {
                        if (declaration instanceof ClassTree type && type.getKind() == Tree.Kind.RECORD && probe.immutableFields(type))
                            immutableValues.add(unit.getPackageName() + "." + type.getSimpleName());
                    }
                }
                changed = immutableValues.size() != previous;
            } while (changed);
            for (var unit : units) new Visitor(root, unit, trees, immutableValues).scan(unit, null);
        }
    }

    private static String simple(String name) {
        String raw = name.replaceAll("<.*>", "");
        return raw.substring(raw.lastIndexOf('.') + 1);
    }

    private static class Visitor extends TreePathScanner<Void, Void> {
        private final CompilationUnitTree unit;
        private final Trees trees;
        private final String path;
        private final Map<String, String> imports = new HashMap<>();
        private final Set<String> enums = new HashSet<>();
        private final Set<String> immutableRecords = new HashSet<>();
        private final Set<String> immutableValues;
        private final String module;
        private final boolean production;
        private final boolean driverModule;
        private final boolean nativeAllowed;
        private boolean wildcardNative;

        Visitor(Path root, CompilationUnitTree unit, Trees trees, Set<String> immutableValues) {
            this.unit = unit;
            this.trees = trees;
            this.immutableValues = immutableValues;
            this.path = root.relativize(Path.of(unit.getSourceFile().toUri())).toString();
            this.module = path.split("/")[0];
            this.production = path.contains("/src/main/");
            this.driverModule = module.startsWith("spring-data-gemfire-driver-");
            this.nativeAllowed = module.matches("spring-data-gemfire-driver-[0-9].*");
            for (var entry : unit.getImports()) {
                var name = entry.getQualifiedIdentifier().toString();
                imports.put(simple(name), name);
                wildcardNative |= name.endsWith(".*") && NATIVE.matcher(name).find();
            }
            new TreeScanner<Void, Void>() {
                public Void visitClass(ClassTree node, Void unused) {
                    if (node.getKind() == Tree.Kind.ENUM && immutableEnum(node)) enums.add(node.getSimpleName().toString());
                    if (node.getKind() == Tree.Kind.RECORD && immutableFields(node)) immutableRecords.add(node.getSimpleName().toString());
                    return super.visitClass(node, unused);
                }
            }.scan(unit, null);
        }

        private void emit(String rule, Tree node, String detail) {
            if (rule.equals("constructor-injection") && imports.values().stream().anyMatch(s -> s.startsWith("com.vmware.gemfire.driver.")))
                rule = "migrated-constructor-injection";
            long start = trees.getSourcePositions().getStartPosition(unit, node);
            long line = start < 0 ? 1 : unit.getLineMap().getLineNumber(start);
            System.out.println(rule + "\t" + path + "\t" + line + "\t" + detail.replaceAll("\\s+", " ").trim());
        }

        private String resolved(String type) {
            var raw = type.replaceAll("<.*>", "");
            return imports.getOrDefault(raw, raw);
        }

        private boolean immutableType(String type) {
            String name = simple(type);
            if (type.contains("[") || type.contains("<")) return false;
            String fq = resolved(type);
            // An arbitrary project class named String/UUID must not gain an exemption.
            return IMMUTABLE.contains(name) && (!fq.contains(".") || fq.startsWith("java."))
                || enums.contains(name) || immutableRecords.contains(name)
                || immutableValues.contains(fq) || immutableValues.contains(unit.getPackageName() + "." + fq);
        }

        private boolean immutableFields(ClassTree node) {
            return node.getMembers().stream().filter(m -> m instanceof VariableTree)
                .map(m -> (VariableTree) m).filter(v -> !v.getModifiers().getFlags().contains(Modifier.STATIC))
                .allMatch(v -> v.getModifiers().getFlags().contains(Modifier.FINAL) && immutableType(v.getType().toString()));
        }

        private boolean immutableEnum(ClassTree node) {
            Set<String> constants = new HashSet<>();
            for (var member : node.getMembers()) {
                if (member instanceof VariableTree v && v.getInitializer() instanceof NewClassTree
                    && v.getType().toString().equals(node.getSimpleName().toString())
                    && v.getModifiers().getFlags().containsAll(Set.of(Modifier.STATIC, Modifier.FINAL)))
                    constants.add(v.getName().toString());
            }
            return node.getMembers().stream().filter(m -> m instanceof VariableTree).map(m -> (VariableTree) m)
                .allMatch(v -> constants.contains(v.getName().toString()) ||
                    v.getModifiers().getFlags().contains(Modifier.FINAL) && (immutableType(v.getType().toString()) ||
                        v.getModifiers().getFlags().contains(Modifier.STATIC)
                        && v.getType().toString().equals(node.getSimpleName().toString())
                        && v.getInitializer() instanceof IdentifierTree alias
                        && constants.contains(alias.getName().toString())));
        }

        private String context(Tree node) {
            if (getCurrentPath().getLeaf() instanceof ImportTree) return node.toString();
            Tree parent = getCurrentPath().getParentPath().getLeaf();
            if (parent instanceof MethodTree method) return method.getName() + "(" + method.getParameters() + "): " + method.getReturnType();
            if (parent instanceof ClassTree type) return type.getSimpleName() + " extends " + type.getExtendsClause() + " implements " + type.getImplementsClause();
            return parent instanceof ImportTree ? node.toString() : parent.toString();
        }

        private void reference(Tree node, String text) {
            var matcher = NATIVE.matcher(text);
            if (!nativeAllowed && matcher.find()) emit("native-source", node, text + " :: " + context(node));
            if (production) {
                String target = text.startsWith("com.vmware.gemfire.driver.spi.") ? "spi"
                    : text.startsWith("com.vmware.gemfire.driver.core.") ? "core"
                    : text.startsWith("com.vmware.gemfire.driver.mock.") ? "mock"
                    : text.startsWith("com.vmware.gemfire.driver.gemfire") ? "native"
                    : text.startsWith("com.vmware.gemfire.driver.contract.") ? "contract-tests"
                    : text.startsWith("org.springframework.data.gemfire.") ? "spring" : "";
                Set<String> allowed = module.endsWith("-spi") ? Set.of("spi")
                    : module.endsWith("-core") ? Set.of("spi", "core")
                    : module.endsWith("-mock") ? Set.of("spi", "mock")
                    : nativeAllowed ? Set.of("spi", "native")
                    : module.endsWith("-contract-tests") ? Set.of("spi", "core", "contract-tests")
                    : Set.of("spi", "core", "spring");
                if (!target.isEmpty() && !allowed.contains(target)) emit("dependency-direction", node, text);
            }
        }

        @Override public Void visitImport(ImportTree node, Void unused) {
            reference(node.getQualifiedIdentifier(), node.getQualifiedIdentifier().toString());
            return null;
        }

        @Override public Void visitMemberSelect(MemberSelectTree node, Void unused) {
            if (NATIVE.matcher(node.toString()).find() || node.toString().startsWith("com.vmware.gemfire.driver.")) {
                reference(node, node.toString());
                return null;
            }
            return super.visitMemberSelect(node, unused);
        }

        @Override public Void visitIdentifier(IdentifierTree node, Void unused) {
            String name = node.getName().toString();
            if (imports.containsKey(name)) reference(node, imports.get(name));
            else if (!nativeAllowed && wildcardNative && Character.isUpperCase(name.charAt(0))) {
                emit("native-wildcard-use", node, context(node));
            }
            return super.visitIdentifier(node, unused);
        }

        @Override public Void visitLiteral(LiteralTree node, Void unused) {
            if (node.getValue() instanceof String value && NATIVE.matcher(value).find()) {
                String rule = "native-string";
                var parent = getCurrentPath().getParentPath().getLeaf();
                if (parent instanceof MethodInvocationTree call &&
                    call.getMethodSelect().toString().matches(".*(?:logger|log)\\.(?:trace|debug|info|warn|error)")) rule = "logger-text";
                if (!nativeAllowed || rule.equals("logger-text")) emit(rule, node, value);
            }
            return super.visitLiteral(node, unused);
        }

        private String constantString(Tree node) {
            if (node instanceof LiteralTree literal && literal.getValue() instanceof String value) return value;
            if (node instanceof ParenthesizedTree parens) return constantString(parens.getExpression());
            if (node instanceof BinaryTree binary && node.getKind() == Tree.Kind.PLUS) {
                String left = constantString(binary.getLeftOperand()), right = constantString(binary.getRightOperand());
                if (left != null && right != null) return left + right;
            }
            return null;
        }

        @Override public Void visitBinary(BinaryTree node, Void unused) {
            String value = constantString(node);
            if (!nativeAllowed && value != null && NATIVE.matcher(value).find()) {
                emit("native-string", node, value);
                return null;
            }
            return super.visitBinary(node, unused);
        }

        @Override public Void visitVariable(VariableTree node, Void unused) {
            Tree parent = getCurrentPath().getParentPath().getLeaf();
            if (!(parent instanceof ClassTree owner)) return super.visitVariable(node, unused);
            var flags = node.getModifiers().getFlags();
            boolean enumConstant = owner.getKind() == Tree.Kind.ENUM && node.getInitializer() instanceof NewClassTree
                && node.getType().toString().equals(owner.getSimpleName().toString());
            boolean implicitStatic = owner.getKind() == Tree.Kind.INTERFACE || owner.getKind() == Tree.Kind.ANNOTATION_TYPE;
            if (!enumConstant && (flags.contains(Modifier.STATIC) || implicitStatic)) {
                if (!(flags.contains(Modifier.FINAL) || implicitStatic) || !immutableType(node.getType().toString())) {
                    emit("static-state", node, owner.getSimpleName() + " :: " + node);
                }
            }
            if (production && !driverModule) {
                String type = simple(node.getType().toString());
                if (Set.of("GemFireDriverManager", "GemFireDriver").contains(type) &&
                    (!flags.contains(Modifier.FINAL) || node.getInitializer() != null))
                    emit("constructor-injection", node, node.toString());
                for (var annotation : node.getModifiers().getAnnotations()) {
                    if (Set.of("Autowired", "Inject", "Resource").contains(simple(annotation.getAnnotationType().toString())))
                        emit("constructor-injection", node, node.toString());
                }
            }
            return super.visitVariable(node, unused);
        }

        @Override public Void visitClass(ClassTree node, Void unused) {
            if (node.getKind() == Tree.Kind.ENUM) {
                long constants = node.getMembers().stream().filter(m -> m instanceof VariableTree v
                    && v.getInitializer() instanceof NewClassTree && v.getType().toString().equals(node.getSimpleName().toString())).count();
                boolean service = node.getMembers().stream().anyMatch(m -> m instanceof MethodTree method && method.getReturnType() != null);
                if (constants == 1 && service) emit("singleton", node, "enum service " + node.getSimpleName());
            }
            return super.visitClass(node, unused);
        }

        @Override public Void visitMethod(MethodTree node, Void unused) {
            if (node.getModifiers().getFlags().contains(Modifier.STATIC)) {
                var owner = (ClassTree) getCurrentPath().getParentPath().getLeaf();
                String name = node.getName().toString();
                boolean ownReturn = node.getReturnType() != null && simple(node.getReturnType().toString()).equals(owner.getSimpleName().toString());
                // Factory allowance concerns immutable owned state; static fields remain subject to the stricter type rule.
                boolean valueFactory = (owner.getKind() == Tree.Kind.RECORD || owner.getKind() == Tree.Kind.ENUM && immutableEnum(owner)
                    || owner.getModifiers().getFlags().contains(Modifier.FINAL))
                    && immutableFields(owner) && !owner.getMembers().stream().anyMatch(m -> m instanceof VariableTree v
                        && v.getModifiers().getFlags().contains(Modifier.STATIC) && !immutableType(v.getType().toString()));
                boolean driverService = owner.getSimpleName().toString().endsWith("DriverManager") ||
                    owner.getImplementsClause().stream().anyMatch(t -> simple(t.toString()).matches("GemFireDriver|DriverConnection|DriverRegion|DriverPool"));
                valueFactory &= !driverService;
                if (name.equals("getInstance") || ownReturn && !valueFactory)
                    emit("static-service", node, owner.getSimpleName() + " :: " + node);
                if (name.matches("reset.*|clear.*") && production) emit("static-reset", node, owner.getSimpleName() + " :: " + node);
                if (production && !driverModule && node.getReturnType() != null &&
                    resolved(node.getReturnType().toString()).matches(".*(?:GemFireDriverManager|GemFireDriver|DriverConnection|DriverRegion|DriverPool)"))
                    emit("driver-static-access", node, node.toString());
            }
            if (production && !driverModule && node.getReturnType() != null) {
                if (node.getName().toString().startsWith("set") && node.getParameters().stream().anyMatch(p ->
                    Set.of("GemFireDriverManager", "GemFireDriver").contains(simple(p.getType().toString()))))
                    emit("constructor-injection", node, node.toString());
                for (var annotation : node.getModifiers().getAnnotations()) {
                    if (Set.of("Autowired", "Inject", "Resource").contains(simple(annotation.getAnnotationType().toString())))
                        emit("constructor-injection", node, node.toString());
                }
            }
            return super.visitMethod(node, unused);
        }

        @Override public Void visitNewClass(NewClassTree node, Void unused) {
            if (production && !driverModule && resolved(node.getIdentifier().toString()).matches(".*(?:GemFireDriverManager|MockGemFireDriver|GemFire[0-9]+Driver)"))
                emit("driver-construction", node, node.toString());
            return super.visitNewClass(node, unused);
        }

        @Override public Void visitMethodInvocation(MethodInvocationTree node, Void unused) {
            String select = node.getMethodSelect().toString();
            if (production && !driverModule && (select.endsWith("ServiceLoader.load") ||
                imports.getOrDefault(select, "").equals("java.util.ServiceLoader.load")))
                emit("driver-discovery", node, node.toString());
            return super.visitMethodInvocation(node, unused);
        }
    }
}
