package grphaqladapter.adaptedschemabuilder.validator;


import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.assertutil.NameValidator;
import grphaqladapter.adaptedschemabuilder.exceptions.MappingGraphqlTypeException;
import grphaqladapter.adaptedschemabuilder.exceptions.SchemaExceptionBuilder;
import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.TypeAnnotations;
import grphaqladapter.adaptedschemabuilder.scalar.ScalarEntry;
import grphaqladapter.annotations.*;

public final class TypeValidator {

    private final static SchemaExceptionBuilder<MappingGraphqlTypeException> EXCEPTION_BUILDER = new SchemaExceptionBuilder<>(MappingGraphqlTypeException.class);

    private static MappingGraphqlTypeException exception(String message, Class clazz) {
        return EXCEPTION_BUILDER.exception(message, clazz, null, null);
    }

    public static void validate(MappedClass mappedClass) {
        /*Assert.isNotNull(mappedClass.baseClass(), "base class is null");
        Assert.isModifierValidForATypeClass(mappedClass.baseClass());
        Assert.isNameValid(mappedClass.typeName());
        validateModifiers(mappedClass);
        for (MappedMethod method : mappedClass.mappedMethods().values()) {
            FieldValidator.validate(method);
            Assert.isMethodMemberOfClass(method.method(), mappedClass.baseClass());
        }*/

    }

    public static void validate(ScalarEntry entry) {
        Assert.isNotNull(entry.type(), exception("provided type is null", entry.type()));
        Assert.isTrue(NameValidator.isNameValid(entry.name()), exception("type name is invalid", entry.type()));
        Assert.isNotNull(entry.coercing(), exception("provided coercing is null", entry.type()));
    }

    private static void validateModifiers(MappedClass mappedClass) {
        /*if (mappedClass.mappedType().isOneOf(MappedClass.MappedType.INPUT_TYPE, MappedClass.MappedType.OBJECT_TYPE) ||
                mappedClass.mappedType().isTopLevelType()) {

            Assert.isAllTrue("just a public class can map to " + mappedClass.mappedType() + " [" + mappedClass.baseClass() + "]",
                    !mappedClass.baseClass().isInterface(),
                    !mappedClass.baseClass().isEnum(),
                    Modifier.isPublic(mappedClass.baseClass().getModifiers()));
        } else if (mappedClass.mappedType().is(MappedClass.MappedType.ENUM)) {
            Assert.isAllTrue("just enum class can map to " + mappedClass.mappedType() + " [" + mappedClass.baseClass() + "]",
                    Modifier.isPublic(mappedClass.baseClass().getModifiers()),
                    mappedClass.baseClass().isEnum());
        } else {
            Assert.isOneTrue("just interfaces can map to " + mappedClass.mappedType() + " [" + mappedClass.baseClass() + "]",
                    Modifier.isPublic(mappedClass.baseClass().getModifiers()),
                    mappedClass.baseClass().isInterface());
        }*/
    }

    public static void validate(GraphqlTypeAnnotation annotation, boolean skipIfNull) {
        /*if (annotation == null && skipIfNull)
            return;
        Assert.isNotNull(annotation, "provided annotation is null");
        if (Assert.isNoNullString(annotation.typeName()))
            Assert.isNameValid(annotation.typeName());*/
    }

    public static void validate(GraphqlTypeAnnotation annotation) {
        validate(annotation, false);
    }


    public static void validate(GraphqlInputTypeAnnotation annotation, boolean skipIfNull) {
        /*if (annotation == null && skipIfNull)
            return;
        Assert.isNotNull(annotation, "provided annotation is null");
        if (Assert.isNoNullString(annotation.typeName()))
            Assert.isNameValid(annotation.typeName());*/
    }

    public static void validate(GraphqlInputTypeAnnotation annotation) {
        validate(annotation, false);
    }


    public static void validate(GraphqlUnionAnnotation annotation, boolean skipIfNull) {
        /*if (annotation == null && skipIfNull)
            return;
        Assert.isNotNull(annotation, "provided annotation is null");
        if (Assert.isNoNullString(annotation.typeName()))
            Assert.isNameValid(annotation.typeName());*/
    }

