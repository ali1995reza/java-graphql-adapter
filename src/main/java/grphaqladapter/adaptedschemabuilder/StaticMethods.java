package grphaqladapter.adaptedschemabuilder;


import graphql.schema.*;
import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.exceptions.MappingGraphqlArgumentException;
import grphaqladapter.adaptedschemabuilder.exceptions.MappingGraphqlFieldException;
import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;
import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedParameter;
import grphaqladapter.adaptedschemabuilder.scalar.ScalarEntry;
import grphaqladapter.adaptedschemabuilder.validator.TypeValidator;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static grphaqladapter.adaptedschemabuilder.exceptions.SchemaExceptionBuilder.exception;

public class StaticMethods {


    private final static GraphQLList createList(GraphQLTypeReference type, int dims) {
        if (dims < 1)
            throw new IllegalStateException("can not create a list with dimensions <1");

        GraphQLList list = new GraphQLList(type);
        --dims;
        for (int i = 0; i < dims; i++) {
            list = new GraphQLList(list);
        }


        return list;
    }


    public static GraphQLArgument buildArgument(MappedClass mappedClass, MappedMethod method, MappedParameter parameter, BuildingContext context) {
        //so handle it please !
        GraphQLArgument.Builder argument = GraphQLArgument.newArgument();
        argument.name(parameter.argumentName());
        GraphQLTypeReference inputType = context.getInputTypeFor(parameter.type());

        Assert.isNotNull(inputType, exception(MappingGraphqlArgumentException.class, "provided input type for [" + parameter.type() + "] is null", mappedClass.baseClass(), method.method(), parameter.parameter()));


        argument.type(
                parameter.isNullable() ?
                        (parameter.isList() ?
                                (createList(inputType, parameter.dimensions())) : inputType)
                        :
                        new GraphQLNonNull((parameter.isList() ?
                                (createList(inputType, parameter.dimensions())) : inputType))
        );
        //todo add description here too !
        return argument.build();
    }


    public static GraphQLFieldDefinition buildField(MappedClass mappedClass, MappedMethod method, BuildingContext context) {
        //so handle it please !
        GraphQLFieldDefinition.Builder definition = GraphQLFieldDefinition.newFieldDefinition();
        definition.name(method.fieldName());
        GraphQLTypeReference outputType = context.geOutputTypeFor(method.type());

        Assert.isNotNull(outputType, exception(MappingGraphqlFieldException.class, "provided output type for [" + method.type() + "] is null", mappedClass.baseClass(), method.method()));


        definition.type(
                method.isNullable() ?
                        (method.isList() ?
                                (createList(outputType, method.dimensions())) : outputType)
                        :
                        new GraphQLNonNull((method.isList() ?
                                (createList(outputType, method.dimensions())) : outputType))
        );


        for (MappedParameter parameter : method.parameters()) {
            if (!parameter.isEnv()) {
                definition.argument(buildArgument(mappedClass, method, parameter, context));
            }
        }

        definition.description(method.description());

        return definition.build();
    }


    public static GraphQLInputObjectField buildInputField(MappedClass mappedClass, MappedMethod method, BuildingContext context) {
        if (method.method().getParameterCount() > 0) {
            throw new IllegalStateException("an InputField mapped method can not contains parameters - [method"
                    + method.method() + "]");
        }

        GraphQLInputObjectField.Builder field =
                GraphQLInputObjectField.newInputObjectField();

        field.name(method.fieldName());

        GraphQLTypeReference inputType = context.getInputTypeFor(method.type());

        Assert.isNotNull(inputType, exception(MappingGraphqlFieldException.class, "can not find InputType for [" + method.type() + "]", mappedClass.baseClass(), method.method()));

        field.type(
                method.isNullable() ?
                        (method.isList() ?
                                (createList(inputType, method.dimensions())) : inputType)
                        :
                        new GraphQLNonNull((method.isList() ?
                                (createList(inputType, method.dimensions())) : inputType))
        );

        field.description(method.description());

        return field.build();
    }


    public static GraphQLInterfaceType buildInterface(MappedClass mappedClass, BuildingContext context) {
        if (mappedClass.mappedType() != MappedClass.MappedType.INTERFACE)
            throw new IllegalStateException("mapped class did not map to interface");

        GraphQLInterfaceType.Builder interfaceType =
                GraphQLInterfaceType.newInterface();

        interfaceType.name(mappedClass.typeName());


        for (MappedMethod method : mappedClass.mappedMethods().values()) {
            interfaceType.field(buildField(mappedClass, method, context));

        }

        interfaceType.description(mappedClass.description());

        return interfaceType.build();
    }

