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
package graphql_adapter.adaptedschema.mapping.validator;

import graphql_adapter.adaptedschema.assertion.Assert;
import graphql_adapter.adaptedschema.exceptions.AdaptedGraphqlSchemaException;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElement;
import graphql_adapter.adaptedschema.mapping.mapped_elements.annotation.AppliedAnnotation;
import graphql_adapter.adaptedschema.utils.NameValidationUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static graphql_adapter.adaptedschema.exceptions.SchemaExceptionBuilder.exception;

public class ElementValidator {

    public static <T extends AdaptedGraphqlSchemaException> void validateElement(MappedElement element, Class<?> clazz, Method method, Parameter parameter, boolean validateName, Class<T> e) {
        Assert.isNotNull(element.mappedType(), exception(e, "element mapped type is null", clazz, method, parameter));
        if (validateName) {
            Assert.isTrue(NameValidationUtils.isNameValid(element.name()), exception(e, "element name is not valid", clazz, method, parameter));
        }
        final int numberOfAppliedAnnotations = element.appliedAnnotations().size();
        Assert.isEquals(numberOfAppliedAnnotations, element.appliedAnnotationsByClass().size(), exception(e, "element applied annotations not equal with each other", clazz, method, parameter));
        Assert.isEquals(numberOfAppliedAnnotations, element.appliedAnnotationsByName().size(), exception(e, "element applied annotations not equal with each other", clazz, method, parameter));

        for (AppliedAnnotation annotation : element.appliedAnnotations()) {
            AppliedAnnotation annotationByName = element.appliedAnnotationsByName().get(annotation.name());
            AppliedAnnotation annotationByClass = element.appliedAnnotationsByClass().get(annotation.annotationClass());
            Assert.isEquals(annotationByClass, annotationByName, exception(e, "resolved annotations from map and list not equals", clazz, method, parameter));
            Assert.isEquals(annotationByClass, annotation, exception(e, "resolved annotations from map and list not equals", clazz, method, parameter));
        }
    }

    public static <T extends AdaptedGraphqlSchemaException> void validateElement(MappedElement element, Class<?> clazz, Method method, boolean validateName, Class<T> e) {
        validateElement(element, clazz, method, null, validateName, e);
    }

    public static <T extends AdaptedGraphqlSchemaException> void validateElement(MappedElement element, Class<?> clazz, boolean validateName, Class<T> e) {
        validateElement(element, clazz, null, null, validateName, e);
    }
}
