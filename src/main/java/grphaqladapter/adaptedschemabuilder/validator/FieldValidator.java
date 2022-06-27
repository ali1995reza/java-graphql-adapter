package grphaqladapter.adaptedschemabuilder.validator;

import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.assertutil.NameValidator;
import grphaqladapter.adaptedschemabuilder.assertutil.StringUtils;
import grphaqladapter.adaptedschemabuilder.exceptions.MappingGraphqlFieldException;
import grphaqladapter.adaptedschemabuilder.exceptions.SchemaExceptionBuilder;
import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;
import grphaqladapter.annotations.GraphqlFieldAnnotation;
import grphaqladapter.annotations.GraphqlInputFieldAnnotation;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class FieldValidator {

    private final static SchemaExceptionBuilder<MappingGraphqlFieldException> EXCEPTION_BUILDER = new SchemaExceptionBuilder(MappingGraphqlFieldException.class);

    private static MappingGraphqlFieldException exception(String message, Class clazz, Method method) {
        return EXCEPTION_BUILDER.exception(message, clazz, method, null);
    }


    public static void validate(MappedMethod mappedMethod, Class clazz, Method method) {

        /*Assert.isNotNegative(mappedMethod.type().dimensions(), exception("can not create mapped method with dimensions <0", clazz, method));
        Assert.isNotNull(mappedMethod.method(), exception("provided method is null", clazz, method));
        Assert.isModifierValidForAFieldMethod(mappedMethod.method());
        Assert.isNotNull(mappedMethod.parameters(), exception("provided parameters is null", clazz, method));
        Assert.isTrue(NameValidator.isNameValid(mappedMethod.name()), exception("type name is not valid", clazz, method));
        Assert.isOneOrMoreFalse(exception("Java primitive types can not be nullable", clazz, method),
                mappedMethod.type().isNullable(), mappedMethod.type().type().isPrimitive());

        if (CompletableFuture.class.isAssignableFrom(mappedMethod.method().getReturnType())) {
            //todo nothing here !
        } else if (mappedMethod.type().dimensions() > 0) {
            Assert.isEquals(mappedMethod.method().getReturnType(), List.class, exception("a list mapped method must has List return-type", clazz, method));
        } else {
            Assert.isEquals(mappedMethod.method().getReturnType(),
                    mappedMethod.type(), exception("parameter type and mapped type not equal", clazz, method));
        }

        if (mappedMethod.setter() != null) {
            Assert.isModifierValidForASetterMethod(mappedMethod.setter());
            Assert.isAValidSetterMethod(mappedMethod.method(), mappedMethod.setter());
        }

        for (MappedParameter parameter : mappedMethod.parameters()) {
            ArgumentValidator.validate(parameter, clazz, method, parameter.parameter());
            Assert.isParameterRelatedToMethod(parameter.parameter(), mappedMethod.method());
        }

        for (int i = 0; i < mappedMethod.parameters().size(); i++) {
            for (int j = i + 1; j < mappedMethod.parameters().size(); j++) {
                Assert.isOneOrMoreFalse(exception("2 MappedParameter with same name exist [" + mappedMethod.parameters().get(i) + "]", clazz, mappedMethod.method()),
                        mappedMethod.parameters().get(i).name().
                                equals(mappedMethod.parameters().get(j).name()));
            }
        }*/

    }

    public static void validateSetterMethod(Method setter, Class clazz, Method method) {
        Assert.isTrue(Modifier.isPublic(method.getModifiers()), exception("just public methods can set as setter method", clazz, setter));

        Assert.isTrue(setter.getParameters()[0].getParameterizedType()
                .equals(method.getGenericReturnType()), exception("setter method not match with field method", clazz, method));

    }

    public static void validate(GraphqlFieldAnnotation annotation, Class clazz, Method method) {
        Assert.isNotNull(annotation, exception("no annotation detected for method", clazz, method));
        Assert.isTrue(NameValidator.isNameValid(annotation.name()), exception("field name is invalid", clazz, method));
        Assert.isOneOrMoreFalse(exception("Java primitive type field can not be nullable", clazz, method),
                annotation.nullable(), method.getReturnType().isPrimitive());
    }

    public static void validate(GraphqlInputFieldAnnotation annotation, Class clazz, Method method) {
        Assert.isNotNull(annotation, exception("no annotation detected for method", clazz, method));
        Assert.isTrue(NameValidator.isNameValid(annotation.name()), exception("input field name is invalid", clazz, method));
        Assert.isOneOrMoreFalse(exception("Java primitive type input field can not be nullable", clazz, method),
                annotation.nullable(), method.getReturnType().isPrimitive());
        Assert.isTrue(StringUtils.isNoNullString(annotation.setter()), exception("input field setter method name is null", clazz, method));
        Assert.isEquals(method.getParameterCount(), 0, exception("input field method can not contains parameters", clazz, method));
    }
}