    public static void validate(GraphqlUnionAnnotation annotation) {
        validate(annotation, false);
    }


    public static void validate(GraphqlInterfaceAnnotation annotation, boolean skipIfNull) {
        /*if (annotation == null && skipIfNull)
            return;

        Assert.isNotNull(annotation, "provided annotation is null");
        if (Assert.isNoNullString(annotation.typeName()))
            Assert.isNameValid(annotation.typeName());*/
    }

    public static void validate(GraphqlInterfaceAnnotation annotation) {
        validate(annotation, false);
    }

    public static void validate(GraphqlEnumAnnotation annotation, boolean skipIfNull) {
        /*if (annotation == null && skipIfNull)
            return;

        Assert.isNotNull(annotation, "provided annotation is null");
        if (Assert.isNoNullString(annotation.typeName()))
            Assert.isNameValid(annotation.typeName());*/

    }

    public final static void validate(GraphqlEnumAnnotation annotation) {

        validate(annotation, false);
    }


    public static void validate(Class cls, TypeAnnotations annotations) {
        /*Assert.isNotNull(cls, "provided class is null");
        Assert.isNotNull(annotations, "provided annotations is null");
        Assert.isModifierValidForATypeClass(cls);

        if (cls.isInterface()) {
            //so handle it please !
            Assert.isNull(annotations.typeAnnotation(), "an interface can not map to GraphqlType [" + cls + "]");
            Assert.isNull(annotations.inputTypeAnnotation(), "an interface can not map to GraphqlInputType [" + cls + "]");
            Assert.isNull(annotations.enumAnnotation(), "an interface can not map to GraphqlEnum [" + cls + "]");
            Assert.isNull(annotations.queryAnnotation(), "an interface can not map to GraphqlQuery [" + cls + "]");
            Assert.isNull(annotations.mutationAnnotation(), "an interface can not map to GraphqlMutation [" + cls + "]");
            Assert.isNull(annotations.subscriptionAnnotation(), "an interface can not map to GraphqlSubscription [" + cls + "]");

            Assert.isOneFalse("an interface can not map to both GraphqlUnion and GraphqlInterface [" + cls + "]",
                    annotations.unionAnnotation() != null, annotations.interfaceAnnotation() != null);
            Assert.isOneFalse("no annotation found for [" + cls + "]",
                    annotations.unionAnnotation() == null, annotations.interfaceAnnotation() == null);

            if (annotations.interfaceAnnotation() != null) {
                //so mapped to interface
                validate(annotations.interfaceAnnotation());
            } else {
                validate(annotations.unionAnnotation());
            }

        } else if (Modifier.isAbstract(cls.getModifiers())) {
            Assert.isNull(annotations.interfaceAnnotation(), "an abstract class can not map to GraphqlInterface [" + cls + "]");
            Assert.isNull(annotations.inputTypeAnnotation(), "an abstract class can not map to GraphqlInputType [" + cls + "]");
            Assert.isNull(annotations.enumAnnotation(), "an abstract class can not map to GraphqlEnum [" + cls + "]");
            Assert.isNull(annotations.unionAnnotation(), "an abstract class can not map to GraphqlUnion [" + cls + "]");
            Assert.isNull(annotations.queryAnnotation(), "an abstract class can not map to GraphqlQuery [" + cls + "]");
            Assert.isNull(annotations.mutationAnnotation(), "an abstract class can not map to GraphqlMutation [" + cls + "]");
            Assert.isNull(annotations.subscriptionAnnotation(), "an abstract class can not map to GraphqlSubscription [" + cls + "]");

            Assert.isNotNull(annotations.typeAnnotation(), "no annotation found for [" + cls + "]");

            validate(annotations.typeAnnotation());

        } else if (cls.isEnum()) {
            Assert.isNull(annotations.typeAnnotation(), "an enum class can not map to GraphqlType [" + cls + "]");
            Assert.isNull(annotations.inputTypeAnnotation(), "an enum class can not map to GraphqlInputType [" + cls + "]");
            Assert.isNull(annotations.interfaceAnnotation(), "an enum class can not map to GraphqlInterface [" + cls + "]");
            Assert.isNull(annotations.unionAnnotation(), "an enum class can not map to GraphqlUnion [" + cls + "]");
            Assert.isNull(annotations.queryAnnotation(), "an enum class can not map to GraphqlQuery [" + cls + "]");
            Assert.isNull(annotations.mutationAnnotation(), "an enum class can not map to GraphqlMutation [" + cls + "]");
            Assert.isNull(annotations.subscriptionAnnotation(), "an enum class can not map to GraphqlSubscription [" + cls + "]");


            Assert.isNotNull(annotations.enumAnnotation(), "no annotation found for [" + cls + "]");

            validate(annotations.enumAnnotation());

        } else {
            Assert.isNull(annotations.unionAnnotation(), "a class can not map to GraphqlUnion [" + cls + "]");
            Assert.isNull(annotations.interfaceAnnotation(), "a class can not map to GraphqlInterface [" + cls + "]");
            Assert.isNull(annotations.enumAnnotation(), "a class can not map to GraphqlEnum [" + cls + "]");
            Assert.isOneFalse("no annotation found for [" + cls + "]",
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

            Assert.isOneFalse("just one of GraphqlQuery , GraphqlMutation and GraphqlSubscription can present [" + cls + "]",
                    queryAnnotation != null && mutationAnnotation != null && subscriptionAnnotation != null,
                    queryAnnotation != null && mutationAnnotation != null,
                    queryAnnotation != null && subscriptionAnnotation != null,
                    mutationAnnotation != null && subscriptionAnnotation != null);

            final boolean topLevelType = queryAnnotation != null || mutationAnnotation != null || subscriptionAnnotation != null;

            Assert.isOneFalse("a top level type [Query,Mutation,Subscription] , can not map to other types [" + cls + "]",
                    topLevelType,
                    inputTypeAnnotation != null && typeAnnotation != null);

            if (topLevelType) {
                //so hndle it later please !
            } else if (inputTypeAnnotation != null && typeAnnotation != null) {
                String inputTypeName =
                        Assert.isNullString(inputTypeAnnotation.typeName()) ? cls.getSimpleName() : inputTypeAnnotation.typeName();
                String typeName =
                        Assert.isNullString(typeAnnotation.typeName()) ? cls.getSimpleName() : typeAnnotation.typeName();

                if (inputTypeName.equals(typeName)) {
                    throw new IllegalStateException("GraphqlType and GraphqlType have same typeName [typeName:" + inputTypeName + " , class:" + cls + "]");
                }

                Assert.isModifierValidForAnInputTypeClass(cls);
            } else if (inputTypeAnnotation != null) {
                Assert.isModifierValidForAnInputTypeClass(cls);
            }

        }*/
    }

