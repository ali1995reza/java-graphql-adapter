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
import graphql_adapter.adaptedschema.utils.builder.IBuilder;

public final class ScalarEntryBuilder implements IBuilder<ScalarEntryBuilder, ScalarEntry> {

    public static ScalarEntryBuilder newBuilder() {
        return new ScalarEntryBuilder();
    }

    private Class<?> type;
    private String name;
    private String description;
    private Coercing<?, ?> coercing;

    private ScalarEntryBuilder() {

    }

    @Override
    public ScalarEntry build() {
        return new ScalaEntryImpl(type(),
                name(),
                description(),
                coercing()
        );
    }

    @Override
    public ScalarEntryBuilder copy(ScalarEntry scalarEntry) {
        return refresh()
                .name(scalarEntry.name())
                .description(scalarEntry.description())
                .coercing(scalarEntry.coercing())
                .type(scalarEntry.type());
    }

    @Override
    public ScalarEntryBuilder refresh() {
        this.type = null;
        this.name = null;
        this.description = null;
        this.coercing = null;
        return this;
    }

    public Coercing<?, ?> coercing() {
        return coercing;
    }

    public ScalarEntryBuilder coercing(Coercing<?, ?> coercing) {
        this.coercing = coercing;
        return this;
    }

    public String description() {
        return description;
    }

    public ScalarEntryBuilder description(String description) {
        this.description = description;
        return this;
    }

    public String name() {
        return name;
    }

    public ScalarEntryBuilder name(String name) {
        this.name = name;
        return this;
    }

    public Class<?> type() {
        return type;
    }

    public ScalarEntryBuilder type(Class<?> type) {
        this.type = type;
        return this;
    }
}
