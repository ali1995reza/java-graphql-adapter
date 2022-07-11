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

package graphql_adapter.adaptedschema.mapping.strategy.descriptions.directive;

import graphql.introspection.Introspection;
import graphql_adapter.adaptedschema.functions.GraphqlDirectiveFunction;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.GraphqlElementDescriptionImpl;
import graphql_adapter.adaptedschema.utils.CollectionUtils;

import java.util.Set;

public class GraphqlDirectiveDescriptionImpl extends GraphqlElementDescriptionImpl implements GraphqlDirectiveDescription {

    private final Set<Introspection.DirectiveLocation> locations;
    private final Class<? extends GraphqlDirectiveFunction<?>> functionality;

    GraphqlDirectiveDescriptionImpl(String name, String description, Set<Introspection.DirectiveLocation> locations, Class<? extends GraphqlDirectiveFunction<?>> functionality) {
        super(name, description);
        this.locations = CollectionUtils.getOrEmptySet(locations);
        this.functionality = functionality;
    }

    @Override
    public Class<? extends GraphqlDirectiveFunction<?>> functionality() {
        return functionality;
    }

    @Override
    public Set<Introspection.DirectiveLocation> locations() {
        return locations;
    }
}
