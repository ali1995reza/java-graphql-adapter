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

import graphql.schema.GraphQLSchema;
import graphql_adapter.adaptedschema.discovered.*;
import graphql_adapter.adaptedschema.tools.object_builder.ObjectBuilder;
import graphql_adapter.adaptedschema.tools.type_finder.TypeFinder;
import graphql_adapter.adaptedschema.utils.CollectionUtils;
import graphql_adapter.adaptedschema.utils.sdl.SDLUtils;
import graphql_adapter.codegenerator.ObjectConstructor;

import java.util.List;

public final class AdaptedGraphQLSchema {

    public static AdaptedGraphQLSchemaBuilder newSchema() {
        return AdaptedGraphQLSchemaBuilder.newBuilder();
    }

    private final GraphQLSchema schema;
    private final String sdl;
    private final List<DiscoveredElement<?, ?>> discoveredElements;
    private final List<DiscoveredInputType> discoveredInputTypes;
    private final List<DiscoveredInterfaceType> discoveredInterfacesTypes;
    private final List<DiscoveredObjectType> discoveredObjectTypes;
    private final List<DiscoveredUnionType> discoveredUnionTypes;
    private final List<DiscoveredEnumType> discoveredEnumTypes;
    private final List<DiscoveredScalarType> discoveredScalarTypes;
    private final List<DiscoveredDirective> discoveredDirectives;
    private final ObjectConstructor objectConstructor;
    private final ObjectBuilder objectBuilder;
    private final TypeFinder typeFinder;

    AdaptedGraphQLSchema(GraphQLSchema schema, List<DiscoveredElement<?, ?>> discoveredElements, ObjectConstructor objectConstructor, boolean usePariTypesForEachOther) {
        this.schema = schema;
        this.sdl = SDLUtils.toSDL(schema);
        this.discoveredInputTypes = CollectionUtils.separateToImmutableList(discoveredElements, DiscoveredInputType.class);
        this.discoveredInterfacesTypes = CollectionUtils.separateToImmutableList(discoveredElements, DiscoveredInterfaceType.class);
        this.discoveredObjectTypes = CollectionUtils.separateToImmutableList(discoveredElements, DiscoveredObjectType.class);
        this.discoveredUnionTypes = CollectionUtils.separateToImmutableList(discoveredElements, DiscoveredUnionType.class);
        this.discoveredEnumTypes = CollectionUtils.separateToImmutableList(discoveredElements, DiscoveredEnumType.class);
        this.discoveredScalarTypes = CollectionUtils.separateToImmutableList(discoveredElements, DiscoveredScalarType.class);
        this.discoveredDirectives = CollectionUtils.separateToImmutableList(discoveredElements, DiscoveredDirective.class);
        this.discoveredElements = CollectionUtils.combineToImmutableList(
                discoveredDirectives,
                discoveredScalarTypes,
                discoveredInputTypes,
                discoveredEnumTypes,
                discoveredInterfacesTypes,
                discoveredObjectTypes,
                discoveredUnionTypes
        );
        this.objectConstructor = objectConstructor;
        this.objectBuilder = ObjectBuilder.of(this.discoveredElements, objectConstructor, usePariTypesForEachOther);
        this.typeFinder = new TypeFinder(this.discoveredElements, usePariTypesForEachOther);
    }

    public String asSchemaDefinitionLanguage() {
        return sdl;
    }

    public List<DiscoveredDirective> discoveredDirectives() {
        return discoveredDirectives;
    }

    public List<DiscoveredElement<?, ?>> discoveredElements() {
        return discoveredElements;
    }

    public List<DiscoveredEnumType> discoveredEnumTypes() {
        return discoveredEnumTypes;
    }

    public List<DiscoveredInputType> discoveredInputTypes() {
        return discoveredInputTypes;
    }

    public List<DiscoveredInterfaceType> discoveredInterfacesTypes() {
        return discoveredInterfacesTypes;
    }

    public List<DiscoveredObjectType> discoveredObjectTypes() {
        return discoveredObjectTypes;
    }

    public List<DiscoveredScalarType> discoveredScalarTypes() {
        return discoveredScalarTypes;
    }

    public List<DiscoveredUnionType> discoveredUnionTypes() {
        return discoveredUnionTypes;
    }

    public GraphQLSchema getSchema() {
        return schema;
    }

    public ObjectBuilder objectBuilder() {
        return objectBuilder;
    }

    public ObjectConstructor objectConstructor() {
        return objectConstructor;
    }

    public TypeFinder typeFinder() {
        return typeFinder;
    }
}
