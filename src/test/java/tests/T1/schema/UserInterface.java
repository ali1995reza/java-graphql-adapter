package tests.T1.schema;

import grphaqladapter.annotations.GraphqlField;
import grphaqladapter.annotations.GraphqlInterface;

@GraphqlInterface
public interface UserInterface {

    @GraphqlField(nullable = false)
    String name();

    @GraphqlField(nullable = false)
    UserType type();

}
