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

package grphaqladapter.adaptedschema.mapping.mapped_elements;

import grphaqladapter.adaptedschema.assertion.Assert;
import grphaqladapter.adaptedschema.mapping.mapped_elements.annotation.AppliedAnnotation;
import grphaqladapter.adaptedschema.utils.builder.IBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public abstract class MappedElementBuilder<T extends MappedElementBuilder<T, E>, E extends MappedElement> implements IBuilder<T, E> {

    private final MappedElementType elementType;
    private final List<AppliedAnnotation> appliedAnnotations = new ArrayList<>();
    private String name;
    private String description;

    protected MappedElementBuilder(MappedElementType elementType) {
        this.elementType = elementType;
    }

    @Override
    public T copy(E element) {
        if (!element.mappedType().is(elementType())) {
            throw new IllegalStateException("can not copy element - difference types [" + element.mappedType() + " , " + elementType() + "]");
        }
        this.refresh();
        element.appliedAnnotations().forEach(this::addAppliedAnnotation);
        return name(element.name())
                .description(element.description());
    }

    public T refresh() {
        this.clearAppliedAnnotations();
        this.name = null;
        this.description = null;
        return (T) this;
    }

    public T addAppliedAnnotation(AppliedAnnotation annotation) {
        Assert.isFalse(this.appliedAnnotations.stream().anyMatch(AppliedAnnotationClassPredictor.of(annotation)),
                new IllegalStateException("already an applied annotation exists for this class type [" + annotation.annotationClass() + "]"));
        this.appliedAnnotations.add(annotation);
        return (T) this;
    }

    public List<AppliedAnnotation> appliedAnnotations() {
        return Collections.unmodifiableList(new ArrayList<>(this.appliedAnnotations));
    }

    public T clearAppliedAnnotations() {
        this.appliedAnnotations.clear();
        return (T) this;
    }

    public T description(String description) {
        this.description = description;
        return (T) this;
    }

    public String description() {
        return description;
    }

    public final MappedElementType elementType() {
        return elementType;
    }

    public T name(String name) {
        this.name = name;
        return (T) this;
    }

    public String name() {
        return name;
    }

    public T removeAppliedAnnotation(AppliedAnnotation annotation) {
        this.appliedAnnotations.remove(annotation);
        return (T) this;
    }

    public T removeAppliedAnnotation(Class clazz) {
        this.appliedAnnotations.removeIf(AppliedAnnotationClassPredictor.of(clazz));
        return (T) this;
    }

    private final static class AppliedAnnotationClassPredictor implements Predicate<AppliedAnnotation> {

        private final Class clazz;

        private AppliedAnnotationClassPredictor(Class clazz) {
            this.clazz = clazz;
        }

        @Override
        public boolean test(AppliedAnnotation annotation) {
            return annotation.annotationClass() == clazz;
        }

        private static Predicate<AppliedAnnotation> of(Class clazz) {
            return new AppliedAnnotationClassPredictor(clazz);
        }

        private static Predicate<AppliedAnnotation> of(AppliedAnnotation annotation) {
            return of(annotation.annotationClass());
        }
    }
}
