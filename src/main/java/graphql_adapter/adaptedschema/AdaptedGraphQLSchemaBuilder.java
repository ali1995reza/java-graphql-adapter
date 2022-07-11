/*
 * Copyright 2022 Alireza Akhoundi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package graphql_adapter.adaptedschema;

import graphql.schema.*;
import graphql_adapter.adaptedschema.assertion.Assert;
import graphql_adapter.adaptedschema.builtinscalars.BuiltInScalarEntries;
import graphql_adapter.adaptedschema.discovered.DiscoveredElement;
import graphql_adapter.adaptedschema.discovered.DiscoveredInterfaceType;
import graphql_adapter.adaptedschema.discovered.DiscoveredObjectType;
import graphql_adapter.adaptedschema.exceptions.MappingGraphqlTypeException;
import graphql_adapter.adaptedschema.exceptions.MultipleGraphqlTopLevelTypeException;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElement;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import graphql_adapter.adaptedschema.mapping.mapped_elements.annotation.MappedAnnotation;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedClass;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedInputTypeClass;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedObjectTypeClass;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedScalarClass;
import graphql_adapter.adaptedschema.mapping.mapped_elements.enums.MappedEnum;
import graphql_adapter.adaptedschema.mapping.mapped_elements.interfaces.MappedInterface;
import graphql_adapter.adaptedschema.mapping.mapped_elements.interfaces.MappedUnionInterface;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedFieldMethod;
import graphql_adapter.adaptedschema.mapping.mapper.ClassMapper;
import graphql_adapter.adaptedschema.scalar.ScalarEntry;
import graphql_adapter.adaptedschema.utils.Reference;
import graphql_adapter.adaptedschema.utils.class_resolver.ClassResolver;
import graphql_adapter.adaptedschema.utils.class_resolver.filter.ClassFilter;
import graphql_adapter.codegenerator.DataFetcherGenerator;
import graphql_adapter.codegenerator.ObjectConstructor;
import graphql_adapter.codegenerator.TypeResolverGenerator;
import graphql_adapter.codegenerator.impl.ReflectionDataFetcherGenerator;
import graphql_adapter.codegenerator.impl.ReflectionObjectConstructor;
import graphql_adapter.codegenerator.impl.SimpleTypeResolverGenerator;

import java.util.*;
import java.util.function.Supplier;

import static graphql_adapter.adaptedschema.exceptions.SchemaExceptionBuilder.exception;
import static graphql_adapter.adaptedschema.utils.ClassUtils.cast;

public final class AdaptedGraphQLSchemaBuilder {

    public static AdaptedGraphQLSchemaBuilder newBuilder() {
        return new AdaptedGraphQLSchemaBuilder();
    }

    private final Set<Class<?>> allClasses = new HashSet<>();
    private final Map<Class<?>, ScalarEntry> scalarEntries;
    private final ClassMapper mapper;
    private DataFetcherGenerator dataFetcherGenerator;
    private TypeResolverGenerator typeResolverGenerator;
    private ObjectConstructor objectConstructor = new ReflectionObjectConstructor();
    private Boolean usePairTypesAsEachOther = true;

    private AdaptedGraphQLSchemaBuilder() {
        this.dataFetcherGenerator = new ReflectionDataFetcherGenerator();
        this.typeResolverGenerator = new SimpleTypeResolverGenerator();
        this.mapper = new ClassMapper();
        this.scalarEntries = new HashMap<>();
        this.addAllBuiltInScalars();
    }

    public synchronized AdaptedGraphQLSchemaBuilder add(Class<?> clazz, Class<?>... classes) {
        addIfNotExists(clazz);
        if (classes != null) {
            for (Class<?> otherClass : classes) {
                addIfNotExists(otherClass);
            }
        }
        return this;
    }

    public synchronized AdaptedGraphQLSchemaBuilder addAll(Collection<Class<?>> cls) {
        for (Class<?> cl : cls) {
            addIfNotExists(cl);
        }
        return this;
    }

    public synchronized AdaptedGraphQLSchemaBuilder addAllBuiltInScalars() {
        scalarEntries.putAll(BuiltInScalarEntries.entries());
        return this;
    }

    public synchronized AdaptedGraphQLSchemaBuilder addGraphqlAnnotatedClassesFormPackage(String packageName, ClassFilter filter) {
        return addAll(ClassResolver.getAllGraphqlAnnotatedClasses(packageName, filter));
    }

    public synchronized AdaptedGraphQLSchemaBuilder addGraphqlAnnotatedClassesFormPackage(String packageName) {
        return addGraphqlAnnotatedClassesFormPackage(packageName, ClassFilter.ACCEPT_ALL);
    }

    public synchronized AdaptedGraphQLSchemaBuilder addPackage(String packageName, ClassFilter filter) {
        return addAll(ClassResolver.getClasses(packageName, filter));
    }

    public synchronized AdaptedGraphQLSchemaBuilder addPackage(String packageName) {
        return addPackage(packageName, ClassFilter.ACCEPT_ALL);
    }

    public synchronized AdaptedGraphQLSchemaBuilder addScalar(ScalarEntry entry) {
        if (entry.type() == String.class || entry.type() == Boolean.class) {
            throw new IllegalStateException("can not remove String and Boolean class - these types always exists");
        }
        scalarEntries.put(entry.type(), entry);
        return this;
    }

    public synchronized AdaptedGraphQLSchema build() {

        Map<Class<?>, Map<MappedElementType, MappedElement>> mappedClasses = mapper.map(scalarEntries.values(), allClasses, Arrays.asList(StringDiscoveredType.getInstance().asMappedElement(),
                BooleanDiscoveredType.getInstance().asMappedElement()), objectConstructor, usePairTypesAsEachOther);

        addStringAndBoolean(mappedClasses);

        MappedClass query = null;
        MappedClass mutation = null;
        MappedClass subscription = null;

        for (Map<MappedElementType, MappedElement> clazz : mappedClasses.values()) {
            if (clazz.containsKey(MappedElementType.QUERY)) {
                Assert.isNull(query, exception(MultipleGraphqlTopLevelTypeException.class, "multiple QUERY type found", ((MappedClass) clazz.get(MappedElementType.QUERY)).baseClass()));
                query = (MappedClass) clazz.get(MappedElementType.QUERY);
            }

            if (clazz.containsKey(MappedElementType.MUTATION)) {
                Assert.isNull(mutation, exception(MultipleGraphqlTopLevelTypeException.class, "multiple MUTATION type found", ((MappedClass) clazz.get(MappedElementType.MUTATION)).baseClass()));
                mutation = (MappedClass) clazz.get(MappedElementType.MUTATION);
            }

            if (clazz.containsKey(MappedElementType.SUBSCRIPTION)) {
                Assert.isNull(subscription, exception(MultipleGraphqlTopLevelTypeException.class, "multiple SUBSCRIPTION type found", ((MappedClass) clazz.get(MappedElementType.SUBSCRIPTION)).baseClass()));
                subscription = (MappedClass) clazz.get(MappedElementType.SUBSCRIPTION);
            }
        }

        GraphQLSchema.Builder schema = GraphQLSchema.newSchema();

        final BuildingContextImpl context =
                new BuildingContextImpl(mappedClasses,
                        schema,
                        objectConstructor,
                        usePairTypesAsEachOther);

        discoverDirectives(mappedClasses, context);

        discoverTypesExceptUnions(mappedClasses, context);

        discoverUnionTypes(mappedClasses, context);

        Map<MappedElement, DiscoveredElement<?, ?>> discoveredElements = getDiscoveredElements(context);

        GraphQLCodeRegistry.Builder code = GraphQLCodeRegistry.newCodeRegistry();

        predictPossibleTypes(discoveredElements, code, context);

        List<DiscoveredElement<?, ?>> allElements = Collections.unmodifiableList(new ArrayList<>(discoveredElements.values()));

        makeImmutable(allElements);

        allElements.stream().filter(AdaptedGraphQLSchemaBuilder::isScalar)
                .forEach(type -> schema.additionalType((GraphQLScalarType) type.asGraphqlElement()));

        allElements.stream().filter(AdaptedGraphQLSchemaBuilder::isInputType)
                .forEach(type -> schema.additionalType((GraphQLInputObjectType) type.asGraphqlElement()));

        allElements.stream().filter(AdaptedGraphQLSchemaBuilder::isDirective)
                .forEach(
                        element -> schema.additionalDirective((GraphQLDirective) element.asGraphqlElement())
                );

        allElements.stream().filter(type -> type.asMappedElement().mappedType().isOneOf(MappedElementType.OBJECT_TYPE, MappedElementType.INTERFACE, MappedElementType.UNION, MappedElementType.ENUM))
                .forEach(type -> schema.additionalType((GraphQLType) type.asGraphqlElement()));

        dataFetcherGenerator.init(allElements);

        if (query != null)
            schema.query((GraphQLObjectType) context.rawTypeOf(query));
        if (mutation != null)
            schema.mutation((GraphQLObjectType) context.rawTypeOf(mutation));
        if (subscription != null)
            schema.subscription((GraphQLObjectType) context.rawTypeOf(subscription));

        Reference<AdaptedGraphQLSchema> schemaReference = new Reference<>();
        addDataFetchers(allElements, dataFetcherGenerator, code, schemaReference, context);

        schema.codeRegistry(code.build());

        return schemaReference.set(new AdaptedGraphQLSchema(schema.build(), allElements, objectConstructor, usePairTypesAsEachOther))
                .lock()
                .get();
    }

    public DiscoveredElement<?, ?> buildDiscoveredElement(MappedElement element, GraphQLNamedSchemaElement schemaElement) {
        if (element.mappedType().isDirective()) {
            return new DiscoveredDirectiveImpl(
                    (MappedAnnotation) element,
                    schemaElement.getName(),
                    (GraphQLDirective) schemaElement
            );
        }

        if (element == StringDiscoveredType.getInstance().asMappedElement()) {
            return StringDiscoveredType.getInstance();
        }

        if (element == BooleanDiscoveredType.getInstance().asMappedElement()) {
            return BooleanDiscoveredType.getInstance();
        }

        MappedClass cls = (MappedClass) element;
        if (cls.mappedType().isObjectType() || cls.mappedType().isTopLevelType()) {
            Assert.isOneOrMoreFalse(exception(MappingGraphqlTypeException.class, "conflict - class mapped to bad type - [MappedClass:" + cls +
                            " , GraphQLType:" + schemaElement + "]", cls.baseClass()),
                    !(schemaElement instanceof GraphQLObjectType));

            return new DiscoveredObjectTypeImpl((MappedObjectTypeClass) cls, schemaElement.getName(), cast(schemaElement));
        } else if (cls.mappedType().isInterface()) {
            Assert.isOneOrMoreFalse(exception(MappingGraphqlTypeException.class, "conflict - class mapped to bad type - [MappedClass:" + cls +
                            " , GraphQLType:" + schemaElement + "]", cls.baseClass()),
                    !(schemaElement instanceof GraphQLInterfaceType));

            return new DiscoveredInterfaceTypeImpl((MappedInterface) cls, schemaElement.getName(), cast(schemaElement));
        } else if (cls.mappedType().isInputType()) {
            Assert.isOneOrMoreFalse(exception(MappingGraphqlTypeException.class, "conflict - class mapped to bad type - [MappedClass:" + cls +
                            " , GraphQLType:" + schemaElement + "]", cls.baseClass()),
                    !(schemaElement instanceof GraphQLInputObjectType));

            return new DiscoveredInputTypeImpl((MappedInputTypeClass) cls, schemaElement.getName(), cast(schemaElement));
        } else if (cls.mappedType().isUnion()) {
            Assert.isOneOrMoreFalse(exception(MappingGraphqlTypeException.class, "conflict - class mapped to bad type - [MappedClass:" + cls +
                            " , GraphQLType:" + schemaElement + "]", cls.baseClass()),
                    !(schemaElement instanceof GraphQLUnionType));

            return new DiscoveredUnionTypeImpl((MappedUnionInterface) cls, schemaElement.getName(), cast(schemaElement));
        } else if (cls.mappedType().isEnum()) {
            Assert.isOneOrMoreFalse(exception(MappingGraphqlTypeException.class, "conflict - class mapped to bad type - [MappedClass:" + cls +
                            " , GraphQLType:" + schemaElement + "]", cls.baseClass()),
                    !(schemaElement instanceof GraphQLEnumType));

            return new DiscoveredEnumTypeImpl((MappedEnum) cls, schemaElement.getName(), cast(schemaElement));
        } else if (cls.mappedType().isScalar()) {
            Assert.isOneOrMoreFalse(exception(MappingGraphqlTypeException.class, "conflict - class mapped to bad type - [MappedClass:" + cls +
                            " , GraphQLType:" + schemaElement + "]", cls.baseClass()),
                    !(schemaElement instanceof GraphQLScalarType));

            return new DiscoveredScalarTypeImpl((MappedScalarClass) cls, schemaElement.getName(), cast(schemaElement));
        }

        throw new IllegalStateException();
    }

    public synchronized AdaptedGraphQLSchemaBuilder clearClasses() {
        allClasses.clear();
        return this;
    }

    public synchronized AdaptedGraphQLSchemaBuilder clearScalars() {
        scalarEntries.clear();
        return this;
    }

    public synchronized AdaptedGraphQLSchemaBuilder dontUsePairTypesAsEachOther() {
        return usePairTypesAsEachOther(false);
    }

    public synchronized AdaptedGraphQLSchemaBuilder generator(TypeResolverGenerator generator) {
        Assert.isNotNull(generator, new IllegalStateException("code generators can not be null"));
        typeResolverGenerator = generator;
        return this;
    }

    public synchronized AdaptedGraphQLSchemaBuilder generator(DataFetcherGenerator generator) {
        Assert.isNotNull(generator, new IllegalStateException("data-fetcher generators can not be null"));
        dataFetcherGenerator = generator;
        return this;
    }

    public ClassMapper mapper() {
        return mapper;
    }

    public synchronized AdaptedGraphQLSchemaBuilder objectConstructor(ObjectConstructor constructor) {
        Assert.isNotNull(constructor, new IllegalStateException("provided object constructor is null"));
        this.objectConstructor = constructor;
        return this;
    }

    public synchronized AdaptedGraphQLSchemaBuilder remove(Class<?> c) {
        if (c == String.class || c == Boolean.class) {
            throw new IllegalStateException("can not remove String and Boolean class - these types always exists");
        }
        allClasses.remove(c);
        return this;
    }

    public synchronized AdaptedGraphQLSchemaBuilder removeScalar(ScalarEntry entry) {
        scalarEntries.remove(entry.type(), entry);
        return this;
    }

    public synchronized AdaptedGraphQLSchemaBuilder removeScalar(Class<?> cls) {
        scalarEntries.remove(cls);
        return this;
    }

    public synchronized AdaptedGraphQLSchemaBuilder usePairTypesAsEachOther(boolean use) {
        this.usePairTypesAsEachOther = use;
        return this;
    }

    public synchronized AdaptedGraphQLSchemaBuilder usePairTypesAsEachOther() {
        return usePairTypesAsEachOther(true);
    }

    private void addDataFetchers(List<DiscoveredElement<?, ?>> discoveredElements, DataFetcherGenerator dataFetcherGenerator, GraphQLCodeRegistry.Builder code, Supplier<AdaptedGraphQLSchema> schemaSupplier, BuildingContext context) {
        DirectiveHandlingContextImpl directiveContext = (DirectiveHandlingContextImpl) context.directiveHandlingContext();
        discoveredElements.stream().filter(AdaptedGraphQLSchemaBuilder::isInterfaceOrType).forEach(d -> {
            Map<String, MappedFieldMethod> methods = d instanceof DiscoveredObjectType ?
                    ((DiscoveredObjectType) d).asMappedElement().fieldMethods() :
                    ((DiscoveredInterfaceType) d).asMappedElement().fieldMethods();

            MappedObjectTypeClass mappedClass = (MappedObjectTypeClass) d.asMappedElement();

            for (MappedFieldMethod method : methods.values()) {
                code.dataFetcher(FieldCoordinates.coordinates(
                        mappedClass.name(),
                        method.name()
                ), new DataFetcherHandler(directiveContext.applyChanges(mappedClass.name(), method.name(), dataFetcherGenerator.generate(mappedClass, method)), mappedClass, method, schemaSupplier));
            }
        });
    }

    private void addIfNotExists(Class<?> c) {
        Assert.isAllFalse(new IllegalStateException("can not add String and Boolean class - these types always exists"),
                c == String.class, c == Boolean.class);
        allClasses.add(c);
    }

    private Map<Class<?>, Map<MappedElementType, MappedElement>> addStringAndBoolean(Map<Class<?>, Map<MappedElementType, MappedElement>> all) {
        Assert.isFalse(all.containsKey(String.class), exception(MappingGraphqlTypeException.class, "there is a mapped class for String - String is a default type", String.class));
        Assert.isFalse(all.containsKey(Boolean.class), exception(MappingGraphqlTypeException.class, "there is a mapped class for Boolean - Boolean is a default type", Boolean.class));
        all.computeIfAbsent(String.class, x -> new HashMap<>()).put(MappedElementType.SCALAR, StringDiscoveredType.getInstance().asMappedElement());
        all.computeIfAbsent(Boolean.class, x -> new HashMap<>()).put(MappedElementType.SCALAR, BooleanDiscoveredType.getInstance().asMappedElement());
        return all;
    }

    private GraphQLNamedSchemaElement discover(MappedElement mappedElement, BuildingContextImpl context) {

        if (mappedElement == StringDiscoveredType.getInstance().asMappedElement()) {
            context.setGraphQLTypeFor(StringDiscoveredType.getInstance().asMappedElement(),
                    StringDiscoveredType.getInstance().asGraphqlElement());
            return StringDiscoveredType.getInstance().asGraphqlElement();
        }

        if (mappedElement == BooleanDiscoveredType.getInstance().asMappedElement()) {
            context.setGraphQLTypeFor(BooleanDiscoveredType.getInstance().asMappedElement(),
                    BooleanDiscoveredType.getInstance().asGraphqlElement());
            return BooleanDiscoveredType.getInstance().asGraphqlElement();
        }

        if (mappedElement.mappedType().isDirective()) {
            GraphQLDirective type = GraphQLElementBuilder.buildDirective((MappedAnnotation) mappedElement, context);
            context.setDirective((MappedAnnotation) mappedElement, type);
            return type;
        }

        GraphQLNamedType type = null;
        MappedClass mappedClass = (MappedClass) mappedElement;

        if (mappedClass.mappedType().isTopLevelType() || mappedClass.mappedType().isObjectType()) {
            type = GraphQLElementBuilder.buildOutputObjectType((MappedObjectTypeClass) mappedClass, context);
            context.setGraphQLTypeFor(mappedClass, type);
        } else if (mappedClass.mappedType().isInterface()) {
            type = GraphQLElementBuilder.buildInterface((MappedInterface) mappedClass, context);
            context.setGraphQLTypeFor(mappedClass, type);
        } else if (mappedClass.mappedType().isInputType()) {
            type = GraphQLElementBuilder.buildInputObjectType((MappedInputTypeClass) mappedClass, context);
            context.setGraphQLTypeFor(mappedClass, type);
        } else if (mappedClass.mappedType().isEnum()) {
            type = GraphQLElementBuilder.buildEnumType((MappedEnum) mappedClass, context);
            context.setGraphQLTypeFor(mappedClass, type);
        } else if (mappedClass.mappedType().isScalar()) {
            type = GraphQLElementBuilder.buildScalarType((MappedScalarClass) mappedClass, context);
            context.setGraphQLTypeFor(mappedClass, type);
        }

        return type;
    }

    private void discoverDirectives(Map<Class<?>, Map<MappedElementType, MappedElement>> mappedClasses, BuildingContextImpl context) {
        mappedClasses.values()
                .forEach(map -> map.values()
                        .stream()
                        .filter(element -> element.mappedType().isDirective())
                        .forEach(mappedClass -> discover(mappedClass, context)));
    }

    private void discoverTypesExceptUnions(Map<Class<?>, Map<MappedElementType, MappedElement>> mappedClasses, BuildingContextImpl context) {
        mappedClasses.values()
                .forEach(map -> map.values()
                        .stream()
                        .filter(element -> !element.mappedType().isOneOf(MappedElementType.UNION, MappedElementType.DIRECTIVE))
                        .forEach(mappedClass -> discover(mappedClass, context)));
    }

    private GraphQLType discoverUnion(MappedUnionInterface unionClass, List<MappedClass> possibles, BuildingContextImpl context) {
        GraphQLUnionType unionType =
                GraphQLElementBuilder.buildUnionType(unionClass, possibles, context);

        context.setGraphQLTypeFor(unionClass, unionType);

        return unionType;
    }

    private void discoverUnionTypes(Map<Class<?>, Map<MappedElementType, MappedElement>> mappedClasses, BuildingContextImpl context) {
        mappedClasses.values()
                .forEach(map -> map.values()
                        .stream()
                        .filter(element -> element.mappedType().isUnion())
                        .forEach(mappedClass -> discoverUnion((MappedUnionInterface) mappedClass,
                                context.possibleTypesOf((MappedClass) mappedClass), context)));
    }

    private Map<MappedElement, DiscoveredElement<?, ?>> getDiscoveredElements(BuildingContextImpl context) {
        Map<MappedElement, DiscoveredElement<?, ?>> discoveredElements = new HashMap<>();

        context.rawTypes().keySet()
                .forEach(mappedClass -> {
                    GraphQLNamedType type = context.rawTypeOf(mappedClass);
                    discoveredElements.put(mappedClass, buildDiscoveredElement(mappedClass, type));
                });

        context.getDirectives()
                .forEach((key, value) -> discoveredElements.put(key, buildDiscoveredElement(key, value)));

        return discoveredElements;
    }

    private static boolean isDirective(DiscoveredElement<?, ?> element) {
        return element.asMappedElement().mappedType().isDirective();
    }

    private static boolean isInputType(DiscoveredElement<?, ?> element) {
        return element.asMappedElement().mappedType().isInputType();
    }

    private static boolean isInterfaceOrType(DiscoveredElement<?, ?> element) {
        return element.asMappedElement().mappedType().isTopLevelType() || element.asMappedElement()
                .mappedType().isOneOf(MappedElementType.INTERFACE, MappedElementType.OBJECT_TYPE);
    }

    private static boolean isScalar(DiscoveredElement<?, ?> element) {
        return element.asMappedElement().mappedType().isScalar();
    }

    private void makeImmutable(List<DiscoveredElement<?, ?>> elements) {
        elements.stream().filter(element -> element instanceof DiscoveredTypeImpl)
                .map(element -> (DiscoveredTypeImpl<?, ?>) element)
                .forEach(DiscoveredTypeImpl::setImmutable);
    }

    private void predictPossibleTypes(Map<MappedElement, DiscoveredElement<?, ?>> discoveredElements, GraphQLCodeRegistry.Builder code, BuildingContextImpl context) {
        DirectiveHandlingContextImpl directiveContext = (DirectiveHandlingContextImpl) context.directiveHandlingContext();
        discoveredElements.values().forEach(discoveredType -> {

            if (discoveredType.asMappedElement().mappedType().isInterface()) {
                DiscoveredInterfaceTypeImpl interfaceType = (DiscoveredInterfaceTypeImpl) discoveredType;

                List<MappedClass> implementors = context.possibleTypesOf((MappedClass) discoveredType.asMappedElement());

                if (implementors != null) {
                    implementors
                            .forEach(mappedClass -> {
                                DiscoveredObjectTypeImpl objectType = (DiscoveredObjectTypeImpl) discoveredElements.get(mappedClass);
                                interfaceType.addImplementor(objectType);
                                objectType.addImplementedInterface(interfaceType);
                            });
                }
                code.typeResolver(interfaceType.name(),
                        directiveContext.applyChanges(interfaceType.name(), typeResolverGenerator.generate(interfaceType)));
            }

            if (discoveredType.asMappedElement().mappedType().isUnion()) {
                DiscoveredUnionTypeImpl unionType = (DiscoveredUnionTypeImpl) discoveredType;

                List<MappedClass> possiableTypes = context.possibleTypesOf((MappedClass) discoveredType.asMappedElement());

                if (possiableTypes != null) {
                    possiableTypes
                            .forEach(mappedClass -> {
                                DiscoveredObjectTypeImpl objectType = (DiscoveredObjectTypeImpl) discoveredElements.get(mappedClass);
                                unionType.addPossibleType(objectType);
                                objectType.addPossibleUnionType(unionType);
                            });
                }
                code.typeResolver(unionType.name(),
                        directiveContext.applyChanges(unionType.name(), typeResolverGenerator.generate(unionType)));
            }
        });
    }
}
