package grphaqladapter.adaptedschemabuilder;

import graphql.language.SelectionSet;
import graphql.schema.DataFetchingFieldSelectionSet;

@Deprecated
public interface GraphqlQueryHandler<T> {

    void initialize(DataFetchingFieldSelectionSet set);
    T execute();
}
