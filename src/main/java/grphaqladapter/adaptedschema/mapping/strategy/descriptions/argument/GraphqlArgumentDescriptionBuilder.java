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

package grphaqladapter.adaptedschema.mapping.strategy.descriptions.argument;

import grphaqladapter.adaptedschema.mapping.strategy.descriptions.DefaultValueContainerElementDescriptionBuilder;

public class GraphqlArgumentDescriptionBuilder extends DefaultValueContainerElementDescriptionBuilder<GraphqlArgumentDescriptionBuilder, GraphqlArgumentDescription> {

    public static GraphqlArgumentDescriptionBuilder newBuilder() {
        return new GraphqlArgumentDescriptionBuilder();
    }

    private boolean systemParameter = false;

    @Override
    public GraphqlArgumentDescription build() {
        return new GraphqlArgumentDescriptionImpl(name(), description(), isNullable(), defaultValue(), isSystemParameter());
    }

    @Override
    public GraphqlArgumentDescriptionBuilder copy(GraphqlArgumentDescription graphqlArgumentDescription) {
        return super.copy(graphqlArgumentDescription)
                .systemParameter(graphqlArgumentDescription.isSystemParameter());
    }

    @Override
    public GraphqlArgumentDescriptionBuilder refresh() {
        this.systemParameter = false;
        return super.refresh();
    }

    public boolean isSystemParameter() {
        return systemParameter;
    }

    public GraphqlArgumentDescriptionBuilder systemParameter(boolean systemParameter) {
        this.systemParameter = systemParameter;
        return this;
    }
}
