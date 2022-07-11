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

package graphql_adapter.adaptedschema.mapping.strategy.descriptions.type;

import graphql_adapter.adaptedschema.mapping.strategy.descriptions.GraphqlTypeNameDescription;
import graphql_adapter.adaptedschema.utils.builder.IBuilder;

import java.util.function.BiFunction;

import static graphql_adapter.adaptedschema.utils.ClassUtils.cast;

public class TypesDescriptionBuilder<B extends TypesDescriptionBuilder<B, E>, E extends GraphqlTypeNameDescription> implements IBuilder<TypesDescriptionBuilder<B, E>, E> {

    public static <E extends GraphqlTypeNameDescription> TypesDescriptionBuilder<? extends TypesDescriptionBuilder<?, E>, E> newBuilder(BiFunction<String, String, E> builder) {
        return new TypesDescriptionBuilder<>(builder);
    }

    private final BiFunction<String, String, E> builder;
    private String name;
    private String description;

    TypesDescriptionBuilder(BiFunction<String, String, E> builder) {
        this.builder = builder;
    }

    @Override
    public B refresh() {
        this.name = null;
        this.description = null;
        return cast(this);
    }

    public String description() {
        return description;
    }

    public B description(String description) {
        this.description = description;
        return cast(this);
    }

    public String name() {
        return name;
    }

    @Override
    public E build() {
        return builder.apply(name(), description());
    }

    @Override
    public B copy(E e) {
        return refresh()
                .description(e.description())
                .name(e.name());
    }

    public B name(String name) {
        this.name = name;
        return cast(this);
    }
}
