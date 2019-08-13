package grphaqladapter.adaptedschemabuilder;

import graphql.language.TypeDefinition;
import graphql.schema.*;
import grphaqladapter.annotations.GraphqlInputType;
import grphaqladapter.annotations.GraphqlType;

import java.util.Arrays;
import java.util.List;

public class SDLStatic {

    private final static List<String> BuiltInTypes = Arrays.asList(new String[]{
            "__Directive" , "__DirectiveLocation" , "__EnumValue" , "__Field" ,
            "__InputValue" , "__Schema" , "__Type" , "__TypeKind"

    });



    private final static String DOUBLE_DOT = " : ";
    private final static char NONE_NULL = '!';
    private final static char START_LIST = '[';
    private final static char END_LIST = ']';
    private final static char ARGUMENT_START = '(';
    private final static char ARGUMENT_END = ')';
    private final static char SPACE = ' ';
    private final static char STRUCTURE_START = '{';
    private final static char STRUCTURE_END = '}';
    private final static char UNION_TYPE_SEPARATOR = '|';
    private final static char AND = '&';
    private final static String IMPLEMENT_KEY_WORD = "implements";
    private final static String ARGUMENT_SEPARATOR = " , ";
    private final static String NEW_LINE = System.lineSeparator();
    private final static String INNER_OFFSET = "     ";
    private final static String INPUT_TYPE_SIGNATURE = "input";
    private final static String OBJECT_TYPE_SIGNATURE = "type";
    private final static String INTERFACE_SIGNATURE = "interface";
    private final static String ENUM_SIGNATURE = "enum";
    private final static String UNION_SIGNATURE = "union";
    private final static String EQUAL = " = ";
    private final static String SCALAR_SIGNATURE = "scalar";

    private final static String getTypeAsString(GraphQLType type)
    {
        StringBuffer buffer = new StringBuffer();
        getTypeAsString(type , buffer);
        return buffer.toString();
    }

    private final static void getTypeAsString(GraphQLType type , final StringBuffer buffer)
    {
        if(type instanceof GraphQLNonNull)
        {
            //so has an inner
            getTypeAsString(((GraphQLNonNull)type).getWrappedType() , buffer);
            buffer.append(NONE_NULL);
        }else if(type instanceof GraphQLList)
        {
            //so handle it please !
            getTypeAsString(((GraphQLList)type).getWrappedType() , buffer);
            buffer.insert(0 , START_LIST);
            buffer.append(END_LIST);
        }else{

            buffer.append(type.getName());
        }
    }

