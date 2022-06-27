package grphaqladapter.adaptedschemabuilder.validator;

import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.assertutil.NameValidator;
import grphaqladapter.adaptedschemabuilder.exceptions.MappingGraphqlArgumentException;
import grphaqladapter.adaptedschemabuilder.exceptions.SchemaExceptionBuilder;
import grphaqladapter.adaptedschemabuilder.mapped.MappedParameter;
import grphaqladapter.annotations.GraphqlArgumentAnnotation;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;


public final class ArgumentValidator {

    private final static SchemaExceptionBuilder<MappingGraphqlArgumentException> EXCEPTION_BUILDER = new SchemaExceptionBuilder<>(MappingGraphqlArgumentException.class);

    private static MappingGraphqlArgumentException exception(String message, Class clazz, Method method, Parameter parameter) {
        return EXCEPTION_BUILDER.exception(message, clazz, method, parameter);
    }

    private static MappingGraphqlArgumentException exception(String message, Parameter parameter) {
        return EXCEPTION_BUILDER.exception(message, null, null, parameter);
    }

    public static void validate(MappedParameter mappedParameter, Class clazz, Method method, Parameter parameter) {
        /*Assert.isNotNull(mappedParameter.type(), exception("provided mapped type is null", clazz, method, parameter));
        Assert.isNotNull(mappedParameter.parameter(), exception("provided parameter is null", clazz, method, parameter));
        if (mappedParameter.model().isDataFetchingEnvironment()) {
            Assert.isEquals(mappedParameter.type().dimensions(), 0, exception("env parameter with dimensions >0", clazz, method, parameter));
        } else {
            Assert.isNotNegative(mappedParameter.type().dimensions(), exception("can not create parameter with dimensions <0", clazz, method, parameter));
            Assert.isTrue(NameValidator.isNameValid(mappedParameter.name()), exception("mapped parameter name is invalid", clazz, method, parameter));
        }
        if (mappedParameter.type().dimensions() > 0) {
            Assert.isEquals(mappedParameter.parameter().getType(), List.class, exception("a list mapped parameter must has List type", clazz, method, parameter));
        } else {
            Assert.isEquals(mappedParameter.parameter().getType(), mappedParameter.type().type(), exception("parameter type and mapped type not equal", clazz, method, parameter));
        }*/
    }

    public static void validate(GraphqlArgumentAnnotation annotation, Class clazz, Method method, Parameter parameter) {
        Assert.isNotNull(annotation, exception("no annotation detected for parameter", clazz, method, parameter));
        Assert.isTrue(NameValidator.isNameValid(annotation.name()), exception("argument name is invalid", clazz, method, parameter));
        Assert.isOneOrMoreFalse(exception("Java primitive type argument can not be nullable", clazz, method, parameter),
                annotation.nullable(), parameter.getType().isPrimitive());
    }

}
