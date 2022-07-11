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
package graphql_adapter.adaptedschema.mapping.strategy.descriptions;

import graphql_adapter.adaptedschema.utils.builder.IBuilder;

public abstract class GraphqlElementDescriptionBuilder<T extends GraphqlElementDescriptionBuilder<T, E>, E extends GraphqlElementDescription> implements IBuilder<T, E> {

    private String name;
    private String description;

    @Override
    public T copy(E e) {
        this.refresh();
        return name(e.name())
                .description(e.description());
    }

    @Override
    public T refresh() {
        this.name = null;
        this.description = null;
        return castThis();
    }

    public String description() {
        return description;
    }

    public T description(String description) {
        this.description = description;
        return castThis();
    }

    public String name() {
        return name;
    }

    public T name(String name) {
        this.name = name;
        return castThis();
    }
}
