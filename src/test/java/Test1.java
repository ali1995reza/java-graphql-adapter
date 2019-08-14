import grphaqladapter.adaptedschemabuilder.AdaptedGraphQLSchema;
import grphaqladapter.adaptedschemabuilder.AdaptedSchemaBuilder;
import grphaqladapter.annotations.*;

import java.util.ArrayList;
import java.util.List;

public class Test1 {


    @GraphqlInterface
    public static interface NameContainer{

        @GraphqlField
        public String name();
    }


    @GraphqlType
    public static class User implements NameContainer{


        @GraphqlField
        @Override
        public String name() {
            return "A user name !";
        }

        @GraphqlField
        public List<User> friends()
        {
            return null;
        }
    }

    @GraphqlQuery
    public static class MyQuery{

        @GraphqlField(nullable = false)
        public List<User> someUsers(@GraphqlArgument(argumentName = "name") String name)
        {
            return new ArrayList<>();
        }
    }



    public static void main(String[] args)
    {
        AdaptedGraphQLSchema schema = AdaptedSchemaBuilder
                .newBuilder()
                .add(NameContainer.class , User.class , MyQuery.class)
                .build();

        System.out.println(schema.asSchemaDefinitionLanguage());
    }
}
