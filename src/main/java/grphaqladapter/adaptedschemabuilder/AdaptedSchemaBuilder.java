package grphaqladapter.adaptedschemabuilder;

import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredInputType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredObjectType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;
import grphaqladapter.adaptedschemabuilder.mapper.ClassMapper;
import grphaqladapter.adaptedschemabuilder.scalar.ScalarEntry;
import grphaqladapter.codegenerator.DataFetcherGenerator;
import grphaqladapter.codegenerator.TypeResolverGenerator;
import grphaqladapter.codegenerator.impl.ReflectionDataFetcherGenerator;
import grphaqladapter.codegenerator.impl.SimpleTypeResolverGenerator;
import graphql.schema.*;
import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;

import java.util.*;

public final class AdaptedSchemaBuilder {



    public final static AdaptedSchemaBuilder newBuilder()
    {
        return new AdaptedSchemaBuilder();
    }

    private final List<Class> allClasses = new ArrayList<>();
    private DataFetcherGenerator dataFetcherGenerator;
    private TypeResolverGenerator typeResolverGenerator;
    private final Map<Class , ScalarEntry> scalarEntries;
    private final ClassMapper mapper;

    private AdaptedSchemaBuilder(){
        dataFetcherGenerator = new ReflectionDataFetcherGenerator();
        typeResolverGenerator = new SimpleTypeResolverGenerator();
        mapper = new ClassMapper();
        scalarEntries = new HashMap<>();
    }

    private void addIfNotExists(Class c)
    {
        if(!allClasses.contains(c))
            allClasses.add(c);
    }

    public synchronized AdaptedSchemaBuilder add(Class c, Class ... cls)
    {
        addIfNotExists(c);
        if(cls!=null) {
            for (Class cl : cls) {
                addIfNotExists(cl);
            }
        }

        return this;
    }

    public synchronized AdaptedSchemaBuilder addAll(Collection<Class> cls)
    {
        for (Class cl : cls) {
            addIfNotExists(cl);
        }

        return this;
    }

    public synchronized AdaptedSchemaBuilder remove(Class c)
    {
        allClasses.remove(c);
        return this;
    }

    public ClassMapper mapper() {
        return mapper;
    }

    public synchronized AdaptedSchemaBuilder generator(TypeResolverGenerator generator)
    {
        Assert.ifNull(generator , "code generators can not be null");

        typeResolverGenerator = generator;
        return this;
    }

    public synchronized AdaptedSchemaBuilder generator(DataFetcherGenerator generator)
    {
        Assert.ifNull(generator , "data-fetcher generators can not be null");


        dataFetcherGenerator = generator;
        return this;
    }

    public synchronized AdaptedSchemaBuilder clearClasses()
    {
        allClasses.clear();
        return this;
    }

    public synchronized AdaptedSchemaBuilder addScalar(ScalarEntry entry)
    {
        scalarEntries.put(entry.type() , entry);
        return this;
    }

    public synchronized AdaptedSchemaBuilder removeScalar(ScalarEntry entry)
    {
        scalarEntries.remove(entry.type() , entry);
        return this;
    }

    public synchronized AdaptedSchemaBuilder removeScalar(Class cls)
    {
        scalarEntries.remove(cls);
        return this;
    }

    public synchronized AdaptedSchemaBuilder clearScalars()
    {
        scalarEntries.clear();
        return this;
    }

    private final Map<Class , GraphQLScalarType> getScalars()
    {
        Map<Class , GraphQLScalarType> scalarTypeMap = new HashMap<>();

        for(Class cls : scalarEntries.keySet())
        {
            scalarTypeMap.put(cls ,
                    StaticMethods.buildScalarType(scalarEntries.get(cls)));
        }

        return scalarTypeMap;
    }



