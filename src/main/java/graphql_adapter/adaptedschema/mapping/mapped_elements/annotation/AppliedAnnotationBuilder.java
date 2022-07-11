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

import graphql_adapter.adaptedschema.assertion.Assert;
import graphql_adapter.adaptedschema.utils.builder.IBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static graphql_adapter.adaptedschema.utils.ClassUtils.cast;

public class AppliedAnnotationBuilder implements IBuilder<AppliedAnnotationBuilder, AppliedAnnotation> {

    public static AppliedAnnotationBuilder newBuilder() {
        return new AppliedAnnotationBuilder();
    }

    private final Map<String, Object> arguments = new HashMap<>();
    private String name;
    private Class<?> annotationClass;

    @Override
    public AppliedAnnotation build() {
        return new AppliedAnnotationImpl(name(), cast(annotationClass()), arguments());
    }

    @Override
    public AppliedAnnotationBuilder copy(AppliedAnnotation appliedAnnotation) {
        this.refresh();
        appliedAnnotation.arguments().forEach(this::addArgument);
        return name(appliedAnnotation.name())
                .annotationClass(appliedAnnotation.annotationClass());
    }

    @Override
    public AppliedAnnotationBuilder refresh() {
        this.clearArguments();
        this.name = null;
        this.annotationClass = null;
        return this;
    }

    public AppliedAnnotationBuilder addArgument(String name, Object value) {
        this.arguments.put(name, value);
        return this;
    }

    public Class<?> annotationClass() {
        return annotationClass;
    }

    public AppliedAnnotationBuilder annotationClass(Class<?> annotationClass) {
        Assert.isTrue(annotationClass.isAnnotation(), new IllegalStateException("applied-annotation just accept annotation class"));
        this.annotationClass = annotationClass;
        return this;
    }

    public Map<String, Object> arguments() {
        return Collections.unmodifiableMap(new HashMap<>(this.arguments));
    }

    public AppliedAnnotationBuilder clearArguments() {
        this.arguments.clear();
        return this;
    }

    public String name() {
        return name;
    }

    public AppliedAnnotationBuilder name(String name) {
        this.name = name;
        return this;
    }

    public AppliedAnnotationBuilder removeArgument(String name) {
        this.arguments.remove(name);
        return this;
    }
}
