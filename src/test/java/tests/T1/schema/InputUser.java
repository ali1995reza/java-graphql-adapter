package tests.T1.schema;

import grphaqladapter.annotations.GraphqlInputField;
import grphaqladapter.annotations.GraphqlInputType;

@GraphqlInputType
public class InputUser {

    private String name;
    private UserType type;


    @GraphqlInputField(inputFieldName = "name", nullable = false, setter = "setName")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @GraphqlInputField(inputFieldName = "type", nullable = false, setter = "setType")
    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }
}
