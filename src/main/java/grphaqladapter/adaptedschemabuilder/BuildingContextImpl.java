package grphaqladapter.adaptedschemabuilder;

import graphql.Scalars;
import graphql.schema.*;
import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.builtinscalars.ID;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredScalarType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;
import grphaqladapter.adaptedschemabuilder.scalar.ScalarEntry;
import grphaqladapter.adaptedschemabuilder.utils.Utils;
import grphaqladapter.annotations.GraphqlSubscription;

import java.util.*;
import java.util.function.Function;

final class BuildingContextImpl implements BuildingContext {


    private final static class ScalarAddController{

        private final static Map<Class , GraphQLScalarType> BuiltInScalars;

        static {
            HashMap<Class , GraphQLScalarType> scalars = new HashMap<>();
            scalars.put(String.class , Scalars.GraphQLString);
            scalars.put(Integer.class , Scalars.GraphQLInt);
            scalars.put(int.class , Scalars.GraphQLInt);
            scalars.put(Long.class , Scalars.GraphQLLong);
            scalars.put(long.class , Scalars.GraphQLLong);
            scalars.put(Double.class , Scalars.GraphQLBigDecimal);
            scalars.put(double.class , Scalars.GraphQLBigDecimal);
            scalars.put(Float.class , Scalars.GraphQLFloat);
            scalars.put(float.class , Scalars.GraphQLFloat);
            scalars.put(Boolean.class , Scalars.GraphQLBoolean);
            scalars.put(boolean.class , Scalars.GraphQLBoolean);
            scalars.put(Character.class , Scalars.GraphQLChar);
            scalars.put(char.class , Scalars.GraphQLChar);
            scalars.put(Byte.class , Scalars.GraphQLByte);
            scalars.put(byte.class , Scalars.GraphQLByte);
            scalars.put(Short.class , Scalars.GraphQLShort);
            scalars.put(short.class , Scalars.GraphQLShort);
            scalars.put(ID.class , ID.ScalarType);



            BuiltInScalars = Collections.unmodifiableMap(scalars);

        }


        private final Map<Class , GraphQLScalarType> scalars;
        private final GraphQLSchema.Builder schema;
        private final List<DiscoveredScalarType> discoveredScalarTypes;
        private final Set<GraphQLScalarType> addedScalars;

        private ScalarAddController(GraphQLSchema.Builder builder , Map<Class , GraphQLScalarType> providedScalars)
        {
            schema = builder;
            HashMap<Class , GraphQLScalarType> scalars = new HashMap<>();
            for(Class cls:providedScalars.keySet())
            {
                GraphQLScalarType scalarType = providedScalars.get(cls);
                scalars.put(cls , scalarType);
            }

            for(Class cls:BuiltInScalars.keySet())
            {
                scalars.putIfAbsent(cls , BuiltInScalars.get(cls));
            }

            this.scalars = Collections.unmodifiableMap(scalars);

            discoveredScalarTypes = new ArrayList<>();

            addedScalars = new HashSet<>();

        }
        private  GraphQLScalarType findScalarTypeFor(Class c)
        {

            GraphQLScalarType scalarType =  scalars.get(c);

            if(scalarType!=null && !addedScalars.contains(scalarType))
            {
                schema.additionalType(scalarType);
                addedScalars.add(scalarType);
                discoveredScalarTypes.add(
                        new DiscoveredScalarTypeImpl(
                                c ,
                                scalarType.getName() ,
                                scalarType
                        )
                );
            }

            return scalarType;
        }

    }




    private final Map<Class , Map<MappedClass.MappedType , MappedClass>> mappedClasses ;
    private final Map<MappedClass , GraphQLType> rawTypes;
    private final Map<MappedClass , List<MappedClass>> possibleTypes;
    private final ScalarAddController scalarAddController;


    BuildingContextImpl(Map<Class, Map<MappedClass.MappedType, MappedClass>> mcs,
                        GraphQLSchema.Builder schemaBuilder ,
                        Map<Class , GraphQLScalarType> providedScalars)
    {
        Assert.ifNull(mcs , "mapped classes is null");
        Assert.ifNull(schemaBuilder , "provided schema builder is null");
        mappedClasses = mcs;
        rawTypes = new HashMap<>();
        possibleTypes = new HashMap<>();
        scalarAddController = new ScalarAddController(schemaBuilder , providedScalars);
    }




    @Override
    public GraphQLTypeReference getInputObjectTypeFor(Class c)
    {

        MappedClass mappedClass =
                getMappedClassFor(c , MappedClass.MappedType.INPUT_TYPE);
        return mappedClass==null?null:new GraphQLTypeReference(mappedClass.typeName());
    }

    @Override
    public GraphQLTypeReference getInputTypeFor(Class c) {
        GraphQLTypeReference reference = getInputObjectTypeFor(c);
        if(reference!=null)return reference;
        reference = getEnumFor(c);
        if(reference!=null)return reference;
        reference = getScalarTypeFor(c);

        return reference;
    }

    @Override
    public GraphQLTypeReference geOutputTypeFor(Class c) {
        GraphQLTypeReference reference = getObjectTypeFor(c);
        if(reference!=null)return reference;
        reference = getEnumFor(c);
        if(reference!=null)return reference;
        reference = getInterfaceFor(c);
        if(reference!=null)return reference;
        reference = getScalarTypeFor(c);

        return reference;
    }

    @Override
    public GraphQLTypeReference getScalarTypeFor(Class c) {
        GraphQLScalarType scalarType =
                scalarAddController.findScalarTypeFor(c);
        return scalarType==null?null:new GraphQLTypeReference(scalarType.getName());
    }


    @Override
    public GraphQLTypeReference getObjectTypeFor(Class c)
    {
        MappedClass mappedClass =
                getMappedClassFor(c , MappedClass.MappedType.OBJECT_TYPE);
        return mappedClass==null?null:new GraphQLTypeReference(mappedClass.typeName());
    }

    public GraphQLType rawTypeOf(MappedClass mappedClass)
    {
        return rawTypes.get(mappedClass);
    }

    public List<MappedClass> possibleTypesOf(MappedClass mappedClass)
    {
        return possibleTypes.get(mappedClass);
    }

    public Map<MappedClass, GraphQLType> rawTypes() {
        return rawTypes;
    }

    @Override
    public GraphQLTypeReference getInterfaceFor(Class c)
    {
        MappedClass mappedClass =
                getMappedClassFor(c , MappedClass.MappedType.INTERFACE);


        return mappedClass==null?null:new GraphQLTypeReference(mappedClass.typeName());
    }

    @Override
    public GraphQLTypeReference getEnumFor(Class c)
    {
        MappedClass mappedClass =
                getMappedClassFor(c , MappedClass.MappedType.ENUM);

        return mappedClass==null?null:new GraphQLTypeReference(mappedClass.typeName());
    }

    @Override
    public GraphQLTypeReference getUnionTypeFor(Class c) {
        MappedClass mappedClass =
                getMappedClassFor(c , MappedClass.MappedType.UNION);
        return mappedClass==null?null:new GraphQLTypeReference(mappedClass.typeName());
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

        Assert.ifConditionTrue("class ["+cls+"] discovered multiple times",
                rawTypes.containsKey(cls));

        rawTypes.put(cls , type);
    }

    public List<DiscoveredScalarType> allScalars()
    {
        return scalarAddController.discoveredScalarTypes;
    }
}
