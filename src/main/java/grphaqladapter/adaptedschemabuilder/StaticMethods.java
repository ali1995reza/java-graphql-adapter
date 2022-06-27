package grphaqladapter.adaptedschemabuilder;


import graphql.introspection.Introspection;
import graphql.schema.*;
import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.exceptions.MappingGraphqlArgumentException;
import grphaqladapter.adaptedschemabuilder.exceptions.MappingGraphqlFieldException;
import grphaqladapter.adaptedschemabuilder.mapped.*;
import grphaqladapter.adaptedschemabuilder.scalar.ScalarEntry;
import grphaqladapter.adaptedschemabuilder.validator.TypeValidator;
import grphaqladapter.annotations.interfaces.GraphqlDirectiveDetails;
import grphaqladapter.annotations.interfaces.GraphqlDirectiveFunction;

import java.util.List;

import static grphaqladapter.adaptedschemabuilder.exceptions.SchemaExceptionBuilder.exception;

public class StaticMethods {

    private static GraphQLList createList(GraphQLTypeReference type, int dims) {
        if (dims < 1)
            throw new IllegalStateException("can not create a list with dimensions <1");

        GraphQLList list = new GraphQLList(type);
        --dims;
        for (int i = 0; i < dims; i++) {
            list = new GraphQLList(list);
        }


        return list;
    }

    public static GraphQLArgument buildArgument(MappedTypeClass mappedClass, MappedFieldMethod method, MappedParameter parameter, BuildingContext context) {
        //so handle it please !
        GraphQLArgument.Builder argumentBuilder = GraphQLArgument.newArgument();
        argumentBuilder.name(parameter.name());
        GraphQLTypeReference inputType = context.getInputTypeFor(parameter.type().type());

        Assert.isNotNull(inputType, exception(MappingGraphqlArgumentException.class, "provided input type for [" + parameter.type() + "] is null", mappedClass.baseClass(), method.method(), parameter.parameter()));


        argumentBuilder.type(
                parameter.type().isNullable() ?
                        (parameter.type().dimensions() > 0 ?
                                (createList(inputType, parameter.type().dimensions())) : inputType)
                        :
                        new GraphQLNonNull((parameter.type().dimensions() > 0 ?
                                (createList(inputType, parameter.type().dimensions())) : inputType))
        );

        argumentBuilder.description(method.description());

        GraphQLArgument argument = argumentBuilder.build();

        for (GraphqlDirectiveDetails details : context.resolveDirective(parameter)) {
            GraphqlDirectiveFunction function = context.objectConstructor()
                    .getInstance(details.annotation().functionality());
            function.setDirective(details);
            argument = function.onFieldArgument(argument, mappedClass, method, parameter, context.directiveHandlingContext());
        }

        return argument;
    }

    public static GraphQLArgument buildArgument(MappedAnnotation mappedClass, MappedAnnotationMethod method, BuildingContext context) {
        //so handle it please !
        GraphQLArgument.Builder argumentBuilder = GraphQLArgument.newArgument();
        argumentBuilder.name(method.name());
        GraphQLTypeReference inputType = context.getInputTypeFor(method.type().type());

        Assert.isNotNull(inputType, exception(MappingGraphqlArgumentException.class, "provided input type for [" + method.type() + "] is null", mappedClass.baseClass(), method.method()));


        argumentBuilder.type(
                method.type().isNullable() ?
                        (method.type().dimensions() > 0 ?
                                (createList(inputType, method.type().dimensions())) : inputType)
                        :
                        new GraphQLNonNull((method.type().dimensions() > 0 ?
                                (createList(inputType, method.type().dimensions())) : inputType))
        );

        argumentBuilder.description(method.description());

        GraphQLArgument argument = argumentBuilder.build();

        for (GraphqlDirectiveDetails details : context.resolveDirective(method)) {
            GraphqlDirectiveFunction function = context.objectConstructor()
                    .getInstance(details.annotation().functionality());
            function.setDirective(details);
            argument = function.onDirectiveArgument(argument, mappedClass, method, context.directiveHandlingContext());
        }

        return argument;
    }

