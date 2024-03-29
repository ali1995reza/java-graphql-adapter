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
package graphql_adapter.adaptedschema.scalar.impl;

import graphql.schema.Coercing;
import graphql_adapter.adaptedschema.scalar.ScalarEntry;

final class ScalaEntryImpl implements ScalarEntry {

    private final Class<?> type;
    private final String name;
    private final String description;
    private final Coercing<?, ?> coercing;

    ScalaEntryImpl(Class<?> type, String name, String description, Coercing<?, ?> coercing) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.coercing = coercing;
    }

    @Override
    public Coercing<?, ?> coercing() {
        return coercing;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Class<?> type() {
        return type;
    }
}
