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
package graphql_adapter.adaptedschema.discovered;

import graphql.schema.GraphQLInterfaceType;
import graphql_adapter.adaptedschema.mapping.mapped_elements.interfaces.MappedInterface;

import java.util.List;
import java.util.Map;

public interface DiscoveredInterfaceType extends DiscoveredType<GraphQLInterfaceType, MappedInterface> {

    List<DiscoveredObjectType> implementors();

    Map<Class<?>, DiscoveredObjectType> implementorsByClass();

    Map<String, DiscoveredObjectType> implementorsByName();
}