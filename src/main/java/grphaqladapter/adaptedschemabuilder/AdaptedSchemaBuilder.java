package grphaqladapter.adaptedschemabuilder;

import graphql.schema.*;
import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredObjectType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredType;
import grphaqladapter.adaptedschemabuilder.exceptions.MappingGraphqlTypeException;
import grphaqladapter.adaptedschemabuilder.exceptions.MultipleGraphqlTopLevelTypeException;
import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;
import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;
import grphaqladapter.adaptedschemabuilder.mapper.ClassMapper;
import grphaqladapter.adaptedschemabuilder.scalar.ScalarEntry;
import grphaqladapter.adaptedschemabuilder.validator.TypeValidator;
import grphaqladapter.codegenerator.DataFetcherGenerator;
import grphaqladapter.codegenerator.ObjectConstructor;
import grphaqladapter.codegenerator.TypeResolverGenerator;
import grphaqladapter.codegenerator.impl.ReflectionDataFetcherGenerator;
import grphaqladapter.codegenerator.impl.ReflectionObjectConstructor;
import grphaqladapter.codegenerator.impl.SimpleTypeResolverGenerator;
import grphaqladapter.parser.PackageParser;
import grphaqladapter.parser.filter.ClassFilter;

import java.util.*;

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


    private void discoverTypesExceptUnions(Map<Class, Map<MappedClass.MappedType, MappedClass>> mappedClasses, BuildingContextImpl context) {
        mappedClasses.values()
                .stream()
                .forEach(map -> {

                    map.values()
                            .stream()
                            .forEach(mappedClass -> {

                                if (mappedClass.mappedType().
                                        is(MappedClass.MappedType.UNION))
                                    return;

                                discover(mappedClass, context);

                            });
                });
    }

    private void discoverUnionTypes(Map<Class, Map<MappedClass.MappedType, MappedClass>> mappedClasses, BuildingContextImpl context) {
        mappedClasses.values()
                .stream()
                .forEach(map -> {

                    map.values()
                            .stream()
                            .forEach(mappedClass -> {

                                if (mappedClass.mappedType().
                                        is(MappedClass.MappedType.UNION)) {
                                    discoverUnion(mappedClass,
                                            context.possibleTypesOf(mappedClass)
                                            , context);

                                }

                            });
                });
    }

    private Map<MappedClass, DiscoveredType> getDiscoveredTypes(BuildingContextImpl context) {
        Map<MappedClass, DiscoveredType> discoveredTypes = new HashMap<>();

        context.rawTypes().keySet()
                .stream()
                .forEach(mappedClass -> {
                    GraphQLNamedType type = context.rawTypeOf(mappedClass);
                    discoveredTypes.put(mappedClass, buildDiscoveredType(mappedClass, type));
                });
        return discoveredTypes;
    }

    private void predictPossibleTypes(Map<MappedClass, DiscoveredType> discoveredTypes, GraphQLCodeRegistry.Builder code, BuildingContextImpl context) {
        discoveredTypes.values()
                .stream().forEach(discoveredType -> {

                    if (discoveredType.asMappedClass().mappedType().
                            is(MappedClass.MappedType.INTERFACE)) {
                        DiscoveredInterfaceTypeImpl interfaceType = (DiscoveredInterfaceTypeImpl) discoveredType;

                        List<MappedClass> implementors = context.possibleTypesOf(discoveredType.asMappedClass());

                        if (implementors != null) {
                            implementors.stream()
                                    .forEach(mappedClass -> {

                                        interfaceType.implementors().add((DiscoveredObjectType)
                                                discoveredTypes.get(mappedClass));
                                    });
                        }

                        interfaceType.setUnmodifiable();

                        code.typeResolver(interfaceType.typeName(),
                                typeResolverGenerator.generate(interfaceType));
                    }

                    if (discoveredType.asMappedClass().mappedType().
                            is(MappedClass.MappedType.UNION)) {
                        DiscoveredUnionTypeImpl unionType = (DiscoveredUnionTypeImpl) discoveredType;

                        List<MappedClass> possiableTypes = context.possibleTypesOf(discoveredType.asMappedClass());

                        if (possiableTypes != null) {
                            possiableTypes.stream()
                                    .forEach(mappedClass -> {

                                        unionType.possibleTypes().add((DiscoveredObjectType)
                                                discoveredTypes.get(mappedClass));
                                    });
                        }

                        unionType.setUnmodifiable();

                        code.typeResolver(unionType.typeName(),
                                typeResolverGenerator.generate(unionType));
                    }

                });
    }

    private void addDataFetchers(List<DiscoveredType> discoveredTypes, DataFetcherGenerator dataFetcherGenerator, GraphQLCodeRegistry.Builder code) {
        discoveredTypes.stream().forEach(d -> {

            MappedClass mappedClass = d.asMappedClass();
            for (MappedMethod method : mappedClass.mappedMethods().values()) {
                code.dataFetcher(FieldCoordinates.coordinates(
                        mappedClass.typeName(),
                        method.fieldName()
                ), dataFetcherGenerator.generate(mappedClass, method, objectConstructor));
            }

        });
    }

    public synchronized AdaptedGraphQLSchema build() {

        Map<Class, Map<MappedClass.MappedType, MappedClass>> mappedClasses =
                mapper.map(allClasses);

        MappedClass query = null;
        MappedClass mutation = null;
        MappedClass subscription = null;

        for (Map<MappedClass.MappedType, MappedClass> clazz : mappedClasses.values()) {
            if (clazz.containsKey(MappedClass.MappedType.QUERY)) {
                Assert.isNull(query, exception(MultipleGraphqlTopLevelTypeException.class, "multiple QUERY type found", clazz.get(MappedClass.MappedType.QUERY).baseClass()));
                query = clazz.get(MappedClass.MappedType.QUERY);
            }

            if (clazz.containsKey(MappedClass.MappedType.MUTATION)) {
                Assert.isNull(query, exception(MultipleGraphqlTopLevelTypeException.class, "multiple MUTATION type found", clazz.get(MappedClass.MappedType.QUERY).baseClass()));
                mutation = clazz.get(MappedClass.MappedType.MUTATION);
            }

            if (clazz.containsKey(MappedClass.MappedType.SUBSCRIPTION)) {
                Assert.isNull(query, exception(MultipleGraphqlTopLevelTypeException.class, "multiple SUBSCRIPTION type found", clazz.get(MappedClass.MappedType.QUERY).baseClass()));
                subscription = clazz.get(MappedClass.MappedType.SUBSCRIPTION);
            }
        }


        GraphQLSchema.Builder schema = GraphQLSchema.newSchema();

        final BuildingContextImpl context =
                new BuildingContextImpl(mappedClasses, schema, getScalars());

        discoverTypesExceptUnions(mappedClasses, context);

        discoverUnionTypes(mappedClasses, context);

        Map<MappedClass, DiscoveredType> discoveredTypes = getDiscoveredTypes(context);

        GraphQLCodeRegistry.Builder code = GraphQLCodeRegistry.newCodeRegistry();

        predictPossibleTypes(discoveredTypes, code, context);

        List<DiscoveredType> allTypes = new ArrayList<>(discoveredTypes.values());
        allTypes.addAll(context.allScalars());
        allTypes = Collections.unmodifiableList(allTypes);

        allTypes.forEach(type -> {
            if (!type.asMappedClass().mappedType().isTopLevelType()) {
                schema.additionalType(type.asGraphQLType());
            }
        });

        dataFetcherGenerator.init(allTypes);

        addDataFetchers(allTypes, dataFetcherGenerator, code);

        if (query != null)
            schema.query((GraphQLObjectType) context.rawTypeOf(query));
        if (mutation != null)
            schema.mutation((GraphQLObjectType) context.rawTypeOf(mutation));
        if (subscription != null)
            schema.subscription((GraphQLObjectType) context.rawTypeOf(subscription));

        schema.codeRegistry(code.build());

        return new AdaptedGraphQLSchema(schema.build(), allTypes);

    }


    private GraphQLType discover(MappedClass mappedClass, BuildingContextImpl context) {
        GraphQLNamedType type = null;
        if (mappedClass.mappedType() == MappedClass.MappedType.OBJECT_TYPE) {

            type = StaticMethods.buildOutputObjectType(mappedClass, context);
            context.setGraphQLTypeFor(mappedClass, type);
            //setDiscoveredTypes !

        } else if (mappedClass.mappedType() == MappedClass.MappedType.INTERFACE) {
            type = StaticMethods.buildInterface(mappedClass, context);
            context.setGraphQLTypeFor(mappedClass, type);
        } else if (mappedClass.mappedType() == MappedClass.MappedType.INPUT_TYPE) {
            type = StaticMethods.buildInputObjectType(mappedClass, context);
            context.setGraphQLTypeFor(mappedClass, type);
        } else if (mappedClass.mappedType() == MappedClass.MappedType.ENUM) {
            type = StaticMethods.buildEnumType(mappedClass, context);
            context.setGraphQLTypeFor(mappedClass, type);
        } else if (mappedClass.mappedType().is(MappedClass.MappedType.QUERY) ||
                mappedClass.mappedType().is(MappedClass.MappedType.MUTATION) ||
                mappedClass.mappedType().is(MappedClass.MappedType.SUBSCRIPTION)) {
            type = StaticMethods.buildOutputObjectType(mappedClass, context);
            context.setGraphQLTypeFor(mappedClass, type);
        }

        return type;
    }

    private GraphQLType discoverUnion(MappedClass unionClass, List<MappedClass> possibles, BuildingContextImpl context) {
        GraphQLUnionType unionType =
                StaticMethods.buildUnionType(unionClass, possibles, context);

        context.setGraphQLTypeFor(unionClass, unionType);

        return unionType;
    }

    public DiscoveredType buildDiscoveredType(MappedClass cls, GraphQLNamedType type) {
        if (cls.mappedType().is(MappedClass.MappedType.OBJECT_TYPE) || cls.mappedType().isTopLevelType()) {
            Assert.isOneFalse(exception(MappingGraphqlTypeException.class, "conflict - class mapped to bad type - [MappedClass:" + cls +
                            " , GraphQLType:" + type + "]", cls.baseClass()),
                    !(type instanceof GraphQLObjectType));

            return new DiscoveredObjectTypeImpl(cls, type.getName(), (GraphQLObjectType) type);
        } else if (cls.mappedType().is(MappedClass.MappedType.INTERFACE)) {
            Assert.isOneFalse(exception(MappingGraphqlTypeException.class, "conflict - class mapped to bad type - [MappedClass:" + cls +
                            " , GraphQLType:" + type + "]", cls.baseClass()),
                    !(type instanceof GraphQLInterfaceType));

            return new DiscoveredInterfaceTypeImpl(cls, type.getName(), (GraphQLInterfaceType) type);
        } else if (cls.mappedType().is(MappedClass.MappedType.INPUT_TYPE)) {
            Assert.isOneFalse(exception(MappingGraphqlTypeException.class, "conflict - class mapped to bad type - [MappedClass:" + cls +
                            " , GraphQLType:" + type + "]", cls.baseClass()),
                    !(type instanceof GraphQLInputObjectType));

            return new DiscoveredInputTypeImpl(cls, type.getName(), (GraphQLInputObjectType) type);
        } else if (cls.mappedType().is(MappedClass.MappedType.UNION)) {
            Assert.isOneFalse(exception(MappingGraphqlTypeException.class, "conflict - class mapped to bad type - [MappedClass:" + cls +
                            " , GraphQLType:" + type + "]", cls.baseClass()),
                    !(type instanceof GraphQLUnionType));

            return new DiscoveredUnionTypeImpl(cls, type.getName(), (GraphQLUnionType) type);
        } else if (cls.mappedType().is(MappedClass.MappedType.ENUM)) {
            Assert.isOneFalse(exception(MappingGraphqlTypeException.class, "conflict - class mapped to bad type - [MappedClass:" + cls +
                            " , GraphQLType:" + type + "]", cls.baseClass()),
                    !(type instanceof GraphQLEnumType));

            return new DiscoveredEnumTypeImpl(cls, type.getName(), (GraphQLEnumType) type);
        }

        throw new IllegalStateException();
    }

}
