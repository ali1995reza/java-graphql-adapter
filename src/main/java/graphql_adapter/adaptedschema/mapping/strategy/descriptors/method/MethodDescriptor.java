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
package graphql_adapter.adaptedschema.mapping.strategy.descriptors.method;

import graphql_adapter.adaptedschema.mapping.strategy.descriptions.argument.GraphqlDirectiveArgumentDescription;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.field.GraphqlFieldDescription;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.field.GraphqlInputFieldDescription;

import java.lang.reflect.Method;

public interface MethodDescriptor {

    GraphqlDirectiveArgumentDescription describeDirectiveArgument(Method method, Class<?> clazz);

    GraphqlFieldDescription describeField(Method method, Class<?> clazz);

    GraphqlInputFieldDescription describeInputField(Method method, Class<?> clazz);

    default boolean skipDirectiveArgument(Method method, Class<?> clazz) {
        return false;
    }

    default boolean skipField(Method method, Class<?> clazz) {
        return false;
    }

    default boolean skipInputField(Method method, Class<?> clazz) {
        return false;
    }
}
