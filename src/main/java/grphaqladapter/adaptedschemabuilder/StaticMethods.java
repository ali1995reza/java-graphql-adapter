package grphaqladapter.adaptedschemabuilder;



import graphql.schema.*;
import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;
import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedParameter;
import grphaqladapter.adaptedschemabuilder.scalar.ScalarEntry;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticMethods {



    private final static GraphQLList createList(GraphQLTypeReference type , int dims)
    {
        if(dims<1)
            throw new IllegalStateException("can not create a list with dimensions <1");

        GraphQLList  list = new GraphQLList(type);
        --dims;
        for(int i=0;i<dims;i++)
        {
            list = new GraphQLList(list);
        }


        return list;
    }




    public static final GraphQLArgument buildArgument(MappedParameter parameter , BuildingContext context)
    {
        //so handle it please !
        GraphQLArgument.Builder argument = GraphQLArgument.newArgument();
        argument.name(parameter.argumentName());
        GraphQLTypeReference inputType = context.getInputTypeFor(parameter.type());

        Assert.ifNull(inputType , "provided input type for ["+parameter.type()+"] is null");


        argument.type(
                parameter.isNullable()?
                        (parameter.isList()?
                                (createList(inputType , parameter.dimensions())):inputType)
                        :
                        new GraphQLNonNull((parameter.isList()?
                                (createList(inputType , parameter.dimensions())):inputType))
        );

        return argument.build();
    }


    public static final GraphQLFieldDefinition buildField(MappedMethod method , BuildingContext context)
    {
        //so handle it please !
        GraphQLFieldDefinition.Builder definition = GraphQLFieldDefinition.newFieldDefinition();
        definition.name(method.fieldName());
        GraphQLTypeReference outputType = context.geOutputTypeFor(method.type());
        Assert.ifNull(outputType , "provided output type for ["+method.type()+"] is null");



        definition.type(
                method.isNullable()?
                        (method.isList()?
                                (createList(outputType , method.dimensions())):outputType)
                        :
                        new GraphQLNonNull((method.isList()?
                                (createList(outputType , method.dimensions())):outputType))
        );


        for(MappedParameter parameter:method.parameters())
        {

            definition.argument(buildArgument(parameter , context));
        }


        return definition.build();
    }



    public static final GraphQLInputObjectField buildInputField(MappedMethod method , BuildingContext context)
    {
        if(method.method().getParameterCount()>0)
        {
            throw new IllegalStateException("an InputField mapped method can not contains parameters - [method"
                    +method.method()+"]");
        }

        GraphQLInputObjectField.Builder field =
                GraphQLInputObjectField.newInputObjectField();

        field.name(method.fieldName());

        GraphQLTypeReference inputType = context.getInputTypeFor(method.type());



        field.type(
                method.isNullable()?
                        (method.isList()?
                                (createList(inputType , method.dimensions())):inputType)
                        :
                        new GraphQLNonNull((method.isList()?
            (createList(inputType , method.dimensions())):inputType))
        );

        return field.build();
    }



    public static final GraphQLInterfaceType buildInterface(MappedClass mappedClass , BuildingContext context)
    {
        if(mappedClass.mappedType()!= MappedClass.MappedType.INTERFACE)
            throw new IllegalStateException("mapped class did not map to interface");

        GraphQLInterfaceType.Builder interfaceType =
                GraphQLInterfaceType.newInterface();

        interfaceType.name(mappedClass.typeName());


        for(MappedMethod method:mappedClass.mappedMethods().values())
        {
            interfaceType.field(buildField(method , context));

        }


        return interfaceType.build();
    }

    public static final GraphQLInputObjectType buildInputObjectType(MappedClass mappedClass , BuildingContext context)
    {
        if(mappedClass.mappedType()!= MappedClass.MappedType.INPUT_TYPE)
            throw new IllegalStateException("mapped class did not map to InputType");

        GraphQLInputObjectType.Builder inputObjectType =
                GraphQLInputObjectType.newInputObject();

        inputObjectType.name(mappedClass.typeName());


        for(MappedMethod method:mappedClass.mappedMethods().values())
        {
            inputObjectType.field(buildInputField(method , context));
        }

        return inputObjectType.build();
    }

    public static final GraphQLObjectType buildOutputObjectType(MappedClass mappedClass , BuildingContext context)
    {
        if(mappedClass.mappedType()!= MappedClass.MappedType.OBJECT_TYPE && !mappedClass.mappedType().isTopLevelType())
            throw new IllegalStateException("mapped class did not map to OutputObjectType");

        GraphQLObjectType.Builder outputObjectType =
                GraphQLObjectType.newObject();

        outputObjectType.name(mappedClass.typeName());


        for(MappedMethod method:mappedClass.mappedMethods().values())
        {
            outputObjectType.field(buildField(method , context));
        }

        for(Class inter : mappedClass.baseClass().getInterfaces())
        {
            if(context.isAnInterface(inter))
            {

                outputObjectType.withInterface(context.getInterfaceFor(inter));
                context.addToPossibleTypesOf(
                        context.getMappedClassFor(inter , MappedClass.MappedType.INTERFACE),
                        mappedClass
                );

            }else if(context.isAnUnion(inter))
            {
                context.addToPossibleTypesOf(
                        context.getMappedClassFor(inter , MappedClass.MappedType.UNION),
                        mappedClass
                );
            }
        }


        return outputObjectType.build();
    }


    private final static Map<String , Object> findEnumValues(Class cls)
    {
        if(!cls.isEnum())
            throw new IllegalStateException("can not find enum values of none-enum class - [class:"
                    +cls+"]");


        Map<String , Object> objects = new HashMap<>();
        final Object[] enums = cls.getEnumConstants();
        int count = 0;
        for(Field field:cls.getDeclaredFields())
        {
            if(Modifier.isStatic(field.getModifiers())
                    && Modifier.isPublic(field.getModifiers())
                    && field.getType() == cls)
            {
                Object o = null;
                try {

                    o = field.get(null);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }

                boolean found = false;

                for(Object en:enums)
                {
                    if(o==en)
                    {
                        found = true;
                        count++;
                        break;
                    }
                }

                if(found)
                {
                    objects.put(field.getName() , o);
                }

            }
        }

        if(count!=enums.length)
        {
            throw new IllegalStateException("can not discover all enum values - [class:"+cls+"]");
        }

        return objects;
    }


    public static final GraphQLEnumType buildEnumType(MappedClass mappedClass , BuildingContext context)
    {
        if(mappedClass.mappedType()!= MappedClass.MappedType.ENUM)
            throw new IllegalStateException("mapped class did not map to EnumType");

        GraphQLEnumType.Builder enumType =
                GraphQLEnumType.newEnum();
        enumType.name(mappedClass.typeName());

        Map<String , Object> enums = findEnumValues(mappedClass.baseClass());

        for(String s:enums.keySet())
        {
            enumType.value(s , enums.get(s));
        }


        return enumType.build();
    }

    public static final GraphQLUnionType buildUnionType(MappedClass unionMappedClass , List<MappedClass> possibles , BuildingContext context)
    {

        GraphQLUnionType.Builder unionType =
                GraphQLUnionType.newUnionType();

        if(!unionMappedClass.mappedType().is(MappedClass.MappedType.UNION))
            throw new IllegalStateException("mapped class not mapped to union type");

        unionType.name(unionMappedClass.typeName());
        for(MappedClass possible:possibles) {
            unionType.possibleType(
                    context.getObjectTypeFor(possible.baseClass())
            );
        }

        return unionType.build();
    }


    public static final GraphQLScalarType buildScalarType(ScalarEntry entry)
    {
        return GraphQLScalarType.newScalar()
                .name(entry.name())
                .coercing(entry.coercing())
                .build();

    }
}
