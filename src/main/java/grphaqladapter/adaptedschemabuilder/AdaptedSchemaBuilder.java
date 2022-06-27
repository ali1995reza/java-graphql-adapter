package grphaqladapter.adaptedschemabuilder;

import graphql.introspection.Introspection;
import graphql.schema.*;
import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredElement;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredInterfaceType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredObjectType;
import grphaqladapter.adaptedschemabuilder.exceptions.MappingGraphqlTypeException;
import grphaqladapter.adaptedschemabuilder.exceptions.MultipleGraphqlTopLevelTypeException;
import grphaqladapter.adaptedschemabuilder.mapped.*;
import grphaqladapter.adaptedschemabuilder.mapper.ClassMapper;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.DirectiveArgumentsValue;
import grphaqladapter.adaptedschemabuilder.scalar.ScalarEntry;
import grphaqladapter.adaptedschemabuilder.utils.Reference;
import grphaqladapter.adaptedschemabuilder.validator.TypeValidator;
import grphaqladapter.annotations.interfaces.GraphqlDirectiveDetails;
import grphaqladapter.annotations.interfaces.ValueParser;
import grphaqladapter.codegenerator.DataFetcherGenerator;
import grphaqladapter.codegenerator.ObjectConstructor;
import grphaqladapter.codegenerator.TypeResolverGenerator;
import grphaqladapter.codegenerator.impl.ReflectionDataFetcherGenerator;
import grphaqladapter.codegenerator.impl.ReflectionObjectConstructor;
import grphaqladapter.codegenerator.impl.SimpleTypeResolverGenerator;
import grphaqladapter.parser.PackageParser;
import grphaqladapter.parser.filter.ClassFilter;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static grphaqladapter.adaptedschemabuilder.exceptions.SchemaExceptionBuilder.exception;

public final class AdaptedSchemaBuilder {

    private final List<Class> allClasses = new ArrayList<>();
    private final Map<Class, ScalarEntry> scalarEntries;
    private final ClassMapper mapper;
    private DataFetcherGenerator dataFetcherGenerator;
    private TypeResolverGenerator typeResolverGenerator;
    private ObjectConstructor objectConstructor = new ReflectionObjectConstructor();

    private AdaptedSchemaBuilder() {
        dataFetcherGenerator = new ReflectionDataFetcherGenerator();
        typeResolverGenerator = new SimpleTypeResolverGenerator();
        mapper = new ClassMapper();
        scalarEntries = new HashMap<>();
    }

    public static AdaptedSchemaBuilder newBuilder() {
        return new AdaptedSchemaBuilder();
    }

    private void addIfNotExists(Class c) {
        if (!allClasses.contains(c))
            allClasses.add(c);
    }

    public synchronized AdaptedSchemaBuilder add(Class c, Class... cls) {
        addIfNotExists(c);
        if (cls != null) {
            for (Class cl : cls) {
                addIfNotExists(cl);
            }
        }

        return this;
    }

    public synchronized AdaptedSchemaBuilder addAll(Collection<Class> cls) {
        for (Class cl : cls) {
            addIfNotExists(cl);
        }

        return this;
    }

    public AdaptedSchemaBuilder addPackage(String packageName, ClassFilter filter) {
        Set<Class> classes = PackageParser.getAllGraphqlAnnotatedClasses(packageName, filter);
        return addAll(classes);
    }

    public AdaptedSchemaBuilder addPackage(String packageName) {
        return addPackage(packageName, ClassFilter.ACCEPT_ALL);
    }

    public synchronized AdaptedSchemaBuilder remove(Class c) {
        allClasses.remove(c);
        return this;
    }

    public ClassMapper mapper() {
        return mapper;
    }

    public synchronized AdaptedSchemaBuilder generator(TypeResolverGenerator generator) {
        Assert.isNotNull(generator, new IllegalStateException("code generators can not be null"));
        typeResolverGenerator = generator;
        return this;
    }

