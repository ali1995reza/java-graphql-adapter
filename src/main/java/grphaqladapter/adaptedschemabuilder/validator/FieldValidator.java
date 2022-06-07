package grphaqladapter.adaptedschemabuilder.validator;

import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedParameter;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.FieldAnnotations;
import grphaqladapter.annotations.GraphqlFieldAnnotation;
import grphaqladapter.annotations.GraphqlInputFieldAnnotation;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class FieldValidator {


    public final static void validate(MappedMethod mappedMethod) {

        Assert.isNotNegative(mappedMethod.dimensions(), "can not create mapped method with dimensions <0");
        Assert.isNotNull(mappedMethod.method(), "provided method is null");
        Assert.isModifierValidForAFieldMethod(mappedMethod.method());
        Assert.isNotNull(mappedMethod.parameters(), "provided parameters is null");
        Assert.isNameValid(mappedMethod.fieldName());
        if (mappedMethod.isNullable()) Assert.isNullable(mappedMethod.type());

        if (CompletableFuture.class.isAssignableFrom(mappedMethod.method().getReturnType())) {
            //todo nothing here !
        } else if (mappedMethod.dimensions() > 0) {
            Assert.isEquals(mappedMethod.method().getReturnType(), List.class, "a list mapped method must has List return-type");
        } else {
            Assert.isEquals(mappedMethod.method().getReturnType(),
                    mappedMethod.type(), "parameter type and mapped type not equal");
        }

        if (mappedMethod.setter() != null) {
            Assert.isModifierValidForASetterMethod(mappedMethod.setter());
            Assert.isAValidSetterMethod(mappedMethod.method(), mappedMethod.setter());
        }

        for (MappedParameter parameter : mappedMethod.parameters()) {
            ArgumentValidator.validate(parameter);
            Assert.isParameterRelatedToMethod(parameter.parameter(), mappedMethod.method());

        }

        for (int i = 0; i < mappedMethod.parameters().size(); i++) {
            for (int j = i + 1; j < mappedMethod.parameters().size(); j++) {
                Assert.isOneFalse("2 MappedParameter with same typeName exist [" + mappedMethod.parameters().get(i) + "]",
                        mappedMethod.parameters().get(i).argumentName().
                                equals(mappedMethod.parameters().get(j).argumentName()));
            }
        }

    }


    public final static void validate(GraphqlFieldAnnotation annotation, boolean skipIfNull) {
        if (annotation == null && skipIfNull)
            return;
        Assert.isNotNull(annotation, "provided annotation is null");
        Assert.isOneFalse("setter method didn't set",
                annotation.inputField(), Assert.isNullString(annotation.setter()));

        if (Assert.isNoNullString(annotation.fieldName()))
            Assert.isNameValid(annotation.fieldName());
    }

    public final static void validate(GraphqlFieldAnnotation annotation) {
        validate(annotation, false);
    }

    public final static void validate(GraphqlInputFieldAnnotation annotation, boolean skipIfNull) {

        if (annotation == null && skipIfNull)
            return;
        Assert.isNotNull(annotation, "provided annotation is null");
        Assert.isOneFalse("setter method didn't set",
                Assert.isNullString(annotation.setter()));

        if (Assert.isNoNullString(annotation.inputFieldName()))
            Assert.isNameValid(annotation.inputFieldName());
    }

    public final static void validate(GraphqlInputFieldAnnotation annotation) {
        validate(annotation, false);
    }

    public final static void validateSetterMethodModifier(Method method) {
        Assert.isOneFalse("just public methods can be map to iput field setter",
                !Modifier.isPublic(method.getModifiers()));
    }

    public final static void validate(Class cls, Method method, FieldAnnotations annotations) {
        //todo fix it !
    }

}
