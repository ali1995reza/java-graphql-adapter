package grphaqladapter.adaptedschemabuilder;

import graphql.schema.DataFetchingFieldSelectionSet;

@Deprecated
public interface GraphqlQueryHandler<T> {

    void initialize(DataFetchingFieldSelectionSet set);

    T execute();
}
