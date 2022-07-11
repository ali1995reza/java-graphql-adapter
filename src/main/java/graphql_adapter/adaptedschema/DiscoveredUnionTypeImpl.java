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

import graphql.schema.GraphQLUnionType;
import graphql_adapter.adaptedschema.discovered.DiscoveredObjectType;
import graphql_adapter.adaptedschema.discovered.DiscoveredUnionType;
import graphql_adapter.adaptedschema.mapping.mapped_elements.interfaces.MappedUnionInterface;

import java.util.*;

final class DiscoveredUnionTypeImpl extends DiscoveredTypeImpl<GraphQLUnionType, MappedUnionInterface> implements DiscoveredUnionType {

    private List<DiscoveredObjectType> possibleTypes = new ArrayList<>();
    private Map<Class<?>, DiscoveredObjectType> possibleTypesByClass = new HashMap<>();
    private Map<String, DiscoveredObjectType> possibleTypesByName = new HashMap<>();

    public DiscoveredUnionTypeImpl(MappedUnionInterface mappedUnionInterface, String name, GraphQLUnionType graphQLType) {
        super(mappedUnionInterface, name, graphQLType);
    }

    @Override
    void makeImmutable() {
        if (this.possibleTypes.isEmpty()) {
            this.possibleTypes = Collections.emptyList();
            this.possibleTypesByClass = Collections.emptyMap();
            this.possibleTypesByName = Collections.emptyMap();
        } else {
            this.possibleTypes = Collections.unmodifiableList(this.possibleTypes);
            this.possibleTypesByClass = Collections.unmodifiableMap(this.possibleTypesByClass);
            this.possibleTypesByName = Collections.unmodifiableMap(this.possibleTypesByName);
        }
    }

    @Override
    public List<DiscoveredObjectType> possibleTypes() {
        return this.possibleTypes;
    }

    @Override
    public Map<Class<?>, DiscoveredObjectType> possibleTypesByClass() {
        return this.possibleTypesByClass;
    }

    @Override
    public Map<String, DiscoveredObjectType> possibleTypesByName() {
        return this.possibleTypesByName;
    }

    synchronized void addPossibleType(DiscoveredObjectType possibleType) {
        this.possibleTypes.add(possibleType);
        this.possibleTypesByClass.put(possibleType.asMappedElement().baseClass(), possibleType);
        this.possibleTypesByName.put(possibleType.name(), possibleType);
    }
}
