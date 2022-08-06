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
package graphql_adapter.adaptedschema.mapping.strategy.descriptions.field;

import graphql_adapter.adaptedschema.mapping.strategy.descriptions.DefaultValueContainerElementDescriptionBuilder;

public class GraphqlInputFieldDescriptionBuilder extends DefaultValueContainerElementDescriptionBuilder<GraphqlInputFieldDescriptionBuilder, GraphqlInputFieldDescription> {

    public static GraphqlInputFieldDescriptionBuilder newBuilder() {
        return new GraphqlInputFieldDescriptionBuilder();
    }

    private String setter;

    private GraphqlInputFieldDescriptionBuilder() {
    }

    @Override
    public GraphqlInputFieldDescription build() {
        return new GraphqlInputFieldDescriptionImpl(name(), description(), nullability(), defaultValue(), setter());
    }

    @Override
    public GraphqlInputFieldDescriptionBuilder copy(GraphqlInputFieldDescription graphqlInputFieldDescription) {
        return super.copy(graphqlInputFieldDescription)
                .setter(graphqlInputFieldDescription.setter());
    }

    @Override
    public GraphqlInputFieldDescriptionBuilder refresh() {
        this.setter = null;
        return super.refresh();
    }

    public GraphqlInputFieldDescriptionBuilder setter(String setter) {
        this.setter = setter;
        return this;
    }

    public String setter() {
        return setter;
    }
}
