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

package graphql_adapter.adaptedschema;


import graphql.introspection.Introspection;
import graphql.language.*;
import graphql.schema.*;
import graphql_adapter.adaptedschema.assertion.Assert;
import graphql_adapter.adaptedschema.exceptions.MappingGraphqlArgumentException;
import graphql_adapter.adaptedschema.exceptions.MappingGraphqlFieldException;
import graphql_adapter.adaptedschema.exceptions.MappingSchemaElementException;
import graphql_adapter.adaptedschema.functions.GraphqlDirectiveFunction;
import graphql_adapter.adaptedschema.mapping.mapped_elements.DimensionModel;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElement;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import graphql_adapter.adaptedschema.mapping.mapped_elements.TypeInformation;
import graphql_adapter.adaptedschema.mapping.mapped_elements.annotation.AppliedAnnotation;
import graphql_adapter.adaptedschema.mapping.mapped_elements.annotation.MappedAnnotation;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedClass;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedInputTypeClass;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedObjectTypeClass;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedScalarClass;
import graphql_adapter.adaptedschema.mapping.mapped_elements.enums.MappedEnum;
import graphql_adapter.adaptedschema.mapping.mapped_elements.enums.MappedEnumConstant;
import graphql_adapter.adaptedschema.mapping.mapped_elements.interfaces.MappedInterface;
import graphql_adapter.adaptedschema.mapping.mapped_elements.interfaces.MappedUnionInterface;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedAnnotationMethod;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedFieldMethod;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedInputFieldMethod;
import graphql_adapter.adaptedschema.mapping.mapped_elements.parameter.MappedParameter;
import graphql_adapter.adaptedschema.system_objects.directive.GraphqlDirectiveDetails;
import graphql_adapter.adaptedschema.utils.CollectionUtils;
import graphql_adapter.adaptedschema.utils.MethodUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static graphql_adapter.adaptedschema.exceptions.SchemaExceptionBuilder.exception;
import static graphql_adapter.adaptedschema.utils.ClassUtils.cast;

public class GraphQLElementBuilder {

    public static GraphQLArgument buildArgument(MappedObjectTypeClass mappedClass, MappedFieldMethod method, MappedParameter parameter, BuildingContext context) {
        //so handle it please !
        GraphQLArgument.Builder argumentBuilder = GraphQLArgument.newArgument()
                .name(parameter.name());

        GraphQLInputType inputType = createInputType(parameter.type(), context);

        Assert.isNotNull(inputType, exception(MappingGraphqlArgumentException.class, "provided input type for [" + parameter.type() + "] is null", mappedClass.baseClass(), method.method(), parameter.parameter()));

        argumentBuilder.type(inputType);

        argumentBuilder.description(parameter.description());

        if (parameter.hasDefaultValue()) {
            argumentBuilder.defaultValueLiteral(buildValueFromObject(parameter.defaultValue(), parameter.type(), context));
        }

        GraphQLArgument argument = argumentBuilder.build();

        for (GraphqlDirectiveDetails details : getDirectiveDetails(parameter, context)) {
            GraphqlDirectiveFunction<?> function = context.objectConstructor()
                    .getInstance(details.annotation().functionality());
            argument = function.onFieldArgument(details, argument, mappedClass, method, parameter, context.directiveHandlingContext());
            argument = addAppliedDirectives(GraphQLArgument.newArgument(argument), details, context).build();
        }

        return argument;
    }

    public static GraphQLArgument buildArgument(MappedAnnotation mappedClass, MappedAnnotationMethod method, BuildingContext context) {
        //so handle it please !
        GraphQLArgument.Builder argumentBuilder = GraphQLArgument.newArgument()
                .name(method.name());

        GraphQLInputType inputType = createInputType(method.type(), context);

        Assert.isNotNull(inputType, exception(MappingGraphqlArgumentException.class, "provided input type for [" + method.type() + "] is null", mappedClass.baseClass(), method.method()));

        argumentBuilder.type(inputType);

        argumentBuilder.description(method.description());

        if (method.hasDefaultValue()) {
            argumentBuilder.defaultValueLiteral(buildValueFromObject(method.defaultValue(), method.type(), context));
        }

        GraphQLArgument argument = argumentBuilder.build();

        for (GraphqlDirectiveDetails details : getDirectiveDetails(method, context)) {
            GraphqlDirectiveFunction<?> function = context.objectConstructor()
                    .getInstance(details.annotation().functionality());
            argument = function.onDirectiveArgument(details, argument, mappedClass, method, context.directiveHandlingContext());
            argument = addAppliedDirectives(GraphQLArgument.newArgument(argument), details, context).build();
        }

        return argument;
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

        for (MappedEnumConstant constant : mappedClass.constantsByName().values()) {
            enumTypeBuilder.value(buildEnumValue(mappedClass, constant, context));
        }

        enumTypeBuilder.description(mappedClass.description());

        GraphQLEnumType enumType = enumTypeBuilder.build();

        for (GraphqlDirectiveDetails details : getDirectiveDetails(mappedClass, context)) {
            GraphqlDirectiveFunction<?> function = context.objectConstructor()
                    .getInstance(details.annotation().functionality());
            enumType = function.onEnum(details, enumType, mappedClass, context.directiveHandlingContext());
            enumType = addAppliedDirectives(GraphQLEnumType.newEnum(enumType), details, context).build();
        }

        return enumType;
    }