    public synchronized AdaptedSchemaBuilder generator(DataFetcherGenerator generator) {
        Assert.isNotNull(generator, new IllegalStateException("data-fetcher generators can not be null"));
        dataFetcherGenerator = generator;
        return this;
    }

    public synchronized AdaptedSchemaBuilder clearClasses() {
        allClasses.clear();
        return this;
    }

    public synchronized AdaptedSchemaBuilder addScalar(ScalarEntry entry) {
        TypeValidator.validate(entry);
        scalarEntries.put(entry.type(), entry);
        return this;
    }

    public synchronized AdaptedSchemaBuilder removeScalar(ScalarEntry entry) {
        scalarEntries.remove(entry.type(), entry);
        return this;
    }

    public synchronized AdaptedSchemaBuilder removeScalar(Class cls) {
        scalarEntries.remove(cls);
        return this;
    }

    public synchronized AdaptedSchemaBuilder clearScalars() {
        scalarEntries.clear();
        return this;
    }

    public synchronized AdaptedSchemaBuilder objectConstructor(ObjectConstructor constructor) {
        Assert.isNotNull(constructor, new IllegalStateException("provided object constructor is null"));
        this.objectConstructor = constructor;
        return this;
    }

    private Map<Class, GraphQLScalarType> getScalars() {
        Map<Class, GraphQLScalarType> scalarTypeMap = new HashMap<>();

        for (Class cls : scalarEntries.keySet()) {
            scalarTypeMap.put(cls,
                    StaticMethods.buildScalarType(scalarEntries.get(cls)));
        }

        return scalarTypeMap;
    }

    private void discoverDirectives(Map<Class, Map<MappedElementType, MappedElement>> mappedClasses, BuildingContextImpl context) {
        mappedClasses.values()
                .stream()
                .forEach(map -> {
                    map.values()
                            .stream()
                            .filter(element -> element.mappedType().is(MappedElementType.DIRECTIVE))
                            .forEach(mappedClass -> {
                                discover(mappedClass, context);
                            });
                });
    }

    private void discoverTypesExceptUnions(Map<Class, Map<MappedElementType, MappedElement>> mappedClasses, BuildingContextImpl context) {
        mappedClasses.values()
                .stream()
                .forEach(map -> {

                    map.values()
                            .stream()
                            .filter(element -> !element.mappedType().isOneOf(MappedElementType.UNION, MappedElementType.DIRECTIVE))
                            .forEach(mappedClass -> {

                                discover(mappedClass, context);

                            });
                });
    }

    private void discoverUnionTypes(Map<Class, Map<MappedElementType, MappedElement>> mappedClasses, BuildingContextImpl context) {
        mappedClasses.values()
                .stream()
                .forEach(map -> {

                    map.values()
                            .stream()
                            .filter(element -> element.mappedType().is(MappedElementType.UNION))
                            .forEach(mappedClass -> {
                                discoverUnion((MappedUnionInterface) mappedClass,
                                        context.possibleTypesOf((MappedClass) mappedClass)
                                        , context);
                            });
                });
    }

    private Map<MappedElement, DiscoveredElement> getDiscoveredElements(BuildingContextImpl context) {
        Map<MappedElement, DiscoveredElement> discoveredElements = new HashMap<>();

        context.rawTypes().keySet()
                .stream()
                .forEach(mappedClass -> {
                    GraphQLNamedType type = context.rawTypeOf(mappedClass);
                    discoveredElements.put(mappedClass, buildDiscoveredElement(mappedClass, type));
                });

        context.getDirectives()
                .entrySet()
                .forEach(entry -> {
                    discoveredElements.put(entry.getKey(), buildDiscoveredElement(entry.getKey(), entry.getValue()));
                });

        return discoveredElements;
    }

