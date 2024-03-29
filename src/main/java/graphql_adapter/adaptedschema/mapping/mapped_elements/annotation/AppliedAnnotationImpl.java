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

import graphql_adapter.adaptedschema.utils.CollectionUtils;

import java.lang.annotation.Annotation;
import java.util.Map;

final class AppliedAnnotationImpl implements AppliedAnnotation {

    private final String name;
    private final Class<? extends Annotation> annotationClass;
    private final Map<String, Object> arguments;

    AppliedAnnotationImpl(String name, Class<? extends Annotation> annotationClass, Map<String, Object> arguments) {
        this.name = name;
        this.annotationClass = annotationClass;
        this.arguments = CollectionUtils.getOrEmptyMap(arguments);
    }

    @Override
    public Class<? extends Annotation> annotationClass() {
        return this.annotationClass;
    }

    @Override
    public Map<String, Object> arguments() {
        return this.arguments;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String toString() {
        return "AppliedAnnotationImpl{" +
                "name='" + name + '\'' +
                ", annotationClass=" + annotationClass +
                ", arguments=" + arguments +
                '}';
    }
}
