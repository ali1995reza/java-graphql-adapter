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

package grphaqladapter.adaptedschema.mapping.strategy.descriptions.directive;

import graphql.introspection.Introspection;
import grphaqladapter.adaptedschema.assertutil.Assert;
import grphaqladapter.adaptedschema.functions.GraphqlDirectiveFunction;
import grphaqladapter.adaptedschema.mapping.strategy.descriptions.GraphqlElementDescriptionBuilder;

import java.util.ArrayList;
import java.util.List;

public class GraphqlDirectiveDescriptionBuilder extends GraphqlElementDescriptionBuilder<GraphqlDirectiveDescriptionBuilder, GraphqlDirectiveDescription> {

    public static GraphqlDirectiveDescriptionBuilder newBuilder() {
        return new GraphqlDirectiveDescriptionBuilder();
    }

    private final List<Introspection.DirectiveLocation> locations = new ArrayList<>();
    private Class<? extends GraphqlDirectiveFunction> functionality;

    public GraphqlDirectiveDescription build() {
        Introspection.DirectiveLocation[] locationsArray = new Introspection.DirectiveLocation[locations.size()];
        locations.toArray(locationsArray);
        return new GraphqlDirectiveDescriptionImpl(name(), description(), locationsArray, functionality());
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

    public GraphqlDirectiveDescriptionBuilder functionality(Class<? extends GraphqlDirectiveFunction> functionality) {
        this.functionality = functionality;
        return this;
    }

    public Class<? extends GraphqlDirectiveFunction> functionality() {
        return functionality;
    }

    public List<Introspection.DirectiveLocation> locations() {
        return new ArrayList<>(locations);
    }

}
