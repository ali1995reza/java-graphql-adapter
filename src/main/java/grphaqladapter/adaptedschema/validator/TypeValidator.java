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

package grphaqladapter.adaptedschema.validator;


import grphaqladapter.adaptedschema.assertutil.Assert;
import grphaqladapter.adaptedschema.assertutil.NameValidator;
import grphaqladapter.adaptedschema.exceptions.MappingGraphqlTypeException;
import grphaqladapter.adaptedschema.exceptions.SchemaExceptionBuilder;
import grphaqladapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import grphaqladapter.adaptedschema.mapping.mapped_elements.classes.MappedClass;
import grphaqladapter.adaptedschema.mapping.strategy.descriptions.GraphqlElementDescription;
import grphaqladapter.adaptedschema.mapping.strategy.descriptions.GraphqlTypeNameDescription;
import grphaqladapter.adaptedschema.mapping.strategy.descriptions.directive.GraphqlDirectiveDescription;
import grphaqladapter.adaptedschema.mapping.strategy.descriptors.classes.TypeDescriptions;
import grphaqladapter.adaptedschema.scalar.ScalarEntry;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class TypeValidator {

    private final static SchemaExceptionBuilder<MappingGraphqlTypeException> EXCEPTION_BUILDER = new SchemaExceptionBuilder<>(MappingGraphqlTypeException.class);

    public static void isMethodMemberOfClass(Method method, Class clazz) {
        try {
            Method realMethod = clazz.getMethod(method.getName(), method.getParameterTypes());
            if (method.equals(realMethod)) {
                return;
            }
        } catch (NoSuchMethodException e) {

        }

        throw exception("method [" + method + "] not a member of class", clazz);
    }

    public static void isModifierValidForATypeClass(Class clazz) {
        Assert.isTrue(Modifier.isPublic(clazz.getModifiers()), exception("a type class modifier must be Public", clazz));
    }

    public static void validate(MappedClass mappedClass, Class clazz) {
        /*Assert.isNotNull(mappedClass.baseClass(), exception("base class is null", clazz));
        Assert.isEquals(mappedClass.baseClass(), clazz, exception("target class and mapped class not same", clazz));
        isModifierValidForATypeClass(mappedClass.baseClass());
        Assert.isTrue(NameValidator.isNameValid(mappedClass.name()), exception("mapped name is not valid", clazz));
        validateModifiers(mappedClass);
        for (MappedMethod method : mappedClass.mappedMethods().values()) {
            FieldValidator.validate(method, mappedClass.baseClass(), method.method());
            isMethodMemberOfClass(method.method(), mappedClass.baseClass());
        }*/

    }

    public static void validate(TypeDescriptions annotations, Class clazz) {
        Assert.isNotNull(annotations, exception("can not detect type annotations", clazz));

        isModifierValidForATypeClass(clazz);
        validateInterfaceClass(annotations, clazz);
        validateAbstractClass(annotations, clazz);
        validateEnumClass(annotations, clazz);
        validateNormalClass(annotations, clazz);
    }

    public static void validate(GraphqlDirectiveDescription annotation, Class clazz) {
        validate(annotation, clazz, false);
    }

    public static void validate(GraphqlDirectiveDescription annotation, Class clazz, boolean skipIfNull) {
        if (skipIfNull && annotation == null) {
            return;
        }
        validate((GraphqlElementDescription) annotation, clazz, skipIfNull);
        Assert.isNull(annotation.locations(), exception("no directive locations set", clazz));
        Assert.isPositive(annotation.locations().length, exception("no directive locations set", clazz));
    }

    public static void validate(GraphqlElementDescription annotation, Class clazz, boolean skipIfNull) {
        if (skipIfNull && annotation == null) {
            return;
        }
        Assert.isNotNull(annotation, exception("can not detect type annotation", clazz));
        Assert.isTrue(NameValidator.isNameValid(annotation.name()), exception("type name is not valid for GraphqlQuery", clazz));
    }

    public static void validate(GraphqlElementDescription annotation, Class clazz) {
        validate(annotation, clazz, false);
    }

    public static void validate(GraphqlTypeNameDescription annotation, Class clazz, boolean skipIfNull) {
        if (skipIfNull && annotation == null) {
            return;
        }
        validate((GraphqlElementDescription) annotation, clazz, skipIfNull);
    }

    public static void validate(GraphqlTypeNameDescription annotation, Class clazz) {
        validate(annotation, clazz, false);
    }

    public static void validate(ScalarEntry entry) {
        Assert.isNotNull(entry.type(), exception("provided type is null", entry.type()));
        Assert.isTrue(NameValidator.isNameValid(entry.name()), exception("type name is invalid", entry.type()));
        Assert.isNotNull(entry.coercing(), exception("provided coercing is null", entry.type()));
    }

    private static MappingGraphqlTypeException exception(String message, Class clazz) {
        return EXCEPTION_BUILDER.exception(message, clazz, null, null);
    }

    private static void validateAbstractClass(TypeDescriptions annotations, Class clazz) {

        if (!Modifier.isAbstract(clazz.getModifiers()) || clazz.isInterface()) {
            return;
        }

        Assert.isNull(annotations.interfaceDescription(), exception("an abstract class can not map to GraphqlInterface", clazz));
        Assert.isNull(annotations.inputTypeDescription(), exception("an abstract class can not map to GraphqlInputType", clazz));
        Assert.isNull(annotations.enumDescription(), exception("an abstract class can not map to GraphqlEnum", clazz));
        Assert.isNull(annotations.unionDescription(), exception("an abstract class can not map to GraphqlUnion", clazz));
        Assert.isNull(annotations.queryDescription(), exception("an abstract class can not map to GraphqlQuery", clazz));
        Assert.isNull(annotations.mutationDescription(), exception("an abstract class can not map to GraphqlMutation", clazz));
        Assert.isNull(annotations.subscriptionDescription(), exception("an abstract class can not map to GraphqlSubscription", clazz));
        Assert.isNull(annotations.directiveDescription(), exception("an abstract class can not map to GraphqlDirective", clazz));

        Assert.isNotNull(annotations.objectTypeDescription(), exception("GraphqlType annotation is needed", clazz));

        validate(annotations.objectTypeDescription(), clazz, false);
    }

    private static void validateAnnotationClass(TypeDescriptions annotations, Class clazz) {
        if (!clazz.isAnnotation()) {
            return;
        }

        Assert.isNull(annotations.objectTypeDescription(), exception("an annotation class can not map to GraphqlType", clazz));
        Assert.isNull(annotations.inputTypeDescription(), exception("an annotation class can not map to GraphqlInputType", clazz));
        Assert.isNull(annotations.interfaceDescription(), exception("an annotation class can not map to GraphqlInterface", clazz));
        Assert.isNull(annotations.unionDescription(), exception("an annotation class can not map to GraphqlUnion", clazz));
        Assert.isNull(annotations.queryDescription(), exception("an annotation class can not map to GraphqlQuery", clazz));
        Assert.isNull(annotations.mutationDescription(), exception("an annotation class can not map to GraphqlMutation", clazz));
        Assert.isNull(annotations.subscriptionDescription(), exception("an annotation class can not map to GraphqlSubscription", clazz));

        validate(annotations.directiveDescription(), clazz);

    }

    private static void validateEnumClass(TypeDescriptions annotations, Class clazz) {

        if (!clazz.isEnum()) {
            return;
        }

        Assert.isNull(annotations.objectTypeDescription(), exception("an enum class can not map to GraphqlType", clazz));
        Assert.isNull(annotations.inputTypeDescription(), exception("an enum class can not map to GraphqlInputType", clazz));
        Assert.isNull(annotations.interfaceDescription(), exception("an enum class can not map to GraphqlInterface", clazz));
        Assert.isNull(annotations.unionDescription(), exception("an enum class can not map to GraphqlUnion", clazz));
        Assert.isNull(annotations.queryDescription(), exception("an enum class can not map to GraphqlQuery", clazz));
        Assert.isNull(annotations.mutationDescription(), exception("an enum class can not map to GraphqlMutation", clazz));
        Assert.isNull(annotations.subscriptionDescription(), exception("an enum class can not map to GraphqlSubscription", clazz));
        Assert.isNull(annotations.directiveDescription(), exception("an enum class can not map to GraphqlDirective", clazz));


        Assert.isNotNull(annotations.enumDescription(), exception("GraphqlEnum annotation is needed", clazz));

        validate(annotations.enumDescription(), clazz, false);
    }

    private static void validateInterfaceClass(TypeDescriptions annotations, Class clazz) {
        if (!clazz.isInterface() || clazz.isAnnotation()) {
            return;
        }
        Assert.isNull(annotations.objectTypeDescription(), exception("an interface can not map to GraphqlType", clazz));
        Assert.isNull(annotations.inputTypeDescription(), exception("an interface can not map to GraphqlInputType", clazz));
        Assert.isNull(annotations.enumDescription(), exception("an interface can not map to GraphqlEnum", clazz));
        Assert.isNull(annotations.queryDescription(), exception("an interface can not map to GraphqlQuery", clazz));
        Assert.isNull(annotations.mutationDescription(), exception("an interface can not map to GraphqlMutation", clazz));
        Assert.isNull(annotations.subscriptionDescription(), exception("an interface can not map to GraphqlSubscription", clazz));
        Assert.isNull(annotations.directiveDescription(), exception("an interface class can not map to GraphqlDirective", clazz));


        Assert.isOneOrMoreFalse(exception("an interface can not map to both GraphqlUnion and GraphqlInterface", clazz),
                annotations.unionDescription() != null, annotations.interfaceDescription() != null);
        Assert.isOneOrMoreFalse(exception("at least one of GraphqlInterface or GraphqlUnion annotation is needed", clazz),
                annotations.unionDescription() == null, annotations.interfaceDescription() == null);

        validate(annotations.interfaceDescription(), clazz, true);
        validate(annotations.unionDescription(), clazz, true);
    }

    private static void validateModifiers(MappedClass mappedClass) {
        if (mappedClass.mappedType().isOneOf(MappedElementType.INPUT_TYPE, MappedElementType.OBJECT_TYPE) ||
                mappedClass.mappedType().isTopLevelType()) {

            Assert.isAllTrue(exception("just a public normal class can map to " + mappedClass.mappedType(), mappedClass.baseClass()),
                    !mappedClass.baseClass().isInterface(),
                    !mappedClass.baseClass().isEnum(),
                    Modifier.isPublic(mappedClass.baseClass().getModifiers()));
        } else if (mappedClass.mappedType().is(MappedElementType.ENUM)) {
            Assert.isAllTrue(exception("just enum class can map to " + mappedClass.mappedType(), mappedClass.baseClass()),
                    Modifier.isPublic(mappedClass.baseClass().getModifiers()),
                    mappedClass.baseClass().isEnum());
        } else {
            Assert.isOneOrMoreTrue(exception("just interfaces can map to " + mappedClass.mappedType(), mappedClass.baseClass()),
                    Modifier.isPublic(mappedClass.baseClass().getModifiers()),
                    mappedClass.baseClass().isInterface());
        }
    }

    private static void validateNormalClass(TypeDescriptions annotations, Class clazz) {

        /*if (clazz.isAnnotation() || Modifier.isAbstract(clazz.getModifiers()) || clazz.isEnum() || clazz.isInterface()) {
            return;
        }

        Assert.isNull(annotations.unionDescriptor(), exception("a class can not map to GraphqlUnion", clazz));
        Assert.isNull(annotations.interfaceDescriptor(), exception("a class can not map to GraphqlInterface", clazz));
        Assert.isNull(annotations.enumDescriptor(), exception("a class can not map to GraphqlEnum", clazz));
        Assert.isOneOrMoreFalse(exception("at least one of GraphqlInputType, GraphqlType, GraphqlQuery, GraphqlSubscription or GraphqlMutation annotation is needed", clazz),
                annotations.inputTypeDescriptor() == null,
                annotations.objectTypeDescriptor() == null,
                annotations.queryDescriptor() == null,
                annotations.mutationDescriptor() == null,
                annotations.subscriptionDescriptor() == null);

        GraphqlInputTypeDescription inputTypeAnnotation = annotations.inputTypeDescriptor();
        GraphqlObjectTypeDescription typeAnnotation = annotations.objectTypeDescriptor();
        GraphqlQueryDescription queryAnnotation = annotations.queryDescriptor();
        GraphqlMutationDescription mutationAnnotation = annotations.mutationDescriptor();
        GraphqlSubscriptionDescription subscriptionAnnotation = annotations.subscriptionDescriptor();

        final boolean topLevelType = queryAnnotation != null || mutationAnnotation != null || subscriptionAnnotation != null;

        if (topLevelType) {

            Assert.isAllTrue(exception("GraphqlQuery, GraphqlMutation and GraphqlSubscription can not combine by GraphqlType or GraphqlInputType", clazz),
                    annotations.inputTypeDescriptor() == null, annotations.objectTypeDescriptor() == null, annotations.directiveDescriptor() == null);

            Assert.isExactlyOneTrue(exception("just one of GraphqlQuery, GraphqlMutation and GraphqlSubscription can present", clazz),
                    queryAnnotation != null,
                    subscriptionAnnotation != null,
                    mutationAnnotation != null);

            validate(queryAnnotation, clazz, true);
            validate(mutationAnnotation, clazz, true);
            validate(subscriptionAnnotation, clazz, true);

        } else {

            Assert.isOneOrMoreTrue(exception("at least one of GraphqlType or GraphqlInputType annotations is needed", clazz),
                    annotations.inputTypeDescriptor() != null, annotations.objectTypeDescriptor() != null);

            if (typeAnnotation != null && inputTypeAnnotation != null) {
                Assert.isNotEquals(typeAnnotation.name(), inputTypeAnnotation.name(), exception("both GraphqlInputType and GraphqlType has same names", clazz));
            }

            validate(inputTypeAnnotation, clazz, true);
            validate(typeAnnotation, clazz, true);
        }*/

    }
}
