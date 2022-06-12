package tests.T1.schema;

import graphql.schema.DataFetchingEnvironment;
import grphaqladapter.annotations.GraphqlArgument;
import grphaqladapter.annotations.GraphqlField;
import grphaqladapter.annotations.GraphqlType;

import java.util.Collections;
import java.util.List;

@GraphqlType
public class IntList {

    private List<Integer> data = Collections.emptyList();

    public static IntList of(List<Integer> integers) {
        IntList list = new IntList();
        list.setData(integers);
        return list;
    }

    public void setData(List<Integer> data) {
        if (data == null) {
            data = Collections.emptyList();
        }
        this.data = data;
    }

    @GraphqlField
    public List<Integer> data() {
        return data;
    }

    @GraphqlField(nullable = false)
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @GraphqlField(nullable = false)
    public int size(DataFetchingEnvironment environment) {
        return data.size();
    }

    @GraphqlField
    public Integer get(@GraphqlArgument(argumentName = "index", nullable = false) int index) {
        return data.get(index);
    }
}
