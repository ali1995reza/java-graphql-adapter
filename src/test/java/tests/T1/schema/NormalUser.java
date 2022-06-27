package tests.T1.schema;

import grphaqladapter.annotations.GraphqlType;

@GraphqlType(name = "User")
public class NormalUser implements UserInterface {

    private String name;

    public static NormalUser create(String name) {
        return new NormalUser()
                .setName(name);
    }

    public NormalUser setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public UserType type() {
        return UserType.NORMAL;
    }
}