    public static GraphQLFieldDefinition buildField(MappedTypeClass mappedClass, MappedFieldMethod method, BuildingContext context) {
        //so handle it please !
        GraphQLFieldDefinition.Builder fieldDefinitionBuilder = GraphQLFieldDefinition.newFieldDefinition();
        fieldDefinitionBuilder.name(method.name());
        GraphQLTypeReference outputType = context.geOutputTypeFor(method.type().type());

        Assert.isNotNull(outputType, exception(MappingGraphqlFieldException.class, "provided output type for [" + method.type() + "] is null", mappedClass.baseClass(), method.method()));


        fieldDefinitionBuilder.type(
                method.type().isNullable() ?
                        (method.type().dimensions() > 0 ?
                                (createList(outputType, method.type().dimensions())) : outputType)
                        :
                        new GraphQLNonNull((method.type().dimensions() > 0 ?
                                (createList(outputType, method.type().dimensions())) : outputType))
        );


        for (MappedParameter parameter : method.parameters()) {
            if (parameter.model().isSchemaArgument()) {
                fieldDefinitionBuilder.argument(buildArgument(mappedClass, method, parameter, context));
            }
        }

        fieldDefinitionBuilder.description(method.description());

        GraphQLFieldDefinition fieldDefinition = fieldDefinitionBuilder.build();

        for (GraphqlDirectiveDetails details : context.resolveDirective(method)) {
            GraphqlDirectiveFunction function = context.objectConstructor()
                    .getInstance(details.annotation().functionality());
            function.setDirective(details);
            fieldDefinition = function.onField(fieldDefinition, mappedClass, method, context.directiveHandlingContext());
        }

        return fieldDefinition;
    }

    public static GraphQLInputObjectField buildInputField(MappedInputTypeClass mappedClass, MappedInputFieldMethod method, BuildingContext context) {
        if (method.method().getParameterCount() > 0) {
            throw new IllegalStateException("an InputField mapped method can not contains parameters - [method"
                    + method.method() + "]");
        }

        GraphQLInputObjectField.Builder inputFieldBuilder =
                GraphQLInputObjectField.newInputObjectField();

        inputFieldBuilder.name(method.name());

        GraphQLTypeReference inputType = context.getInputTypeFor(method.type().type());

        Assert.isNotNull(inputType, exception(MappingGraphqlFieldException.class, "can not find InputType for [" + method.type() + "]", mappedClass.baseClass(), method.method()));

        inputFieldBuilder.type(
                method.type().isNullable() ?
                        (method.type().dimensions() > 0 ?
                                (createList(inputType, method.type().dimensions())) : inputType)
                        :
                        new GraphQLNonNull((method.type().dimensions() > 0 ?
                                (createList(inputType, method.type().dimensions())) : inputType))
        );

        inputFieldBuilder.description(method.description());

        GraphQLInputObjectField inputField = inputFieldBuilder.build();

        for (GraphqlDirectiveDetails details : context.resolveDirective(method)) {
            GraphqlDirectiveFunction function = context.objectConstructor()
                    .getInstance(details.annotation().functionality());
            function.setDirective(details);
            inputField = function.onInputObjectField(inputField, mappedClass, method, context.directiveHandlingContext());
        }

        return inputField;
    }

    public static GraphQLInterfaceType buildInterface(MappedInterface mappedClass, BuildingContext context) {

        GraphQLInterfaceType.Builder interfaceTypeBuilder =
                GraphQLInterfaceType.newInterface();

        interfaceTypeBuilder.name(mappedClass.name());


        for (MappedFieldMethod method : mappedClass.fieldMethods().values()) {
            interfaceTypeBuilder.field(buildField(mappedClass, method, context));

        }

        interfaceTypeBuilder.description(mappedClass.description());

        GraphQLInterfaceType interfaceType = interfaceTypeBuilder.build();

        for (GraphqlDirectiveDetails details : context.resolveDirective(mappedClass)) {
            GraphqlDirectiveFunction function = context.objectConstructor()
                    .getInstance(details.annotation().functionality());
            function.setDirective(details);
            interfaceType = function.onInterface(interfaceType, mappedClass, context.directiveHandlingContext());
        }

        return interfaceType;
    }

    public static GraphQLInputObjectType buildInputObjectType(MappedInputTypeClass mappedClass, BuildingContext context) {

        GraphQLInputObjectType.Builder inputObjectTypeBuilder =
                GraphQLInputObjectType.newInputObject();

        inputObjectTypeBuilder.name(mappedClass.name());

        for (MappedInputFieldMethod method : mappedClass.inputFiledMethods().values()) {
            inputObjectTypeBuilder.field(buildInputField(mappedClass, method, context));
        }

        inputObjectTypeBuilder.description(mappedClass.description());

        GraphQLInputObjectType inputObjectType = inputObjectTypeBuilder.build();

        for (GraphqlDirectiveDetails details : context.resolveDirective(mappedClass)) {
            GraphqlDirectiveFunction function = context.objectConstructor()
                    .getInstance(details.annotation().functionality());
            function.setDirective(details);
            inputObjectType = function.onInputObjectType(inputObjectType, mappedClass, context.directiveHandlingContext());
        }

        return inputObjectType;
    }

