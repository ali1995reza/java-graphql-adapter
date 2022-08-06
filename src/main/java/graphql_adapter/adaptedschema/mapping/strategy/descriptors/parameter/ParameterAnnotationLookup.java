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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParameterAnnotationLookup {

    public static <T extends Annotation> ParameterAnnotationLookupResult<T> findFirstAppears(Method method, int parameterIndex, Class<T> annotationClass) {

        Parameter parameter = method.getParameters()[parameterIndex];

        if (parameter.getAnnotation(annotationClass) != null) {
            return new ParameterAnnotationLookupResult<>(parameter.getAnnotation(annotationClass), parameter);
        } else {

            if (method.getDeclaringClass().getSuperclass() != null) {
                try {

                    Method m = method.getDeclaringClass().getSuperclass().getMethod(method.getName(), method.getParameterTypes());
                    ParameterAnnotationLookupResult<T> result = findFirstAppears(m, parameterIndex, annotationClass);
                    if (result != null) {
                        return result;
                    }
                } catch (NoSuchMethodException ignored) {
                }
            }
            for (Class<?> cls : method.getDeclaringClass().getInterfaces()) {
                try {
                    Method m = cls.getMethod(method.getName(), method.getParameterTypes());
                    ParameterAnnotationLookupResult<T> result = findFirstAppears(m, parameterIndex, annotationClass);
                    if (result != null) {
                        return result;
                    }
                } catch (NoSuchMethodException ignored) {
                }
            }
        }

        return null;
    }

    public static List<Annotation> getAllAnnotations(Parameter parameter, Method method, int index) {
        List<Annotation> list = new ArrayList<>();
        getAllAnnotations(parameter, method, index, list);
        return list;
    }

    private static void getAllAnnotations(Parameter parameter, Method method, int index, List<Annotation> list) {
        Class<?> clazz = method.getDeclaringClass();
        for (Class<?> inter : clazz.getInterfaces()) {
            try {
                Method superMethod = getMethod(inter, method.getName(), method.getParameterTypes());
                getAllAnnotations(superMethod.getParameters()[index], superMethod, index, list);
            } catch (NoSuchMethodException ignored) {
            }
        }
        if (clazz.getSuperclass() != null) {
            try {
                Method superMethod = getMethod(clazz.getSuperclass(), method.getName(), method.getParameterTypes());
                getAllAnnotations(superMethod.getParameters()[index], superMethod, index, list);
            } catch (NoSuchMethodException ignored) {
            }
        }
        list.addAll(Arrays.asList(parameter.getAnnotations()));
    }

    private static Method getMethod(Class<?> clazz, String name, Class<?>... params) throws NoSuchMethodException {
        try {
            return clazz.getMethod(name, params);
        } catch (NoSuchMethodException ignored) {
        }

        try {
            return clazz.getDeclaredMethod(name, params);
        } catch (NoSuchMethodException e) {
            throw e;
        }
    }
}