    private void predictPossibleTypes(Map<MappedElement, DiscoveredElement> discoveredElements, GraphQLCodeRegistry.Builder code, BuildingContextImpl context) {
        DirectiveHandlingContextImpl directiveContext = (DirectiveHandlingContextImpl) context.directiveHandlingContext();
        discoveredElements.values()
                .stream().forEach(discoveredType -> {

                    if (discoveredType.asMappedElement().mappedType().
                            is(MappedElementType.INTERFACE)) {
                        DiscoveredInterfaceTypeImpl interfaceType = (DiscoveredInterfaceTypeImpl) discoveredType;

                        List<MappedClass> implementors = context.possibleTypesOf((MappedClass) discoveredType.asMappedElement());

                        if (implementors != null) {
                            implementors.stream()
                                    .forEach(mappedClass -> {

                                        interfaceType.implementors().add((DiscoveredObjectType)
                                                discoveredElements.get(mappedClass));
                                    });
                        }

                        interfaceType.setUnmodifiable();

                        code.typeResolver(interfaceType.name(),
                                directiveContext.applyChanges(interfaceType.name(), typeResolverGenerator.generate(interfaceType)));
                    }

                    if (discoveredType.asMappedElement().mappedType().
                            is(MappedElementType.UNION)) {
                        DiscoveredUnionTypeImpl unionType = (DiscoveredUnionTypeImpl) discoveredType;

                        List<MappedClass> possiableTypes = context.possibleTypesOf((MappedClass) discoveredType.asMappedElement());

                        if (possiableTypes != null) {
                            possiableTypes.stream()
                                    .forEach(mappedClass -> {

                                        unionType.possibleTypes().add((DiscoveredObjectType)
                                                discoveredElements.get(mappedClass));
                                    });
                        }

                        unionType.setUnmodifiable();

                        code.typeResolver(unionType.name(),
                                directiveContext.applyChanges(unionType.name(), typeResolverGenerator.generate(unionType)));
                    }

                });
    }

    private void addDataFetchers(List<DiscoveredElement> discoveredElements, DataFetcherGenerator dataFetcherGenerator, GraphQLCodeRegistry.Builder code, Supplier<AdaptedGraphQLSchema> schemaSupplier, BuildingContext context) {
        DirectiveHandlingContextImpl directiveContext = (DirectiveHandlingContextImpl) context.directiveHandlingContext();
        discoveredElements.stream().filter(AdaptedSchemaBuilder::isInterfaceOrType).forEach(d -> {
            Map<String, MappedFieldMethod> methods = d instanceof DiscoveredObjectType ?
                    ((DiscoveredObjectType) d).asMappedElement().fieldMethods() :
                    ((DiscoveredInterfaceType) d).asMappedElement().fieldMethods();

            MappedTypeClass mappedClass = (MappedTypeClass) d.asMappedElement();

            for (MappedFieldMethod method : methods.values()) {
                code.dataFetcher(FieldCoordinates.coordinates(
                        mappedClass.name(),
                        method.name()
                ), new DataFetcherHandler(directiveContext.applyChanges(mappedClass.name(), method.name(), dataFetcherGenerator.generate(mappedClass, method)), mappedClass, method, schemaSupplier));
            }
        });
    }

