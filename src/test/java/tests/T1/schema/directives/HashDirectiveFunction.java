/*
 * Copyright 2022 Alireza Akhoundi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tests.T1.schema.directives;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import grphaqladapter.adaptedschema.functions.GraphqlDirectiveFunction;
import grphaqladapter.adaptedschema.functions.SchemaDirectiveHandlingContext;
import grphaqladapter.adaptedschema.mapping.mapped_elements.classes.MappedObjectTypeClass;
import grphaqladapter.adaptedschema.mapping.mapped_elements.method.MappedFieldMethod;
import grphaqladapter.adaptedschema.system_objects.directive.GraphqlDirectiveDetails;
import grphaqladapter.adaptedschema.utils.DataFetcherAdapter;
import grphaqladapter.adaptedschema.utils.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

public class HashDirectiveFunction implements GraphqlDirectiveFunction {

    private MessageDigest digest;

    @Override
    public Object handleFieldDirective(GraphqlDirectiveDetails directive, Object value, Object source, MappedFieldMethod field, DataFetchingEnvironment env) {
        return hash(value, directive);
    }

    @Override
    public GraphQLFieldDefinition onField(GraphqlDirectiveDetails directive, GraphQLFieldDefinition fieldDefinition, MappedObjectTypeClass typeClass, MappedFieldMethod field, SchemaDirectiveHandlingContext context) {
        context.changeDataFetcherBehavior(typeClass.name(), field.name(), dataFetcher -> DataFetcherAdapter.of(dataFetcher, value -> hash(value, directive)));
        return GraphqlDirectiveFunction.super.onField(directive, fieldDefinition, typeClass, field, context);
    }

    private Object hasAsync(CompletableFuture future, GraphqlDirectiveDetails directive) {
        return future.thenApply(value -> hashSync(value, directive));
    }

    private Object hash(Object object, GraphqlDirectiveDetails directive) {
        if (object == null) {
            return null;
        }
        if (object instanceof CompletableFuture) {
            return hasAsync((CompletableFuture) object, directive);
        }
        return hashSync(object, directive);
    }

    private Object hashSync(Object object, GraphqlDirectiveDetails directive) {
        if (!(object instanceof String)) {
            return object;
        }
        String input = (String) object;
        MessageDigest messageDigest = messageDigest(directive.getArgument("algorithm"));
        messageDigest.update(input.getBytes(StandardCharsets.UTF_8));
        String salt = directive.getArgument("salt");
        if (StringUtils.isNonNullString(salt)) {
            messageDigest.update(salt.getBytes(StandardCharsets.UTF_8));
        }
        return Base64.getEncoder().encodeToString(messageDigest.digest());
    }

    private MessageDigest messageDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
