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
package graphql_adapter.adaptedschema.mapping.strategy.descriptors.utils;

import graphql_adapter.adaptedschema.functions.ValueParser;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.DefaultValueDetails;
import graphql_adapter.annotations.DefaultValue;
import graphql_adapter.annotations.GraphqlDescription;
import graphql_adapter.annotations.SkipElement;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

public class DescriptorUtils {

    public static <T extends Annotation> T getAnnotation(AnnotatedElement element, Class<T> annotationClass) {
        return element.getAnnotation(annotationClass);
    }

    public static DefaultValueDetails getDefaultValue(AnnotatedElement element) {
        DefaultValue defaultValue = getAnnotation(element, DefaultValue.class);
        if (defaultValue == null) {
            return null;
        }
        return new DefaultValueDetails(defaultValue.value(), defaultValue.valueParser());
    }

    public static DefaultValueDetails getDefaultValueOfAnnotationMethod(Method method, Class<? extends ValueParser<?, ?>> parser) {
        DefaultValueDetails realDetails = getDefaultValue(method);
        if (realDetails != null) {
            return realDetails;
        }
        if (method.getDefaultValue() == null) {
            return null;
        }
        return new DefaultValueDetails(method.getDefaultValue(), parser);
    }

    public static String getDescription(AnnotatedElement element) {
        GraphqlDescription description = getAnnotation(element, GraphqlDescription.class);
        if (description == null) {
            return null;
        }
        return description.value();
    }

    public static boolean isSkipElementAnnotationPresent(AnnotatedElement element) {
        return getAnnotation(element, SkipElement.class) != null;
    }
}