    public synchronized AdaptedGraphQLSchema build() {

        Map<Class, Map<MappedElementType, MappedElement>> mappedClasses =
                mapper.map(allClasses);

        MappedClass query = null;
        MappedClass mutation = null;
        MappedClass subscription = null;

        for (Map<MappedElementType, MappedElement> clazz : mappedClasses.values()) {
            if (clazz.containsKey(MappedElementType.QUERY)) {
                Assert.isNull(query, exception(MultipleGraphqlTopLevelTypeException.class, "multiple QUERY type found", ((MappedClass) clazz.get(MappedElementType.QUERY)).baseClass()));
                query = (MappedClass) clazz.get(MappedElementType.QUERY);
            }

            if (clazz.containsKey(MappedElementType.MUTATION)) {
                Assert.isNull(query, exception(MultipleGraphqlTopLevelTypeException.class, "multiple MUTATION type found", ((MappedClass) clazz.get(MappedElementType.MUTATION)).baseClass()));
                mutation = (MappedClass) clazz.get(MappedElementType.MUTATION);
            }

            if (clazz.containsKey(MappedElementType.SUBSCRIPTION)) {
                Assert.isNull(query, exception(MultipleGraphqlTopLevelTypeException.class, "multiple SUBSCRIPTION type found", ((MappedClass) clazz.get(MappedElementType.SUBSCRIPTION)).baseClass()));
                subscription = (MappedClass) clazz.get(MappedElementType.SUBSCRIPTION);
            }
        }

        GraphQLSchema.Builder schema = GraphQLSchema.newSchema();

        Map<Class, MappedAnnotation> annotations = separateAnnotations(mappedClasses);

        final BuildingContextImpl context =
                new BuildingContextImpl(mappedClasses,
                        schema,
                        getScalars(),
                        element -> this.getDirectives(element, annotations),
                        objectConstructor);

        discoverDirectives(mappedClasses, context);

        discoverTypesExceptUnions(mappedClasses, context);

        discoverUnionTypes(mappedClasses, context);

        Map<MappedElement, DiscoveredElement> discoveredElements = getDiscoveredElements(context);

        GraphQLCodeRegistry.Builder code = GraphQLCodeRegistry.newCodeRegistry();

        predictPossibleTypes(discoveredElements, code, context);

        List<DiscoveredElement> allElements = new ArrayList<>(discoveredElements.values());
        allElements.addAll(context.allScalars());
        allElements = Collections.unmodifiableList(allElements);

        allElements.stream().filter(AdaptedSchemaBuilder::isScalar)
                .forEach(type -> {
                    schema.additionalType((GraphQLScalarType) type.asGraphqlElement());
                });

        allElements.stream().filter(AdaptedSchemaBuilder::isInputType)
                .forEach(type -> schema.additionalType((GraphQLInputObjectType) type.asGraphqlElement()));

        allElements.stream().filter(AdaptedSchemaBuilder::isDirective)
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

        return schemaReference.set(new AdaptedGraphQLSchema(schema.build(), allElements, objectConstructor))
                .lock()
                .get();

    }

    private static Map<Class, MappedAnnotation> separateAnnotations(Map<Class, Map<MappedElementType, MappedElement>> mappedClasses) {
        Map<Class, MappedAnnotation> annotations = new HashMap<>();
        mappedClasses.forEach(new BiConsumer<Class, Map<MappedElementType, MappedElement>>() {
            @Override
            public void accept(Class aClass, Map<MappedElementType, MappedElement> types) {
                types.values().stream().filter(element -> element.mappedType().is(MappedElementType.DIRECTIVE))
                        .map(element -> (MappedAnnotation) element)
                        .forEach(annotation -> annotations.put(annotation.baseClass(), annotation));
            }
        });
        return Collections.unmodifiableMap(annotations);
    }

    private GraphQLNamedSchemaElement discover(MappedElement mappedElement, BuildingContextImpl context) {

        if (mappedElement.mappedType().is(MappedElementType.DIRECTIVE)) {
            GraphQLDirective type = StaticMethods.buildDirective((MappedAnnotation) mappedElement, context);
            context.setDirective((MappedAnnotation) mappedElement, type);
            return type;
        }

        GraphQLNamedType type = null;
        MappedClass mappedClass = (MappedClass) mappedElement;

        if (mappedClass.mappedType().isTopLevelType() || mappedClass.mappedType().is(MappedElementType.OBJECT_TYPE)) {
            type = StaticMethods.buildOutputObjectType((MappedTypeClass) mappedClass, context);
            context.setGraphQLTypeFor(mappedClass, type);
        } else if (mappedClass.mappedType().is(MappedElementType.INTERFACE)) {
            type = StaticMethods.buildInterface((MappedInterface) mappedClass, context);
            context.setGraphQLTypeFor(mappedClass, type);
        } else if (mappedClass.mappedType().is(MappedElementType.INPUT_TYPE)) {
            type = StaticMethods.buildInputObjectType((MappedInputTypeClass) mappedClass, context);
            context.setGraphQLTypeFor(mappedClass, type);
        } else if (mappedClass.mappedType().is(MappedElementType.ENUM)) {
            type = StaticMethods.buildEnumType((MappedEnum) mappedClass, context);
            context.setGraphQLTypeFor(mappedClass, type);
        } else if (mappedClass.mappedType().isTopLevelType()) {
            type = StaticMethods.buildOutputObjectType((MappedTypeClass) mappedClass, context);
            context.setGraphQLTypeFor(mappedClass, type);
        }

        return type;
    }

