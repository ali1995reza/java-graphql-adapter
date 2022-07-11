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

package graphql_adapter.adaptedschema.utils.sdl;

import graphql.language.*;
import graphql.schema.*;
import graphql_adapter.adaptedschema.utils.CollectionUtils;
import graphql_adapter.adaptedschema.utils.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class SDLUtils {

    private final static Set<String> BuiltInTypes = new HashSet<>(Arrays.asList("__Directive", "__DirectiveLocation", "__EnumValue", "__Field",
            "__InputValue", "__Schema", "__Type", "__TypeKind"));


    private final static char NONE_NULL = '!';
    private final static char START_LIST = '[';
    private final static char END_LIST = ']';
    private final static char ARGUMENT_START = '(';
    private final static char ARGUMENT_END = ')';
    private final static char SPACE = ' ';
    private final static char STRUCTURE_START = '{';
    private final static char STRUCTURE_END = '}';
    private final static char AND = '&';
    private final static char DOUBLE_QUOTATION = '"';
    private final static char AT_SIGN = '@';
    private final static String DOUBLE_DOT = ": ";
    private final static String IMPLEMENT_KEY_WORD = "implements";
    private final static String ARGUMENT_SEPARATOR = ", ";
    private final static String DIRECTIVE_LOCATION_SEPARATOR = " | ";
    private final static String UNION_TYPE_SEPARATOR = " | ";
    private final static String NEW_LINE = System.lineSeparator();
    private final static String DOUBLE_NEW_LINE = NEW_LINE + NEW_LINE;
    private final static String END_OF_LINE = "\n";
    private final static String INNER_OFFSET = spaces(3);
    private final static String INPUT_TYPE_SIGNATURE = "input";
    private final static String OBJECT_TYPE_SIGNATURE = "type";
    private final static String INTERFACE_SIGNATURE = "interface";
    private final static String ENUM_SIGNATURE = "enum";
    private final static String UNION_SIGNATURE = "union";
    private final static String DIRECTIVE_SIGNATURE = "directive";
    private final static String SCALAR_SIGNATURE = "scalar";
    private final static String MUTATION_SIGNATURE = "mutation";
    private final static String QUERY_SIGNATURE = "query";
    private final static String SUBSCRIPTION_SIGNATURE = "subscription";
    private final static String SCHEMA_SIGNATURE = "schema";
    private final static String EQUAL = " = ";
    private final static String ON = "on";
    private final static String THREE_DOUBLE_QUOTATION = "\"\"\"";
    private final static String EMPTY = "";

    public static StringBuffer createBuffer(GraphQLNamedSchemaElement element, boolean newLine) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(fromDescription(element.getDescription()));
        if (newLine) {
            buffer.append(NEW_LINE);
        }
        return buffer;
    }

    public static StringBuffer createBuffer(GraphQLNamedSchemaElement element) {
        return createBuffer(element, true);
    }

    public static String getSchemaObjectAsSDL(GraphQLSchema schema) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(SCHEMA_SIGNATURE).append(SPACE).append(STRUCTURE_START).append(DOUBLE_NEW_LINE);
        if (schema.getQueryType() != null) {
            stringBuilder.append(INNER_OFFSET).append(QUERY_SIGNATURE).append(DOUBLE_DOT).append(SPACE)
                    .append(schema.getQueryType().getName()).append(DOUBLE_NEW_LINE);
        }
        if (schema.getMutationType() != null) {
            stringBuilder.append(INNER_OFFSET).append(MUTATION_SIGNATURE).append(DOUBLE_DOT).append(SPACE)
                    .append(schema.getMutationType().getName()).append(DOUBLE_NEW_LINE);
        }
        if (schema.getSubscriptionType() != null) {
            stringBuilder.append(INNER_OFFSET).append(SUBSCRIPTION_SIGNATURE).append(DOUBLE_DOT).append(SPACE)
                    .append(schema.getSubscriptionType().getName()).append(DOUBLE_NEW_LINE);
        }
        return stringBuilder.append(STRUCTURE_END).toString();
    }

    public static String toSDL(GraphQLAppliedDirective directive) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(AT_SIGN).append(directive.getName());
        if (!CollectionUtils.isEmpty(directive.getArguments())) {
            buffer.append(ARGUMENT_START);
            CollectionUtils.forEach(directive.getArguments(), argument ->
                            buffer.append(argument.getName()).append(DOUBLE_DOT)
                                    .append(toSDL(argument.getArgumentValue())).append(ARGUMENT_SEPARATOR)
                                    .append(SPACE)
                    , argument -> buffer.append(argument.getName()).append(DOUBLE_DOT)
                            .append(toSDL(argument.getArgumentValue()))
            );
            buffer.append(ARGUMENT_END);
        }
        return buffer.toString();
    }

    public static String toSDL(GraphQLArgument argument) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(argument.getName());
        stringBuilder.append(DOUBLE_DOT);
        stringBuilder.append(getTypeAsString(argument.getType()));
        if (argument.hasSetDefaultValue()) {
            stringBuilder.append(EQUAL).append(toSDL(argument.getArgumentDefaultValue()));
        }
        if (!CollectionUtils.isEmpty(argument.getAppliedDirectives())) {
            stringBuilder.append(SPACE).append(toSDLOneLine(argument.getAppliedDirectives()));
        }
        return stringBuilder.toString();
    }

    public static String toSDL(GraphQLFieldDefinition field) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(field.getName());

        if (field.getArguments().size() > 0) {
            buffer.append(ARGUMENT_START);
            CollectionUtils.forEach(field.getArguments(),
                    argument -> buffer.append(getDescriptionForFieldArgument(argument)).append(toSDL(argument))
                            .append(ARGUMENT_SEPARATOR),
                    argument -> buffer.append(getDescriptionForFieldArgument(argument)).append(toSDL(argument)));
            buffer.append(ARGUMENT_END);
        }

        buffer.append(DOUBLE_DOT);

        buffer.append(getTypeAsString(field.getType()));

        if (!CollectionUtils.isEmpty(field.getAppliedDirectives())) {
            buffer.append(SPACE).append(toSDLOneLine(field.getAppliedDirectives()));
        }

        return buffer.toString();
    }

    public static String toSDL(GraphQLInputObjectField field) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(field.getName());
        stringBuilder.append(DOUBLE_DOT);
        stringBuilder.append(getTypeAsString(field.getType()));

        if (field.hasSetDefaultValue()) {
            stringBuilder.append(EQUAL).append(toSDL(field.getInputFieldDefaultValue().getValue()));
        }

        if (!CollectionUtils.isEmpty(field.getAppliedDirectives())) {
            stringBuilder.append(SPACE).append(toSDLOneLine(field.getAppliedDirectives()));
        }

        return stringBuilder.toString();
    }

    public static String toSDL(GraphQLScalarType type) {
        StringBuffer buffer = createBuffer(type);
        buffer.append(SCALAR_SIGNATURE);
        buffer.append(SPACE);
        buffer.append(type.getName());
        if (!CollectionUtils.isEmpty(type.getAppliedDirectives())) {
            buffer.append(SPACE).append(toSDLOneLine(type.getAppliedDirectives()));
        }
        return buffer.toString();
    }

    public static String toSDL(GraphQLInputObjectType inputType) {
        StringBuffer buffer = createBuffer(inputType);
        buffer.append(INPUT_TYPE_SIGNATURE);
        buffer.append(SPACE);
        buffer.append(inputType.getName());
        buffer.append(SPACE);
        if (!CollectionUtils.isEmpty(inputType.getAppliedDirectives())) {
            buffer.append(toSDLOneLine(inputType.getAppliedDirectives())).append(SPACE);
        }
        buffer.append(STRUCTURE_START);
        buffer.append(NEW_LINE);
        buffer.append(NEW_LINE);
        for (GraphQLInputObjectField field : inputType.getFields()) {
            buffer.append(fromDescription(INNER_OFFSET, field.getDescription()));
            buffer.append(INNER_OFFSET);
            buffer.append(toSDL(field));
            buffer.append(NEW_LINE);
            buffer.append(NEW_LINE);
        }
        buffer.append(STRUCTURE_END);
        return buffer.toString();
    }

    public static String toSDL(GraphQLObjectType objectType) {
        StringBuffer buffer = createBuffer(objectType);
        buffer.append(OBJECT_TYPE_SIGNATURE);
        buffer.append(SPACE);
        buffer.append(objectType.getName());

        if (objectType.getInterfaces().size() > 0) {
            buffer.append(SPACE)
                    .append(IMPLEMENT_KEY_WORD)
                    .append(SPACE);
        }

        CollectionUtils.forEach(objectType.getInterfaces(),
                interfaceType -> buffer.append(interfaceType.getName()).append(SPACE).append(AND).append(SPACE),
                interfaceType -> buffer.append(interfaceType.getName()));

        buffer.append(SPACE);

        if (!CollectionUtils.isEmpty(objectType.getAppliedDirectives())) {
            buffer.append(toSDLOneLine(objectType.getAppliedDirectives())).append(SPACE);
        }

        buffer.append(STRUCTURE_START);
        buffer.append(NEW_LINE);
        buffer.append(NEW_LINE);
        for (GraphQLFieldDefinition field : objectType.getFieldDefinitions()) {
            buffer.append(fromDescription(INNER_OFFSET, field.getDescription())).append(NEW_LINE);
            buffer.append(INNER_OFFSET);
            buffer.append(toSDL(field));
            buffer.append(NEW_LINE);
            buffer.append(NEW_LINE);
        }
        buffer.append(STRUCTURE_END);
        return buffer.toString();
    }

    public static String toSDL(GraphQLInterfaceType interfaceType) {
        StringBuffer buffer = createBuffer(interfaceType);
        buffer.append(INTERFACE_SIGNATURE);
        buffer.append(SPACE);
        buffer.append(interfaceType.getName());
        buffer.append(SPACE);
        if (!CollectionUtils.isEmpty(interfaceType.getAppliedDirectives())) {
            buffer.append(toSDLOneLine(interfaceType.getAppliedDirectives())).append(SPACE);
        }
        buffer.append(STRUCTURE_START);
        buffer.append(NEW_LINE);
        buffer.append(NEW_LINE);
        for (GraphQLFieldDefinition field : interfaceType.getFieldDefinitions()) {
            buffer.append(fromDescription(INNER_OFFSET, field.getDescription()));
            buffer.append(INNER_OFFSET);
            buffer.append(toSDL(field));
            buffer.append(NEW_LINE);
            buffer.append(NEW_LINE);
        }
        buffer.append(STRUCTURE_END);
        return buffer.toString();
    }

    public static String toSDL(GraphQLEnumType enumType) {
        StringBuffer buffer = createBuffer(enumType);
        buffer.append(ENUM_SIGNATURE);
        buffer.append(SPACE);
        buffer.append(enumType.getName());
        buffer.append(SPACE);
        if (!CollectionUtils.isEmpty(enumType.getAppliedDirectives())) {
            buffer.append(toSDLOneLine(enumType.getAppliedDirectives())).append(SPACE);
        }
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
        return buffer.toString();
    }

    public static String toSDL(GraphQLUnionType unionType) {
        StringBuffer buffer = createBuffer(unionType);
        buffer.append(UNION_SIGNATURE);
        buffer.append(SPACE);
        buffer.append(unionType.getName());

        if (!CollectionUtils.isEmpty(unionType.getAppliedDirectives())) {
            buffer.append(SPACE).append(toSDLOneLine(unionType.getAppliedDirectives()));
        }

        buffer.append(EQUAL);

        CollectionUtils.forEach(unionType.getTypes(),
                outputType -> buffer.append(outputType.getName()).append(UNION_TYPE_SEPARATOR),
                outputType -> buffer.append(outputType.getName()));

        return buffer.toString();
    }

    public static String toSDL(GraphQLDirective directive) {
        StringBuffer buffer = createBuffer(directive);
        buffer.append(DIRECTIVE_SIGNATURE);
        buffer.append(SPACE);
        buffer.append(AT_SIGN);
        buffer.append(directive.getName());

        if (!CollectionUtils.isEmpty(directive.getArguments())) {

            buffer.append(ARGUMENT_START);

            CollectionUtils.forEach(directive.getArguments(),
                    argument -> buffer.append(DOUBLE_NEW_LINE).append(fromDescription(INNER_OFFSET, argument.getDescription()))
                            .append(NEW_LINE).append(INNER_OFFSET).append(toSDL(argument)),
                    argument -> buffer.append(DOUBLE_NEW_LINE).append(fromDescription(INNER_OFFSET, argument.getDescription()))
                            .append(NEW_LINE).append(INNER_OFFSET).append(toSDL(argument)).append(DOUBLE_NEW_LINE));

            buffer.append(ARGUMENT_END);
        }
        buffer.append(SPACE).append(ON).append(SPACE);

        CollectionUtils.forEach(directive.validLocations(),
                location -> buffer.append(location).append(DIRECTIVE_LOCATION_SEPARATOR),
                buffer::append);

        return buffer.toString();
    }

    public static String toSDL(Value<?> value) {
        if (value instanceof NullValue) {
            return "null";
        }
        if (value instanceof StringValue) {
            return '"' + ((StringValue) value).getValue() + '"';
        }
        if (value instanceof IntValue) {
            return String.valueOf(((IntValue) value).getValue());
        }
        if (value instanceof FloatValue) {
            return String.valueOf(((FloatValue) value).getValue());
        }
        if (value instanceof BooleanValue) {
            return String.valueOf(((BooleanValue) value).isValue());
        }
        if (value instanceof EnumValue) {
            return String.valueOf(((EnumValue) value).getName());
        }
        if (value instanceof ArrayValue) {
            return toSDL((ArrayValue) value);
        }
        if (value instanceof ObjectValue) {
            return toSDL((ObjectValue) value);
        }
        return null;
    }

    public static String toSDL(Object o) {
        if (o instanceof InputValueWithState) {
            InputValueWithState valueWithState = (InputValueWithState) o;
            if (valueWithState.isNotSet()) {
                return EMPTY;
            } else {
                return toSDL(valueWithState.getValue());
            }
        }
        if (o instanceof Value) {
            return toSDL((Value<?>) o);
        }
        if (o instanceof String) {
            return '"' + o.toString() + '"';
        }
        return String.valueOf(o);
    }

    public static String toSDL(GraphQLSchema schema) {
        StringBuffer buffer = new StringBuffer();

        List<GraphQLNamedType> allTypes = schema.getAllTypesAsList();

        CollectionUtils.separateToStream(allTypes, GraphQLScalarType.class)
                .filter(SDLUtils::filterBuiltInTypes)
                .forEach(type -> buffer.append(toSDL(type)).append(DOUBLE_NEW_LINE));

        schema.getDirectives()
                .stream()
                .filter(SDLUtils::filterBuiltInTypes)
                .forEach(directive -> buffer.append(toSDL(directive)).append(DOUBLE_NEW_LINE));

        CollectionUtils.separateToStream(allTypes, GraphQLEnumType.class)
                .filter(SDLUtils::filterBuiltInTypes)
                .forEach(type -> buffer.append(toSDL(type)).append(DOUBLE_NEW_LINE));

        CollectionUtils.separateToStream(allTypes, GraphQLInputObjectType.class)
                .filter(SDLUtils::filterBuiltInTypes)
                .forEach(type -> buffer.append(toSDL(type)).append(DOUBLE_NEW_LINE));

        CollectionUtils.separateToStream(allTypes, GraphQLInterfaceType.class)
                .filter(SDLUtils::filterBuiltInTypes)
                .forEach(type -> buffer.append(toSDL(type)).append(DOUBLE_NEW_LINE));

        separateJustObjects(schema)
                .filter(SDLUtils::filterBuiltInTypes)
                .forEach(type -> buffer.append(toSDL(type)).append(DOUBLE_NEW_LINE));

        CollectionUtils.separateToStream(allTypes, GraphQLUnionType.class)
                .filter(SDLUtils::filterBuiltInTypes)
                .forEach(type -> buffer.append(toSDL(type)).append(DOUBLE_NEW_LINE));

        separateTopLevelObjects(schema)
                .filter(SDLUtils::filterBuiltInTypes)
                .forEach(type -> buffer.append(toSDL(type)).append(DOUBLE_NEW_LINE));

        buffer.append(getSchemaObjectAsSDL(schema));

        return buffer.toString();
    }

    public static String toSDLOneLine(List<GraphQLAppliedDirective> directives) {
        if (CollectionUtils.isEmpty(directives)) {
            return EMPTY;
        }
        StringBuffer buffer = new StringBuffer();
        CollectionUtils.forEach(directives, directive -> buffer.append(toSDL(directive)).append(SPACE),
                directive -> buffer.append(toSDL(directive)));
        return buffer.toString();
    }

    public static String toSDLSeparateLine(List<GraphQLAppliedDirective> directives) {
        if (CollectionUtils.isEmpty(directives)) {
            return EMPTY;
        }
        StringBuffer buffer = new StringBuffer();
        directives.forEach(directive -> buffer.append(toSDL(directive)).append(NEW_LINE));
        return buffer.toString();
    }

    private static boolean filterBuiltInTypes(GraphQLNamedSchemaElement element) {
        return !BuiltInTypes.contains(element.getName());
    }

    private static String fromDescription(String offset, String description) {
        if (description == null) {
            return EMPTY;
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (description.contains(END_OF_LINE)) {
            stringBuilder.append(offset).append(THREE_DOUBLE_QUOTATION).append(SPACE);
            String[] lines = description.split(END_OF_LINE);
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                if (i != 0) {
                    stringBuilder.append(offset).append(spaces(4));
                }
                stringBuilder.append(line);
                if (i < lines.length - 1) {
                    stringBuilder.append(NEW_LINE);
                }
            }
            stringBuilder.append(SPACE).append(THREE_DOUBLE_QUOTATION);
        } else {
            stringBuilder.append(offset).append(DOUBLE_QUOTATION).append(SPACE).append(description).append(SPACE).append(DOUBLE_QUOTATION);
        }
        return stringBuilder.toString();
    }

    private static String fromDescription(String description) {
        return fromDescription(EMPTY, description);
    }

    private static String getDescriptionForFieldArgument(GraphQLArgument argument) {
        if (StringUtils.isNullString(argument.getDescription())) {
            return EMPTY;
        }
        return new StringBuilder().append(DOUBLE_QUOTATION)
                .append(SPACE)
                .append(argument.getDescription())
                .append(SPACE)
                .append(DOUBLE_QUOTATION)
                .append(SPACE)
                .toString();
    }

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

    private static Stream<GraphQLObjectType> separateJustObjects(GraphQLSchema schema) {
        return CollectionUtils.separateToStream(schema.getAllTypesAsList(), GraphQLObjectType.class)
                .filter(type -> !isTopLevel(type, schema));
    }

    private static Stream<GraphQLObjectType> separateTopLevelObjects(GraphQLSchema schema) {
        return CollectionUtils.separateToStream(schema.getAllTypesAsList(), GraphQLObjectType.class)
                .filter(type -> isTopLevel(type, schema));
    }

    private static String shiftAllLinesSeparately(String s, int shift) {
        String[] lines = s.split(END_OF_LINE);
        final String shiftString = spaces(shift);
        StringBuffer buffer = new StringBuffer();
        CollectionUtils.forEach(
                lines,
                line -> buffer.append(shiftString).append(line).append(NEW_LINE),
                line -> buffer.append(shiftString).append(line)
        );
        return buffer.toString();
    }

    private static String spaces(int size) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            stringBuilder.append(SPACE);
        }
        return stringBuilder.toString();
    }

    private static String toSDL(ArrayValue value) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[");
        CollectionUtils.forEach(value.getValues(), v -> buffer.append(toSDL(v)).append(", "),
                v -> buffer.append(toSDL(v)));
        return buffer.append("]").toString();
    }

    private static String toSDL(ObjectValue value) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("{");
        CollectionUtils.forEach(value.getObjectFields(), field -> buffer.append(field.getName()).append(": ").append(toSDL(field.getValue())).append(", "),
                field -> buffer.append(field.getName()).append(": ").append(toSDL(field.getValue())));
        return buffer.append("}").toString();
    }

}
