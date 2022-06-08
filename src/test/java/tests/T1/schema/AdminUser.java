package tests.T1.schema;

import grphaqladapter.annotations.GraphqlField;
import grphaqladapter.annotations.GraphqlType;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@GraphqlType(typeName = "Admin")
public class AdminUser implements UserInterface {


    private String name;

    public static AdminUser create(String name) {
        return new AdminUser()
                .setName(name);
    }

    public AdminUser setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public UserType type() {
        return UserType.ADMIN;
    }

    @GraphqlField
    public String token() {
        return Base64.getEncoder().encodeToString(name.getBytes(StandardCharsets.UTF_8));
    }
}
