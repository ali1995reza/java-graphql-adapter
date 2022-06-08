package tests.T1.schema;

import grphaqladapter.annotations.GraphqlArgument;
import grphaqladapter.annotations.GraphqlField;
import grphaqladapter.annotations.GraphqlQuery;

import java.util.ArrayList;
import java.util.List;

@GraphqlQuery
public class Query {

    @GraphqlField
    public IntList getList(@GraphqlArgument(argumentName = "period") IntPeriodScalar periodScalar) {

        List<Integer> list = new ArrayList<>();
        periodScalar.forEach(list::add);

        return IntList.of(list);

    }
}
