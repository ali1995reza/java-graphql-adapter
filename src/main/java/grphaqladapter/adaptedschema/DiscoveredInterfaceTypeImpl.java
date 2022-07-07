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

package grphaqladapter.adaptedschema;

import graphql.schema.GraphQLInterfaceType;
import grphaqladapter.adaptedschema.discovered.DiscoveredInterfaceType;
import grphaqladapter.adaptedschema.discovered.DiscoveredObjectType;
import grphaqladapter.adaptedschema.mapping.mapped_elements.interfaces.MappedInterface;

import java.util.*;

final class DiscoveredInterfaceTypeImpl extends DiscoveredTypeImpl<GraphQLInterfaceType, MappedInterface> implements DiscoveredInterfaceType {

    private List<DiscoveredObjectType> implementors = new ArrayList<>();
    private Map<Class, DiscoveredObjectType> implementorsByClass = new HashMap<>();
    private Map<String, DiscoveredObjectType> implementorsByName = new HashMap<>();


    public DiscoveredInterfaceTypeImpl(MappedInterface mappedInterface, String name, GraphQLInterfaceType graphQLType) {
        super(mappedInterface, name, graphQLType);
    }

    @Override
    public List<DiscoveredObjectType> implementors() {
        return this.implementors;
    }

    @Override
    public Map<Class, DiscoveredObjectType> implementorsByClass() {
        return this.implementorsByClass;
    }

    @Override
    public Map<String, DiscoveredObjectType> implementorsByName() {
        return this.implementorsByName;
    }

    @Override
    void makeImmutable() {
        if (this.implementors.isEmpty()) {
            this.implementors = Collections.emptyList();
            this.implementorsByClass = Collections.emptyMap();
            this.implementorsByName = Collections.emptyMap();
        } else {
            this.implementors = Collections.unmodifiableList(this.implementors);
            this.implementorsByClass = Collections.unmodifiableMap(this.implementorsByClass);
            this.implementorsByName = Collections.unmodifiableMap(this.implementorsByName);
        }
    }

    synchronized void addImplementor(DiscoveredObjectType implementor) {
        this.implementors.add(implementor);
        this.implementorsByClass.put(implementor.asMappedElement().baseClass(), implementor);
        this.implementorsByName.put(implementor.name(), implementor);
    }
}
