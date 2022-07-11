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

import graphql.Scalars;
import graphql_adapter.adaptedschema.discovered.DiscoveredScalarType;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedScalarClass;

final class BooleanDiscoveredType extends DiscoveredScalarTypeImpl {

    private final static BooleanDiscoveredType INSTANCE = new BooleanDiscoveredType();

    public static DiscoveredScalarType getInstance() {
        return INSTANCE;
    }

    private BooleanDiscoveredType() {
        super(MappedScalarClass.newScalarClass()
                        .baseClass(Boolean.class)
                        .name(Scalars.GraphQLBoolean.getName())
                        .description(Scalars.GraphQLBoolean.getDescription())
                        .coercing(Scalars.GraphQLBoolean.getCoercing())
                        .build(),
                Scalars.GraphQLBoolean.getName(),
                Scalars.GraphQLBoolean);
    }
}
