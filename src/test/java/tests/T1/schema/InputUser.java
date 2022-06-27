package tests.T1.schema;

import grphaqladapter.annotations.GraphqlInputField;
import grphaqladapter.annotations.GraphqlInputType;

@GraphqlInputType
public class InputUser {

    private String name;
    private UserType type;


    @GraphqlInputField(name = "name", nullable = false, setter = "setName")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @GraphqlInputField(name = "type", nullable = false, setter = "setType")
    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "InputUser{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