    public static GraphQLObjectType buildOutputObjectType(MappedTypeClass mappedClass, BuildingContext context) {

        GraphQLObjectType.Builder objectTypeBuilder =
                GraphQLObjectType.newObject();

        objectTypeBuilder.name(mappedClass.name());


        for (MappedFieldMethod method : mappedClass.fieldMethods().values()) {
            objectTypeBuilder.field(buildField(mappedClass, method, context));
        }

        for (Class inter : mappedClass.baseClass().getInterfaces()) {
            if (context.isAnInterface(inter)) {

                objectTypeBuilder.withInterface(context.getInterfaceFor(inter));
                context.addToPossibleTypesOf(
                        context.getMappedClassFor(inter, MappedElementType.INTERFACE),
                        mappedClass
                );

            } else if (context.isAnUnion(inter)) {
                context.addToPossibleTypesOf(
                        context.getMappedClassFor(inter, MappedElementType.UNION),
                        mappedClass
                );
            }
        }

        objectTypeBuilder.description(mappedClass.description());

        GraphQLObjectType objectType = objectTypeBuilder.build();

        for (GraphqlDirectiveDetails details : context.resolveDirective(mappedClass)) {
            GraphqlDirectiveFunction function = context.objectConstructor()
                    .getInstance(details.annotation().functionality());
            function.setDirective(details);
            objectType = function.onObject(objectType, mappedClass, context.directiveHandlingContext());
        }

        return objectType;
    }

    public static GraphQLDirective buildDirective(MappedAnnotation mappedAnnotation, BuildingContext context) {

        GraphQLDirective.Builder directiveType =
                GraphQLDirective.newDirective();

        directiveType.name(mappedAnnotation.name());

        for (Introspection.DirectiveLocation location : mappedAnnotation.locations()) {
            directiveType.validLocation(location);
        }


        for (MappedAnnotationMethod method : mappedAnnotation.mappedMethods().values()) {
            directiveType.argument(buildArgument(mappedAnnotation, method, context));
        }

        directiveType.description(mappedAnnotation.description());

        return directiveType.build();

    }

    public static GraphQLEnumType buildEnumType(MappedEnum mappedClass, BuildingContext context) {
        GraphQLEnumType.Builder enumTypeBuilder =
                GraphQLEnumType.newEnum();
        enumTypeBuilder.name(mappedClass.name());

        for (MappedEnumConstant constant : mappedClass.constants().values()) {
            enumTypeBuilder.value(constant.name(), constant.constant());
        }

        enumTypeBuilder.description(mappedClass.description());

        GraphQLEnumType enumType = enumTypeBuilder.build();

        for (GraphqlDirectiveDetails details : context.resolveDirective(mappedClass)) {
            GraphqlDirectiveFunction function = context.objectConstructor()
                    .getInstance(details.annotation().functionality());
            function.setDirective(details);
            enumType = function.onEnum(enumType, mappedClass, context.directiveHandlingContext());
        }

        return enumType;
    }

    public static GraphQLUnionType buildUnionType(MappedUnionInterface mappedClass, List<MappedClass> possibles, BuildingContext context) {

        GraphQLUnionType.Builder unionTypeBuilder =
                GraphQLUnionType.newUnionType();

        if (!mappedClass.mappedType().is(MappedElementType.UNION))
            throw new IllegalStateException("mapped class not mapped to union type");

        unionTypeBuilder.name(mappedClass.name());
        for (MappedClass possible : possibles) {
            unionTypeBuilder.possibleType(
                    context.getObjectTypeFor(possible.baseClass())
            );
        }

        unionTypeBuilder.description(mappedClass.description());

        GraphQLUnionType unionType = unionTypeBuilder.build();

        for (GraphqlDirectiveDetails details : context.resolveDirective(mappedClass)) {
            GraphqlDirectiveFunction function = context.objectConstructor()
                    .getInstance(details.annotation().functionality());
            function.setDirective(details);
            unionType = function.onUnion(unionType, mappedClass, context.directiveHandlingContext());
        }

        return unionType;

    }

    public static GraphQLScalarType buildScalarType(ScalarEntry entry) {
        TypeValidator.validate(entry);
        return GraphQLScalarType.newScalar()
                .name(entry.name())
                .coercing(entry.coercing())
                .description(entry.description())
                .coercing(entry.coercing())
                .build();

    }
}
