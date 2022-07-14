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
package graphql_adapter.adaptedschema.mapping.strategy.descriptors.annotations;

import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElement;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import graphql_adapter.adaptedschema.mapping.mapped_elements.annotation.MappedAnnotation;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedClass;
import graphql_adapter.adaptedschema.mapping.mapped_elements.enums.MappedEnumConstant;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedAnnotationMethod;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedMethod;
import graphql_adapter.adaptedschema.mapping.mapped_elements.parameter.MappedParameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class AnnotationBaseAppliedDirectiveDescriptor implements AppliedDirectiveDescriptor {

    @Override
    public List<DirectiveArgumentsValue> describeAppliedDirectives(MappedElement element, Map<Class<?>, MappedAnnotation> annotationsMap) {
        if (element.mappedType().isOneOf(MappedElementType.FIELD, MappedElementType.INPUT_FIELD)) {
            MappedMethod method = (MappedMethod) element;
            return detectDirectives(method.method().getAnnotations(), annotationsMap);
        } else if (element instanceof MappedClass) {
            MappedClass mappedClass = (MappedClass) element;
            return detectDirectives(mappedClass.baseClass().getAnnotations(), annotationsMap);
        } else if (element instanceof MappedEnumConstant) {
            MappedEnumConstant constant = (MappedEnumConstant) element;
            Field field = getField(constant);
            return detectDirectives(field.getAnnotations(), annotationsMap);
        } else if (element.mappedType().isArgument()) {
            if (element instanceof MappedParameter) {
                MappedParameter parameter = (MappedParameter) element;
                return detectDirectives(parameter.parameter().getAnnotations(), annotationsMap);
            } else if (element instanceof MappedAnnotationMethod) {
                MappedAnnotationMethod method = (MappedAnnotationMethod) element;
                return detectDirectives(method.method().getAnnotations(), annotationsMap);
            }
        }
        return null;
    }

    private List<DirectiveArgumentsValue> detectDirectives(Annotation[] annotations, Map<Class<?>, MappedAnnotation> annotationsMap) {
        if (annotations == null || annotations.length == 0) {
            return Collections.emptyList();
        }
        List<DirectiveArgumentsValue> values = new ArrayList<>();
        for (Annotation annotation : annotations) {
            MappedAnnotation mappedAnnotation = annotationsMap.get(annotation.annotationType());
            if (mappedAnnotation == null) {
                continue;
            }
            values.add(getValues(annotation, mappedAnnotation));
        }
        return values;
    }

    private static Field getField(MappedEnumConstant constant) {
        try {
            return constant.constant().getDeclaringClass()
                    .getField(constant.constant().name());
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
    }

    private static DirectiveArgumentsValue getValues(Annotation a, MappedAnnotation annotation) {
        if (a == null) {
            return null;
        }
        if (annotation.mappedMethods().isEmpty()) {
            return DirectiveArgumentsValueImpl.empty(a.annotationType());
        }
        Map<String, Object> values = new HashMap<>();
        annotation.mappedMethods().values().forEach(
                method -> {
                    Object value = invoke(a, method.method());
                    if (value == null) {
                        return;
                    }
                    values.put(method.name(), value);
                }
        );
        return new DirectiveArgumentsValueImpl(values, a.annotationType());
    }

    private static Object invoke(Object o, Method method) {
        try {
            return method.invoke(o);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