    public synchronized AdaptedGraphQLSchema build()
    {


        Map<Class , Map<MappedClass.MappedType , MappedClass>> mappedClasses =
                mapper.map(allClasses);

        MappedClass query = null;
        MappedClass mutation = null;
        MappedClass subscription = null;

        for(Map<MappedClass.MappedType , MappedClass> mapClass:mappedClasses.values())
        {
            if(mapClass.containsKey(MappedClass.MappedType.QUERY))
            {
                Assert.ifNotNull(query , "multiple query type found");
                query = mapClass.get(MappedClass.MappedType.QUERY);
            }

            if(mapClass.containsKey(MappedClass.MappedType.MUTATION))
            {
                Assert.ifNotNull(mutation , "multiple mutation type found");
                mutation = mapClass.get(MappedClass.MappedType.MUTATION);
            }

            if(mapClass.containsKey(MappedClass.MappedType.SUBSCRIPTION))
            {
                Assert.ifNotNull(mutation , "multiple subscription type found");
                subscription = mapClass.get(MappedClass.MappedType.SUBSCRIPTION);
            }
        }



        GraphQLSchema.Builder schema = GraphQLSchema.newSchema();

        final BuildingContextImpl context =
                new BuildingContextImpl(mappedClasses , schema , getScalars());


        mappedClasses.values()
                .stream()
                .forEach(map ->{

                    map.values()
                            .stream()
                            .forEach(mappedClass -> {

                                if(mappedClass.mappedType().
                                        is(MappedClass.MappedType.UNION))
                                    return;

                                discover(mappedClass , context);

                            });
                });

        mappedClasses.values()
                .stream()
                .forEach(map ->{

                    map.values()
                            .stream()
                            .forEach(mappedClass -> {

                                if(mappedClass.mappedType().
                                        is(MappedClass.MappedType.UNION))
                                {
                                    discoverUnion(mappedClass ,
                                            context.possibleTypesOf(mappedClass)
                                            , context);

                                }

                            });
                });



        Map<MappedClass , DiscoveredType> discoveredTypes = new HashMap<>();


        context.rawTypes().keySet()
                .stream()
                .forEach(mappedClass -> {

                    GraphQLType type = context.rawTypeOf(mappedClass);
                    discoveredTypes.put(mappedClass , buildDiscoveredType(mappedClass , type));

                });

        GraphQLCodeRegistry.Builder code = GraphQLCodeRegistry.newCodeRegistry();

        discoveredTypes.values()
                .stream().forEach(discoveredType -> {

            if(discoveredType.asMappedClass().mappedType().
                    is(MappedClass.MappedType.INTERFACE))
            {
                DiscoveredInterfaceTypeImpl interfaceType = (DiscoveredInterfaceTypeImpl)discoveredType;

                List<MappedClass> implementors = context.possibleTypesOf(discoveredType.asMappedClass());

                if(implementors!=null)
                {
                    implementors.stream()
                            .forEach(mappedClass -> {

                                interfaceType.implementors().add((DiscoveredObjectType)
                                        discoveredTypes.get(mappedClass));
                            });
                }

                interfaceType.setUnmodifiable();

                code.typeResolver(interfaceType.typeName() ,
                        typeResolverGenerator.generate(interfaceType));
            }

            if(discoveredType.asMappedClass().mappedType().
                    is(MappedClass.MappedType.UNION))
            {
                DiscoveredUnionTypeImpl unionType = (DiscoveredUnionTypeImpl)discoveredType;

                List<MappedClass> possiableTypes = context.possibleTypesOf(discoveredType.asMappedClass());

                if(possiableTypes!=null)
                {
                    possiableTypes.stream()
                            .forEach(mappedClass -> {

                                unionType.possibleTypes().add((DiscoveredObjectType)
                                        discoveredTypes.get(mappedClass));
                            });
                }

                unionType.setUnmodifiable();

                code.typeResolver(unionType.typeName() ,
                        typeResolverGenerator.generate(unionType));
            }

        });


        List<DiscoveredType> allTypes = new ArrayList<>();
        List<DiscoveredInputType> inputTypes = new ArrayList<>();
        discoveredTypes.values().stream()
                .forEach(t ->{
                    allTypes.add(t) ;

                    if(t.asMappedClass().mappedType().isTopLevelType())
                        return;

                    schema.additionalType(t.asGraphQLType());

                    if(t instanceof DiscoveredInputType)
                    {
                        inputTypes.add((DiscoveredInputType) t);
                    }
                });

        allTypes.addAll(context.allScalars());
        List<DiscoveredType> unmodifiableTypes = Collections.unmodifiableList(allTypes);

        dataFetcherGenerator.init(unmodifiableTypes);

        unmodifiableTypes.stream().forEach(d -> {

            MappedClass mappedClass = d.asMappedClass();
            for(MappedMethod method:mappedClass.mappedMethods().values())
            {
                code.dataFetcher(FieldCoordinates.coordinates(
                        mappedClass.typeName() ,
                        method.fieldName()
                ) , dataFetcherGenerator.generate(mappedClass , method));
            }

        });

        GraphQLType queryType = query==null?null:context.rawTypeOf(query);
        GraphQLType mutationType = mutation==null?null:context.rawTypeOf(mutation);
        GraphQLType subscriptionType = subscription==null?null:context.rawTypeOf(subscription);

        if(queryType!=null)
            schema.query((GraphQLObjectType) queryType);
        if(mutationType!=null)
            schema.mutation((GraphQLObjectType) mutationType);
        if(subscriptionType!=null)
            schema.subscription((GraphQLObjectType) subscriptionType);


        schema.codeRegistry(code.build());

        return new AdaptedGraphQLSchema(schema.build() , unmodifiableTypes);

    }



