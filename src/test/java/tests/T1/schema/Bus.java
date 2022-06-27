package tests.T1.schema;

import grphaqladapter.annotations.GraphqlField;
import grphaqladapter.annotations.GraphqlType;
import tests.T1.schema.directives.Delay;
import tests.T1.schema.directives.MD5;
import tests.T1.schema.directives.ToString;
import tests.T1.schema.directives.UpperCase;

@GraphqlType
public class Bus implements Vehicle {

    private String model;
    private Integer produceYear;
    private Integer size;

    @UpperCase
    @Delay(0)
    @MD5(salt = "some_salt")
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

    @ToString
    @GraphqlField(fieldName = "size")
    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
