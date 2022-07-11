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
package graphql_adapter.codegenerator.impl;

import graphql.TypeResolutionEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.TypeResolver;
import graphql_adapter.adaptedschema.discovered.DiscoveredInterfaceType;
import graphql_adapter.adaptedschema.discovered.DiscoveredObjectType;
import graphql_adapter.adaptedschema.discovered.DiscoveredUnionType;
import graphql_adapter.codegenerator.TypeResolverGenerator;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleTypeResolverGenerator implements TypeResolverGenerator {

    @Override
    public TypeResolver generate(DiscoveredUnionType unionType) {

        return new TypeResolverImpl(unionType);
    }

    @Override
    public TypeResolver generate(DiscoveredInterfaceType interfaceType) {
        return new TypeResolverImpl(interfaceType);
    }

    private final static class TypeResolverImpl implements TypeResolver {

        private final List<DiscoveredObjectType> possibleTypes;
        private final ConcurrentHashMap<Class<?>, GraphQLObjectType> resolvedTypesCache = new ConcurrentHashMap<>();

        private TypeResolverImpl(List<DiscoveredObjectType> possibleTypes) {
            this.possibleTypes = possibleTypes;
            for (DiscoveredObjectType objectType : this.possibleTypes) {
                resolvedTypesCache.put(objectType.asMappedElement().baseClass(),
                        objectType.asGraphqlElement());
            }
        }

        private TypeResolverImpl(DiscoveredUnionType unionType) {
            this(unionType.possibleTypes());
        }

        private TypeResolverImpl(DiscoveredInterfaceType interfaceType) {
            this(interfaceType.implementors());
        }

        @Override
        public GraphQLObjectType getType(TypeResolutionEnvironment typeResolutionEnvironment) {
            Class<?> clazz = typeResolutionEnvironment.getObject().getClass();
            return resolvedTypesCache.computeIfAbsent(clazz, this::findType);
        }

        private GraphQLObjectType findType(Class<?> clazz) {
            for (DiscoveredObjectType objectType : possibleTypes) {
                if (objectType.asMappedElement().baseClass().isAssignableFrom(clazz))
                    return objectType.asGraphqlElement();
            }
            return null;
        }
    }
}
