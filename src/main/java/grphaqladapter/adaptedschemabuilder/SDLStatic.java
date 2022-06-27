package grphaqladapter.adaptedschemabuilder;

import graphql.schema.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SDLStatic {

    private final static List<String> BuiltInTypes = Arrays.asList("__Directive", "__DirectiveLocation", "__EnumValue", "__Field",
            "__InputValue", "__Schema", "__Type", "__TypeKind");


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
    private final static char DOUBLE_QUOTATION = '"';
    private final static String IMPLEMENT_KEY_WORD = "implements";
    private final static String ARGUMENT_SEPARATOR = " , ";
    private final static String NEW_LINE = System.lineSeparator();
    private final static String END_OF_LINE = "\n";
    private final static String INNER_OFFSET = "     ";
    private final static String INPUT_TYPE_SIGNATURE = "input";
    private final static String OBJECT_TYPE_SIGNATURE = "type";
    private final static String INTERFACE_SIGNATURE = "interface";
    private final static String ENUM_SIGNATURE = "enum";
    private final static String UNION_SIGNATURE = "union";
    private final static String EQUAL = " = ";
    private final static String SCALAR_SIGNATURE = "scalar";
    private final static String THREE_DOUBLE_QUOTATION = "\"\"\"";
    private final static String EMPTY = "";

    private static String getTypeAsString(GraphQLType type) {
        StringBuffer buffer = new StringBuffer();
        getTypeAsString(type, buffer);
        return buffer.toString();
    }

    private static void getTypeAsString(GraphQLType type, final StringBuffer buffer) {
        if (type instanceof GraphQLNonNull) {
            //so has an inner
            getTypeAsString(((GraphQLNonNull) type).getWrappedType(), buffer);
            buffer.append(NONE_NULL);
        } else if (type instanceof GraphQLList) {
            //so handle it please !
            getTypeAsString(((GraphQLList) type).getWrappedType(), buffer);
            buffer.insert(0, START_LIST);
            buffer.append(END_LIST);
        } else {

            buffer.append(((GraphQLNamedType) type).getName());
        }
    }

    public static String from(GraphQLArgument argument) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(argument.getName());
        buffer.append(DOUBLE_DOT);
        buffer.append(getTypeAsString(argument.getType()));
        return buffer.toString();
    }

    public static String from(GraphQLFieldDefinition field) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(field.getName());
        if (field.getArguments().size() > 0) {
            buffer.append(ARGUMENT_START);
            for (int i = 0; i < field.getArguments().size(); i++) {
                GraphQLArgument argument = field.getArguments().get(i);
                buffer.append(from(argument));

                if (i != field.getArguments().size() - 1) {
                    buffer.append(ARGUMENT_SEPARATOR);
                }
            }
            buffer.append(ARGUMENT_END);
        }

        buffer.append(DOUBLE_DOT);

        buffer.append(getTypeAsString(field.getType()));

        return buffer.toString();
    }

    public static String from(GraphQLInputObjectField field) {
        StringBuffer buffer = new StringBuffer();

        buffer.append(field.getName());
        buffer.append(DOUBLE_DOT);
        buffer.append(getTypeAsString(field.getType()));

        return buffer.toString();
    }

    public static String from(GraphQLNamedType type) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(fromDescription(type.getDescription()));
        if (type instanceof GraphQLScalarType) {
            buffer.append(SCALAR_SIGNATURE);
            buffer.append(SPACE);
            buffer.append(type.getName());
        } else if (type instanceof GraphQLInputObjectType) {
            GraphQLInputObjectType inputType = (GraphQLInputObjectType) type;
            buffer.append(INPUT_TYPE_SIGNATURE);
            buffer.append(SPACE);
            buffer.append(inputType.getName());
            buffer.append(SPACE);
            buffer.append(STRUCTURE_START);
            buffer.append(NEW_LINE);
            buffer.append(NEW_LINE);
            for (GraphQLInputObjectField field : inputType.getFields()) {
                buffer.append(fromDescription(INNER_OFFSET, field.getDescription()));
                buffer.append(INNER_OFFSET);
                buffer.append(from(field));
                buffer.append(NEW_LINE);
                buffer.append(NEW_LINE);
            }
            buffer.append(STRUCTURE_END);
        } else if (type instanceof GraphQLObjectType) {
            GraphQLObjectType objectType = (GraphQLObjectType) type;
            buffer.append(OBJECT_TYPE_SIGNATURE);
            buffer.append(SPACE);
            buffer.append(objectType.getName());

            if (objectType.getInterfaces().size() > 0) {
                buffer.append(SPACE)
                        .append(IMPLEMENT_KEY_WORD)
                        .append(SPACE);
            }

            int numberOfInterfaces = objectType.getInterfaces().size();

            for (int i = 0; i < numberOfInterfaces; i++) {
                GraphQLNamedOutputType interfaceType =
                        objectType.getInterfaces().get(i);
                buffer.append(interfaceType.getName());
                if (i != numberOfInterfaces - 1) {
                    buffer.append(SPACE).append(AND).append(SPACE);
                }
            }


            buffer.append(SPACE);
            buffer.append(STRUCTURE_START);
            buffer.append(NEW_LINE);
            buffer.append(NEW_LINE);
            for (GraphQLFieldDefinition field : objectType.getFieldDefinitions()) {
                buffer.append(fromDescription(INNER_OFFSET, field.getDescription()));
                buffer.append(INNER_OFFSET);
                buffer.append(from(field));
                buffer.append(NEW_LINE);
                buffer.append(NEW_LINE);
            }
            buffer.append(STRUCTURE_END);
        } else if (type instanceof GraphQLInterfaceType) {
            GraphQLInterfaceType interfaceType = (GraphQLInterfaceType) type;
            buffer.append(INTERFACE_SIGNATURE);
            buffer.append(SPACE);
            buffer.append(interfaceType.getName());
            buffer.append(SPACE);
            buffer.append(STRUCTURE_START);
            buffer.append(NEW_LINE);
            buffer.append(NEW_LINE);
            for (GraphQLFieldDefinition field : interfaceType.getFieldDefinitions()) {
                buffer.append(fromDescription(INNER_OFFSET, field.getDescription()));
                buffer.append(INNER_OFFSET);
                buffer.append(from(field));
                buffer.append(NEW_LINE);
                buffer.append(NEW_LINE);
            }
            buffer.append(STRUCTURE_END);
        } else if (type instanceof GraphQLEnumType) {
            GraphQLEnumType enumType = (GraphQLEnumType) type;
            buffer.append(ENUM_SIGNATURE);
            buffer.append(SPACE);
            buffer.append(enumType.getName());
            buffer.append(SPACE);
            buffer.append(STRUCTURE_START);
            buffer.append(NEW_LINE);
            buffer.append(NEW_LINE);
            for (GraphQLEnumValueDefinition value : enumType.getValues()) {
                buffer.append(INNER_OFFSET);
                buffer.append(value.getName());
                buffer.append(NEW_LINE);
                buffer.append(NEW_LINE);
            }
            buffer.append(STRUCTURE_END);
        } else if (type instanceof GraphQLUnionType) {
            GraphQLUnionType unionType = (GraphQLUnionType) type;
            buffer.append(UNION_SIGNATURE);
            buffer.append(SPACE);
            buffer.append(unionType.getName());
            buffer.append(EQUAL);

            int numberOfPossibleTypes = unionType.getTypes().size();

            for (int i = 0; i < numberOfPossibleTypes; i++) {
                GraphQLNamedOutputType outputType = unionType.getTypes().get(i);
                buffer.append(outputType.getName());
                if (i != numberOfPossibleTypes - 1) {
                    buffer.append(SPACE)
                            .append(UNION_TYPE_SEPARATOR)
                            .append(SPACE);
                }
            }
        } else {
            throw new IllegalStateException("unknown type : " + type);
        }


        return buffer.toString();
    }

    private static String fromDescription(String offset, String description) {
        if (description == null) {
            return EMPTY;
        }
        StringBuffer buffer = new StringBuffer();
        if (description.contains(END_OF_LINE)) {
            buffer.append(offset).append(THREE_DOUBLE_QUOTATION).append(SPACE);
            String[] lines = description.split(END_OF_LINE);
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                if (i != 0) {
                    buffer.append(offset).append(spaces(4));
                }
                buffer.append(line);
                if (i < lines.length - 1) {
                    buffer.append(NEW_LINE);
                }
            }
            buffer.append(SPACE).append(THREE_DOUBLE_QUOTATION);
        } else {
            buffer.append(offset).append(DOUBLE_QUOTATION).append(SPACE).append(description).append(SPACE).append(DOUBLE_QUOTATION);
        }
        return buffer.append(NEW_LINE).toString();
    }

    private static String fromDescription(String description) {
        return fromDescription(EMPTY, description);
    }

    public static String from(GraphQLSchema schema) {
        StringBuffer buffer = new StringBuffer();

        List<GraphQLNamedType> allTypes = schema.getAllTypesAsList();

        separate(allTypes, GraphQLScalarType.class)
                .forEach(type -> append(buffer, type));

        separate(allTypes, GraphQLEnumType.class)
                .forEach(type -> append(buffer, type));

        separate(allTypes, GraphQLInputObjectType.class)
                .forEach(type -> append(buffer, type));

        separate(allTypes, GraphQLInterfaceType.class)
                .forEach(type -> append(buffer, type));

        separateJustObjects(schema)
                .forEach(type -> append(buffer, type));

        separate(allTypes, GraphQLUnionType.class)
                .forEach(type -> append(buffer, type));

        separateTopLevelObjects(schema)
                .forEach(type -> append(buffer, type));

        return buffer.toString();
    }

    private static void append(StringBuffer buffer, GraphQLNamedType type) {
        if (BuiltInTypes.contains(type.getName()))
            return;
        buffer.append(from(type)).append(NEW_LINE).append(NEW_LINE);
    }

    private static <T> Stream<T> stream(List<?> list, Class<T> clazz) {
        return list.stream().filter(x -> clazz.isAssignableFrom(x.getClass()))
                .map(x -> (T) x);
    }

    private static <T> List<T> separate(List<?> list, Class<T> clazz) {
        return stream(list, clazz).collect(Collectors.toList());
    }

    private static boolean hasSameName(GraphQLObjectType t1, GraphQLObjectType t2) {
        if (t1 == null || t2 == null) {
            return false;
        }
        return t1.getName().equals(t2.getName());
    }

    private static boolean isTopLevel(GraphQLObjectType type, GraphQLSchema schema) {
        return hasSameName(schema.getQueryType(), type)
                || hasSameName(schema.getMutationType(), type)
                || hasSameName(schema.getSubscriptionType(), type);
    }

    private static List<GraphQLObjectType> separateJustObjects(GraphQLSchema schema) {
        return stream(schema.getAllTypesAsList(), GraphQLObjectType.class)
                .filter(type -> !isTopLevel(type, schema))
                .collect(Collectors.toList());
    }

    private static List<GraphQLObjectType> separateTopLevelObjects(GraphQLSchema schema) {
        return stream(schema.getAllTypesAsList(), GraphQLObjectType.class)
                .filter(type -> isTopLevel(type, schema))
                .collect(Collectors.toList());
    }

    private static String spaces(int size) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < size; i++) {
            buffer.append(SPACE);
        }
        return buffer.toString();
    }

}
