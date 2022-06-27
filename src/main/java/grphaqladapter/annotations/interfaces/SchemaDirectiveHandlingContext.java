package grphaqladapter.annotations.interfaces;

import graphql.schema.TypeResolver;
import grphaqladapter.adaptedschemabuilder.mapped.MappedElement;
import grphaqladapter.codegenerator.AdaptedDataFetcher;

import java.util.function.Function;

public interface SchemaDirectiveHandlingContext {

    <T extends MappedElement> T getElement(String name);

    <T> SchemaDirectiveHandlingContext changeDataFetcherBehavior(String parent, String field, Function<AdaptedDataFetcher<T>, AdaptedDataFetcher<T>> function);

    SchemaDirectiveHandlingContext changeTypeResolverBehavior(String typeName, Function<TypeResolver, TypeResolver> function);
}