    private final GraphQLType discover(MappedClass mappedClass , BuildingContextImpl context)
    {
        GraphQLType type = null;
        if(mappedClass.mappedType() == MappedClass.MappedType.OBJECT_TYPE)
        {

            type = StaticMethods.buildOutputObjectType(mappedClass , context);
            context.setGraphQLTypeFor(mappedClass , type);
            //setDiscoveredTypes !

        }else if(mappedClass.mappedType() == MappedClass.MappedType.INTERFACE)
        {
            type = StaticMethods.buildInterface(mappedClass , context);
            context.setGraphQLTypeFor(mappedClass , type);
        }else if(mappedClass.mappedType() == MappedClass.MappedType.INPUT_TYPE)
        {
            type = StaticMethods.buildInputObjectType(mappedClass , context);
            context.setGraphQLTypeFor(mappedClass , type);
        }else if(mappedClass.mappedType() == MappedClass.MappedType.ENUM)
        {
            type = StaticMethods.buildEnumType(mappedClass , context);
            context.setGraphQLTypeFor(mappedClass , type);
        }else if(mappedClass.mappedType().is(MappedClass.MappedType.QUERY) ||
                mappedClass.mappedType().is(MappedClass.MappedType.MUTATION) ||
                mappedClass.mappedType().is(MappedClass.MappedType.SUBSCRIPTION))
        {
            type = StaticMethods.buildOutputObjectType(mappedClass , context);
            context.setGraphQLTypeFor(mappedClass , type);
        }

        return type;
    }

    private final GraphQLType discoverUnion(MappedClass unionClass , List<MappedClass> possibles ,  BuildingContextImpl context)
    {
        GraphQLUnionType unionType =
                StaticMethods.buildUnionType(unionClass , possibles , context);

        context.setGraphQLTypeFor(unionClass , unionType);

        return unionType;
    }

    public DiscoveredType buildDiscoveredType(MappedClass cls , GraphQLType type)
    {
        if(cls.mappedType().is(MappedClass.MappedType.OBJECT_TYPE))
        {
            Assert.ifConditionTrue("conflict - mapped class mapped to bad type - [MappedClass:"+cls+
                    " , GraphQLType:"+type+"]" ,
                    !(type instanceof GraphQLObjectType));

            return new DiscoveredObjectTypeImpl(cls , type.getName() , (GraphQLObjectType)type);
        }else if(cls.mappedType().is(MappedClass.MappedType.INTERFACE))
        {
            Assert.ifConditionTrue("conflict - mapped class mapped to bad type - [MappedClass:"+cls+
                            " , GraphQLType:"+type+"]" ,
                    !(type instanceof GraphQLInterfaceType));

            return new DiscoveredInterfaceTypeImpl(cls , type.getName() , (GraphQLInterfaceType) type);
        }else if(cls.mappedType().is(MappedClass.MappedType.INPUT_TYPE))
        {
            Assert.ifConditionTrue("conflict - mapped class mapped to bad type - [MappedClass:"+cls+
                            " , GraphQLType:"+type+"]" ,
                    !(type instanceof GraphQLInputObjectType));

            return new DiscoveredInputTypeImpl(cls , type.getName() , (GraphQLInputObjectType)type);
        }else if(cls.mappedType().is(MappedClass.MappedType.UNION))
        {
            Assert.ifConditionTrue("conflict - mapped class mapped to bad type - [MappedClass:"+cls+
                            " , GraphQLType:"+type+"]" ,
                    !(type instanceof GraphQLUnionType));

            return new DiscoveredUnionTypeImpl(cls , type.getName() , (GraphQLUnionType)type);
        }else if(cls.mappedType().is(MappedClass.MappedType.ENUM))
        {
            Assert.ifConditionTrue("conflict - mapped class mapped to bad type - [MappedClass:"+cls+
                            " , GraphQLType:"+type+"]" ,
                    !(type instanceof GraphQLEnumType));

            return new DiscoveredEnumTypeImpl(cls , type.getName() , (GraphQLEnumType) type);
        }else if(cls.mappedType().isTopLevelType())
        {

            Assert.ifConditionTrue("conflict - mapped class mapped to bad type - [MappedClass:"+cls+
                            " , GraphQLType:"+type+"]" ,
                    !(type instanceof GraphQLObjectType));

            return new DiscoveredObjectTypeImpl(cls , type.getName() , (GraphQLObjectType) type);
        }


        throw new IllegalStateException();
    }


}
