package grphaqladapter.adaptedschemabuilder;

import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredInputType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredInterfaceType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredObjectType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;
import grphaqladapter.adaptedschemabuilder.mapper.ClassMapper;
import grphaqladapter.annotations.GraphqlType;
import grphaqladapter.codegenerator.DataFetcherGenerator;
import grphaqladapter.codegenerator.TypeResolverGenerator;
import grphaqladapter.codegenerator.impl.ReflectionDataFetcherGenerator;
import grphaqladapter.codegenerator.impl.SimpleTypeResolverGenerator;
import graphql.Scalars;
import graphql.schema.*;
import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;
import sun.awt.SunHints;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public final class AdaptedSchemaBuilder {




    private final static class BuildingContextImpl implements BuildingContext {


        private final static GraphQLScalarType findScalarTypeFor(Class c)
        {
            if(c == String.class)
            {

                return Scalars.GraphQLString;
            }else if(c == int.class || c == Integer.class)
            {
                return Scalars.GraphQLInt;
            }else if(c == long.class || c == Long.class)
            {
                return Scalars.GraphQLLong;
            }else if(c == float.class || c == Float.class)
            {
                return Scalars.GraphQLFloat;
            }else if(c == double.class || c == Double.class)
            {
                return Scalars.GraphQLBigDecimal;
            }else if(c == char.class || c == Character.class)
            {
                return Scalars.GraphQLChar;
            }else if(c == byte.class || c == Byte.class)
            {
                return Scalars.GraphQLByte;
            }else if(c == boolean.class || c == Boolean.class)
            {
                return Scalars.GraphQLBoolean;
            }else if(c == short.class || c == Short.class)
            {
                return Scalars.GraphQLShort;
            }

            return null;

        }

        private final Map<Class , Map<MappedClass.MappedType , MappedClass>> mappedClasses ;
        private final Map<MappedClass , GraphQLType> rawTypes = new HashMap<>();
        private final Map<MappedClass , List<MappedClass>> possibleTypes
                = new HashMap<>();


        BuildingContextImpl(Map<Class , Map<MappedClass.MappedType , MappedClass>> mcs)
        {

            mappedClasses = mcs;
        }




        @Override
        public GraphQLTypeReference getInputTypeFor(Class c)
        {
            if(findScalarTypeFor(c)!=null)
                return new GraphQLTypeReference(findScalarTypeFor(c).getName());
            return new GraphQLTypeReference(getMappedClassFor(c ,
                    MappedClass.MappedType.INPUT_TYPE).
                    typeName());
        }



        @Override
        public GraphQLTypeReference getObjectTypeFor(Class c)
        {

            if(findScalarTypeFor(c)!=null)
                return new GraphQLTypeReference(findScalarTypeFor(c).getName());

            return new GraphQLTypeReference(
                    getMappedClassFor(c , MappedClass.MappedType.OBJECT_TYPE)
                            .typeName()
            );
        }


        @Override
        public GraphQLTypeReference getInterfaceFor(Class c)
        {

            return new GraphQLTypeReference(getMappedClassFor(c ,
                    MappedClass.MappedType.INTERFACE).typeName());
        }

        @Override
        public GraphQLTypeReference getEnumFor(Class c)
        {
            return new GraphQLTypeReference(getMappedClassFor(c ,
                    MappedClass.MappedType.ENUM).typeName());
        }

        @Override
        public boolean isAnInterface(Class cls)
        {
            return mappedClasses.get(cls)!=null &&
                    mappedClasses.get(cls).get(MappedClass.MappedType.INTERFACE)!=null;
        }

        @Override
        public boolean isAnUnion(Class cls) {
            return mappedClasses.get(cls)!=null &&
                    mappedClasses.get(cls).get(MappedClass.MappedType.UNION)!=null;
        }

        @Override
        public void addToPossibleTypesOf(MappedClass mappedClass, MappedClass possible) {

            if(mappedClass.mappedType()!= MappedClass.MappedType.INTERFACE &&
                    mappedClass.mappedType()!= MappedClass.MappedType.UNION){
                throw new IllegalStateException("just interfaces or union types have possible types");
            }
            if(mappedClasses.get(mappedClass.baseClass())
                    .get(mappedClass.mappedType())!=mappedClass)
            {
                throw new IllegalStateException("this class dose not related to this context");
            }

            List<MappedClass> list = possibleTypes.get(mappedClass);

            if(list==null)
            {
                list = new ArrayList<>();
                possibleTypes.put(mappedClass , list);
            }

            list.add(possible);
        }

        @Override
        public MappedClass getMappedClassFor(Class cls, MappedClass.MappedType mappedType) {
            if(mappedClasses.get(cls)==null)
                return null;

            return mappedClasses.get(cls).get(mappedType);
        }

        public void setGraphQLTypeFor(MappedClass cls , GraphQLType type)
        {

            if(rawTypes.containsKey(cls))
                assertClassDiscoveredMultipleTimes(cls);

            rawTypes.put(cls , type);
        }
    }




    public final static AdaptedSchemaBuilder newBuilder()
    {
        return new AdaptedSchemaBuilder();
    }

    private final List<Class> allClasses = new ArrayList<>();
    private DataFetcherGenerator dataFetcherGenerator;
    private TypeResolverGenerator typeResolverGenerator;
    private final ClassMapper mapper;

    private AdaptedSchemaBuilder(){
        dataFetcherGenerator = new ReflectionDataFetcherGenerator();
        typeResolverGenerator = new SimpleTypeResolverGenerator();
        mapper = new ClassMapper();
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
        if(generator==null)
            throw new IllegalStateException("code generators can not be null");

        typeResolverGenerator = generator;
        return this;
    }

    public synchronized AdaptedSchemaBuilder generator(DataFetcherGenerator generator)
    {
        if(generator==null)
            throw new IllegalStateException("code generators can not be null");


        dataFetcherGenerator = generator;
        return this;
    }

    public synchronized AdaptedSchemaBuilder clearClasses()
    {
        allClasses.clear();
        return this;
    }

    public synchronized AdaptedGraphQLSchema build()
    {
        GraphQLSchema.Builder schema = GraphQLSchema.newSchema();




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


        final BuildingContextImpl context =
                new BuildingContextImpl(mappedClasses);



        for(Map<MappedClass.MappedType , MappedClass> map:mappedClasses.values()) {
            for (MappedClass mappedClass : map.values())
            {
                if(mappedClass.mappedType().is(MappedClass.MappedType.UNION))
                    continue;

                discover(mappedClass , context);
            }
        }


        for(Map<MappedClass.MappedType , MappedClass> map:mappedClasses.values()) {
            for (MappedClass mappedClass : map.values())
            {
                if(mappedClass.mappedType().is(MappedClass.MappedType.UNION))
                {
                    discoverUnion(mappedClass , context.possibleTypes.get(mappedClass)
                            , context);

                }
            }
        }

        Map<MappedClass , DiscoveredType> discoveredTypes = new HashMap<>();
        //so put everything in !
        for(MappedClass mappedClass : context.rawTypes.keySet())
        {

            GraphQLType type = context.rawTypes.get(mappedClass);
            discoveredTypes.put(mappedClass , buildDiscoveredType(mappedClass , type));
        }

        GraphQLCodeRegistry.Builder code = GraphQLCodeRegistry.newCodeRegistry();

        for(DiscoveredType discoveredType:discoveredTypes.values())
        {
            if(discoveredType.asMappedClass().mappedType().
                    is(MappedClass.MappedType.INTERFACE))
            {
                DiscoveredInterfaceTypeImpl interfaceType = (DiscoveredInterfaceTypeImpl)discoveredType;

                List<MappedClass> implementors = context.possibleTypes.get(discoveredType.asMappedClass());

                if(implementors!=null)
                {
                    for(MappedClass mappedClass:implementors)
                    {
                        interfaceType.implementors().add((DiscoveredObjectType)
                                discoveredTypes.get(mappedClass));
                    }
                }

                interfaceType.setUnmodifiable();

                code.typeResolver(interfaceType.typeName() ,
                        typeResolverGenerator.generate(interfaceType));
            }


            if(discoveredType.asMappedClass().mappedType().
                    is(MappedClass.MappedType.UNION))
            {
                DiscoveredUnionTypeImpl unionType = (DiscoveredUnionTypeImpl)discoveredType;

                List<MappedClass> possiableTypes = context.possibleTypes.get(discoveredType.asMappedClass());

                if(possiableTypes!=null)
                {
                    for(MappedClass mappedClass:possiableTypes)
                    {
                        unionType.possibleTypes().add((DiscoveredObjectType)
                                discoveredTypes.get(mappedClass));
                    }
                }

                unionType.setUnmodifiable();

                code.typeResolver(unionType.typeName() ,
                        typeResolverGenerator.generate(unionType));
            }
        }

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

        dataFetcherGenerator.init(inputTypes);
        allTypes.stream().forEach(d -> {

            MappedClass mappedClass = d.asMappedClass();
            for(MappedMethod method:mappedClass.mappedMethods().values())
            {
                code.dataFetcher(FieldCoordinates.coordinates(
                        mappedClass.typeName() ,
                        method.fieldName()
                ) , dataFetcherGenerator.generate(mappedClass , method));
            }

        });

        GraphQLType queryType = query==null?null:context.rawTypes.get(query);
        GraphQLType mutationType = mutation==null?null:context.rawTypes.get(mutation);
        GraphQLType subscriptionType = subscription==null?null:context.rawTypes.get(subscription);

        if(queryType!=null)
            schema.query((GraphQLObjectType) queryType);
        if(mutationType!=null)
            schema.mutation((GraphQLObjectType) mutationType);
        if(subscriptionType!=null)
            schema.subscription((GraphQLObjectType) subscriptionType);


        schema.codeRegistry(code.build());

        return new AdaptedGraphQLSchema(schema.build() , allTypes);

    }

    private final static void assertClassDiscoveredMultipleTimes(MappedClass cls)
    {
        throw new IllegalStateException("class ["+cls.baseClass()+"] discovered multiple times for ["+cls.mappedType()+" - "+cls.typeName()+"]");
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
