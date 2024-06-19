/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static java.util.Arrays.stream;
import static org.springframework.data.gemfire.util.ArrayUtils.defaultIfEmpty;
import static org.springframework.data.gemfire.util.ArrayUtils.nullSafeArray;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalArgumentException;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalStateException;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionShortcut;
import org.apache.geode.cache.Scope;
import org.apache.geode.cache.client.ClientRegionShortcut;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AspectJTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.data.gemfire.RegionAttributesFactoryBean;
import org.springframework.data.gemfire.ScopeType;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.AbstractAnnotationConfigSupport;
import org.springframework.data.gemfire.config.annotation.support.CacheTypeAwareRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.GemFireComponentClassTypeScanner;
import org.springframework.data.gemfire.config.xml.GemfireConstants;
import org.springframework.data.gemfire.mapping.GemfireMappingContext;
import org.springframework.data.gemfire.mapping.GemfirePersistentEntity;
import org.springframework.data.gemfire.mapping.annotation.ClientRegion;
import org.springframework.data.gemfire.mapping.annotation.LocalRegion;
import org.springframework.data.gemfire.support.CompositeTypeFilter;
import org.springframework.data.gemfire.util.SpringExtensions;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * The {@link EntityDefinedRegionsConfiguration} class is Spring {@link ImportBeanDefinitionRegistrar} used in
 * the {@link EnableEntityDefinedRegions} annotation to dynamically create GemFire/Geode {@link Region Regions}
 * based on the application persistent entity classes.
 *
 * @author John Blum
 * @see ClassLoader
 * @see Annotation
 * @see Region
 * @see BeanDefinition
 * @see BeanDefinitionBuilder
 * @see BeanDefinitionRegistry
 * @see ImportBeanDefinitionRegistrar
 * @see org.springframework.data.gemfire.LocalRegionFactoryBean
 * @see RegionAttributesFactoryBean
 * @see ClientRegionFactoryBean
 * @see AbstractAnnotationConfigSupport
 * @see CacheTypeAwareRegionFactoryBean
 * @see GemFireComponentClassTypeScanner
 * @see GemfireMappingContext
 * @see GemfirePersistentEntity
 * @see ClientRegion
 * @see LocalRegion
 * @see org.springframework.data.gemfire.mapping.annotation.Region
 * @since 1.9.0
 */
