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
package graphql_adapter.adaptedschema.mapping.strategy.descriptors.method;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MethodAnnotationLookup {

    public static <T extends Annotation> MethodAnnotationLookupResult<T> findFirstAppears(Method method, Class<T> annotationClass) {
        if (method.getAnnotation(annotationClass) != null) {
            return new MethodAnnotationLookupResult<>(method.getAnnotation(annotationClass), method);
        } else {

            if (method.getDeclaringClass().getSuperclass() != null) {
                try {

                    Method m = method.getDeclaringClass().getSuperclass().getMethod(method.getName(), method.getParameterTypes());
                    MethodAnnotationLookupResult<T> result = findFirstAppears(m, annotationClass);
                    if (result != null) {
                        return result;
                    }
                } catch (NoSuchMethodException ignored) {
                }
            }
            for (Class<?> cls : method.getDeclaringClass().getInterfaces()) {
                try {
                    Method m = cls.getMethod(method.getName(), method.getParameterTypes());
                    MethodAnnotationLookupResult<T> result = findFirstAppears(m, annotationClass);
                    if (result != null) {
                        return result;
                    }
                } catch (NoSuchMethodException ignored) {
                }
            }
        }

        return null;
    }

    public static List<Annotation> getAllAnnotations(Method method) {
        List<Annotation> list = new ArrayList<>();
        getAllAnnotations(method, list);
        return list;
    }

    private static void getAllAnnotations(Method method, List<Annotation> list) {
        Class<?> clazz = method.getDeclaringClass();
        for (Class<?> inter : clazz.getInterfaces()) {
            try {
                Method superMethod = getMethod(inter, method.getName(), method.getParameterTypes());
                getAllAnnotations(superMethod, list);
            } catch (NoSuchMethodException ignored) {
            }
        }
        if (clazz.getSuperclass() != null) {
            try {
                Method superMethod = getMethod(clazz.getSuperclass(), method.getName(), method.getParameterTypes());
                getAllAnnotations(superMethod, list);
            } catch (NoSuchMethodException ignored) {
            }
        }
        list.addAll(Arrays.asList(method.getAnnotations()));
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
