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

import graphql.schema.GraphQLObjectType;
import grphaqladapter.adaptedschema.discovered.DiscoveredInterfaceType;
import grphaqladapter.adaptedschema.discovered.DiscoveredObjectType;
import grphaqladapter.adaptedschema.discovered.DiscoveredUnionType;
import grphaqladapter.adaptedschema.mapping.mapped_elements.classes.MappedObjectTypeClass;

import java.util.*;

final class DiscoveredObjectTypeImpl extends DiscoveredTypeImpl<GraphQLObjectType, MappedObjectTypeClass> implements DiscoveredObjectType {

    private List<DiscoveredInterfaceType> implementedInterfaces = new ArrayList<>();
    private Map<Class, DiscoveredInterfaceType> implementedInterfacesByClass = new HashMap<>();
    private Map<String, DiscoveredInterfaceType> implementedInterfacesByName = new HashMap<>();
    private List<DiscoveredUnionType> possibleUnionTypes = new ArrayList<>();
    private Map<Class, DiscoveredUnionType> possibleUnionTypesByClass = new HashMap<>();
    private Map<String, DiscoveredUnionType> possibleUnionTypesByName = new HashMap<>();

    public DiscoveredObjectTypeImpl(MappedObjectTypeClass mappedObjectTypeClass, String name, GraphQLObjectType graphQLType) {
        super(mappedObjectTypeClass, name, graphQLType);
    }

    @Override
    public List<DiscoveredInterfaceType> implementedInterfaces() {
        return this.implementedInterfaces;
    }

    @Override
    public Map<Class, DiscoveredInterfaceType> implementedInterfacesByClass() {
        return this.implementedInterfacesByClass;
    }

    @Override
    public Map<String, DiscoveredInterfaceType> implementedInterfacesByName() {
        return this.implementedInterfacesByName;
    }

    @Override
    void makeImmutable() {
        if (implementedInterfaces.isEmpty()) {
            this.implementedInterfaces = Collections.emptyList();
            this.implementedInterfacesByClass = Collections.emptyMap();
            this.implementedInterfacesByName = Collections.emptyMap();
        } else {
            this.implementedInterfaces = Collections.unmodifiableList(this.implementedInterfaces);
            this.implementedInterfacesByClass = Collections.unmodifiableMap(this.implementedInterfacesByClass);
            this.implementedInterfacesByName = Collections.unmodifiableMap(this.implementedInterfacesByName);
        }
        if (possibleUnionTypes.isEmpty()) {
            this.possibleUnionTypes = Collections.emptyList();
            this.possibleUnionTypesByClass = Collections.emptyMap();
            this.possibleUnionTypesByName = Collections.emptyMap();
        } else {
            this.possibleUnionTypes = Collections.unmodifiableList(this.possibleUnionTypes);
            this.possibleUnionTypesByClass = Collections.unmodifiableMap(this.possibleUnionTypesByClass);
            this.possibleUnionTypesByName = Collections.unmodifiableMap(this.possibleUnionTypesByName);
        }
    }

    @Override
    public List<DiscoveredUnionType> possibleUnionTypes() {
        return this.possibleUnionTypes;
    }

    @Override
    public Map<Class, DiscoveredUnionType> possibleUnionTypesByClass() {
        return this.possibleUnionTypesByClass;
    }

    @Override
    public Map<String, DiscoveredUnionType> possibleUnionTypesByName() {
        return this.possibleUnionTypesByName;
    }

    synchronized void addImplementedInterface(DiscoveredInterfaceType interfaceType) {
        this.implementedInterfaces.add(interfaceType);
        this.implementedInterfacesByClass.put(interfaceType.asMappedElement().baseClass(), interfaceType);
        this.implementedInterfacesByName.put(interfaceType.name(), interfaceType);
    }

    synchronized void addPossibleUnionType(DiscoveredUnionType unionType) {
        this.possibleUnionTypes.add(unionType);
        this.possibleUnionTypesByClass.put(unionType.asMappedElement().baseClass(), unionType);
        this.possibleUnionTypesByName.put(unionType.name(), unionType);
    }
}
