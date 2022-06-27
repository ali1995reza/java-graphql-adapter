package grphaqladapter.adaptedschemabuilder.validator;


import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.assertutil.NameValidator;
import grphaqladapter.adaptedschemabuilder.exceptions.MappingGraphqlTypeException;
import grphaqladapter.adaptedschemabuilder.exceptions.SchemaExceptionBuilder;
import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;
import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.TypeAnnotations;
import grphaqladapter.adaptedschemabuilder.scalar.ScalarEntry;
import grphaqladapter.annotations.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class TypeValidator {

    private final static SchemaExceptionBuilder<MappingGraphqlTypeException> EXCEPTION_BUILDER = new SchemaExceptionBuilder<>(MappingGraphqlTypeException.class);

    private static MappingGraphqlTypeException exception(String message, Class clazz) {
        return EXCEPTION_BUILDER.exception(message, clazz, null, null);
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

    public static void isModifierValidForATypeClass(Class clazz) {
        Assert.isTrue(Modifier.isPublic(clazz.getModifiers()), exception("a type class modifier must be Public", clazz));
    }

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

    private static void validateInterfaceClass(TypeAnnotations annotations, Class clazz) {
        if (!clazz.isInterface() || clazz.isAnnotation()) {
            return;
        }
        Assert.isNull(annotations.typeAnnotation(), exception("an interface can not map to GraphqlType", clazz));
        Assert.isNull(annotations.inputTypeAnnotation(), exception("an interface can not map to GraphqlInputType", clazz));
        Assert.isNull(annotations.enumAnnotation(), exception("an interface can not map to GraphqlEnum", clazz));
        Assert.isNull(annotations.queryAnnotation(), exception("an interface can not map to GraphqlQuery", clazz));
        Assert.isNull(annotations.mutationAnnotation(), exception("an interface can not map to GraphqlMutation", clazz));
        Assert.isNull(annotations.subscriptionAnnotation(), exception("an interface can not map to GraphqlSubscription", clazz));
        Assert.isNull(annotations.directiveAnnotation(), exception("an interface class can not map to GraphqlDirective", clazz));


        Assert.isOneOrMoreFalse(exception("an interface can not map to both GraphqlUnion and GraphqlInterface", clazz),
                annotations.unionAnnotation() != null, annotations.interfaceAnnotation() != null);
        Assert.isOneOrMoreFalse(exception("at least one of GraphqlInterface or GraphqlUnion annotation is needed", clazz),
                annotations.unionAnnotation() == null, annotations.interfaceAnnotation() == null);

        validate(annotations.interfaceAnnotation(), clazz, true);
        validate(annotations.unionAnnotation(), clazz, true);
    }

    private static void validateAbstractClass(TypeAnnotations annotations, Class clazz) {

        if (!Modifier.isAbstract(clazz.getModifiers()) || clazz.isInterface()) {
            return;
        }

        Assert.isNull(annotations.interfaceAnnotation(), exception("an abstract class can not map to GraphqlInterface", clazz));
        Assert.isNull(annotations.inputTypeAnnotation(), exception("an abstract class can not map to GraphqlInputType", clazz));
        Assert.isNull(annotations.enumAnnotation(), exception("an abstract class can not map to GraphqlEnum", clazz));
        Assert.isNull(annotations.unionAnnotation(), exception("an abstract class can not map to GraphqlUnion", clazz));
        Assert.isNull(annotations.queryAnnotation(), exception("an abstract class can not map to GraphqlQuery", clazz));
        Assert.isNull(annotations.mutationAnnotation(), exception("an abstract class can not map to GraphqlMutation", clazz));
        Assert.isNull(annotations.subscriptionAnnotation(), exception("an abstract class can not map to GraphqlSubscription", clazz));
        Assert.isNull(annotations.directiveAnnotation(), exception("an abstract class can not map to GraphqlDirective", clazz));

        Assert.isNotNull(annotations.typeAnnotation(), exception("GraphqlType annotation is needed", clazz));

        validate(annotations.typeAnnotation(), clazz, false);
    }

    private static void validateEnumClass(TypeAnnotations annotations, Class clazz) {

        if (!clazz.isEnum()) {
            return;
        }

        Assert.isNull(annotations.typeAnnotation(), exception("an enum class can not map to GraphqlType", clazz));
        Assert.isNull(annotations.inputTypeAnnotation(), exception("an enum class can not map to GraphqlInputType", clazz));
        Assert.isNull(annotations.interfaceAnnotation(), exception("an enum class can not map to GraphqlInterface", clazz));
        Assert.isNull(annotations.unionAnnotation(), exception("an enum class can not map to GraphqlUnion", clazz));
        Assert.isNull(annotations.queryAnnotation(), exception("an enum class can not map to GraphqlQuery", clazz));
        Assert.isNull(annotations.mutationAnnotation(), exception("an enum class can not map to GraphqlMutation", clazz));
        Assert.isNull(annotations.subscriptionAnnotation(), exception("an enum class can not map to GraphqlSubscription", clazz));
        Assert.isNull(annotations.directiveAnnotation(), exception("an enum class can not map to GraphqlDirective", clazz));


        Assert.isNotNull(annotations.enumAnnotation(), exception("GraphqlEnum annotation is needed", clazz));

        validate(annotations.enumAnnotation(), clazz, false);
    }

    private static void validateAnnotationClass(TypeAnnotations annotations, Class clazz) {
        if(!clazz.isAnnotation()) {
            return;
        }

        Assert.isNull(annotations.typeAnnotation(), exception("an annotation class can not map to GraphqlType", clazz));
        Assert.isNull(annotations.inputTypeAnnotation(), exception("an annotation class can not map to GraphqlInputType", clazz));
        Assert.isNull(annotations.interfaceAnnotation(), exception("an annotation class can not map to GraphqlInterface", clazz));
        Assert.isNull(annotations.unionAnnotation(), exception("an annotation class can not map to GraphqlUnion", clazz));
        Assert.isNull(annotations.queryAnnotation(), exception("an annotation class can not map to GraphqlQuery", clazz));
        Assert.isNull(annotations.mutationAnnotation(), exception("an annotation class can not map to GraphqlMutation", clazz));
        Assert.isNull(annotations.subscriptionAnnotation(), exception("an annotation class can not map to GraphqlSubscription", clazz));

        validate(annotations.directiveAnnotation(), clazz);

    }

    private static void validateNormalClass(TypeAnnotations annotations, Class clazz) {

        if (clazz.isAnnotation() || Modifier.isAbstract(clazz.getModifiers()) || clazz.isEnum() || clazz.isInterface()) {
            return;
        }

        Assert.isNull(annotations.unionAnnotation(), exception("a class can not map to GraphqlUnion", clazz));
        Assert.isNull(annotations.interfaceAnnotation(), exception("a class can not map to GraphqlInterface", clazz));
        Assert.isNull(annotations.enumAnnotation(), exception("a class can not map to GraphqlEnum", clazz));
        Assert.isOneOrMoreFalse(exception("at least one of GraphqlInputType, GraphqlType, GraphqlQuery, GraphqlSubscription or GraphqlMutation annotation is needed", clazz),
                annotations.inputTypeAnnotation() == null,
                annotations.typeAnnotation() == null,
                annotations.queryAnnotation() == null,
                annotations.mutationAnnotation() == null,
                annotations.subscriptionAnnotation() == null);

        GraphqlInputTypeAnnotation inputTypeAnnotation = annotations.inputTypeAnnotation();
        GraphqlTypeAnnotation typeAnnotation = annotations.typeAnnotation();
        GraphqlQueryAnnotation queryAnnotation = annotations.queryAnnotation();
        GraphqlMutationAnnotation mutationAnnotation = annotations.mutationAnnotation();
        GraphqlSubscriptionAnnotation subscriptionAnnotation = annotations.subscriptionAnnotation();

        final boolean topLevelType = queryAnnotation != null || mutationAnnotation != null || subscriptionAnnotation != null;

        if (topLevelType) {

            Assert.isAllTrue(exception("GraphqlQuery, GraphqlMutation and GraphqlSubscription can not combine by GraphqlType or GraphqlInputType", clazz),
                    annotations.inputTypeAnnotation() == null, annotations.typeAnnotation() == null, annotations.directiveAnnotation() == null);

            Assert.isExactlyOneTrue(exception("just one of GraphqlQuery, GraphqlMutation and GraphqlSubscription can present", clazz),
                    queryAnnotation != null,
                    subscriptionAnnotation != null,
                    mutationAnnotation != null);

            validate(queryAnnotation, clazz, true);
            validate(mutationAnnotation, clazz, true);
            validate(subscriptionAnnotation, clazz, true);

        } else {

            Assert.isOneOrMoreTrue(exception("at least one of GraphqlType or GraphqlInputType annotations is needed", clazz),
                    annotations.inputTypeAnnotation() != null, annotations.typeAnnotation() != null);

            if (typeAnnotation != null && inputTypeAnnotation != null) {
                Assert.isNotEquals(typeAnnotation.name(), inputTypeAnnotation.name(), exception("both GraphqlInputType and GraphqlType has same names", clazz));
            }

            validate(inputTypeAnnotation, clazz, true);
            validate(typeAnnotation, clazz, true);
        }

    }

    public static void validate(TypeAnnotations annotations, Class clazz) {
        Assert.isNotNull(annotations, exception("can not detect type annotations", clazz));

        isModifierValidForATypeClass(clazz);
        validateInterfaceClass(annotations, clazz);
        validateAbstractClass(annotations, clazz);
        validateEnumClass(annotations, clazz);
        validateNormalClass(annotations, clazz);
    }

    public static void validate(GraphqlDirectiveAnnotation annotation, Class clazz) {
        validate(annotation, clazz, false);
    }

    public static void validate(GraphqlDirectiveAnnotation annotation, Class clazz, boolean skipIfNull) {
        if (skipIfNull && annotation == null) {
            return;
        }
        validate((GraphqlElementAnnotation) annotation, clazz, skipIfNull);
        Assert.isNull(annotation.locations(), exception("no directive locations set", clazz));
        Assert.isPositive(annotation.locations().length, exception("no directive locations set", clazz));
    }

    public static void validate(GraphqlElementAnnotation annotation, Class clazz, boolean skipIfNull) {
        if (skipIfNull && annotation == null) {
            return;
        }
        Assert.isNotNull(annotation, exception("can not detect type annotation", clazz));
        Assert.isTrue(NameValidator.isNameValid(annotation.name()), exception("type name is not valid for GraphqlQuery", clazz));
    }

    public static void validate(GraphqlElementAnnotation annotation, Class clazz) {
        validate(annotation, clazz, false);
    }

    public static void validate(GraphqlTypeNameAnnotation annotation, Class clazz, boolean skipIfNull) {
        if (skipIfNull && annotation == null) {
            return;
        }
        validate((GraphqlElementAnnotation) annotation, clazz, skipIfNull);
    }

    public static void validate(GraphqlTypeNameAnnotation annotation, Class clazz) {
        validate(annotation, clazz, false);
    }

    public static void validate(ScalarEntry entry) {
        Assert.isNotNull(entry.type(), exception("provided type is null", entry.type()));
        Assert.isTrue(NameValidator.isNameValid(entry.name()), exception("type name is invalid", entry.type()));
        Assert.isNotNull(entry.coercing(), exception("provided coercing is null", entry.type()));
    }
}
