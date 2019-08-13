package grphaqladapter.adaptedschemabuilder.validator;

import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.mapped.MappedParameter;
import grphaqladapter.annotations.*;

import java.lang.reflect.Parameter;
import java.util.List;

public final class ArgumentValidator {


    public final static void validate(MappedParameter mappedParameter)
    {
        Assert.ifNegative(mappedParameter.dimensions() , "can not create parameter with dimensions <0");
        Assert.ifNull(mappedParameter.type() , "provided type is null");
        Assert.ifNull(mappedParameter.parameter() , "provided parameter is null");
        Assert.ifNameNotValid(mappedParameter.argumentName());
        if(mappedParameter.dimensions()>0) {
            Assert.ifNotEqual(mappedParameter.parameter().getType(), List.class , "a list mapped parameter must has List type");
        }else
        {
            Assert.ifNotEqual(mappedParameter.parameter().getType() , mappedParameter.type() , "parameter type and mapped type not equal");
        }
    }

    public final static void validate(GraphqlArgumentAnnotation annotation)
    {
        Assert.ifNull(annotation , "provided annotation is null");
        if(Assert.isNoNullString(annotation.argumentName()))
            Assert.ifNameNotValid(annotation.argumentName());
    }

    public final static void validateArgumentAnnotation(Parameter parameter , GraphqlArgumentAnnotation argument)
    {
        Assert.ifNull(argument , "parameter dose not contain GraphqlArgument annotation ["+parameter+"]");


        Assert.ifConditionTrue("can not detect typeName for parameter ["+parameter+"] because typeName not set int GraphqlAnnotation and parameter typeName not present" ,
                Assert.isNullString(argument.argumentName()) , !parameter.isNamePresent());
        String name = Assert.isNullString(argument.argumentName())?parameter.getName():argument.argumentName();

        Assert.ifNameNotValid(name);
        if(argument.nullable())Assert.ifCantBeNullable(parameter.getType());


    }


    public final static void validateArgumentAnnotation(Parameter parameter)
    {
        GraphqlArgument argument = parameter.getAnnotation(GraphqlArgument.class);
        Assert.ifNull(argument , "parameter dose not contain GraphqlArgument annotation ["+parameter+"]");


        Assert.ifConditionTrue("can not detect typeName for parameter ["+parameter+"] because typeName not set int GraphqlAnnotation and parameter typeName not present" ,
                Assert.isNullString(argument.argumentName()) , !parameter.isNamePresent());
        String name = Assert.isNullString(argument.argumentName())?parameter.getName():argument.argumentName();

        Assert.ifNameNotValid(name);
        if(argument.nullable())Assert.ifCantBeNullable(parameter.getType());

    }

}
