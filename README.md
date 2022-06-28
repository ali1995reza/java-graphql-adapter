# [GraphQL](https://graphql.org/) Java Adapter

Purpose of this library is to create `GraphQLSchema` by consuming your java classes.

To create a Schema you have to use `AdaptedGraphQLSchemaBuilder` class. This class help you to create your schema using
your classes.

To add a class you can use :

```java
AdaptedGraphQLSchemaBuilder.newBuilder()
        .add(MyFirstClass.class,MySecondClass.class);
```

Or you can add all classes in a package using below code:

```java
AdaptedGraphQLSchemaBuilder.newBuilder()
        .addPackage("my.awesome.package");
```

And to creat the `AdaptedGraphQLSchema` you have to call `build()`.

```java
AdaptedGraphQLSchema adaptedSchema=AdaptedGraphQLSchemaBuilder.newBuilder()
        .add(MyFirstClass.class,MySecondClass.class)
        .addPackage("my.awesome.package")
        .build();
```

An `AdaptedGraphQLSchema` has some methods to get all `DiscoveredElements` and also a `GraphQLSchema` which you can use
it later to create a `GraphQL`instance.

It is possible to get GraphQLSchema by use `graphQLSchema()` method.

Also, a `AdaptedGraphQLSchema` will provide your full schema in graphql **SDL** by using below code:

```java
String schemaInSdl=adaptedSchema.asSchemaDefinitionLanguage();
        System.out.println(schemaInSdl);
```

### Mapping Strategy

How this schema will build? Ok let's explain. There is a tool which we call it `ClassMapper`that
each `AdaptedGraphQLSchemaBuilder` instance has one.You can access it like this:

```java
ClassMapper classMapper=AdaptedGraphQLSchemaBuilder.newBuilder()
        .add(MyFirstClass.class,MySecondClass.class)
        .addPackage("my.awesome.package")
        .mapper();
```

The strategy of how to map your classes is here. `ClassMapper` will use **detectors**
to detect the class descriptions.You can change it by your own way. The default mapping strategy will use **
annotations** and
**POJO** model.

There is 4 types of detectors :

- `ClassAnnotationDetector`, which detect annotations of a class
- `MethodAnnotationDetector`, which detect annotations of a method
- `ParameterAnnotationDetector`, which detect annotations of a parameter
- `AppliedDirectiveDetector`, which detect applied directives on elements

`ClassMapper` hold user a chain of detectors. To change the behavior of mapper you can set your detectors. For example
if you want to apply a custom `ClassAnnotationDetector`
you have to do it like this:

```java
classMapper.classAnnotationDetectorChain(ChainBuilder.newBuilder()
        .addToLast(new MyCustomClassAnnotationDetector())
        .addToLast(new ClassRealAnnotationDetector())
        //and more detectors ...
        .build());
```

### Annotations

As we explained in last part there is a dynamic mapping strategy. In this library I just developed an annotation base
mapping strategy. There is multiple annotations for this strategy.

#### @GraphqlType(`name() default ""`)

This annotation will use to set class as GraphQL object-type. You can specify the name or the default strategy will use
the class name.

#### @GraphqlInputType(`name() default ""`)

This annotation will use to set a class as GraphQL input-type. You can specify the name or the default strategy will use
the class name.

#### @GraphqlEnum(`name() default ""`)

This annotation will use to set an enum-class as GraphQL enum. You can specify the name or the default strategy will use
the enum-class name.

#### @GraphqlInterface(`name() default ""`)

This annotation will use to set an interface as GraphQL interface. You can specify the name or the default strategy will
use the interface name.

#### @GraphqlInterface(`name() default ""`)

This annotation will use to set an interface as GraphQL interface. You can specify the name or the default strategy will
use the interface name.

#### @GraphqlQuery(`name() default ""`)

This annotation will use to set class as GraphQL query. You can specify the name or the default strategy will use the
class name.

#### @GraphqlMutation(`name() default ""`)

