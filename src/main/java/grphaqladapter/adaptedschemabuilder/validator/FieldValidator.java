package grphaqladapter.adaptedschemabuilder.validator;

import grphaqladapter.adaptedschemabuilder.GraphqlQueryHandler;
import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedParameter;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.FieldAnnotations;
import grphaqladapter.annotations.GraphqlFieldAnnotation;
import grphaqladapter.annotations.GraphqlInputFieldAnnotation;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.function.Predicate;

public final class FieldValidator {


    public final static void validate(MappedMethod mappedMethod , boolean skipSetter)
    {

        Assert.ifNegative(mappedMethod.dimensions() , "can not create mapped method with dimensions <0");
        Assert.ifNull(mappedMethod.method() , "provided method is null");
        Assert.ifModifierNotValidForAFieldMethod(mappedMethod.method());
        Assert.ifNull(mappedMethod.parameters() , "provided parameters is null");
        Assert.ifNameNotValid(mappedMethod.fieldName());
        if(mappedMethod.isNullable())Assert.ifCantBeNullable(mappedMethod.type());
        if(mappedMethod.isQueryHandler())
        {

            Assert.ifNotEqual(mappedMethod.method().getReturnType(),
                    GraphqlQueryHandler.class , "parameter type and mapped type not equal");

        } else if(mappedMethod.dimensions()>0) {
            Assert.ifNotEqual(mappedMethod.method().getReturnType(), List.class , "a list mapped method must has List return-type");
        }else
        {
            Assert.ifNotEqual(mappedMethod.method().getReturnType(),
                    mappedMethod.type(), "parameter type and mapped type not equal");
        }

        if(!skipSetter)
            Assert.ifNull(mappedMethod.setter() , "setter can't be null - method ["+mappedMethod.method()+"]");

        if(!skipSetter || mappedMethod.setter()!=null) {
            Assert.ifModifierNotValidForASetterMethod(mappedMethod.setter());
            Assert.ifNotAValidSetterMethod(mappedMethod.method(), mappedMethod.setter());
        }

        for(MappedParameter parameter:mappedMethod.parameters())
        {
            ArgumentValidator.validate(parameter);
            Assert.ifParameterNotRelatedToMethod(parameter.parameter() , mappedMethod.method());

        }

        for(int i=0;i<mappedMethod.parameters().size();i++)
        {
            for(int j=i+1;j<mappedMethod.parameters().size();j++)
            {
                Assert.ifConditionTrue("2 MappedParameter with same typeName exist ["+mappedMethod.parameters().get(i)+"]" ,
                        mappedMethod.parameters().get(i).argumentName().
                                equals(mappedMethod.parameters().get(j).argumentName()));
            }
        }

    }

    public final static void validate(MappedMethod mappedMethod){
        validate(mappedMethod , false);
    }

    public final static void validate(GraphqlFieldAnnotation annotation , boolean skipIfNull)
    {
        if(annotation==null && skipIfNull)
            return;
        Assert.ifNull(annotation , "provided annotation is null");
        Assert.ifConditionTrue("setter method didn't set" ,
                annotation.inputField() , Assert.isNullString(annotation.setter()));

        if(Assert.isNoNullString(annotation.fieldName()))
            Assert.ifNameNotValid(annotation.fieldName());
    }
    public final static void validate(GraphqlFieldAnnotation annotation)
    {
        validate(annotation , false);
    }

    public final static void validate(GraphqlInputFieldAnnotation annotation , boolean skipIfNull)
    {

        if(annotation==null && skipIfNull)
            return;
        Assert.ifNull(annotation , "provided annotation is null");
        Assert.ifConditionTrue("setter method didn't set" ,
                Assert.isNullString(annotation.setter()));

        if(Assert.isNoNullString(annotation.inputFieldName()))
            Assert.ifNameNotValid(annotation.inputFieldName());
    }
    public final static void validate(GraphqlInputFieldAnnotation annotation)
    {
        validate(annotation , false);
    }

    public final static void validateSetterMethodModifier(Method method)
    {
        Assert.ifConditionTrue("just public methods can be map to iput field setter" ,
                !Modifier.isPublic(method.getModifiers()));
    }

    public final static void validate(Class cls , Method method , FieldAnnotations annotations)
    {
        //todo fix it !
    }

}
