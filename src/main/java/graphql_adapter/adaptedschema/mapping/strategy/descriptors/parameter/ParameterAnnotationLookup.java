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

package graphql_adapter.adaptedschema.mapping.strategy.descriptors.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class ParameterAnnotationLookup {

    public static <T extends Annotation> T findFirstAppears(Method method, int parameterIndex, Class<T> annotationClass) {

        Parameter parameter = method.getParameters()[parameterIndex];

        if (parameter.getAnnotation(annotationClass) != null) {
            return parameter.getAnnotation(annotationClass);
        } else {

            if (method.getDeclaringClass().getSuperclass() != null) {
                try {

                    Method m = method.getDeclaringClass().getSuperclass().getMethod(method.getName(), method.getParameterTypes());
                    T annotation = findFirstAppears(m, parameterIndex, annotationClass);
                    if (annotation != null) {
                        return annotation;
                    }
                } catch (NoSuchMethodException ignored) {
                }
            }
            for (Class<?> cls : method.getDeclaringClass().getInterfaces()) {
                try {
                    Method m = cls.getMethod(method.getName(), method.getParameterTypes());
                    T annotation = findFirstAppears(m, parameterIndex, annotationClass);
                    if (annotation != null) {
                        return annotation;
                    }
                } catch (NoSuchMethodException ignored) {
                }
            }
        }

        return null;
    }
}
