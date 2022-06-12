package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.argument;

import grphaqladapter.adaptedschemabuilder.assertutil.StringUtils;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.ParameterAnnotationDetector;
import grphaqladapter.annotations.GraphqlArgument;
import grphaqladapter.annotations.GraphqlArgumentAnnotation;
import grphaqladapter.annotations.impl.argument.GraphqlArgumentAnnotationBuilder;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class ParameterRealAnnotationDetector implements ParameterAnnotationDetector {

    private final GraphqlArgumentAnnotationBuilder builder;

    public ParameterRealAnnotationDetector() {
        this.builder = GraphqlArgumentAnnotationBuilder.newBuilder();
    }


    @Override
    public synchronized GraphqlArgumentAnnotation detectArgumentAnnotation(Method method, Parameter parameter, int parameterIndex) {

        GraphqlArgument argument = ParameterAnnotationLookup.findFirstAppears(method, parameterIndex, GraphqlArgument.class);

        if (argument == null)
            return null;

        String name = StringUtils.isNullString(argument.argumentName()) ? parameter.getName() : argument.argumentName();

        return builder.setArgumentName(name)
                .setNullable(argument.nullable())
                .build();
    }
}