    public static GraphQLEnumValueDefinition buildEnumValue(MappedEnum mappedEnum, MappedEnumConstant enumConstant, BuildingContext context) {
        GraphQLEnumValueDefinition enumValue = GraphQLEnumValueDefinition.newEnumValueDefinition()
                .name(enumConstant.name())
                .value(enumConstant.constant())
                .description(enumConstant.description())
                .build();

        for (GraphqlDirectiveDetails details : getDirectiveDetails(enumConstant, context)) {
            GraphqlDirectiveFunction<?> function = context.objectConstructor()
                    .getInstance(details.annotation().functionality());
            enumValue = function.onEnumValue(details, enumValue, mappedEnum, enumConstant, context.directiveHandlingContext());
            enumValue = addAppliedDirectives(GraphQLEnumValueDefinition.newEnumValueDefinition(enumValue), details, context).build();
        }

        return enumValue;
    }

    public static GraphQLFieldDefinition buildField(MappedObjectTypeClass mappedClass, MappedFieldMethod method, BuildingContext context) {
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

        for (GraphqlDirectiveDetails details : getDirectiveDetails(method, context)) {
            GraphqlDirectiveFunction<?> function = context.objectConstructor()
                    .getInstance(details.annotation().functionality());
            fieldDefinition = function.onField(details, fieldDefinition, mappedClass, method, context.directiveHandlingContext());
            fieldDefinition = addAppliedDirectives(GraphQLFieldDefinition.newFieldDefinition(fieldDefinition), details, context).build();
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

        GraphQLInputType inputType = createInputType(method.type(), context);

        Assert.isNotNull(inputType, exception(MappingGraphqlFieldException.class, "can not find InputType for [" + method.type() + "]", mappedClass.baseClass(), method.method()));

        inputFieldBuilder.type(inputType);

        inputFieldBuilder.description(method.description());

        if (method.hasDefaultValue()) {
            inputFieldBuilder.defaultValueLiteral(buildValueFromObject(method.defaultValue(), method.type(), context));
        }

        GraphQLInputObjectField inputField = inputFieldBuilder.build();

        for (GraphqlDirectiveDetails details : getDirectiveDetails(method, context)) {
            GraphqlDirectiveFunction<?> function = context.objectConstructor()
                    .getInstance(details.annotation().functionality());
            inputField = function.onInputObjectField(details, inputField, mappedClass, method, context.directiveHandlingContext());
            inputField = addAppliedDirectives(GraphQLInputObjectField.newInputObjectField(inputField), details, context).build();
        }

        return inputField;
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

        for (GraphqlDirectiveDetails details : getDirectiveDetails(mappedClass, context)) {
            GraphqlDirectiveFunction<?> function = context.objectConstructor()
                    .getInstance(details.annotation().functionality());
            inputObjectType = function.onInputObjectType(details, inputObjectType, mappedClass, context.directiveHandlingContext());
            inputObjectType = addAppliedDirectives(GraphQLInputObjectType.newInputObject(inputObjectType), details, context).build();
        }

        return inputObjectType;
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

        for (GraphqlDirectiveDetails details : getDirectiveDetails(mappedClass, context)) {
            GraphqlDirectiveFunction<?> function = context.objectConstructor()
                    .getInstance(details.annotation().functionality());
            interfaceType = function.onInterface(details, interfaceType, mappedClass, context.directiveHandlingContext());
            interfaceType = addAppliedDirectives(GraphQLInterfaceType.newInterface(interfaceType), details, context).build();
        }

        return interfaceType;
    }

    public static GraphQLObjectType buildOutputObjectType(MappedObjectTypeClass mappedClass, BuildingContext context) {

        GraphQLObjectType.Builder objectTypeBuilder =
                GraphQLObjectType.newObject();

        objectTypeBuilder.name(mappedClass.name());


        for (MappedFieldMethod method : mappedClass.fieldMethods().values()) {
            objectTypeBuilder.field(buildField(mappedClass, method, context));
        }

        for (Class<?> inter : mappedClass.baseClass().getInterfaces()) {
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

        for (GraphqlDirectiveDetails details : getDirectiveDetails(mappedClass, context)) {
            GraphqlDirectiveFunction<?> function = context.objectConstructor()
                    .getInstance(details.annotation().functionality());
            objectType = function.onObject(details, objectType, mappedClass, context.directiveHandlingContext());
            objectType = addAppliedDirectives(GraphQLObjectType.newObject(objectType), details, context).build();
        }

        return objectType;
    }

    public static GraphQLScalarType buildScalarType(MappedScalarClass scalarClass, BuildingContext context) {

        GraphQLScalarType scalarType = GraphQLScalarType.newScalar()
                .name(scalarClass.name())
                .coercing(scalarClass.coercing())
                .description(scalarClass.description())
                .build();

        for (GraphqlDirectiveDetails details : getDirectiveDetails(scalarClass, context)) {
            GraphqlDirectiveFunction<?> function = context.objectConstructor()
                    .getInstance(details.annotation().functionality());
            scalarType = function.onScalar(details, scalarType, scalarClass, context.directiveHandlingContext());
            scalarType = addAppliedDirectives(GraphQLScalarType.newScalar(scalarType), details, context).build();
        }

        return scalarType;
    }

    public static GraphQLUnionType buildUnionType(MappedUnionInterface mappedClass, List<MappedClass> possibles, BuildingContext context) {

        GraphQLUnionType.Builder unionTypeBuilder =
                GraphQLUnionType.newUnionType();

        if (!mappedClass.mappedType().isUnion())
            throw new IllegalStateException("mapped class not mapped to union type");

        unionTypeBuilder.name(mappedClass.name());
        for (MappedClass possible : possibles) {
            unionTypeBuilder.possibleType(
                    context.getObjectTypeFor(possible.baseClass())
            );
        }

        unionTypeBuilder.description(mappedClass.description());

        GraphQLUnionType unionType = unionTypeBuilder.build();

        for (GraphqlDirectiveDetails details : getDirectiveDetails(mappedClass, context)) {
            GraphqlDirectiveFunction<?> function = context.objectConstructor()
                    .getInstance(details.annotation().functionality());
            unionType = function.onUnion(details, unionType, mappedClass, context.directiveHandlingContext());
            unionType = addAppliedDirectives(GraphQLUnionType.newUnionType(unionType), details, context).build();
        }

        return unionType;

    }

    public static Value<?> getValueFromEnum(Object o, BuildingContext context) {
        if (o == null) {
            return NullValue.newNullValue().build();
        }
        MappedEnum mappedEnum = context.getMappedClassFor(o.getClass(), MappedElementType.ENUM);
        return EnumValue.newEnumValue(mappedEnum.constantsByEnumValue().get(o).name()).build();
    }

    private static <B extends GraphqlDirectivesContainerTypeBuilder<B, E>, E extends GraphqlTypeBuilder<E>> B addAppliedDirectives(B builder, GraphqlDirectiveDetails directiveDetails, BuildingContext context) {
        GraphQLAppliedDirective.Builder appliedDirective = GraphQLAppliedDirective.newDirective()
                .name(directiveDetails.annotation().name());

        for (MappedAnnotationMethod argument : directiveDetails.annotation().mappedMethods().values()) {
            TypeInformation<?> type = argument.type();
            Object value = directiveDetails.getArgument(argument.name());

            GraphQLInputType inputType = createInputType(type, context);
            Assert.isNotNull(inputType, exception(MappingGraphqlArgumentException.class, "provided input type for [" + type.type() + "] is null", null, argument.method()));
            appliedDirective.argument(GraphQLAppliedDirectiveArgument.newArgument()
                    .type(inputType)
                    .name(argument.name())
                    .valueLiteral(buildValueFromObject(value, type, context))
                    .build());
        }
        builder.withAppliedDirective(appliedDirective);
        return builder;
    }

    private static Value<?> buildArrayValue(Object o, Class<?> clazz, int dimensions, DimensionModel dimensionModel, BuildingContext context) {
        if (dimensionModel.isArray()) {
            return buildArrayValueFromArray(o, clazz, dimensions, context);
        } else {
            return buildArrayValueFromList(o, clazz, dimensions, context);
        }
    }

    private static Value<?> buildArrayValueFromArray(Object array, Class<?> clazz, int dimensions, BuildingContext context) {
        if (array == null) {
            return NullValue.newNullValue().build();
        }

        final int remainingDims = --dimensions;

        ArrayValue.Builder arrayValue = ArrayValue.newArrayValue();
        CollectionUtils.forEachUnknownArray(array, object -> arrayValue.value(buildValueFromObject(object, clazz, remainingDims, DimensionModel.ARRAY, context)));
        return arrayValue.build();
    }

    private static Value<?> buildArrayValueFromList(Object o, Class<?> clazz, int dimensions, BuildingContext context) {
        if (o == null) {
            return NullValue.of();
        }

        List<?> list = cast(o);

        --dimensions;

        ArrayValue.Builder arrayValue = ArrayValue.newArrayValue();
        for (Object listMember : list) {
            arrayValue.value(buildValueFromObject(listMember, clazz, dimensions, DimensionModel.LIST, context));
        }
        return arrayValue.build();
    }

    private static Value<?> buildValueFromObject(Object value, Class<?> clazz, int dimensions, DimensionModel dimensionModel, BuildingContext context) {
        if (dimensions > 0) {
            return buildArrayValue(value, clazz, dimensions, dimensionModel, context);
        } else {
            return buildValueFromSingleObject(value, context);
        }
    }

    private static Value<?> buildValueFromObject(Object value, TypeInformation<?> type, BuildingContext context) {
        return buildValueFromObject(value, type.type(), type.dimensions(), type.dimensionModel(), context);
    }

    private static Value<?> buildValueFromSingleObject(Object o, BuildingContext context) {
        if (o == null) {
            return NullValue.newNullValue().build();
        }
        Class<?> clazz = o.getClass();
        if (context.isScalar(clazz)) {
            return getValueFromScalar(o, context);
        } else if (context.isEnum(clazz)) {
            return getValueFromEnum(o, context);
        } else {

            ObjectValue.Builder objectValue = ObjectValue.newObjectValue();
            MappedInputTypeClass inputTypeClass = context.getMappedClassFor(clazz, MappedElementType.INPUT_TYPE);

            Assert.isNotNull(inputTypeClass, new IllegalStateException("can not find any type for class [" + clazz + "]"));

            for (MappedInputFieldMethod field : inputTypeClass.inputFiledMethods().values()) {
                Object result = MethodUtils.invoke(o, field.method());
                TypeInformation<?> type = field.type();
                objectValue.objectField(ObjectField.newObjectField()
                        .name(field.name())
                        .value(buildValueFromObject(result, type.type(), type.dimensions(), type.dimensionModel(), context))
                        .build());
            }
            return objectValue.build();
        }
    }

    private static GraphQLInputType createInputType(TypeInformation<?> type, BuildingContext context) {
        try {
            GraphQLTypeReference reference = context.getPossibleInputTypeFor(type.type());
            GraphQLInputType inputType = type.hasDimensions() ? createList(reference, type.dimensions()) : reference;
            if (!type.isNullable()) {
                inputType = GraphQLNonNull.nonNull(inputType);
            }
            return inputType;
        } catch (Exception e) {
            throw new IllegalStateException("mapping type " + type + " throw an exception", e);
        }
    }

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

    private static List<GraphqlDirectiveDetails> getDirectiveDetails(MappedElement element, BuildingContext context) {
        if (CollectionUtils.isEmpty(element.appliedAnnotations())) {
            return Collections.emptyList();
        }
        List<GraphqlDirectiveDetails> details = new ArrayList<>();
        for (AppliedAnnotation appliedAnnotation : element.appliedAnnotations()) {
            MappedAnnotation mappedAnnotation = context.getMappedClassFor(appliedAnnotation.annotationClass(), MappedElementType.DIRECTIVE);
            Assert.isNotNull(mappedAnnotation, exception(MappingSchemaElementException.class, "can not find MappedAnnotation for class [" + appliedAnnotation.annotationClass() + "]", appliedAnnotation.annotationClass()));
            details.add(new GraphqlDirectiveDetails(mappedAnnotation, appliedAnnotation.arguments()));
        }
        return details;
    }

    private static Value<?> getValueFromScalar(Object o, BuildingContext context) {
        if (o == null) {
            return NullValue.newNullValue().build();
        }
        MappedScalarClass scalarClass = context.getMappedClassFor(o.getClass(), MappedElementType.SCALAR);
        return scalarClass.coercing().valueToLiteral(o);
    }

}
