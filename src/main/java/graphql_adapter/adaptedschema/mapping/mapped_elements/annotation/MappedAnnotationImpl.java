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
package graphql_adapter.adaptedschema.mapping.mapped_elements.annotation;

import graphql.introspection.Introspection;
import graphql_adapter.adaptedschema.functions.GraphqlDirectiveFunction;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedClassImpl;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedAnnotationMethod;
import graphql_adapter.adaptedschema.utils.CollectionUtils;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

final class MappedAnnotationImpl extends MappedClassImpl implements MappedAnnotation {

    private final Map<String, MappedAnnotationMethod> mappedMethods;
    private final Set<Introspection.DirectiveLocation> locations;
    private final Class<? extends GraphqlDirectiveFunction<?>> functionality;

    MappedAnnotationImpl(String name, String description, Class<? extends Annotation> baseClass, Map<String, MappedAnnotationMethod> mappedMethods, Set<Introspection.DirectiveLocation> locations, Class<? extends GraphqlDirectiveFunction<?>> functionality) {
        super(name, MappedElementType.DIRECTIVE, description, Collections.emptyList(), baseClass);
        this.mappedMethods = CollectionUtils.getOrEmptyMap(mappedMethods);
        this.locations = CollectionUtils.getOrEmptySet(locations);
        this.functionality = functionality;
    }

    @Override
    public Class<? extends GraphqlDirectiveFunction<?>> functionality() {
        return functionality;
    }

    @Override
    public Set<Introspection.DirectiveLocation> locations() {
        return locations;
    }

    @Override
    public Map<String, MappedAnnotationMethod> mappedMethods() {
        return mappedMethods;
    }
}
