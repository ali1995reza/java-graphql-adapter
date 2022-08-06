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
import graphql_adapter.adaptedschema.assertion.Assert;
import graphql_adapter.adaptedschema.functions.GraphqlDirectiveFunction;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.GraphqlElementDescriptionBuilder;

import java.util.HashSet;
import java.util.Set;

public class GraphqlDirectiveDescriptionBuilder extends GraphqlElementDescriptionBuilder<GraphqlDirectiveDescriptionBuilder, GraphqlDirectiveDescription> {

    public static GraphqlDirectiveDescriptionBuilder newBuilder() {
        return new GraphqlDirectiveDescriptionBuilder();
    }

    private final Set<Introspection.DirectiveLocation> locations = new HashSet<>();
    private Class<? extends GraphqlDirectiveFunction<?>> functionality;

    public GraphqlDirectiveDescription build() {
        return new GraphqlDirectiveDescriptionImpl(name(), description(), locations(), functionality());
    }

    @Override
    public GraphqlDirectiveDescriptionBuilder copy(GraphqlDirectiveDescription graphqlDirectiveDescription) {
        super.copy(graphqlDirectiveDescription)
                .functionality(graphqlDirectiveDescription.functionality());
        graphqlDirectiveDescription.locations().forEach(this::addLocation);
        return this;
    }

    @Override
    public GraphqlDirectiveDescriptionBuilder refresh() {
        this.clearLocations();
        this.functionality = null;
        return super.refresh();
    }

    public GraphqlDirectiveDescriptionBuilder addLocation(Introspection.DirectiveLocation location) {
        Assert.isNotNull(location, new NullPointerException("null location provided"));
        if (!locations.contains(location)) {
            this.locations.add(location);
        }
        return this;
    }

    public GraphqlDirectiveDescriptionBuilder addLocations(Introspection.DirectiveLocation... locations) {
        Assert.isNotNull(locations, new NullPointerException("null locations provided"));
        Assert.allNotNull(new NullPointerException("null location provided"), locations);
        for (Introspection.DirectiveLocation location : locations) {
            addLocation(location);
        }
        return this;
    }

    public GraphqlDirectiveDescriptionBuilder clearLocations() {
        this.locations.clear();
        return this;
    }

    public GraphqlDirectiveDescriptionBuilder functionality(Class<? extends GraphqlDirectiveFunction<?>> functionality) {
        this.functionality = functionality;
        return this;
    }

    public Class<? extends GraphqlDirectiveFunction<?>> functionality() {
        return functionality;
    }

    public Set<Introspection.DirectiveLocation> locations() {
        return new HashSet<>(locations);
    }

    public GraphqlDirectiveDescriptionBuilder removeLocation(Introspection.DirectiveLocation location) {
        this.locations.remove(location);
        return this;
    }
}
