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

package grphaqladapter.adaptedschema.mapping.mapped_elements.annotation;

import grphaqladapter.adaptedschema.mapping.mapped_elements.AppliedAnnotation;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AppliedAnnotationBuilder {

    public static AppliedAnnotationBuilder newBuilder() {
        return new AppliedAnnotationBuilder();
    }
    private final Map<String, Object> arguments = new HashMap<>();
    private String name;
    private Class<? extends Annotation> annotationClass;

    public AppliedAnnotationBuilder addArgument(String name, Object value) {
        this.arguments.put(name, value);
        return this;
    }

    public Class<? extends Annotation> annotationClass() {
        return annotationClass;
    }

    public AppliedAnnotationBuilder annotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
        return this;
    }

    public Map<String, Object> arguments() {
        return Collections.unmodifiableMap(new HashMap<>(this.arguments));
    }

    public AppliedAnnotation build() {
        return new AppliedAnnotationImpl(name(), annotationClass(), arguments());
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

    public AppliedAnnotationBuilder refresh() {
        this.clearArguments();
        this.name = null;
        this.annotationClass = null;
        return this;
    }

    public AppliedAnnotationBuilder removeArgument(String name) {
        this.arguments.remove(name);
        return this;
    }
}
