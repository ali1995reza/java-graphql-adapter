package grphaqladapter.adaptedschemabuilder.validator;

import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.mapped.MappedParameter;
import grphaqladapter.annotations.GraphqlArgument;
import grphaqladapter.annotations.GraphqlArgumentAnnotation;

import java.lang.reflect.Parameter;
import java.util.List;

public final class ArgumentValidator {

    public static void validate(MappedParameter mappedParameter) {
        Assert.isNotNull(mappedParameter.type(), "provided type is null");
        Assert.isNotNull(mappedParameter.parameter(), "provided parameter is null");
        if (mappedParameter.isEnv()) {
            Assert.isEquals(mappedParameter.dimensions(), 0, "env parameter with dimensions >0");
        } else {
            Assert.isNotNegative(mappedParameter.dimensions(), "can not create parameter with dimensions <0");
            Assert.isNameValid(mappedParameter.argumentName());
        }
        if (mappedParameter.dimensions() > 0) {
            Assert.isEquals(mappedParameter.parameter().getType(), List.class, "a list mapped parameter must has List type");
        } else {
            Assert.isEquals(mappedParameter.parameter().getType(), mappedParameter.type(), "parameter type and mapped type not equal");
        }
    }

    public static void validate(GraphqlArgumentAnnotation annotation) {
        Assert.isNotNull(annotation, "provided annotation is null");
        if (Assert.isNoNullString(annotation.argumentName()))
            Assert.isNameValid(annotation.argumentName());
    }

    public static void validateArgumentAnnotation(Parameter parameter, GraphqlArgumentAnnotation argument) {
        Assert.isNotNull(argument, "parameter dose not contain GraphqlArgument annotation [" + parameter + "]");


        Assert.isOneFalse("can not detect typeName for parameter [" + parameter + "] because typeName not set int GraphqlAnnotation and parameter typeName not present",
                Assert.isNullString(argument.argumentName()), !parameter.isNamePresent());
        String name = Assert.isNullString(argument.argumentName()) ? parameter.getName() : argument.argumentName();

        Assert.isNameValid(name);
        if (argument.nullable()) Assert.isNullable(parameter.getType());

    }


    public static void validateArgumentAnnotation(Parameter parameter) {
        GraphqlArgument argument = parameter.getAnnotation(GraphqlArgument.class);
        Assert.isNotNull(argument, "parameter dose not contain GraphqlArgument annotation [" + parameter + "]");


        Assert.isOneFalse("can not detect typeName for parameter [" + parameter + "] because typeName not set int GraphqlAnnotation and parameter typeName not present",
                Assert.isNullString(argument.argumentName()), !parameter.isNamePresent());
        String name = Assert.isNullString(argument.argumentName()) ? parameter.getName() : argument.argumentName();

        Assert.isNameValid(name);
        if (argument.nullable()) Assert.isNullable(parameter.getType());

    }

}