    public static GraphQLInputObjectType buildInputObjectType(MappedClass mappedClass, BuildingContext context) {
        if (mappedClass.mappedType() != MappedClass.MappedType.INPUT_TYPE)
            throw new IllegalStateException("mapped class did not map to InputType");

        GraphQLInputObjectType.Builder inputObjectType =
                GraphQLInputObjectType.newInputObject();

        inputObjectType.name(mappedClass.typeName());

        for (MappedMethod method : mappedClass.mappedMethods().values()) {
            inputObjectType.field(buildInputField(mappedClass, method, context));
        }

        inputObjectType.description(mappedClass.description());

        return inputObjectType.build();
    }

    public static GraphQLObjectType buildOutputObjectType(MappedClass mappedClass, BuildingContext context) {
        if (mappedClass.mappedType() != MappedClass.MappedType.OBJECT_TYPE && !mappedClass.mappedType().isTopLevelType())
            throw new IllegalStateException("mapped class did not map to OutputObjectType");

        GraphQLObjectType.Builder outputObjectType =
                GraphQLObjectType.newObject();

        outputObjectType.name(mappedClass.typeName());


        for (MappedMethod method : mappedClass.mappedMethods().values()) {
            outputObjectType.field(buildField(mappedClass, method, context));
        }

        for (Class inter : mappedClass.baseClass().getInterfaces()) {
            if (context.isAnInterface(inter)) {

                outputObjectType.withInterface(context.getInterfaceFor(inter));
                context.addToPossibleTypesOf(
                        context.getMappedClassFor(inter, MappedClass.MappedType.INTERFACE),
                        mappedClass
                );

            } else if (context.isAnUnion(inter)) {
                context.addToPossibleTypesOf(
                        context.getMappedClassFor(inter, MappedClass.MappedType.UNION),
                        mappedClass
                );
            }
        }

        outputObjectType.description(mappedClass.description());

        return outputObjectType.build();
    }

    private static Map<String, Object> findEnumValues(Class cls) {
        if (!cls.isEnum())
            throw new IllegalStateException("can not find enum values of none-enum class - [class:"
                    + cls + "]");


        Map<String, Object> objects = new HashMap<>();
        final Object[] enums = cls.getEnumConstants();
        int count = 0;
        for (Field field : cls.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())
                    && Modifier.isPublic(field.getModifiers())
                    && field.getType() == cls) {
                Object o = null;
                try {

                    o = field.get(null);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }

                boolean found = false;

                for (Object en : enums) {
                    if (o == en) {
                        found = true;
                        count++;
                        break;
                    }
                }

                if (found) {
                    objects.put(field.getName(), o);
                }

            }
        }

        if (count != enums.length) {
            throw new IllegalStateException("can not discover all enum values - [class:" + cls + "]");
        }

        return objects;
    }

    public static GraphQLEnumType buildEnumType(MappedClass mappedClass, BuildingContext context) {
        if (mappedClass.mappedType() != MappedClass.MappedType.ENUM)
            throw new IllegalStateException("mapped class did not map to EnumType");

        GraphQLEnumType.Builder enumType =
                GraphQLEnumType.newEnum();
        enumType.name(mappedClass.typeName());

        Map<String, Object> enums = findEnumValues(mappedClass.baseClass());

        for (String s : enums.keySet()) {
            enumType.value(s, enums.get(s));
        }

        enumType.description(mappedClass.description());

        return enumType.build();
    }

    public static GraphQLUnionType buildUnionType(MappedClass mappedClass, List<MappedClass> possibles, BuildingContext context) {

        GraphQLUnionType.Builder unionType =
                GraphQLUnionType.newUnionType();

        if (!mappedClass.mappedType().is(MappedClass.MappedType.UNION))
            throw new IllegalStateException("mapped class not mapped to union type");

        unionType.name(mappedClass.typeName());
        for (MappedClass possible : possibles) {
            unionType.possibleType(
                    context.getObjectTypeFor(possible.baseClass())
            );
        }

        unionType.description(mappedClass.description());

        return unionType.build();
    }

    public static GraphQLScalarType buildScalarType(ScalarEntry entry) {
        TypeValidator.validate(entry);
        return GraphQLScalarType.newScalar()
                .name(entry.name())
                .coercing(entry.coercing())
                .description(entry.description())
                .build();

    }
}
