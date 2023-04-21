---
title: POJO Mapping
---

<!-- 
 Copyright (c) VMware, Inc. 2022. All rights reserved.
 Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 agreements. See the NOTICE file distributed with this work for additional information regarding
 copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance with the License. You may obtain a
 copy of the License at
 
 http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software distributed under the License
 is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 or implied. See the License for the specific language governing permissions and limitations under
 the License.
-->

<!--
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->

This topic describes:

- [Entity Mapping](#mapping.entities)

- [Repository Mapping](#mapping.repositories)

- [MappingPdxSerializer](#mapping.pdx-serializer)


## <a id="object-mapping-fundamentals"></a>Object Mapping Fundamentals

This section covers the fundamentals of Spring Data object mapping,
object creation, field and property access, mutability and immutability.
Note that this section only applies to Spring Data modules that do not
use the object mapping of the underlying data store (like JPA). Also be
sure to consult the store-specific sections for store-specific object
mapping, like indexes, customizing column or field names or the like.

Core responsibility of the Spring Data object mapping is to create
instances of domain objects and map the store-native data structures
onto those. This means we need two fundamental steps:

1. Instance creation by using one of the constructors exposed.

2. Instance population to materialize all exposed properties.

### <a id="object-creation"></a> Object Creation

Spring Data automatically tries to detect a persistent entity's
constructor to be used to materialize objects of that type. The
resolution algorithm works as follows:

1. If there is a single static factory method annotated with `@PersistenceCreator` then it is used.

2. If there is a single constructor, it is used.

3. If there are multiple constructors and exactly one is annotated with `@PersistenceCreator`, it is used.

4. If there's a no-argument constructor, it is used. Other constructors will be ignored.

The value resolution assumes constructor/factory method argument names
to match the property names of the entity, i.e. the resolution will be
performed as if the property was to be populated, including all
customizations in mapping (different datastore column or field name
etc.). This also requires either parameter names information available
in the class file or an `@ConstructorProperties` annotation being
present on the constructor.

The value resolution can be customized by using Spring Framework's
`@Value` value annotation using a store-specific SpEL expression. Please
consult the section on store specific mappings for further details.


#### Object Creation Internals

To avoid the overhead of reflection, Spring Data object creation uses a
factory class generated at runtime by default, which will call the
domain classes constructor directly. For example, for this example type:

```highlight
class Person {
  Person(String firstname, String lastname) { … }
}
```

We will create a factory class semantically equivalent to this one at
runtime:

```highlight
class PersonObjectInstantiator implements ObjectInstantiator {

  Object newInstance(Object... args) {
    return new Person((String) args[0], (String) args[1]);
  }
}
```

This gives us an approximately 10% performance boost over reflection. For
the domain class to be eligible for such optimization, it needs to
adhere to a set of constraints:

- It must not be a private class

- It must not be a non-static inner class

- It must not be a CGLib proxy class

- The constructor to be used by Spring Data must not be private

If any of these criteria match, Spring Data will fall back to entity
instantiation via reflection.

### <a id="property-population"></a>Property Population

Once an instance of the entity has been created, Spring Data populates
all remaining persistent properties of that class. Unless already
populated by the entity's constructor (i.e. consumed through its
constructor argument list), the identifier property will be populated
first to allow the resolution of cyclic object references. After that,
all non-transient properties that have not already been populated by the
constructor are set on the entity instance. For that we use the
following algorithm:

1. If the property is immutable but exposes a `with…` method (see below), we use the `with…` method to create a new entity instance with the new property value.

2. If property access (i.e. access through getters and setters) is defined, we're invoking the setter method.

3. If the property is mutable we set the field directly.

4. If the property is immutable we're using the constructor to be used by persistence operations (see [Object Creation](#object-creation)) to create a copy of the instance.

5.  By default, we set the field value directly.

#### Property Population Internals

Similarly to our [optimizations in object creation](#object-creation), we also use Spring Data
runtime generated accessor classes to interact with the entity instance.

```highlight
class Person {

  private final Long id;
  private String firstname;
  private @AccessType(Type.PROPERTY) String lastname;

  Person() {
    this.id = null;
  }

  Person(Long id, String firstname, String lastname) {
    // Field assignments
  }

  Person withId(Long id) {
    return new Person(id, this.firstname, this.lastame);
  }

  void setLastname(String lastname) {
    this.lastname = lastname;
  }
}
```

**Example 1. A generated Property Accessor**

```highlight
class PersonPropertyAccessor implements PersistentPropertyAccessor {

  private static final MethodHandle firstname;              <!--SEE COMMENT 2-->

  private Person person;                                    <!--SEE COMMENT 1-->

  public void setProperty(PersistentProperty property, Object value) {

    String name = property.getName();

    if ("firstname".equals(name)) {
      firstname.invoke(person, (String) value);             <!--SEE COMMENT 2-->
    } else if ("id".equals(name)) {
      this.person = person.withId((Long) value);            <!--SEE COMMENT 3-->
    } else if ("lastname".equals(name)) {
      this.person.setLastname((String) value);              <!--SEE COMMENT 4-->
    }
  }
}
```

**Comments**:

1. PropertyAccessor's hold a mutable instance of the underlying object. This is to enable mutations of otherwise immutable properties.

2. By default, Spring Data uses field-access to read and write property values. As per visibility rules of `private` fields, `MethodHandles` are used to interact with fields.

3. The class exposes a `withId(…)` method that is used to set the identifier, e.g. when an instance is inserted into the datastore and an identifier has been generated. Calling `withId(…)` creates a new `Person` object. All subsequent mutations will take place in the new instance leaving the previous untouched.

4. Using property-access allows direct method invocations without using `MethodHandles`.

This gives us an approximately 25% performance boost over reflection. For
the domain class to be eligible for such optimization, it needs to
adhere to a set of constraints:

- Types must not reside in the default or under the `java` package.

- Types and their constructors must be `public`

- Types that are inner classes must be `static`.

- The used Java Runtime must allow for declaring classes in the originating `ClassLoader`. Java 9 and newer impose certain limitations.

By default, Spring Data attempts to use generated property accessors and
falls back to reflection-based ones if a limitation is detected.

Let's have a look at the following entity:

**Example 2. A sample entity**

```highlight
class Person {

  private final @Id Long id;                                                <!--SEE COMMENT 1-->
  private final String firstname, lastname;                                 <!--SEE COMMENT 2-->
  private final LocalDate birthday;
  private final int age;                                                    <!--SEE COMMENT 3-->

  private String comment;                                                   <!--SEE COMMENT 4-->
  private @AccessType(Type.PROPERTY) String remarks;                        <!--SEE COMMENT 5-->

  static Person of(String firstname, String lastname, LocalDate birthday) { <!--SEE COMMENT 6-->

    return new Person(null, firstname, lastname, birthday,
      Period.between(birthday, LocalDate.now()).getYears());
  }

  Person(Long id, String firstname, String lastname, LocalDate birthday, int age) { <!--SEE COMMENT 6-->

    this.id = id;
    this.firstname = firstname;
    this.lastname = lastname;
    this.birthday = birthday;
    this.age = age;
  }

  Person withId(Long id) {                                                  <!--SEE COMMENT 1-->
    return new Person(id, this.firstname, this.lastname, this.birthday, this.age);
  }

  void setRemarks(String remarks) {                                         <!--SEE COMMENT 5-->
    this.remarks = remarks;
  }
}
```

**Comments:**

1. The identifier property is final but set to `null` in the constructor. The class exposes a `withId(…)` method that's used to set the identifier, e.g. when an instance is inserted into the datastore and an identifier has been generated. The original `Person` instance stays unchanged as a new one is created. The same pattern is usually applied for other properties that are store managed but might have to be changed for persistence operations. The wither method is optional as the persistence constructor (see 6) is effectively a copy constructor and setting the property will be translated into creating a fresh instance with the new identifier value applied.

2. The `firstname` and `lastname` properties are ordinary immutable properties potentially exposed through getters.

3. The `age` property is an immutable but derived one from the `birthday` property. With the design shown, the database value will trump the defaulting as Spring Data uses the only declared constructor. Even if the intent is that the calculation should be preferred, it's important that this constructor also takes `age` as parameter (to potentially ignore it) as otherwise the property population step will attempt to set the age field and fail due to it being immutable and no `with…` method being present.

4. The `comment` property is mutable is populated by setting its field directly.

5. The `remarks` properties are mutable and populated by setting the `comment` field directly or by invoking the setter method for

6.  The class exposes a factory method and a constructor for object creation. The core idea here is to use factory methods instead of additional constructors to avoid the need for constructor disambiguation through `@PersistenceCreator`. Instead, defaulting of properties is handled within the factory method. If you want Spring Data to use the factory method for object instantiation, annotate it with `@PersistenceCreator`.

### <a id="general-recommendations"></a>General Recommendations

- **Use Immutable Objects**: Immutable objects are direct straightforward to create as materializing an object is then a matter of calling its constructor only. Also, this avoids your domain objects to be littered with setter methods that allow client code to manipulate the objects state. If you need those, prefer to make them package protected so that they can only be invoked by a limited amount of co-located types. Constructor-only materialization is up to 30% faster than properties population.

- **Provide an All-arguments Constructor**: Even if you cannot or do not want to model your entities as immutable values, there's still value in providing a constructor that takes all properties of the entity as arguments, including the mutable ones, as this allows the object mapping to skip the property population for optimal performance.

- **Use Factory Methods Instead of Overloaded Constructors to Avoid `@PersistenceCreator`**: With an all-argument constructor needed for optimal performance, we usually want to expose more application use case specific constructors that omit things like auto-generated identifiers etc. It's an established pattern to rather use static factory methods to expose these variants of the all-arguments constructor.

- **Adhere to the Constraints that Allow the Generated instantiator and property accessor classes to be used**

- **For identifiers to be generated, still use a final field in combination with an all-arguments persistence constructor (preferred) or a `with…` method**

- **Use Lombok to Avoid Boilerplate Code**: As persistence operations usually require a constructor taking all arguments, their declaration becomes a tedious repetition of boilerplate parameter to field assignments that can best be avoided by using Lombok's `@AllArgsConstructor`.

#### Overriding Properties

Java allows a flexible design of domain classes where a subclass could
define a property that is already declared with the same name in its
superclass. Consider the following example:

```highlight
public class SuperType {

   private CharSequence field;

   public SuperType(CharSequence field) {
      this.field = field;
   }

   public CharSequence getField() {
      return this.field;
   }

   public void setField(CharSequence field) {
      this.field = field;
   }
}

public class SubType extends SuperType {

   private String field;

   public SubType(String field) {
      super(field);
      this.field = field;
   }

   @Override
   public String getField() {
      return this.field;
   }

   public void setField(String field) {
      this.field = field;

      // optional
      super.setField(field);
   }
}
```

Both classes define a `field` using assignable types. `SubType` however
shadows `SuperType.field`. Depending on the class design, using the
constructor could be the only default approach to set `SuperType.field`.
Alternatively, calling `super.setField(…)` in the setter could set the
`field` in `SuperType`. All these mechanisms create conflicts to some
degree because the properties share the same name yet might represent
two distinct values. Spring Data skips super-type properties if types
are not assignable. That is, the type of the overridden property must be
assignable to its super-type property type to be registered as override,
otherwise the super-type property is considered transient. We generally
recommend using distinct property names.

Spring Data modules generally support overridden properties holding
different values. From a programming model perspective, there are a few
things to consider:

1. Which property should be persisted (default to all declared properties)? You can exclude properties by annotating these with `@Transient`.

2. How to represent properties in your data store? Using the same field/column name for different values typically leads to corrupt data so you should annotate least one of the properties using an explicit field/column name.

3. Using `@AccessType(PROPERTY)` cannot be used as the super-property cannot be generally set without making any further assumptions of the setter implementation.

### <a id="kotlin-support"></a>Kotlin support

Spring Data adapts specifics of Kotlin to allow object creation and
mutation.

#### Kotlin object creation

Kotlin classes are supported to be instantiated , all classes are
immutable by default and require explicit property declarations to
define mutable properties. Consider the following `data` class `Person`:

```highlight
data class Person(val id: String, val name: String)
```

The class above compiles to a typical class with an explicit
constructor.We can customize this class by adding another constructor
and annotate it with `@PersistenceCreator` to indicate a constructor
preference:


```highlight
data class Person(var id: String, val name: String) {

    @PersistenceCreator
    constructor(id: String) : this(id, "unknown")
}
```

Kotlin supports parameter optionality by allowing default values to be
used if a parameter is not provided. When Spring Data detects a
constructor with parameter defaulting, then it leaves these parameters
absent if the data store does not provide a value (or simply returns
`null`) so Kotlin can apply parameter defaulting.Consider the following
class that applies parameter defaulting for `name`

```highlight
data class Person(var id: String, val name: String = "unknown")
```

Every time the `name` parameter is either not part of the result or its
value is `null`, then the `name` defaults to `unknown`.

#### Property population of Kotlin data classes

In Kotlin, all classes are immutable by default and require explicit
property declarations to define mutable properties. Consider the
following `data` class `Person`:

```highlight
data class Person(val id: String, val name: String)
```

This class is effectively immutable. It allows creating new instances as
Kotlin generates a `copy(…)` method that creates new object instances
copying all property values from the existing object and applying
property values provided as arguments to the method.

#### Kotlin Overriding Properties

Kotlin allows declaring [property
overrides](https://kotlinlang.org/docs/inheritance.html#overriding-properties)
to alter properties in subclasses.

```highlight
open class SuperType(open var field: Int)

class SubType(override var field: Int = 1) :
    SuperType(field) {
}
```

Such an arrangement renders two properties with the name `field`. Kotlin
generates property accessors (getters and setters) for each property in
each class. Effectively, the code looks like as follows:

```highlight
public class SuperType {

   private int field;

   public SuperType(int field) {
      this.field = field;
   }

   public int getField() {
      return this.field;
   }

   public void setField(int field) {
      this.field = field;
   }
}

public final class SubType extends SuperType {

   private int field;

   public SubType(int field) {
      super(field);
      this.field = field;
   }

   public int getField() {
      return this.field;
   }

   public void setField(int field) {
      this.field = field;
   }
}
```

Getters and setters on `SubType` set only `SubType.field` and not
`SuperType.field`. In such an arrangement, using the constructor is the
only default approach to set `SuperType.field`. Adding a method to
`SubType` to set `SuperType.field` via `this.SuperType.field = …` is
possible but falls outside of supported conventions. Property overrides
create conflicts to some degree because the properties share the same
name yet might represent two distinct values. We generally recommend
using distinct property names.

Spring Data modules generally support overridden properties holding
different values. From a programming model perspective there are a few
things to consider:

1. Which property should be persisted (default to all declared properties)? You can exclude properties by annotating these with `@Transient`.

2. How to represent properties in your data store? Using the same field/column name for different values typically leads to corrupt data so you should annotate least one of the properties using an explicit field/column name.

3. Using `@AccessType(PROPERTY)` cannot be used as the super-property cannot be set.

## <a id="entity-mapping"></a>Entity Mapping

Spring Data for VMware GemFire provides support to map entities that are
stored in a Region. The mapping metadata is defined by using annotations
on application domain classes, as the following example shows:

**Example 3. Mapping a domain class to a VMware GemFire Region**

```highlight
@Region("People")
public class Person {

  @Id Long id;

  String firstname;
  String lastname;

  @PersistenceConstructor
  public Person(String firstname, String lastname) {
    // …
  }

  …
}
```

The `@Region` annotation can be used to customize the Region in which an
instance of the `Person` class is stored. The `@Id` annotation can be
used to annotate the property that should be used as the cache Region
key, identifying the Region entry. The `@PersistenceConstructor`
annotation helps to disambiguate multiple potentially available
constructors, taking parameters and explicitly marking the constructor
annotated as the constructor to be used to construct entities. In an
application domain class with no or only a single constructor, you can
omit the annotation.

In addition to storing entities in top-level Regions, entities can be
stored in Sub-Regions as well, as the following example shows:

```highlight
@Region("/Users/Admin")
public class Admin extends User {
  …
}

@Region("/Users/Guest")
public class Guest extends User {
  …
}
```
Be sure to use the full path of the GemFire Region, as defined with
the Spring Data for VMware GemFire XML namespace by using the `id` or
`name` attributes of the `<*-region>` element.

### <a id="entity-mapping-by-region-type"></a>Entity Mapping by Region Type

In addition to the `@Region` annotation, Spring Data for VMware GemFire
also recognizes type-specific Region mapping annotations:
`@ClientRegion`, `@LocalRegion`, `@PartitionRegion`, and
`@ReplicateRegion`.

Functionally, these annotations are treated exactly the same as the
generic `@Region` annotation in the Spring Data for VMware GemFire mapping infrastructure. However,
these additional mapping annotations are useful in Spring Data for
VMware GemFire's annotation configuration model. When combined with the
`@EnableEntityDefinedRegions` configuration annotation on a Spring
`@Configuration` annotated class, it is possible to generate Regions in
the local cache, whether the application is a client or peer.

These annotations let you be more specific about what type of Region
your application entity class should be mapped to and also has an impact
on the data management policies of the Region (for example,
partition — also known as sharding — versus replicating data).

Using these type-specific Region mapping annotations with the Spring Data for VMware GemFire
annotation configuration model saves you from having to explicitly
define these Regions in configuration.

## <a id="repository-mapping"></a>Repository Mapping

As an alternative to specifying the Region in which the entity is stored
by using the `@Region` annotation on the entity class, you can also
specify the `@Region` annotation on the entity's `Repository` interface.
See [Spring Data for VMware GemFire Repositories](#gemfire-repositories)
for more details.

However, suppose you want to store a `Person` record in multiple Apache
GemFire Regions (for example, `People` and `Customers`). Then you can
define your corresponding `Repository` interface extensions as follows:

```highlight
@Region("People")
public interface PersonRepository extends GemfireRepository<Person, String> {
…
}

@Region("Customers")
public interface CustomerRepository extends GemfireRepository<Person, String> {
...
}
```

Then, using each Repository individually, you can store the entity in
multiple VMware GemFire Regions, as the following example shows:

```highlight
@Service
class CustomerService {

  CustomerRepository customerRepo;

  PersonRepository personRepo;

  Customer update(Customer customer) {
    customerRepo.save(customer);
    personRepo.save(customer);
    return customer;
  }
```

You can even wrap the `update` service method in a Spring managed
transaction, either as a local cache transaction or a global
transaction.

### <a id="mappingpdxserializer"></a>MappingPdxSerializer


Spring Data for VMware GemFire provides a custom
[PdxSerializer](https://gemfire.docs.pivotal.io/apidocs/gf-100/org/apache/geode/pdx/PdxSerializer.html)
implementation, called `MappingPdxSerializer`, that uses Spring Data
mapping metadata to customize entity serialization.

The serializer also lets you customize entity instantiation by using the
Spring Data `EntityInstantiator` abstraction. By default, the serializer
use the `ReflectionEntityInstantiator`, which uses the persistence
constructor of the mapped entity. The persistence constructor is either
the default constructor, a singly declared constructor, or a constructor
explicitly annotated with `@PersistenceConstructor`.

To provide arguments for constructor parameters, the serializer reads
fields with the named constructor parameter, explicitly identified by
using Spring's `@Value` annotation, from the supplied
[PdxReader](https://gemfire.docs.pivotal.io/apidocs/gf-100/org/apache/geode/pdx/PdxReader.html),
as shown in the following example:


**Example 4. Using `@Value` on entity constructor parameters**

```highlight
public class Person {

  public Person(@Value("#root.thing") String firstName, @Value("bean") String lastName) {
    …
  }
}
```

An entity class annotated in this way has the "thing" field read from
the `PdxReader` and passed as the argument value for the constructor
parameter, `firstname`. The value for `lastName` is a Spring bean with
the name "bean".

In addition to the custom instantiation logic and strategy provided by
`EntityInstantiators`, the `MappingPdxSerializer` also provides
capabilities well beyond [ReflectionBasedAutoSerializer](https://gemfire.docs.pivotal.io/apidocs/gf-100/org/apache/geode/pdx/ReflectionBasedAutoSerializer.html).

While VMware GemFire's `ReflectionBasedAutoSerializer` conveniently uses
Java Reflection to populate entities and uses regular expressions to
identify types that should be handled (serialized and deserialized) by
the serializer, it cannot, unlike `MappingPdxSerializer`, perform the
following:

- Register custom `PdxSerializer` objects per entity field or property names and types.

- Conveniently identifies ID properties.

- Automatically handles read-only properties.

- Automatically handles transient properties.

- Allows more robust type filtering in a `null` and type-safe manner (for example, not limited to only expressing types with regex).

We now explore each feature of the `MappingPdxSerializer` in a bit more
detail.

### <a id="custom-pdxserializer-registration"></a>Custom PdxSerializer Registration

The `MappingPdxSerializer` gives you the ability to register custom
`PdxSerializers` based on an entity's field or property names and types.

For example, suppose you have defined an entity type modeling a `User`
as follows:

```highlight
package example.app.security.auth.model;

public class User {

  private String name;

  private Password password;

  ...
}
```

While the user's name probably does not require any special logic to
serialize the value, serializing the password on the other hand might
require additional logic to handle the sensitive nature of the field or
property.

Perhaps you want to protect the password when sending the value over the
network, between a client and a server, beyond TLS alone, and you only
want to store the salted hash. When using the `MappingPdxSerializer`,
you can register a custom `PdxSerializer` to handle the user's password,
as follows:

**Example 5. Registering custom `PdxSerializers` by POJO field/property type**

```highlight
Map<?, PdxSerializer> customPdxSerializers = new HashMap<>();

customPdxSerializers.put(Password.class, new SaltedHashPasswordPdxSerializer());

mappingPdxSerializer.setCustomPdxSerializers(customPdxSerializers);
```

After registering the application-defined
`SaltedHashPasswordPdxSerializer` instance with the `Password`
application domain model type, the `MappingPdxSerializer` will then
consult the custom `PdxSerializer` to serialize and deserialize all
`Password` objects regardless of the containing object (for example,
`User`).

However, suppose you want to customize the serialization of `Passwords`
only on `User` objects. To do so, you can register the custom
`PdxSerializer` for the `User` type by specifying the fully qualified
name of the `Class's` field or property, as the following example shows:

**Example 6. Registering custom `PdxSerializers` by POJO field/property name**

```highlight
Map<?, PdxSerializer> customPdxSerializers = new HashMap<>();

customPdxSerializers.put("example.app.security.auth.model.User.password", new SaltedHashPasswordPdxSerializer());

mappingPdxSerializer.setCustomPdxSerializers(customPdxSerializers);
```

Notice the use of the fully-qualified field or property name (that is
`example.app.security.auth.model.User.password`) as the custom
`PdxSerializer` registration key.

<p class="note"><strong>Note</strong>: You could construct the registration key by using a
more logical code snippet, such as the following:
<code>User.class.getName().concat(".password");</code>. We recommended
this over the example shown earlier. The preceding example is as explicit as possible about the semantics of registration.</p>

### <a id="mapping-id-properties"></a>Mapping ID Properties

Like GemFire's `ReflectionBasedAutoSerializer`, Spring Data for VMware GemFire's
`MappingPdxSerializer` is also able to determine the identifier of the
entity. However, `MappingPdxSerializer` does so by using Spring Data's
mapping metadata, specifically by finding the entity property designated
as the identifier using Spring Data's
[@Id](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/annotation/Id.html)
annotation. Alternatively, any field or property named "id" not
explicitly annotated with `@Id` is also designated as the entity's
identifier.

For example:

```highlight
class Customer {

  @Id
  Long id;

  ...
}
```

In this case, the `Customer` `id` field is marked as the identifier
field in the PDX type metadata by using
[PdxWriter.markIdentifierField(:String)](https://gemfire.docs.pivotal.io/apidocs/gf-100/org/apache/geode/pdx/PdxWriter.html#markIdentityField-java.lang.String-)
when the `PdxSerializer.toData(..)` method is called during
serialization.

### <a id="mapping-read-only-properties"></a>Mapping Read-Only Properties

What happens when your entity defines a read-only property?

First, it is important to understand what a "read-only" property is. If
you define a POJO by following the
[JavaBeans](https://www.oracle.com/technetwork/java/javase/documentation/spec-136004.html)
specification (as Spring does), you might define a POJO with a read-only
property, as follows:

```highlight
package example;

class ApplicationDomainType {

  private AnotherType readOnly;

  public AnotherType getReadOnly() [
    this.readOnly;
  }

  ...
}
```

The `readOnly` property is read-only because it does not provide a
setter method. It only has a getter method. In this case, the `readOnly`
property (not to be confused with the `readOnly` `DomainType` field) is
considered read-only.

As a result, the `MappingPdxSerializer` will not try to set a value for
this property when populating an instance of `ApplicationDomainType` in
the `PdxSerializer.fromData(:Class<ApplicationDomainType>, :PdxReader)`
method during deserialization, particularly if a value is present in the
PDX serialized bytes.

This is useful in situations where you might be returning a view or
projection of some entity type and you only want to set state that is
writable. Perhaps the view or projection of the entity is based on
authorization or some other criteria. The point is, you can leverage
this feature as is appropriate for your application's use cases and
requirements. If you want the field or property to always be written,
define a setter method.

### <a id="mapping-transient-properties"></a>Mapping Transient Properties

What happens when your entity defines `transient` properties?

You would expect the `transient` fields or properties of your entity not
to be serialized to PDX when serializing the entity. That is exactly
what happens, unlike VMware GemFire's own `ReflectionBasedAutoSerializer`,
which serializes everything accessible from the object through Java
Reflection.

The `MappingPdxSerializer` will not serialize any fields or properties
that are qualified as being transient, either by using Java's own
`transient` keyword (in the case of class instance fields) or by using
the
[@Transient](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/annotation/Transient.html)
Spring Data annotation on either fields or properties.

For example, you might define an entity with transient fields and
properties as follows:

```highlight
package example;

class Process {

  private transient int id;

  private File workingDirectory;

  private String name;

  private Type type;

  @Transient
  public String getHostname() {
    ...
  }

  ...
}
```

Neither the `Process` `id` field nor the readable `hostname` property
are written to PDX.

### <a id="filtering-by-class-type"></a>

Similar to `ReflectionBasedAutoSerializer`, Spring Data for VMware GemFire's
`MappingPdxSerializer` lets you filter the types of objects that are
serialized and deserialized.

However, unlike `ReflectionBasedAutoSerializer`, which
uses complex regular expressions to express which types the serializer
handles, Spring Data for VMware GemFire's `MappingPdxSerializer` uses the much more robust
[java.util.function.Predicate](https://docs.oracle.com/javase/8/docs/api/java/util/function/Predicate.html) interface and API to express type-matching criteria.

You can implement a <code>Predicate</code> using Java's <a
href="https://docs.oracle.com/javase/8/docs/api/java/util/regex/package-summary.html">regular
expression support</a>

Using Java's `Predicate` interface, you can compose
`Predicates` by using convenient and appropriate API methods, including:

* [and(:Predicate)](https://docs.oracle.com/javase/8/docs/api/java/util/function/Predicate.html#and-java.util.function.Predicate-)

* [or(:Predicate)](https://docs.oracle.com/javase/8/docs/api/java/util/function/Predicate.html#or-java.util.function.Predicate-)

* [negate()](https://docs.oracle.com/javase/8/docs/api/java/util/function/Predicate.html#negate--)

The following example shows the `Predicate` API in action:

```highlight
Predicate<Class?>> customerTypes =
  type -> Customer.class.getPackage().getName().startsWith(type.getName()); // Include all types in the same package as `Customer`

Predicate includedTypes = customerTypes
  .or(type -> User.class.isAssignble(type)); // Additionally, include User sub-types (e.g. Admin, Guest, etc)

mappingPdxSerializer.setIncludeTypeFilters(includedTypes);

mappingPdxSerializer.setExcludeTypeFilters(
  type -> !Reference.class.getPackage(type.getPackage()); // Exclude Reference types
```

<p class="note"><strong>Note</strong>: Any <code>Class</code> object passed to your
<code>Predicate</code> is guaranteed not to be <code>null</code>.</p>

Spring Data for VMware GemFire's `MappingPdxSerializer` includes support for both include and
exclude class type filters.

#### Exclude Type Filtering

By default, Spring Data for VMware GemFire's `MappingPdxSerializer` registers pre-defined
`Predicates` that filter, or exclude types from the following packages:

- `java.*`

- `com.gemstone.gemfire.*`

- `org.apache.geode.*`

- `org.springframework.*`

Additionally, the `MappingPdxSerializer` filters `null` objects when
calling `PdxSerializer.toData(:Object, :PdxWriter)` and `null` class
types when calling `PdxSerializer.fromData(:Class≤?>, :PdxReader)`
methods.

You can add exclusions for other class types, or an entire
package of types, by defining a `Predicate` and adding it to the
`MappingPdxSerializer` as shown earlier.

The `MappingPdxSerializer.setExcludeTypeFilters(:Predicate<Class≤?>>>)`
method is additive, meaning it composes your application-defined type
filters with the existing, pre-defined type filter `Predicates`
indicated above using the `Predicate.and(:Predicate<Class≤?>>)` method.

#### Include Type Filtering

To include a class type explicitly, or override a class type
filter that implicitly excludes a class type required by your
application (for example, `java.security.Principal`, which is excluded
by default with the `java.*` package exclude type filter on
`MappingPdxSerializer`), then define the appropriate `Predicate`
and add it to the serializer using
`MappingPdxSerializer.setIncludeTypeFilters(:Predicate<Class≤?>>)`
method, as follows:




```highlight
Predicate<Class≤?>> principalTypeFilter =
  type -> java.security.Principal.class.isAssignableFrom(type);

mappingPdxSerializer.setIncludeTypeFilters(principalTypeFilters);
```

The `MappingPdxSerializer.setIncludeTypeFilters(:Predicate<Class≤?>>)`
method, like `setExcludeTypeFilters(:Predicate<Class≤?>>)`, is additive
and therefore composes any passed type filter using
`Predicate.or(:Predicate<Class≤?>>)`. You can call
`setIncludeTypeFilters(:Predicate<Class≤?>>)` as many time as necessary.

When include type filters are present, then the `MappingPdxSerializer`
makes a decision of whether to de/serialize an instance of a class type
when the class type is either not implicitly excluded OR when the class
type is explicitly included, whichever returns true. Then, an instance
of the class type will be serialized or deserialized appropriately.

For example, when a type filter of `Predicate<Class<Principal>>` is
explicitly registered as shown previously, it cancels out the implicit
exclude type filter on `java.*` package types.

