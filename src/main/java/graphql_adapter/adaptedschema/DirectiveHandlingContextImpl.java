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

package graphql_adapter.adaptedschema;

import graphql.schema.TypeResolver;
import graphql_adapter.adaptedschema.assertion.Assert;
import graphql_adapter.adaptedschema.functions.SchemaDirectiveHandlingContext;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElement;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedObjectTypeClass;
import graphql_adapter.adaptedschema.mapping.mapped_elements.interfaces.MappedInterface;
import graphql_adapter.adaptedschema.mapping.mapped_elements.interfaces.MappedUnionInterface;
import graphql_adapter.adaptedschema.utils.StringUtils;
import graphql_adapter.codegenerator.AdaptedGraphQLDataFetcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static graphql_adapter.adaptedschema.utils.ClassUtils.cast;

final class DirectiveHandlingContextImpl implements SchemaDirectiveHandlingContext {

    private final Map<String, MappedElement> elements;
    private final Map<String, List<Function<AdaptedGraphQLDataFetcher<?>, AdaptedGraphQLDataFetcher<?>>>> dataFetchersBehavior = new HashMap<>();
    private final Map<String, List<Function<TypeResolver, TypeResolver>>> typeResolversBehavior = new HashMap<>();

    public DirectiveHandlingContextImpl(Map<String, MappedElement> elements) {
        this.elements = elements;
    }

    @Override
    public synchronized SchemaDirectiveHandlingContext changeDataFetcherBehavior(String parent, String field, Function<AdaptedGraphQLDataFetcher<?>, AdaptedGraphQLDataFetcher<?>> function) {
        Assert.isNotNull(function, new IllegalStateException("function is null"));
        MappedObjectTypeClass typeClass = getMappedTypeClass(parent);
        Assert.isTrue(typeClass.fieldMethods().containsKey(field), new IllegalStateException("can not find field [" + field + "] in type [" + parent + "]"));
        List<Function<AdaptedGraphQLDataFetcher<?>, AdaptedGraphQLDataFetcher<?>>> list = dataFetchersBehavior.computeIfAbsent(key(parent, field), x -> new ArrayList<>());
        list.add(function);
        return this;
    }

    @Override
    public synchronized SchemaDirectiveHandlingContext changeTypeResolverBehavior(String typeName, Function<TypeResolver, TypeResolver> function) {
        Assert.isNotNull(function, new IllegalStateException("function is null"));
        MappedElement element = getElement(typeName);
        Assert.isAllTrue(new IllegalStateException("can not find any type with type-resolve behavior with name [" + typeName + "]"),
                element != null,
                element instanceof MappedInterface || element instanceof MappedUnionInterface);
        List<Function<TypeResolver, TypeResolver>> list = typeResolversBehavior.computeIfAbsent(typeName, x -> new ArrayList<>());
        Assert.isNotNull(list, new IllegalStateException("[" + typeName + "] has not type-resolve behavior"));
        list.add(function);
        return this;
    }

    @Override
    public <T extends MappedElement> T getElement(String name) {
        return cast(elements.get(name));
    }

    AdaptedGraphQLDataFetcher<?> applyChanges(String parent, String field, AdaptedGraphQLDataFetcher<?> dataFetcher) {
        List<Function<AdaptedGraphQLDataFetcher<?>, AdaptedGraphQLDataFetcher<?>>> list = dataFetchersBehavior.get(key(parent, field));
        if (list == null) {
            return dataFetcher;
        }
        for (Function<AdaptedGraphQLDataFetcher<?>, AdaptedGraphQLDataFetcher<?>> function : list) {
            dataFetcher = function.apply(dataFetcher);
        }
        Assert.isNotNull(dataFetcher, new NullPointerException("data fetcher is null [field:" + field + ", parent:" + parent + "]"));
        return dataFetcher;
    }

    TypeResolver applyChanges(String typeName, TypeResolver typeResolver) {
        List<Function<TypeResolver, TypeResolver>> list = typeResolversBehavior.get(typeName);
        if (list == null) {
            return typeResolver;
        }
        for (Function<TypeResolver, TypeResolver> function : list) {
            typeResolver = function.apply(typeResolver);
        }
        Assert.isNotNull(typeName, new NullPointerException("type resolver is null [type:" + typeName + "]"));
        return typeResolver;
    }

    private MappedObjectTypeClass getMappedTypeClass(String name) {
        MappedElement element = elements.get(name);
        if (!(element instanceof MappedObjectTypeClass)) {
            throw new IllegalStateException("can not find any type with name [" + name + "]");
        }
        return (MappedObjectTypeClass) element;
    }

    private static String key(String parent, String field) {
        Assert.isTrue(StringUtils.isNonNullString(parent), new IllegalStateException("parent name is empty or null"));
        Assert.isTrue(StringUtils.isNonNullString(field), new IllegalStateException("field name is empty or null"));
        return parent + "_" + field;

    }
}
