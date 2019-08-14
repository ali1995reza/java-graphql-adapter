import grphaqladapter.adaptedschemabuilder.AdaptedGraphQLSchema;
import grphaqladapter.adaptedschemabuilder.AdaptedSchemaBuilder;
import grphaqladapter.annotations.*;

import javax.xml.ws.FaultAction;
import java.util.List;

public class Test2 {

    @GraphqlInterface
    public static interface Character{

        @GraphqlField(nullable = false)
        String id();
        @GraphqlField(nullable = false)
        String name();
        @GraphqlField
        List<Character> friends();
        @GraphqlField(nullable = false)
        List<Episode> appearsIn();

    }


    @GraphqlType
    public class Human implements Character{

        @GraphqlField(nullable = false)
        @Override
        public String id() {
            return null;
        }

        @GraphqlField(nullable = false)
        @Override
        public String name() {
            return null;
        }

        @GraphqlField
        @Override
        public List<Character> friends() {
            return null;
        }

        @GraphqlField(nullable = false)
        @Override
        public List<Episode> appearsIn() {
            return null;
        }

        @GraphqlField
        public String homePlanet()
        {
            return null;
        }
    }


    @GraphqlType
    public class Droid implements Character{

        @GraphqlField(nullable = false)
        @Override
        public String id() {
            return null;
        }

        @GraphqlField(nullable = false)
        @Override
        public String name() {
            return null;
        }

        @GraphqlField
        @Override
        public List<Character> friends() {
            return null;
        }

        @GraphqlField(nullable = false)
        @Override
        public List<Episode> appearsIn() {
            return null;
        }

        @GraphqlField
        public String primaryFunction()
        {
            return null;
        }
    }

    @GraphqlEnum
    public static enum Episode{
        NEW_HOPE ,
        EMPIRE ,
        JEDI
    }



    @GraphqlQuery
    public static class QueryType{

        @GraphqlField
        public Character hero(@GraphqlArgument(argumentName = "episode") Episode episode){
            return null;
        }

        @GraphqlField
        public Human human(@GraphqlArgument(argumentName = "id")String id){
            return null;
        }

        @GraphqlField
        public Droid droid(@GraphqlArgument(argumentName = "id")String id){
            return null;
        }
    }


    public static void main(String[] args)
    {
        AdaptedGraphQLSchema schema = AdaptedSchemaBuilder.newBuilder()
                .add(Character.class
                        , Human.class
                        , Droid.class
                        , Episode.class
                        , QueryType.class)
                .build();

        System.out.println(schema.asSchemaDefinitionLanguage());

    }
}