This annotation will use to set class as GraphQL mutation. You can specify the name or the default strategy will use the
class name.

#### @GraphqlSubscription(`name() default ""`)

This annotation will use to set class as GraphQL subscription. You can specify the name or the default strategy will use
the class name.

#### @GraphqlDirective(`name() default "";` `locations();` `functionality() default DefaultGraphqlDirectiveFunction.class;`)

This annotation will use to set an annotation-type class as Graphql directive. You can specify the name or the default
strategy will use the class name. Also, you have to set possible locations of directive. There is an attribute
called `functionality` which is a class that will handle the directive functionality in building-schema time and
execution time.

#### @GraphqlField(`name() default "";` `nullable() default true;`)

This annotation will use to set a method as GraphQL field. You can specify the name or the default strategy will use the
method name. Also, it is possible to set field nullable. Each field is nullable by default.

#### @GraphqlInputField(`name() default "";` `nullable() default true;` `setter() default "";`)

This annotation will use to set a method as GraphQL input-field. You can specify the name or the default strategy will
use the method name. Also, it is possible to set input-field nullable. Each input-field is nullable by default. You have
to set the setter-method name of the input-field too.

#### @GraphqlArgument(`name() default "";` `nullable() default true;`)

This annotation will use to set a parameter of a method as GraphQL field. You can specify the name or the default
strategy will use the method name. Also, it is possible to set argument nullable. Each argument is nullable by default.
You can skip a parameter, so it will always be null or (0,false) in primitives. There is 3 system-parameter types which
will provide details of fetching which you can set them in any position of parameters. System-parameter types are
`DataFetchingEnvironment`, `GraphqlDirectivesHolder` and `AdaptedGraphQLSchema`.

#### @GraphqlDirectiveArgument(`name() default "";` `type() default Void.class;` `nullable() default true;` `dimensions() default -1;` `valueParser() default ValueParser.DefaultParser.class;`)

This annotation will use to set an annotation-method as a GraphQL directive argument. As possible types of an annotation
method is limited you can set custom type details for the method. If you set custom type you have to set a
custom `ValueParser` just for building-schema time. In execution time type resolving and building object will be
automatic even if you set custom type.

#### @SkipElement

If an element such as a class, method or parameter contains this annotation the default strategy will skip this element
and don't map it.

### Directive Functionality

Directives in GraphQL help to create more modularity. As GraphQL is just a specification each implementation of it
should implement this functionality by own self. In this library we have a `GraphqlDirectiveFunction` class which help
us to create functionality of a directive. Each directive can extends this class to create a custom functionality. For
example lets implement `@UpperCase` directive.

We will create `UpperCaseDirectiveFunction` like this:

```java
import graphql.language.OperationDefinition;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import grphaqladapter.adaptedschemabuilder.mapped.MappedFieldMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedTypeClass;
import grphaqladapter.adaptedschemabuilder.utils.DataFetcherAdapter;
import grphaqladapter.annotations.interfaces.GraphqlDirectiveFunction;
import grphaqladapter.annotations.interfaces.SchemaDirectiveHandlingContext;

import java.util.concurrent.CompletableFuture;

public class UpperCaseDirectiveFunction extends GraphqlDirectiveFunction<Object> {

    @Override
    public Object handleFieldDirective(Object value, Object source, MappedFieldMethod field, DataFetchingEnvironment env) {
        return upperCase(value);
    }

    @Override
    public Object handleOperationDirective(Object value, Object source, OperationDefinition operation, MappedFieldMethod field, DataFetchingEnvironment env) {
        return upperCase(value);
    }

    @Override
    public GraphQLFieldDefinition onField(GraphQLFieldDefinition fieldDefinition, MappedTypeClass typeClass, MappedFieldMethod field, SchemaDirectiveHandlingContext context) {
        if (field.type().type() != String.class || field.type().dimensions() > 0) {
            throw new IllegalStateException("UpperCase directive can just apply on String type fields");
        }
        context.changeDataFetcherBehavior(typeClass.name(), field.name(), dataFetcher -> DataFetcherAdapter.of(dataFetcher, this::upperCase));
        return fieldDefinition;
    }

    private CompletableFuture upperAsync(CompletableFuture future) {
        return future.thenApply(this::upperSync);
    }

    private Object upperSync(Object object) {
        if (!(object instanceof String)) {
            return object;
        }
        String str = (String) object;
        char[] data = str.toCharArray();
        for (int i = 0; i < data.length; i++) {
            if (Character.isUpperCase(data[i])) {
                continue;
            }
            data[i] = Character.toUpperCase(data[i]);
        }
        return new String(data);
    }

    private Object upperCase(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof CompletableFuture) {
            return upperAsync((CompletableFuture) object);
        } else {
            return upperSync(object);
        }

    }
}
```

