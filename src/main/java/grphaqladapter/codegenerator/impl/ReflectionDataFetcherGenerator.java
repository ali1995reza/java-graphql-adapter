package grphaqladapter.codegenerator.impl;

import graphql.schema.DataFetchingEnvironment;
import grphaqladapter.adaptedschemabuilder.AdaptedGraphQLSchema;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredElement;
import grphaqladapter.adaptedschemabuilder.mapped.MappedFieldMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedParameter;
import grphaqladapter.adaptedschemabuilder.mapped.MappedTypeClass;
import grphaqladapter.annotations.interfaces.GraphqlDirectivesHolder;
import grphaqladapter.codegenerator.AdaptedDataFetcher;
import grphaqladapter.codegenerator.DataFetcherGenerator;

import java.util.List;

public class ReflectionDataFetcherGenerator implements DataFetcherGenerator {

    @Override
    public AdaptedDataFetcher generate(MappedTypeClass cls, MappedFieldMethod method) {
        return new ReflectionDataFetcher(cls, method);
    }

    @Override
    public synchronized void init(List<DiscoveredElement> elements) {
    }

    private final static class ReflectionDataFetcher implements AdaptedDataFetcher {

        private final MappedTypeClass mappedClass;
        private final MappedFieldMethod mappedFieldMethod;

        private ReflectionDataFetcher(MappedTypeClass mappedClass, MappedFieldMethod mappedFieldMethod) {
            this.mappedClass = mappedClass;
            this.mappedFieldMethod = mappedFieldMethod;
        }

        @Override
        public Object get(AdaptedGraphQLSchema schema, GraphqlDirectivesHolder directivesHolder, final Object source, DataFetchingEnvironment environment) throws Exception {

            final List<MappedParameter> parameters = mappedFieldMethod.parameters();

            Object result = null;
            if (parameters == null || parameters.size() < 1) {
                result = mappedFieldMethod.method().invoke(source);
            } else {
                Object[] args = new Object[parameters.size()];
                for (int i = 0; i < parameters.size(); i++) {
                    MappedParameter parameter = parameters.get(i);
                    if (parameter.model().isAdaptedSchema()) {
                        args[i] = schema;
                    } else if (parameter.model().isDataFetchingEnvironment()) {
                        args[i] = environment;
                    } else if (parameter.model().isDirectives()) {
                        args[i] = directivesHolder;
                    } else if (parameter.model().isSkipped()) {
                        args[i] = schema.typeFinder().getSkippedValue(parameter.parameter());
                    } else {
                        args[i] = schema.objectBuilder().buildFromObject(parameter.type(), environment.getArgument(parameter.name()));
                    }
                }

                result = mappedFieldMethod.method().invoke(source, args);
            }

            return result;
        }

    }

}