    private GraphQLType discoverUnion(MappedUnionInterface unionClass, List<MappedClass> possibles, BuildingContextImpl context) {
        GraphQLUnionType unionType =
                StaticMethods.buildUnionType(unionClass, possibles, context);

        context.setGraphQLTypeFor(unionClass, unionType);

        return unionType;
    }

    public DiscoveredElement buildDiscoveredElement(MappedElement element, GraphQLNamedSchemaElement schemaElement) {
        if (element.mappedType().is(MappedElementType.DIRECTIVE)) {
            return new DiscoveredDirectiveImpl(
                    (MappedAnnotation) element,
                    schemaElement.getName(),
                    (GraphQLDirective) schemaElement
            );
        }

        MappedClass cls = (MappedClass) element;
        if (cls.mappedType().is(MappedElementType.OBJECT_TYPE) || cls.mappedType().isTopLevelType()) {
            Assert.isOneOrMoreFalse(exception(MappingGraphqlTypeException.class, "conflict - class mapped to bad type - [MappedClass:" + cls +
                            " , GraphQLType:" + schemaElement + "]", cls.baseClass()),
                    !(schemaElement instanceof GraphQLObjectType));

            return new DiscoveredObjectTypeImpl((MappedTypeClass) cls, schemaElement.getName(), (GraphQLObjectType) schemaElement);
        } else if (cls.mappedType().is(MappedElementType.INTERFACE)) {
            Assert.isOneOrMoreFalse(exception(MappingGraphqlTypeException.class, "conflict - class mapped to bad type - [MappedClass:" + cls +
                            " , GraphQLType:" + schemaElement + "]", cls.baseClass()),
                    !(schemaElement instanceof GraphQLInterfaceType));

            return new DiscoveredInterfaceTypeImpl((MappedInterface) cls, schemaElement.getName(), (GraphQLInterfaceType) schemaElement);
        } else if (cls.mappedType().is(MappedElementType.INPUT_TYPE)) {
            Assert.isOneOrMoreFalse(exception(MappingGraphqlTypeException.class, "conflict - class mapped to bad type - [MappedClass:" + cls +
                            " , GraphQLType:" + schemaElement + "]", cls.baseClass()),
                    !(schemaElement instanceof GraphQLInputObjectType));

            return new DiscoveredInputTypeImpl((MappedInputTypeClass) cls, schemaElement.getName(), (GraphQLInputObjectType) schemaElement);
        } else if (cls.mappedType().is(MappedElementType.UNION)) {
            Assert.isOneOrMoreFalse(exception(MappingGraphqlTypeException.class, "conflict - class mapped to bad type - [MappedClass:" + cls +
                            " , GraphQLType:" + schemaElement + "]", cls.baseClass()),
                    !(schemaElement instanceof GraphQLUnionType));

            return new DiscoveredUnionTypeImpl((MappedUnionInterface) cls, schemaElement.getName(), (GraphQLUnionType) schemaElement);
        } else if (cls.mappedType().is(MappedElementType.ENUM)) {
            Assert.isOneOrMoreFalse(exception(MappingGraphqlTypeException.class, "conflict - class mapped to bad type - [MappedClass:" + cls +
                            " , GraphQLType:" + schemaElement + "]", cls.baseClass()),
                    !(schemaElement instanceof GraphQLEnumType));

            return new DiscoveredEnumTypeImpl((MappedEnum) cls, schemaElement.getName(), (GraphQLEnumType) schemaElement);
        }

        throw new IllegalStateException();
    }

    private static boolean isDirective(DiscoveredElement element) {
        return element.asMappedElement().mappedType().is(MappedElementType.DIRECTIVE);
    }