And then you have to define directives like this:

```java
import graphql.introspection.Introspection;
import grphaqladapter.annotations.GraphqlDirective;
import grphaqladapter.annotations.GraphqlDirectiveArgument;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@GraphqlDirective(locations = {Introspection.DirectiveLocation.FIELD, Introspection.DirectiveLocation.FIELD_DEFINITION}, functionality = UpperCaseDirectiveFunction.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface UpperCase {
}
```

With this implementation you add this directive to a field like this:

```java
import grphaqladapter.annotations.GraphqlType;

import java.util.concurrent.CompletableFuture;

@GraphqlType
public class DeveloperInfo {

    @UpperCase
    @GraphqlField
    public String firstname() {
        return "Alireza"; //will return ALIREZA
    }

    @UpperCase
    @GraphqlField
    public CompletableFuture<String> lastname() {
        return CompletableFuture.completedFuture("Akhoundi"); //will return AKHOUNDI
    }

}
```

Or you can use it in operations like this:

```
query {
    getDeveloper {
        firstname
        lastname @UpperCase
    }
}
```

In some cases you need to get directives before handling field fetching. For this type of use-case you can
use `preHandleFieldDirective`. By overriding thi method you can add a custom functionality. Another way is to get
directives as parameter. You can add `GraphqlDirectivesHolder` to field parameters and default strategy will map it to
system-parameter and system will provide it in execution-time automatically.For example, we can get `@UpperCase`
directive like this:

```java
import grphaqladapter.annotations.GraphqlType;

@GraphqlType
public class MyType {

    @GraphqlField
    public String myField(GraphqlDirectivesHolder holder) {
        GraphqlDirectiveDetails details = holder.fieldDirectives()
                .directives().get(UpperCase.class);
        if (details != null) {
            return "SOME_DETAILS";
        }
        return "some_details";
    }
}
```

### ObjectConstructor

Both in building-schema time and execution time, need to create instance from difference classes. For example when a
query pass an input-type as a parameter, system will create an instance of that type in runtime. By default, system will
use the public-default constructor which has not any parameters. If you need your own object constructor you can
implement your custom object-constructor and set it in builder. For example, you will define your constructor like this:

```java
import grphaqladapter.codegenerator.ObjectConstructor;
import grphaqladapter.codegenerator.impl.ReflectionObjectConstructor;

public class MyCustomObjectConstructor implements ObjectConstructor {

    private final ReflectionObjectConstructor reflectionObjectConstructor = new ReflectionObjectConstructor();

    @Override
    public <T> T getInstance(Class<T> clazz) {
        if (clazz == MyFirstClass.class) {
            return new MyFirstClass("some input parameter");
        }
        return reflectionObjectConstructor.getInstance(clazz);
    }
}
```

And then set it like this:

```java
AdaptedGraphQLSchema adaptedSchema=AdaptedGraphQLSchemaBuilder.newBuilder()
        .add(MyFirstClass.class,MySecondClass.class)
        .addPackage("my.awesome.package")
        .objectConstructor(new MyCustomObjectConstructor())
        .build();
```

One of use-cases of custom constructor is when you're developing your app using spring, and you need defined beans and
DI(Dependency Injection).

