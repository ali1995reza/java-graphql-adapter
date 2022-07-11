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

package test_graphql_adapter.schema.types;

import graphql_adapter.codegenerator.ObjectConstructor;
import graphql_adapter.codegenerator.impl.ReflectionObjectConstructor;

import static graphql_adapter.adaptedschema.utils.ClassUtils.cast;

public class CustomObjectConstructor implements ObjectConstructor {

    private final ObjectConstructor constructor = new ReflectionObjectConstructor();

    @Override
    public <T> T getInstance(Class<T> clazz) {
        if (clazz == InputUser.class) {
            return cast(new InputUser());
        }
        return constructor.getInstance(clazz);
    }
}