    private static boolean isScalar(DiscoveredElement element) {
        return element.asMappedElement().mappedType().is(MappedElementType.SCALAR);
    }

    private static boolean isInputType(DiscoveredElement element) {
        return element.asMappedElement().mappedType().is(MappedElementType.INPUT_TYPE);
    }

    private static boolean isInterfaceOrType(DiscoveredElement element) {
        return element.asMappedElement().mappedType().isTopLevelType() || element.asMappedElement()
                .mappedType().isOneOf(MappedElementType.INTERFACE, MappedElementType.OBJECT_TYPE);
    }

    private static Map<Class, MappedAnnotation> separateAnnotations(Map<Class, MappedAnnotation> annotations, Introspection.DirectiveLocation location) {
        return annotations.values().stream().filter(element -> element.locations().contains(location))
                .collect(Collectors.toMap(
                        MappedClass::baseClass,
                        element -> element
                ));
    }

    private static Map<Class, MappedAnnotation> separateAnnotations(Map<Class, MappedAnnotation> annotations, MappedElementType type) {
        switch (type) {
            case OBJECT_TYPE:
            case QUERY:
            case MUTATION:
            case SUBSCRIPTION:
                return separateAnnotations(annotations, Introspection.DirectiveLocation.OBJECT);
            case INPUT_TYPE:
                return separateAnnotations(annotations, Introspection.DirectiveLocation.INPUT_OBJECT);
            case FIELD:
                return separateAnnotations(annotations, Introspection.DirectiveLocation.FIELD_DEFINITION);
            case INPUT_FIELD:
                return separateAnnotations(annotations, Introspection.DirectiveLocation.INPUT_FIELD_DEFINITION);
            case ENUM_VALUE:
                return separateAnnotations(annotations, Introspection.DirectiveLocation.ENUM_VALUE);
            case ENUM:
                return separateAnnotations(annotations, Introspection.DirectiveLocation.ENUM);
            case SCALAR:
                return separateAnnotations(annotations, Introspection.DirectiveLocation.SCALAR);
            case ARGUMENT:
                return separateAnnotations(annotations, Introspection.DirectiveLocation.ARGUMENT_DEFINITION);
            case UNION:
                return separateAnnotations(annotations, Introspection.DirectiveLocation.UNION);
            case INTERFACE:
                return separateAnnotations(annotations, Introspection.DirectiveLocation.INTERFACE);
            default:
                return Collections.emptyMap();
        }
    }

    private List<GraphqlDirectiveDetails> getDirectives(MappedElement element, Map<Class, MappedAnnotation> annotations) {

        List<DirectiveArgumentsValue> values = mapper().detectDirective(element, separateAnnotations(annotations, element.mappedType()));
        if (values == null || values.isEmpty()) {
            return Collections.emptyList();
        }
        List<GraphqlDirectiveDetails> details = new ArrayList<>();
        for (DirectiveArgumentsValue value : values) {
            MappedAnnotation mappedAnnotation = annotations.get(value.annotationClass());
            if (mappedAnnotation == null) {
                throw new IllegalStateException("can not detect mapped annotation for class [" + value.annotationClass() + "]");
            }
            details.add(new GraphqlDirectiveDetails(
                    mappedAnnotation,
                    convertArgumentValuesToMap(value, mappedAnnotation)
            ));
        }
        return details;
    }

    private Map<String, Object> convertArgumentValuesToMap(DirectiveArgumentsValue values, MappedAnnotation annotation) {
        if (annotation.mappedMethods().isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Object> map = new HashMap<>();
        for (MappedAnnotationMethod method : annotation.mappedMethods().values()) {
            Object value = values.getArgumentValue(method.name());
            if (value == null) {
                continue;
            }
            ValueParser parser = this.objectConstructor.getInstance(method.valueParser());
            map.put(method.name(), parser.parse(value, method.type()));
        }
        if (map.isEmpty()) {
            return Collections.emptyMap();
        }
        return map;
    }
}