@SuppressWarnings("unused")
public class EntityDefinedRegionsConfiguration extends AbstractAnnotationConfigSupport
		implements ImportBeanDefinitionRegistrar {

	protected static final ClientRegionShortcut DEFAULT_CLIENT_REGION_SHORTCUT = ClientRegionShortcut.PROXY;

	protected static final RegionShortcut DEFAULT_SERVER_REGION_SHORTCUT = RegionShortcut.REPLICATE;

	@Autowired(required = false)
	private GemfireMappingContext mappingContext;

	@Autowired(required = false)
	@SuppressWarnings("all")
	private List<RegionConfigurer> regionConfigurers = Collections.emptyList();

	/**
	 * Returns the {@link Annotation} {@link Class type} that configures and creates {@link Region Regions}
	 * for application persistent entities.
	 *
	 * @return the {@link Annotation} {@link Class type} that configures and creates {@link Region Regions}
	 * for application persistent entities.
	 * @see EnableEntityDefinedRegions
	 * @see Annotation
	 * @see Class
	 */
	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return EnableEntityDefinedRegions.class;
	}

	/**
	 * Registers {@link Region} bean definitions in the Spring context for all application domain object
	 * that have been identified as {@link GemfirePersistentEntity persistent entities}.
	 *
	 * @param importingClassMetadata {@link Class} with the {@link EnableEntityDefinedRegions} annotation.
	 * @param registry {@link BeanDefinitionRegistry} used to register the {@link Region} bean definitions
	 * in the Spring context.
	 * @see BeanDefinitionRegistry
	 * @see AnnotationMetadata
	 */
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

		if (isAnnotationPresent(importingClassMetadata)) {

			AnnotationAttributes enableEntityDefinedRegionsAttributes = getAnnotationAttributes(importingClassMetadata);

			newGemFireComponentClassTypeScanner(importingClassMetadata, enableEntityDefinedRegionsAttributes).scan()
				.forEach(persistentEntityType -> {

					RegionBeanDefinitionMetadata regionMetadata =
						RegionBeanDefinitionMetadata.with(getPersistentEntity(persistentEntityType))
							.using(enableEntityDefinedRegionsAttributes);

					registerRegionBeanDefinition(regionMetadata, registry);

					postProcess(importingClassMetadata, registry,
						regionMetadata.getPersistentEntity().orElse(null));
				});
		}
	}

	protected GemFireComponentClassTypeScanner newGemFireComponentClassTypeScanner(
			AnnotationMetadata importingClassMetadata, AnnotationAttributes enableEntityDefinedRegionsAttributes) {

		Set<String> resolvedBasePackages =
			resolveBasePackages(importingClassMetadata, enableEntityDefinedRegionsAttributes);

		TypeFilter resolvedIncludesTypeFilter =
			CompositeTypeFilter.composeOr(resolveIncludes(enableEntityDefinedRegionsAttributes));

		TypeFilter resolvedRegionAnnotatedPersistentEntityTypeFilter =
			CompositeTypeFilter.composeOr(resolveRegionAnnotatedPersistentEntityTypeFilters());

		return GemFireComponentClassTypeScanner.from(resolvedBasePackages).with(resolveBeanClassLoader())
			.withExcludes(resolveExcludes(enableEntityDefinedRegionsAttributes))
			.withIncludes(CompositeTypeFilter.composeAnd(resolvedIncludesTypeFilter,
				resolvedRegionAnnotatedPersistentEntityTypeFilter));
	}

	protected Set<String> resolveBasePackages(AnnotationMetadata importingClassMetaData,
			AnnotationAttributes enableEntityDefinedRegionAttributes) {

		Set<String> resolvedBasePackages = new HashSet<>();

		Collections.addAll(resolvedBasePackages, resolveProperty(entitiesProperty("base-packages"),
			String[].class, nullSafeArray(defaultIfEmpty(
					enableEntityDefinedRegionAttributes.getStringArray("basePackages"),
					enableEntityDefinedRegionAttributes.getStringArray("value")),
				String.class)));

		stream(nullSafeArray(enableEntityDefinedRegionAttributes.getClassArray(
			"basePackageClasses"), Class.class))
				.forEach(type -> resolvedBasePackages.add(type.getPackage().getName()));

		if (resolvedBasePackages.isEmpty()) {
			resolvedBasePackages.add(ClassUtils.getPackageName(importingClassMetaData.getClassName()));
		}

		return resolvedBasePackages;
	}

	protected Iterable<TypeFilter> resolveExcludes(AnnotationAttributes enableEntityDefinedRegionsAttributes) {
		return parseFilters(enableEntityDefinedRegionsAttributes.getAnnotationArray("excludeFilters"));
	}

	protected Iterable<TypeFilter> resolveIncludes(AnnotationAttributes enableEntityDefinedRegionsAttributes) {
		return parseFilters(enableEntityDefinedRegionsAttributes.getAnnotationArray("includeFilters"));
	}

	protected Iterable<TypeFilter> resolveRegionAnnotatedPersistentEntityTypeFilters() {

		return org.springframework.data.gemfire.mapping.annotation.Region.REGION_ANNOTATION_TYPES.stream()
			.map(AnnotationTypeFilter::new)
			.collect(Collectors.toSet());
	}

	private Iterable<TypeFilter> parseFilters(AnnotationAttributes[] componentScanFilterAttributes) {

		return stream(nullSafeArray(componentScanFilterAttributes, AnnotationAttributes.class))
			.flatMap(filterAttributes -> typeFiltersFor(filterAttributes).stream())
			.collect(Collectors.toSet());
	}

	@SuppressWarnings("unchecked")
	private Set<TypeFilter> typeFiltersFor(AnnotationAttributes filterAttributes) {

		Set<TypeFilter> typeFilters = new HashSet<>();

		FilterType filterType = filterAttributes.getEnum("type");

		stream(nullSafeArray(filterAttributes.getClassArray("value"), Class.class))
			.forEach(filterClass -> {

				switch (filterType) {
					case ANNOTATION:
						Assert.isAssignable(Annotation.class, filterClass,
							String.format("@ComponentScan.Filter class [%s] must be an Annotation", filterClass));
						typeFilters.add(new AnnotationTypeFilter((Class<Annotation>) filterClass));
						break;
					case ASSIGNABLE_TYPE:
						typeFilters.add(new AssignableTypeFilter(filterClass));
						break;
					case CUSTOM:
						Assert.isAssignable(TypeFilter.class, filterClass,
							String.format("@ComponentScan.Filter class [%s] must be a TypeFilter", filterClass));
						typeFilters.add(BeanUtils.instantiateClass(filterClass, TypeFilter.class));
						break;
					default:
						throw newIllegalArgumentException(
							"Illegal filter type [%s] when 'value' or 'classes' are specified", filterType);
				}
			});

		stream(nullSafeGetPatterns(filterAttributes)).forEach(pattern -> {

			switch (filterType) {
				case ASPECTJ:
					typeFilters.add(new AspectJTypeFilter(pattern, resolveBeanClassLoader()));
					break;
				case REGEX:
					typeFilters.add(new RegexPatternTypeFilter(Pattern.compile(pattern)));
					break;
				default:
					throw newIllegalArgumentException(
						"Illegal filter type [%s] when 'patterns' are specified", filterType);
			}
		});

		return typeFilters;
	}

	/**
	 * Safely reads the {@code pattern} attribute from the given {@link AnnotationAttributes}
	 * and returns an empty array if the attribute is not present.
	 *
	 * @param filterAttributes {@link AnnotationAttributes} from which to extract the {@code pattern} attribute value.
	 * @return a {@link String} array.
	 */
	private String[] nullSafeGetPatterns(AnnotationAttributes filterAttributes) {

		return SpringExtensions.<String[]>safeGetValue(() ->
			nullSafeArray(filterAttributes.getStringArray("pattern"), String.class), () -> new String[0]);
	}

	/**
	 * Returns the associated {@link GemfirePersistentEntity persistent entity} for the given application
	 * domain object type.
	 *
	 * @param persistentEntityType {@link Class type} of the application domain object used to lookup
	 * the {@link GemfirePersistentEntity persistent entity} from the {@link @GemfireMappingContext mapping context}.
	 * @return the {@link GemfirePersistentEntity persistent entity} for the given application domain object type.
	 * @see GemfirePersistentEntity
	 * @see Class
	 */
	protected GemfirePersistentEntity<?> getPersistentEntity(Class<?> persistentEntityType) {
		return resolveMappingContext().getPersistentEntity(persistentEntityType);
	}

	/**
	 * Resolves the {@link GemfireMappingContext mapping context} by returning the configured
	 * {@link GemfireMappingContext mapping context} if present, or attempts to lookup
	 * the {@link GemfireMappingContext mapping context} from the configured {@link BeanFactory}.
	 * If the lookup is unsuccessful, then this method will return a new {@link GemfireMappingContext mapping context}.
	 *
	 * @return the resolved {@link GemfireMappingContext mapping context}.
	 * @see GemfireMappingContext
	 */
	protected @NonNull GemfireMappingContext resolveMappingContext() {

		if (this.mappingContext == null) {
			this.mappingContext = SpringExtensions.<GemfireMappingContext>safeGetValue(() ->
				getBeanFactory().getBean(GemfireMappingContext.class), GemfireMappingContext::new);
		}

		return this.mappingContext;
	}

	/**
	 * Registers an individual bean definition in the Spring container for the {@link Region} determined from
	 * the application domain object, {@link GemfirePersistentEntity persistent entity}.
	 *
	 * @param regionMetadata {@link RegionBeanDefinitionMetadata} used to configure the {@link Region} bean definition.
	 * @param registry {@link BeanDefinitionRegistry} used to register the {@link Region} bean definition
	 * in the Spring context.
	 * @see BeanDefinitionRegistry
	 * @see GemfirePersistentEntity
	 */
	protected void registerRegionBeanDefinition(RegionBeanDefinitionMetadata regionMetadata,
			BeanDefinitionRegistry registry) {

		BeanDefinitionBuilder regionFactoryBeanBuilder =
			BeanDefinitionBuilder.genericBeanDefinition(CacheTypeAwareRegionFactoryBean.class)
				.addPropertyReference("cache", GemfireConstants.DEFAULT_GEMFIRE_CACHE_NAME)
				.addPropertyValue("close", false)
				.addPropertyValue("regionConfigurers", resolveRegionConfigurers());

		setRegionAttributes(regionFactoryBeanBuilder, regionMetadata);

		registry.registerBeanDefinition(regionMetadata.getRegionName(), regionFactoryBeanBuilder.getBeanDefinition());
	}

	protected List<RegionConfigurer> resolveRegionConfigurers() {

		return Optional.ofNullable(this.regionConfigurers)
			.filter(regionConfigurers -> !regionConfigurers.isEmpty())
			.orElseGet(() ->
				Collections.singletonList(LazyResolvingComposableRegionConfigurer.create(getBeanFactory())));
	}

	protected BeanDefinitionBuilder setRegionAttributes(BeanDefinitionBuilder regionFactoryBeanBuilder,
			RegionBeanDefinitionMetadata regionMetadata) {

		Optional.<Annotation>ofNullable(regionMetadata.getRegionAnnotation()).ifPresent(regionAnnotation -> {

			AnnotationAttributes regionAnnotationAttributes = getAnnotationAttributes(regionAnnotation);

			regionFactoryBeanBuilder.addPropertyValue("clientRegionShortcut",
				resolveClientRegionShortcut(regionMetadata, regionAnnotation, regionAnnotationAttributes));

			regionFactoryBeanBuilder.addPropertyValue("serverRegionShortcut",
				resolveServerRegionShortcut(regionMetadata, regionAnnotation, regionAnnotationAttributes));

			if (regionAnnotationAttributes.containsKey("diskStoreName")) {

				String diskStoreName = regionAnnotationAttributes.getString("diskStoreName");

				setPropertyValueIfNotDefault(regionFactoryBeanBuilder, "diskStoreName",
					diskStoreName, "");

				if (StringUtils.hasText(diskStoreName)) {
					regionFactoryBeanBuilder.addDependsOn(diskStoreName);
				}
			}

			if (regionAnnotationAttributes.containsKey("ignoreIfExists")) {
				regionFactoryBeanBuilder.addPropertyValue("lookupEnabled",
					regionAnnotationAttributes.getBoolean("ignoreIfExists"));
			}

			if (regionMetadata.isStrict()) {
				regionFactoryBeanBuilder.addPropertyValue("keyConstraint",
					regionMetadata.getRegionKeyConstraint());

				regionFactoryBeanBuilder.addPropertyValue("valueConstraint",
					regionMetadata.getRegionValueConstraint());
			}

			BeanDefinitionBuilder regionAttributesFactoryBeanBuilder =
				BeanDefinitionBuilder.genericBeanDefinition(RegionAttributesFactoryBean.class);

			regionFactoryBeanBuilder.addPropertyValue("attributes",
				regionAttributesFactoryBeanBuilder.getBeanDefinition());

			if (regionAnnotationAttributes.containsKey("diskSynchronous")) {
				setPropertyValueIfNotDefault(regionAttributesFactoryBeanBuilder, "diskSynchronous",
					regionAnnotationAttributes.getBoolean("diskSynchronous"), true);
			}

			if (regionAnnotationAttributes.containsKey("ignoreJta")) {
				setPropertyValueIfNotDefault(regionAttributesFactoryBeanBuilder, "ignoreJTA",
					regionAnnotationAttributes.getBoolean("ignoreJta"), false);
			}

			setClientRegionAttributes(regionMetadata, regionAnnotationAttributes, regionFactoryBeanBuilder);
		});

		return regionFactoryBeanBuilder;
	}

	protected ClientRegionShortcut resolveClientRegionShortcut(RegionBeanDefinitionMetadata regionMetadata,
			Annotation regionAnnotation, AnnotationAttributes regionAnnotationAttributes) {

		return ClientRegion.class.equals(regionAnnotation.annotationType())
			? regionAnnotationAttributes.getEnum("shortcut")
			: regionMetadata.resolveClientRegionShortcut(DEFAULT_CLIENT_REGION_SHORTCUT);
	}

	protected RegionShortcut resolveServerRegionShortcut(RegionBeanDefinitionMetadata regionMetadata,
			Annotation regionAnnotation, AnnotationAttributes regionAnnotationAttributes) {

		Class<? extends Annotation> regionAnnotationType = regionAnnotation.annotationType();

		boolean persistent = (regionAnnotationAttributes.containsKey("persistent")
			&& regionAnnotationAttributes.getBoolean("persistent"));

		return LocalRegion.class.equals(regionAnnotationType)
				? (persistent ? RegionShortcut.LOCAL_PERSISTENT : RegionShortcut.LOCAL)
			: regionMetadata.resolveServerRegionShortcut(DEFAULT_SERVER_REGION_SHORTCUT);
	}

	protected BeanDefinitionBuilder setClientRegionAttributes(RegionBeanDefinitionMetadata regionMetadata,
			AnnotationAttributes regionAnnotationAttributes, BeanDefinitionBuilder regionFactoryBeanBuilder) {

		String resolvedPoolName = regionAnnotationAttributes.containsKey("poolName")
			? regionAnnotationAttributes.getString("poolName")
			: regionMetadata.getPoolName().orElse(ClientRegionFactoryBean.DEFAULT_POOL_NAME);

		setPropertyValueIfNotDefault(regionFactoryBeanBuilder, "poolName",
			resolvedPoolName, ClientRegionFactoryBean.DEFAULT_POOL_NAME);

		return regionFactoryBeanBuilder;
	}

	private <T> BeanDefinitionBuilder setPropertyReferenceIfSet(BeanDefinitionBuilder beanDefinitionBuilder,
			String propertyName, String beanName) {

		return (StringUtils.hasText(beanName)
			? beanDefinitionBuilder.addPropertyReference(propertyName, beanName)
			: beanDefinitionBuilder);
	}

	private <T> BeanDefinitionBuilder setPropertyValueIfNotDefault(BeanDefinitionBuilder beanDefinitionBuilder,
			String propertyName, T value, T defaultValue) {

		return (value != null && !value.equals(defaultValue)
			? beanDefinitionBuilder.addPropertyValue(propertyName, value)
			: beanDefinitionBuilder);
	}

	/**
	 * Performs addition post processing on the {@link GemfirePersistentEntity} to offer additional feature support
	 * (e.g. dynamic Index creation).
	 *
	 * @param importingClassMetadata {@link AnnotationMetadata} for the importing application class.
	 * @param registry {@link BeanDefinitionRegistry} used to register Spring bean definitions.
	 * @param persistentEntity {@link GemfirePersistentEntity} to process.
	 * @return the given {@link GemfirePersistentEntity}.
	 * @see BeanDefinitionRegistry
	 * @see AnnotationMetadata
	 * @see GemfirePersistentEntity
	 */
	protected GemfirePersistentEntity<?> postProcess(AnnotationMetadata importingClassMetadata,
			BeanDefinitionRegistry registry, GemfirePersistentEntity<?> persistentEntity) {

		return persistentEntity;
	}

	/**
	 * The {@link RegionBeanDefinitionMetadata} class encapsulates details for creating a {@link Region}
	 * from application persistent entities.  The details are captured during a persistent entity component scan.
	 *
	 * @see GemfirePersistentEntity
	 */
	protected static class RegionBeanDefinitionMetadata {

		/**
		 * Factory method used to construct a new instance of the {@link RegionBeanDefinitionMetadata}.
		 *
		 * @return a new instance of {@link RegionBeanDefinitionMetadata}.
		 */
		protected static RegionBeanDefinitionMetadata with(GemfirePersistentEntity<?> persistentEntity) {
			return new RegionBeanDefinitionMetadata(persistentEntity);
		}

		private boolean strict;

		private ClientRegionShortcut clientRegionShortcut;

		private final GemfirePersistentEntity<?> persistentEntity;

		private RegionShortcut serverRegionShortcut;

		private String poolName;

		protected RegionBeanDefinitionMetadata(@NonNull GemfirePersistentEntity<?> persistentEntity) {
			Assert.notNull(persistentEntity, "GemfirePersistentEntity is required");
			this.persistentEntity = persistentEntity;
		}

		protected boolean isStrict() {
			return this.strict;
		}

		protected Optional<ClientRegionShortcut> getClientRegionShortcut() {
			return Optional.ofNullable(this.clientRegionShortcut);
		}

		protected ClientRegionShortcut resolveClientRegionShortcut(ClientRegionShortcut defaultClientRegionShortcut) {
			return getClientRegionShortcut().orElse(defaultClientRegionShortcut);
		}

		protected Optional<GemfirePersistentEntity<?>> getPersistentEntity() {
			return Optional.ofNullable(this.persistentEntity);
		}

		protected @NonNull GemfirePersistentEntity<?> resolvePersistentEntity() {
			return getPersistentEntity().orElseThrow(() ->
				newIllegalStateException("GemfirePersistentEntity could not be resolved"));
		}

		protected Optional<String> getPoolName() {
			return Optional.ofNullable(this.poolName)
				.filter(StringUtils::hasText);
		}

		protected <T extends Annotation> T getRegionAnnotation() {
			return resolvePersistentEntity().getRegionAnnotation();
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		protected @NonNull Class<?> getRegionKeyConstraint() {

			return Optional.ofNullable(resolvePersistentEntity().getIdProperty())
				.map(idProperty -> idProperty.getActualType())
				.orElse((Class) Object.class);
		}

		protected String getRegionName() {
			return resolvePersistentEntity().getRegionName();
		}

		@SuppressWarnings("all")
		protected @NonNull Class<?> getRegionValueConstraint() {

			return Optional.ofNullable(resolvePersistentEntity().getType())
				.orElse((Class) Object.class);
		}

		protected Optional<RegionShortcut> getServerRegionShortcut() {
			return Optional.ofNullable(this.serverRegionShortcut);
		}

		protected RegionShortcut resolveServerRegionShortcut(RegionShortcut defaultServerRegionShortcut) {
			return getServerRegionShortcut().orElse(defaultServerRegionShortcut);
		}

		protected @NonNull RegionBeanDefinitionMetadata is(boolean strict) {
			this.strict = strict;
			return this;
		}

		protected @NonNull RegionBeanDefinitionMetadata using(
				@Nullable AnnotationAttributes enableEntityDefinedRegionsAttributes) {

			return Optional.ofNullable(enableEntityDefinedRegionsAttributes)
				.map(it ->
					this.using(it.<ClientRegionShortcut>getEnum("clientRegionShortcut"))
						.using(it.getString("poolName"))
						.using(it.<RegionShortcut>getEnum("serverRegionShortcut"))
						.is(it.getBoolean("strict"))
				)
				.orElse(this);
		}

		protected @NonNull RegionBeanDefinitionMetadata using(@Nullable ClientRegionShortcut clientRegionShortcut) {
			this.clientRegionShortcut = clientRegionShortcut;
			return this;
		}

		protected @NonNull RegionBeanDefinitionMetadata using(@Nullable RegionShortcut serverRegionShortcut) {
			this.serverRegionShortcut = serverRegionShortcut;
			return this;
		}

		protected @NonNull RegionBeanDefinitionMetadata using(@Nullable String poolName) {
			this.poolName = poolName;
			return this;
		}
	}
}
