package grphaqladapter.codegenerator;

import graphql.schema.DataFetchingEnvironment;
import grphaqladapter.adaptedschemabuilder.AdaptedGraphQLSchema;
import grphaqladapter.annotations.interfaces.GraphqlDirectivesHolder;

public interface AdaptedDataFetcher<T> {

    Object get(AdaptedGraphQLSchema schema, GraphqlDirectivesHolder directivesHolder, Object source, DataFetchingEnvironment environment) throws Exception;
}
