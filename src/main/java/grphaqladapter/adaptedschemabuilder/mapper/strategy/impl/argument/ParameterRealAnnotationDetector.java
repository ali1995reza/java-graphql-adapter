package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.argument;

import graphql.schema.DataFetchingEnvironment;
import grphaqladapter.adaptedschemabuilder.AdaptedGraphQLSchema;
import grphaqladapter.adaptedschemabuilder.assertutil.StringUtils;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.ParameterAnnotationDetector;
import grphaqladapter.annotations.GraphqlArgument;
import grphaqladapter.annotations.GraphqlArgumentAnnotation;
import grphaqladapter.annotations.SkipElement;
import grphaqladapter.annotations.impl.argument.GraphqlArgumentAnnotationBuilder;
import grphaqladapter.annotations.interfaces.GraphqlDirectivesHolder;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class ParameterRealAnnotationDetector implements ParameterAnnotationDetector {

    @Override
    public boolean skip(Method method, Parameter parameter, int parameterIndex) {
        return parameter.getAnnotation(SkipElement.class) != null;
    }

    @Override
    public boolean isSystemParameter(Method method, Parameter parameter, int parameterIndex) {
        return parameter.getType() == DataFetchingEnvironment.class ||
                parameter.getType() == GraphqlDirectivesHolder.class ||
                parameter.getType() == AdaptedGraphQLSchema.class;
    }

    @Override
    public GraphqlArgumentAnnotation detectArgumentAnnotation(Method method, Parameter parameter, int parameterIndex) {

        GraphqlArgument argument = ParameterAnnotationLookup.findFirstAppears(method, parameterIndex, GraphqlArgument.class);

        if (argument == null)
            return null;

        String name = StringUtils.isNullString(argument.name()) ? parameter.getName() : argument.name();

        return GraphqlArgumentAnnotationBuilder.newBuilder()
                .name(name)
                .nullable(argument.nullable())
                .build();
    }
}
