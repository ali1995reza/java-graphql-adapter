package grphaqladapter.adaptedschemabuilder.utils;

import graphql.schema.DataFetchingEnvironment;
import grphaqladapter.adaptedschemabuilder.AdaptedGraphQLSchema;
import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.annotations.interfaces.GraphqlDirectivesHolder;
import grphaqladapter.codegenerator.AdaptedDataFetcher;

import java.util.function.Function;

public class DataFetcherAdapter implements AdaptedDataFetcher<Object> {

    public static <IN, OUT> AdaptedDataFetcher of(AdaptedDataFetcher<IN> wrapped, Function<IN, OUT> valueAdapter) {
        return new DataFetcherAdapter(wrapped, valueAdapter);
    }

    private final AdaptedDataFetcher wrapped;
    private final Function valueAdapter;

    public DataFetcherAdapter(AdaptedDataFetcher wrapped, Function valueAdapter) {
        Assert.isNotNull(wrapped, new NullPointerException("wrapped data fetcher is null"));
        Assert.isNotNull(valueAdapter, new NullPointerException("value handler is null"));
        this.wrapped = wrapped;
        this.valueAdapter = valueAdapter;
    }

    @Override
    public Object get(AdaptedGraphQLSchema schema, GraphqlDirectivesHolder directivesHolder, Object source, DataFetchingEnvironment environment) throws Exception {
        return valueAdapter.apply(wrapped.get(schema, directivesHolder, source, environment));
    }
}
