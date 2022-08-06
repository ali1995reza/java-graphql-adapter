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
package graphql_adapter.adaptedschema.mapping.strategy.descriptors.validators;

import graphql_adapter.adaptedschema.mapping.mapped_elements.GraphqlValidator;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.method.MethodAnnotationLookup;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.parameter.ParameterAnnotationLookup;
import graphql_adapter.adaptedschema.utils.CollectionUtils;
import graphql_adapter.annotations.GraphqlValidation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class AnnotationBaseValidatorDescriptor implements ValidatorDescriptor {
    @Override
    public List<GraphqlValidator> describeArgumentValidators(Parameter parameter, Method method, int index, Class<?> clazz) {
        List<Annotation> annotations = ParameterAnnotationLookup.getAllAnnotations(parameter, method, index);
        return fromAnnotations(annotations);
    }

    @Override
    public List<GraphqlValidator> describeDirectiveArgumentValidators(Method method, Class<? extends Annotation> clazz) {
        List<Annotation> annotations = MethodAnnotationLookup.getAllAnnotations(method);
        return fromAnnotations(annotations);
    }

    @Override
    public List<GraphqlValidator> describeInputFieldValidators(Method method, Class<?> clazz) {
        List<Annotation> annotations = MethodAnnotationLookup.getAllAnnotations(method);
        return fromAnnotations(annotations);
    }

    private List<GraphqlValidator> fromAnnotations(List<Annotation> annotations) {
        if (CollectionUtils.isEmpty(annotations)) {
            return null;
        }
        List<GraphqlValidator> validators = new ArrayList<>();
        for (Annotation annotation : annotations) {
            GraphqlValidator validator = getValidator(annotation);
            if (validator != null) {
                validators.add(validator);
            }
        }
        return CollectionUtils.isEmpty(validators) ? null : validators;
    }

    private GraphqlValidator getValidator(Annotation annotation) {
        GraphqlValidation validation = annotation.annotationType().getAnnotation(GraphqlValidation.class);
        if (validation != null) {
            return GraphqlValidator.newValidator()
                    .validationFunction(validation.value())
                    .validationArgument(annotation)
                    .build();
        }
        return null;
    }
}
