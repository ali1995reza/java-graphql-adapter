import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import grphaqladapter.adaptedschemabuilder.AdaptedGraphQLSchema;
import grphaqladapter.adaptedschemabuilder.AdaptedSchemaBuilder;
import grphaqladapter.annotations.*;
import grphaqladapter.parser.PackageParser;
import grphaqladapter.parser.filter.NameFilter;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.BiFunction;

public class Test1 {

    private static ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

    @GraphqlInterface
    public static interface NameContainer {

        @GraphqlField
        public CompletableFuture<String> name();
    }

    @GraphqlType
    public static class User implements NameContainer {


        @GraphqlField
        @Override
        public CompletableFuture<String> name() {
            return new MyF<>("A user name !");
        }

        @GraphqlField
        public List<User> friends() {
            return null;
        }
    }

    @GraphqlQuery
    public static class MyQuery {

        @GraphqlField(nullable = false)
        public CompletableFuture<List<User>> someUsers(@GraphqlArgument(argumentName = "name") String name) {
            MyF f = new MyF();
            service.schedule(new Runnable() {
                @Override
                public void run() {
                    f.complete(Arrays.asList(new User()));
                }
            }, 5, TimeUnit.SECONDS);

            return f;
        }
    }

    public static class MyF<T> extends CompletableFuture<T> {
        public MyF(T t) {
            complete(t);
        }

        public MyF() {
        }

        @Override
        public <U> CompletableFuture<U> handle(BiFunction<? super T, Throwable, ? extends U> fn) {
            return super.handle(fn);
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        AdaptedGraphQLSchema schema = AdaptedSchemaBuilder
                .newBuilder()
                .addPackage("", NameFilter.startWith("Test1"))
                .build();

        GraphQL graphQL = GraphQL.newGraphQL(schema.getSchema()).build();

        ExecutionResult o = graphQL.execute(ExecutionInput.newExecutionInput().query("{\n" +
                "\tsomeUsers(name : \"myname\"){\n" +
                "\t\tname\n" +
                "\t}\n" +
                "}"));

        System.err.println(o.getData().toString());
        System.out.println(schema.asSchemaDefinitionLanguage());
    }
}