### TypeFinder

After you create an `AdaptedGraphQLSchema` you can get the `TypeFinder` form it. A TypeFinder will help you
find `DiscoveredElement`s.For example, you can find
`DiscoveredObjectType` like this:

```java
DiscoveredObjectType discoveredObjectType=adaptedSchema
        .typeFinder().findObjectTypeByClass(MyFirstClass.class);
```

### ObjectBuilder

This tool will help you create object instances. For example, you can read an argument from DataFetching like this:

```java
import grphaqladapter.annotations.GraphqlType;

@GraphqlType
public class MyType {

    @GraphqlField
    public String myField(DataFetchingEnvironment environment, AdaptedGraphQLSchema schema) {
        MyInputType argument = (MyInputType) schema.objectBuilder()
                .buildFromObject(MyInputType.class, environment.getArgument("argument"));
        return "some result";
    }
}
```

### Lets Create Our Schema

In this section we will create a schema. We can not call this tutorial, but it is the same.

Let's describe our schema. We want to create a schema which let our clients calculate something like multiply matrices,
appending strings and some optional functions (Directives) on fields like
`@UpperCase` for `String`s.

First we need to create our custom input types. In this case we create an input-type for appending strings.

```java
import grphaqladapter.annotations.GraphqlInputType;

@GraphqlInputType
public class AppendStringParam {

    private String first;
    private String second;
    private Character appendingChar;

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public Character getAppendingChar() {
        return appendingChar;
    }

    public void setAppendingChar(Character appendingChar) {
        this.appendingChar = appendingChar;
    }
}
```

In next step we declare our `UpperCase` directive. We have to define the functionality first.

```java
import graphql.language.OperationDefinition;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import grphaqladapter.adaptedschemabuilder.mapped.MappedFieldMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedTypeClass;
import grphaqladapter.adaptedschemabuilder.utils.DataFetcherAdapter;
import grphaqladapter.annotations.interfaces.GraphqlDirectiveFunction;
import grphaqladapter.annotations.interfaces.SchemaDirectiveHandlingContext;

import java.util.concurrent.CompletableFuture;

public class UpperCaseDirectiveFunction extends GraphqlDirectiveFunction<Object> {

    @Override
    public Object handleFieldDirective(Object value, Object source, MappedFieldMethod field, DataFetchingEnvironment env) {
        return upperCase(value);
    }

    @Override
    public Object handleOperationDirective(Object value, Object source, OperationDefinition operation, MappedFieldMethod field, DataFetchingEnvironment env) {
        return upperCase(value);
    }

    @Override
    public GraphQLFieldDefinition onField(GraphQLFieldDefinition fieldDefinition, MappedTypeClass typeClass, MappedFieldMethod field, SchemaDirectiveHandlingContext context) {
        if (field.type().type() != String.class || field.type().dimensions() > 0) {
            throw new IllegalStateException("UpperCase directive can just apply on String type fields");
        }
        context.changeDataFetcherBehavior(typeClass.name(), field.name(), dataFetcher -> DataFetcherAdapter.of(dataFetcher, this::upperCase));
        return fieldDefinition;
    }

    private CompletableFuture upperAsync(CompletableFuture future) {
        return future.thenApply(this::upperSync);
    }

    private Object upperSync(Object object) {
        if (!(object instanceof String)) {
            return object;
        }
        String str = (String) object;
        char[] data = str.toCharArray();
        for (int i = 0; i < data.length; i++) {
            if (Character.isUpperCase(data[i])) {
                continue;
            }
            data[i] = Character.toUpperCase(data[i]);
        }
        return new String(data);
    }

    private Object upperCase(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof CompletableFuture) {
            return upperAsync((CompletableFuture) object);
        } else {
            return upperSync(object);
        }

    }
}
```

Now we are able to define the annotation for this directive:

```java
import graphql.introspection.Introspection;
import grphaqladapter.annotations.GraphqlDirective;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@GraphqlDirective(locations = {Introspection.DirectiveLocation.FIELD, Introspection.DirectiveLocation.FIELD_DEFINITION}, functionality = UpperCaseDirectiveFunction.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface UpperCase {
}
```

And now we have to create our Query type.

```java
import grphaqladapter.annotations.GraphqlArgument;
import grphaqladapter.annotations.GraphqlField;
import grphaqladapter.annotations.GraphqlQuery;

import java.util.ArrayList;
import java.util.List;

@GraphqlQuery
public class Query {

    @GraphqlField(nullable = false)
    public List<List<Integer>> multiplyMatrices(@GraphqlArgument(name = "a") List<List<Integer>> first,
                                                @GraphqlArgument(name = "b") int[][] second) {
        int i, j, k;

        int row1 = first.size();
        int col1 = first.get(0).size();

        int row2 = second.length;
        int col2 = second[0].length;

        if (row2 != col1) {
            throw new IllegalStateException("Multiplication Not Possible");
        }

        // Matrix to store the result
        // The product matrix will
        // be of size row1 x col2
        List<List<Integer>> result = new ArrayList<>();

        // Multiply the two matrices
        for (i = 0; i < row1; i++) {
            result.add(new ArrayList<>());
            for (j = 0; j < col2; j++) {
                int sum = 0;
                for (k = 0; k < row2; k++)
                    sum += first.get(i).get(k) * second[k][j];
                result.get(i).add(sum);
            }
        }
        return result;
    }

    @GraphqlField(nullable = false)
    public String appendStrings(@GraphqlArgument(name = "param") AppendStringParam param) {
        return param.getFirst() + param.getAppendingChar() + param.getSecond();
    }

}
```

After this we have to create schema from objects:

```java
AdaptedGraphQLSchema schema=AdaptedGraphQLSchemaBuilder
        .newBuilder()
        .add(AppendStringParam.class)
        .add(Query.class)
        .add(UpperCase.class)
        .build();

        GraphQL graphQL=GraphQL.newGraphQL(schema.getSchema()).build();
```

Now we can execute our queries like this:

```java
import graphql.ExecutionResult;
import graphql.GraphQL;
import grphaqladapter.adaptedschemabuilder.AdaptedGraphQLSchema;
import grphaqladapter.adaptedschemabuilder.AdaptedGraphQLSchemaBuilder;
import tests.T1.schema.directives.UpperCase;

import java.util.Map;

public class Main {

    public static void main(String[] args) {
        AdaptedGraphQLSchema schema = AdaptedGraphQLSchemaBuilder
                .newBuilder()
                .add(AppendStringParam.class)
                .add(Query.class)
                .add(UpperCase.class)
                .build();

        GraphQL graphQL = GraphQL.newGraphQL(schema.getSchema()).build();

        //multiply matrices
        ExecutionResult result = graphQL.execute("query {\n" +
                "    multiplyMatrices(a:[[1,1,1],[2,2,2],[3,3,3],[4,4,4]], " +
                "b:[[1,1,1,1],[2,2,2,2],[3,3,3,3]])\n" +
                "}");

        System.out.println(((Map) result.getData()).get("multiplyMatrices"));

        //append strings
        result = graphQL.execute("query {\n" +
                "    appendStrings(param:{first:\"Alireza\", second:\"Akhoundi\", appendingChar:\"@\"})\n" +
                "}");

        System.out.println(((Map) result.getData()).get("appendStrings"));

        //append strings and apply @UpperCase directrive
        result = graphQL.execute("query {\n" +
                "    appendStrings(param:{first:\"Alireza\", second:\"Akhoundi\", appendingChar:\"@\"}) @UpperCase\n" +
                "}");

        System.out.println(((Map) result.getData()).get("appendStrings"));
    }
}
```

After run output will something like this:

```
[[6, 6, 6, 6], [12, 12, 12, 12], [18, 18, 18, 18], [24, 24, 24, 24]]
Alireza@Akhoundi
ALIREZA@AKHOUNDI
```