    public final static String from(GraphQLArgument argument)
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(argument.getName());
        buffer.append(DOUBLE_DOT);
        buffer.append(getTypeAsString(argument.getType()));
        return buffer.toString();
    }

    public final static String from(GraphQLFieldDefinition field)
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(field.getName());
        if(field.getArguments().size()>0)
        {
            buffer.append(ARGUMENT_START);
            for(int i=0;i<field.getArguments().size();i++)
            {
                GraphQLArgument argument = field.getArguments().get(i);
                buffer.append(from(argument));

                if(i!=field.getArguments().size()-1)
                {
                    buffer.append(ARGUMENT_SEPARATOR);
                }
            }
            buffer.append(ARGUMENT_END);
        }

        buffer.append(DOUBLE_DOT);
        buffer.append(getTypeAsString(field.getType()));

        return buffer.toString();
    }
    public final static String from(GraphQLInputObjectField field)
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(field.getName());

        buffer.append(DOUBLE_DOT);
        buffer.append(getTypeAsString(field.getType()));

        return buffer.toString();
    }

    public final static String from(GraphQLType type)
    {
        StringBuffer buffer =  new StringBuffer();
        if(type instanceof GraphQLScalarType)
        {
            buffer.append(SCALAR_SIGNATURE);
            buffer.append(SPACE);
            buffer.append(type.getName());
        }else if(type instanceof GraphQLInputObjectType)
        {
            GraphQLInputObjectType inputType = (GraphQLInputObjectType) type;
            buffer.append(INPUT_TYPE_SIGNATURE);
            buffer.append(SPACE);
            buffer.append(inputType.getName());
            buffer.append(SPACE);
            buffer.append(STRUCTURE_START);
            buffer.append(NEW_LINE);
            for(GraphQLInputObjectField field:inputType.getFields())
            {
                buffer.append(INNER_OFFSET);
                buffer.append(from(field));
                buffer.append(NEW_LINE);
            }
            buffer.append(STRUCTURE_END);
        }else if(type instanceof GraphQLObjectType)
        {
            GraphQLObjectType objectType = (GraphQLObjectType) type;
            buffer.append(OBJECT_TYPE_SIGNATURE);
            buffer.append(SPACE);
            buffer.append(objectType.getName());

            if(objectType.getInterfaces().size()>0)
            {
                buffer.append(SPACE)
                        .append(IMPLEMENT_KEY_WORD)
                        .append(SPACE);
            }

            int numberOfInterfaces = objectType.getInterfaces().size();

            for(int i=0;i<numberOfInterfaces;i++)
            {
                GraphQLOutputType interfaceType =
                        objectType.getInterfaces().get(i);
                buffer.append(interfaceType.getName());
                if(i!=numberOfInterfaces-1)
                {
                    buffer.append(SPACE).append(AND).append(SPACE);
                }
            }


            buffer.append(SPACE);
            buffer.append(STRUCTURE_START);
            buffer.append(NEW_LINE);
            for(GraphQLFieldDefinition field:objectType.getFieldDefinitions())
            {
                buffer.append(INNER_OFFSET);
                buffer.append(from(field));
                buffer.append(NEW_LINE);
            }
            buffer.append(STRUCTURE_END);
        }else if(type instanceof GraphQLInterfaceType)
        {
            GraphQLInterfaceType interfaceType = (GraphQLInterfaceType) type;
            buffer.append(INTERFACE_SIGNATURE);
            buffer.append(SPACE);
            buffer.append(interfaceType.getName());
            buffer.append(SPACE);
            buffer.append(STRUCTURE_START);
            buffer.append(NEW_LINE);
            for(GraphQLFieldDefinition field:interfaceType.getFieldDefinitions())
            {
                buffer.append(INNER_OFFSET);
                buffer.append(from(field));
                buffer.append(NEW_LINE);
            }
            buffer.append(STRUCTURE_END);
        }else if(type instanceof GraphQLEnumType)
        {
            GraphQLEnumType enumType = (GraphQLEnumType) type;
            buffer.append(ENUM_SIGNATURE);
            buffer.append(SPACE);
            buffer.append(enumType.getName());
            buffer.append(SPACE);
            buffer.append(STRUCTURE_START);
            buffer.append(NEW_LINE);
            for(GraphQLEnumValueDefinition value:enumType.getValues())
            {
                buffer.append(INNER_OFFSET);
                buffer.append(value.getName());
                buffer.append(NEW_LINE);
            }
            buffer.append(STRUCTURE_END);
        }else if(type instanceof GraphQLUnionType)
        {
            GraphQLUnionType unionType = (GraphQLUnionType) type;
            buffer.append(UNION_SIGNATURE);
            buffer.append(SPACE);
            buffer.append(unionType.getName());
            buffer.append(EQUAL);

            int numberOfPossibleTypes = unionType.getTypes().size();

            for(int i=0;i<numberOfPossibleTypes;i++)
            {
                GraphQLOutputType outputType = unionType.getTypes().get(i);
                buffer.append(outputType.getName());
                if(i!=numberOfPossibleTypes-1)
                {
                    buffer.append(SPACE)
                            .append(UNION_TYPE_SEPARATOR)
                            .append(SPACE);
                }
            }
        }else
        {
            throw new IllegalStateException("unknown type : "+type);
        }


        return buffer.toString();
    }


    public final static String from(GraphQLSchema schema)
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append(NEW_LINE).append(NEW_LINE);

        for(GraphQLType type:schema.getAllTypesAsList())
        {
            if(BuiltInTypes.contains(type.getName()))
                continue;
            buffer.append(from(type)).append(NEW_LINE).append(NEW_LINE);
        }

        return buffer.toString();
    }


}
