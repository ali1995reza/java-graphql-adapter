package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.argument;

import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.ParameterAnnotationDetector;
import grphaqladapter.adaptedschemabuilder.utils.Utils;
import grphaqladapter.annotations.GraphqlArgumentAnnotation;
import grphaqladapter.annotations.NotNull;
import grphaqladapter.annotations.impl.argument.GraphqlArgumentAnnotationBuilder;


import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

public class ParameterAutomaticAnnotationBuilder implements ParameterAnnotationDetector {

    private final GraphqlArgumentAnnotationBuilder builder;
    private final Class<? extends Annotation> notNullAnnotation;

    public ParameterAutomaticAnnotationBuilder(Class<? extends Annotation> notNullAnnotation) {
        this.notNullAnnotation = Utils.nullifyOrGetDefault(notNullAnnotation , NotNull.class);
        builder = GraphqlArgumentAnnotationBuilder.newBuilder();
    }

    public ParameterAutomaticAnnotationBuilder() {
        this(NotNull.class);
    }

    @Override
    public synchronized GraphqlArgumentAnnotation detectAnnotationFor(Parameter parameter, int parameterIndex) {

        Assert.ifConditionTrue("can not find the typeName of parameter because parameter typeName not present" ,
                !parameter.isNamePresent());

        boolean nullable = parameter.getAnnotation(notNullAnnotation)==null;
        return builder.setArgumentName(parameter.getName())
                .setNullable(nullable)
                .build();
    }
}
