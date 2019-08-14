package grphaqladapter.adaptedschemabuilder;

import graphql.Scalars;
import graphql.schema.*;
import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.builtinscalars.ID;
import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

final class BuildingContextImpl implements BuildingContext {


    private final static class ScalarAddController{
        private final Function<GraphQLScalarType, GraphQLScalarType> controller =
                new Function<GraphQLScalarType, GraphQLScalarType>() {
                    @Override
                    public GraphQLScalarType apply(GraphQLScalarType type) {
                        schema.additionalType(type);
                        return type;
                    }
                };


        private final HashMap<GraphQLScalarType , GraphQLScalarType> scalarControllerMap;
        private final GraphQLSchema.Builder schema;
        private ScalarAddController(GraphQLSchema.Builder builder)
        {
            schema = builder;
            scalarControllerMap = new HashMap<>();
        }
        private  GraphQLScalarType findScalarTypeFor(Class c)
        {

            if(c == String.class)
            {
                return scalarControllerMap.computeIfAbsent(Scalars.GraphQLString ,
                        controller);
            }else if(c == int.class || c == Integer.class)
            {
                return scalarControllerMap.computeIfAbsent(Scalars.GraphQLInt ,
                        controller);
            }else if(c == long.class || c == Long.class)
            {
                return scalarControllerMap.computeIfAbsent(Scalars.GraphQLLong ,
                        controller);
            }else if(c == float.class || c == Float.class)
            {
                return scalarControllerMap.computeIfAbsent(Scalars.GraphQLFloat ,
                        controller);
            }else if(c == double.class || c == Double.class)
            {
                return scalarControllerMap.computeIfAbsent(Scalars.GraphQLBigDecimal ,
                        controller);
            }else if(c == char.class || c == Character.class)
            {
                return scalarControllerMap.computeIfAbsent(Scalars.GraphQLChar ,
                        controller);
            }else if(c == byte.class || c == Byte.class)
            {
                return scalarControllerMap.computeIfAbsent(Scalars.GraphQLByte ,
                        controller);
            }else if(c == boolean.class || c == Boolean.class)
            {
                return scalarControllerMap.computeIfAbsent(Scalars.GraphQLBoolean ,
                        controller);
            }else if(c == short.class || c == Short.class)
            {
                return scalarControllerMap.computeIfAbsent(Scalars.GraphQLShort ,
                        controller);
            }else if(c == ID.class)
            {
                return scalarControllerMap.computeIfAbsent(ID.ScalarType , controller);
            }

            return null;

        }

    }




    private final Map<Class , Map<MappedClass.MappedType , MappedClass>> mappedClasses ;
    private final Map<MappedClass , GraphQLType> rawTypes;
    private final Map<MappedClass , List<MappedClass>> possibleTypes;
    private final GraphQLSchema.Builder schemaBuilder;
    private final ScalarAddController scalarAddController;


    BuildingContextImpl(Map<Class, Map<MappedClass.MappedType, MappedClass>> mcs,
                        GraphQLSchema.Builder schemaBuilder)
    {
        Assert.ifNull(mcs , "mapped classes is null");
        Assert.ifNull(schemaBuilder , "provided schema builder is null");
        mappedClasses = mcs;
        this.schemaBuilder = schemaBuilder;
        rawTypes = new HashMap<>();
        possibleTypes = new HashMap<>();
        scalarAddController = new ScalarAddController(schemaBuilder);
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
        GraphQLTypeReference reference = getScalarTypeFor(c);
        if(reference!=null)return reference;
        reference = getInputObjectTypeFor(c);
        if(reference!=null)return reference;
        return getEnumFor(c);
    }

    @Override
    public GraphQLTypeReference geOutputTypeFor(Class c) {
        GraphQLTypeReference reference = getScalarTypeFor(c);
        if(reference!=null)return reference;
        reference = getObjectTypeFor(c);
        if(reference!=null)return reference;
        reference = getEnumFor(c);
        if(reference!=null)return reference;
        reference = getInterfaceFor(c);
        if(reference!=null)return reference;

        return null;
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
}
