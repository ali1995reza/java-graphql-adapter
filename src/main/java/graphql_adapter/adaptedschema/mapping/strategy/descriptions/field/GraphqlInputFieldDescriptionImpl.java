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

import graphql_adapter.adaptedschema.mapping.strategy.descriptions.DefaultValueDetails;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.GraphqlDefaultValueContainerElementDescriptionImpl;

import java.util.List;

class GraphqlInputFieldDescriptionImpl extends GraphqlDefaultValueContainerElementDescriptionImpl implements GraphqlInputFieldDescription {

    private final String setter;

    public GraphqlInputFieldDescriptionImpl(String name, String description, List<Boolean> nullability, DefaultValueDetails defaultValue, String setter) {
        super(name, description, nullability, defaultValue);
        this.setter = setter;
    }

    @Override
    public String setter() {
        return this.setter;
    }
}
