import graphql.execution.ExecutionContext;
import graphql.execution.ExecutionStrategy;
import graphql.execution.ExecutionStrategyParameters;
import graphql.execution.NonNullableFieldWasNullException;
import graphql.language.SelectionSet;
import graphql.schema.*;
import grphaqladapter.adaptedschemabuilder.GraphqlQueryHandler;
import grphaqladapter.adaptedschemabuilder.mapper.MappingStatics;
import grphaqladapter.codegenerator.impl.ReflectionDataFetcherGenerator;
import graphql.ExecutionResult;
import graphql.GraphQL;
import grphaqladapter.adaptedschemabuilder.AdaptedGraphQLSchema;
import grphaqladapter.adaptedschemabuilder.AdaptedSchemaBuilder;
import grphaqladapter.annotations.*;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class Main {

    public static interface  MyInt <T>{

        public T get();
    }

    public static class MC{

        @GraphqlField
        public boolean isWork(){return true;}
    }

    @GraphqlInterface
    public static interface AnInterface{

        @GraphqlField
        String gotIt(@GraphqlArgument(argumentName = "someName" , nullable = false) String s);
    }


    @GraphqlType
    @GraphqlInputType(typeName = "MyClassa")
    public static class MyClass extends MC implements MyInt<String> , AnInterface{

        @GraphqlField
        public String name(@GraphqlArgument String  sessionId)
        {
            return sessionId;
        }

        @GraphqlField
        public List<List<List<String>>> ss(){
            return null;
        }

        public void setGetVal(String s){

        }

        @GraphqlField(nullable = false , inputField = true , setter = "setGetVal")
        @Override
        public String get() {
            return null;
        }

        @GraphqlField(nullable = false)
        public MyClass subSet()
        {
            return null;
        }

        @Override
        @GraphqlField
        public String gotIt(@GraphqlArgument(argumentName = "someName" , nullable = false) String s) {
            return null;
        }
    }

    @GraphqlType
    public static class UrClass{

        @GraphqlField
        public String getName()
        {
            return null;
        }
    }


    @GraphqlEnum(typeName = "DifEnum")
    public enum  MyEnum{

        SS , PS , DS;
    }

    static interface  SS extends MyInt{

    }

    static volatile int ST = 100;

    static class InputClass{

    }

    static class ClassTest{


    }

    static GraphqlQueryHandler method(GraphqlQueryHandler<String> s)
    {
        return null;
    }

    public static void main(String[] args) throws NoSuchMethodException {

        Method method = Main.class.getDeclaredMethod("method" , GraphqlQueryHandler.class);

        System.out.println(MappingStatics.findTypeDetails(method.getParameters()[0]));
        System.exit(1);

        ReflectionDataFetcherGenerator generator =
                new ReflectionDataFetcherGenerator();

        AdaptedGraphQLSchema adaptedGraphQLSchema = AdaptedSchemaBuilder.newBuilder()
                .add(MyEnum.class , MyClass.class , UrClass.class , AnInterface.class)
                .build();


        System.out.println(adaptedGraphQLSchema.asSchemaDefinitionLanguage());

        System.out.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");

        GraphQL graphQL =
                GraphQL.newGraphQL(adaptedGraphQLSchema.getSchema()).build();

        ExecutionResult result = graphQL.execute("query maq{\n" +
                "    test{\n" +
                "        gotIt(someName : \"HelloWorld\" )\n" +
                "    }\n" +
                "}");

        System.out.println(result);


    }
}
