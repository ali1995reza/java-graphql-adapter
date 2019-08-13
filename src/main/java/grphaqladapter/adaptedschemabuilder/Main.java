package grphaqladapter.adaptedschemabuilder;

import graphql.schema.DataFetchingFieldSelectionSet;
import grphaqladapter.codegenerator.impl.ReflectionDataFetcherGenerator;
import graphql.ExecutionResult;
import graphql.GraphQL;
import grphaqladapter.annotations.*;

import java.util.List;


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
        String gotIt(@GraphqlArgument(argumentName = "arg", nullable = false) List<List<MaInput>> s);
    }

    @GraphqlInputType
    public static class MaInput{

        private String maStr;

        @GraphqlInputField(setter = "setMaStr")
        public String maStr()
        {
            return maStr;
        }

        public void setMaStr(String maStr) {
            this.maStr = maStr;
        }

        @Override
        public String toString() {
            return "{maStr:"+maStr+"}";
        }
    }

    @GraphqlUnion
    public static interface MyUnion{}



    @GraphqlType
    @GraphqlInputType(typeName = "MyClassa")
    public static class MyClass extends MC implements MyInt<String> , AnInterface , MyUnion{

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
        public String gotIt(@GraphqlArgument(argumentName = "arg", nullable = false) List<List<MaInput>> s){

            return s.toString();
        }
    }

    @GraphqlType(typeName = "Query")
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

    @GraphqlQuery(typeName = "MaQueryType")
    public static class ClassTest{

        @GraphqlField
        public String test(List<List<List<List<MaInput>>>> arg)
        {
            return arg.toString();
        }
    }

    public static void main(String[] args)
    {

        AdaptedGraphQLSchema adaptedGraphQLSchema = AdaptedSchemaBuilder.newBuilder()
                .add(ClassTest.class, MyUnion.class , MyEnum.class , MyClass.class , UrClass.class , AnInterface.class , MaInput.class)
                .build();

        System .out.println(adaptedGraphQLSchema.asSchemaDefinitionLanguage());

        System.out.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");

        GraphQL graphQL =
                GraphQL.newGraphQL(adaptedGraphQLSchema.getSchema()).build();

        ExecutionResult result = graphQL.execute("query maq{\n" +
                "test(arg : [[{maStr:\"HelloWorld\"},{maStr:\"HelloWorld\"}],[{maStr:\"HelloWorld\"} , {maStr:\"HelloWorld\"}]] )\n" +
                "}");

            System.out.println(result);

        long start = System.currentTimeMillis();
        for(int i=0;i<1000;i++) {
            result = graphQL.execute("query maq{\n" +
                    "test(arg : [[{maStr:\"HelloWorld\"},{maStr:\"HelloWorld\"}],[{maStr:\"HelloWorld\"} , {maStr:\"HelloWorld\"}]] )\n" +
                    "}");

            if(!result.isDataPresent())
                System.out.println(result);
        }

        System.out.println(System.currentTimeMillis()-start);

    }
}
