package tests.T1.schema;

import grphaqladapter.annotations.GraphqlField;
import grphaqladapter.annotations.GraphqlType;

@GraphqlType
public class Car implements Vehicle {

    private String model;
    private Integer produceYear;

    @GraphqlField(fieldName = "model")
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @GraphqlField(fieldName = "produceYear")
    public Integer getProduceYear() {
        return produceYear;
    }

    public void setProduceYear(Integer produceYear) {
        this.produceYear = produceYear;
    }
}