    public static void validate(GraphqlQueryAnnotation annotation, boolean skipIfNull) {
        /*if (annotation == null && skipIfNull)
            return;
        Assert.isNotNull(annotation, "provided annotation is null");
        if (Assert.isNoNullString(annotation.typeName()))
            Assert.isNameValid(annotation.typeName());*/
    }

    public static void validate(GraphqlQueryAnnotation annotation) {
        validate(annotation, false);
    }

    public static void validate(GraphqlMutationAnnotation annotation, boolean skipIfNull) {
        /*if (annotation == null && skipIfNull)
            return;
        Assert.isNotNull(annotation, "provided annotation is null");
        if (Assert.isNoNullString(annotation.typeName()))
            Assert.isNameValid(annotation.typeName());*/
    }

    public static void validate(GraphqlMutationAnnotation annotation) {
        validate(annotation, false);
    }

    public static void validate(GraphqlSubscriptionAnnotation annotation, boolean skipIfNull) {
        /*if (annotation == null && skipIfNull)
            return;
        Assert.isNotNull(annotation, "provided annotation is null");
        if (Assert.isNoNullString(annotation.typeName()))
            Assert.isNameValid(annotation.typeName());*/
    }

    public static void validate(GraphqlSubscriptionAnnotation annotation) {
        validate(annotation, false);
    }
}
