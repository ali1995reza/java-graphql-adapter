package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.directive;

import grphaqladapter.adaptedschemabuilder.mapper.strategy.DirectiveArgumentsValue;

import java.util.Collections;
import java.util.Map;

final class DirectiveArgumentsValueImpl implements DirectiveArgumentsValue {


    public static DirectiveArgumentsValue empty(Class annotationClass) {
        return new DirectiveArgumentsValueImpl(annotationClass);
    }


    private final Map<String, Object> values;
    private final Class annotationClass;

    DirectiveArgumentsValueImpl(Map<String, Object> values, Class annotationClass) {
        this.values = values;
        this.annotationClass = annotationClass;
    }

    DirectiveArgumentsValueImpl(Class annotationClass) {
        this(Collections.emptyMap(), annotationClass);
    }

    @Override
    public <T> T getArgumentValue(String argument) {
        return (T) values.get(argument);
    }

    @Override
    public Class annotationClass() {
        return annotationClass;
    }

    @Override
    public String toString() {
        return "DirectiveArgumentsValueImpl{" +
                "values=" + values +
                ", annotationClass=" + annotationClass +
                '}';
    }
}
