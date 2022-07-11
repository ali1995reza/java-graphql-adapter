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

import graphql.schema.GraphQLDirective;
import graphql_adapter.adaptedschema.discovered.DiscoveredDirective;
import graphql_adapter.adaptedschema.mapping.mapped_elements.annotation.MappedAnnotation;

final class DiscoveredDirectiveImpl implements DiscoveredDirective {

    private final MappedAnnotation mappedAnnotation;
    private final String name;
    private final GraphQLDirective graphQLDirective;

    DiscoveredDirectiveImpl(MappedAnnotation mappedAnnotation, String name, GraphQLDirective graphQLDirective) {
        this.mappedAnnotation = mappedAnnotation;
        this.name = name;
        this.graphQLDirective = graphQLDirective;
    }

    @Override
    public GraphQLDirective asGraphqlElement() {
        return graphQLDirective;
    }

    @Override
    public MappedAnnotation asMappedElement() {
        return mappedAnnotation;
    }

    @Override
    public String name() {
        return name;
    }
}
