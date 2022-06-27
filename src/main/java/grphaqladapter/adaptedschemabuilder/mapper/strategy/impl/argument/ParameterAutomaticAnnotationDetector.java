package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.argument;

import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.ParameterAnnotationDetector;
import grphaqladapter.annotations.GraphqlArgumentAnnotation;
import grphaqladapter.annotations.impl.argument.GraphqlArgumentAnnotationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class ParameterAutomaticAnnotationDetector implements ParameterAnnotationDetector {

    private final String argNameIfNotPresent;

    private ParameterAutomaticAnnotationDetector(Class<? extends Annotation> notNullAnnotation, String argNameIfNotPresent) {
        this.notNullAnnotation = notNullAnnotation;
        this.argNameIfNotPresent = argNameIfNotPresent;
        builder = GraphqlArgumentAnnotationBuilder.newBuilder();
    }

    private final GraphqlArgumentAnnotationBuilder builder;
    private final Class<? extends Annotation> notNullAnnotation;

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public synchronized GraphqlArgumentAnnotation detectArgumentAnnotation(Method method, Parameter parameter, int parameterIndex) {
        return builder.name(getName(parameter, parameterIndex))
                .nullable(isNullable(parameter))
                .build();
    }

    private String getName(Parameter parameter, int index) {
        Assert.isOneOrMoreTrue(new IllegalStateException("can not find the type-name of parameter because parameter typeName not present"),
                !parameter.isNamePresent(), argNameIfNotPresent == null);
        if (parameter.isNamePresent()) {
            return parameter.getName();
        }
        return argNameIfNotPresent + index;
    }

    private boolean isNullable(Parameter parameter) {
        if (parameter.getType().isPrimitive()) {
            return false;
        }
        return notNullAnnotation == null ? true : parameter.getAnnotation(notNullAnnotation) == null;
    }

    public static class Builder {
        private Class<? extends Annotation> notNullAnnotation;
        private String argNameIfNotPresent;

        public Builder withNotNullAnnotation(Class<? extends Annotation> annotation) {
            this.notNullAnnotation = annotation;
            return this;
        }

        public Builder argNameIfNotPresent(String name) {
            this.argNameIfNotPresent = name;
            return this;
        }

        public ParameterAnnotationDetector build() {
            return new ParameterAutomaticAnnotationDetector(notNullAnnotation, argNameIfNotPresent);
        }
    }
}
