package tests.T1.schema.directives;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import grphaqladapter.adaptedschemabuilder.mapped.MappedFieldMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedTypeClass;
import grphaqladapter.adaptedschemabuilder.utils.DataFetcherAdapter;
import grphaqladapter.annotations.interfaces.GraphqlDirectiveFunction;
import grphaqladapter.annotations.interfaces.SchemaDirectiveHandlingContext;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

public class MD5DirectiveFunction extends GraphqlDirectiveFunction {

    MessageDigest digest;

    public MD5DirectiveFunction() {
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private Object hash(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof CompletableFuture) {
            return hasAsync((CompletableFuture) object);
        }
        return hashSync(object);
    }

    private Object hasAsync(CompletableFuture future) {
        return future.thenApply(this::hashSync);
    }

    private Object hashSync(Object object) {
        if (!(object instanceof String)) {
            return object;
        }
        String input = (String) object;
        digest.update(input.getBytes(StandardCharsets.UTF_8));
        String salt = directive().getArgument("salt");
        if (salt != null) {
            digest.update(salt.getBytes(StandardCharsets.UTF_8));
        }
        return Base64.getEncoder().encodeToString(digest.digest());
    }

    @Override
    public Object handleFieldDirective(Object value, Object source, MappedFieldMethod field, DataFetchingEnvironment env) {
        return hash(value);
    }

    @Override
    public GraphQLFieldDefinition onField(GraphQLFieldDefinition fieldDefinition, MappedTypeClass typeClass, MappedFieldMethod field, SchemaDirectiveHandlingContext context) {
        context.changeDataFetcherBehavior(typeClass.name(), field.name(), dataFetcher -> DataFetcherAdapter.of(dataFetcher, this::hash));
        return super.onField(fieldDefinition, typeClass, field, context);
    }
}
