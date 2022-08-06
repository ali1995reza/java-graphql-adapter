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
package graphql_adapter.adaptedschema.mapping.mapper.utils;

import graphql_adapter.annotations.GraphqlNonNull;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static graphql_adapter.adaptedschema.utils.ClassUtils.cast;

public class DimensionsNullabilityUtils {

    public static List<Boolean> getNullabilityOfDimensions(Method method) {
        return getNullabilityOfDimensions(method, true);
    }

    public static List<Boolean> getNullabilityOfDimensions(Parameter parameter) {
        return getNullabilityOfDimensions(parameter, true);
    }

    public static List<Boolean> getNullabilityOfDimensions(Method method, boolean nonNullPrimitives) {
        return getNullabilityOfDimensions(MappingUtils.findTypeDetails(method), method, nonNullPrimitives);
    }

    public static List<Boolean> getNullabilityOfDimensions(Parameter parameter, boolean nonNullPrimitives) {
        return getNullabilityOfDimensions(MappingUtils.findTypeDetails(parameter), parameter, nonNullPrimitives);
    }

    public static List<Boolean> getNullabilityOfDimensions(TypeDetails details, Method method, boolean nonNullPrimitives) {
        return getNullabilityOfDimensions(details, method.getAnnotatedReturnType(), nonNullPrimitives);
    }

    public static List<Boolean> getNullabilityOfDimensions(TypeDetails details, Parameter parameter, boolean nonNullPrimitives) {
        return getNullabilityOfDimensions(details, parameter.getAnnotatedType(), nonNullPrimitives);
    }

    public static List<Boolean> getNullabilityOfDimensions(TypeDetails details, AnnotatedType annotatedType, boolean nonNullPrimitives) {
        if (details.isAsync()) {
            if (annotatedType instanceof AnnotatedParameterizedType) {
                AnnotatedParameterizedType type = cast(annotatedType);
                return getNullabilityOfDimensionsDontCheckAsync(details, type.getAnnotatedActualTypeArguments()[0], nonNullPrimitives);
            } else {
                return Collections.singletonList(false);
            }
        }
        return getNullabilityOfDimensionsDontCheckAsync(details, annotatedType, nonNullPrimitives);
    }

    public static List<Boolean> getNullabilityOfDimensionsDontCheckAsync(TypeDetails details, AnnotatedType annotatedType, boolean nonNullPrimitives) {
        if (details.dimensionModel().isSingle()) {
            return getNullabilityOfSingle(annotatedType, nonNullPrimitives);
        } else if (details.dimensionModel().isArray()) {
            return getNullabilityOfArray(details.dimensions(), annotatedType, nonNullPrimitives);
        } else {
            return getNullabilityOfList(details.dimensions(), annotatedType, nonNullPrimitives);
        }
    }

    private static List<Boolean> getNullabilityOfArray(int dimensions, AnnotatedType type, boolean nonNullPrimitives) {
        List<Boolean> list = new ArrayList<>();
        getNullabilityOfArray(dimensions, type, nonNullPrimitives, list);
        return list;
    }

    private static void getNullabilityOfArray(int dimensions, AnnotatedType type, boolean nonNullPrimitives, List<Boolean> list) {
        if (dimensions > 0) {
            AnnotatedArrayType arrayType = cast(type);
            list.add(arrayType.getAnnotation(GraphqlNonNull.class) == null);
            getNullabilityOfArray(--dimensions, arrayType.getAnnotatedGenericComponentType(), nonNullPrimitives, list);
        } else {
            if (nonNullPrimitives) {
                list.add(!isPrimitive(type) && type.getAnnotation(GraphqlNonNull.class) == null);
            } else {
                list.add(type.getAnnotation(GraphqlNonNull.class) == null);
            }
        }
    }

    private static List<Boolean> getNullabilityOfList(int dimensions, AnnotatedType type, boolean nonNullPrimitives) {
        List<Boolean> list = new ArrayList<>();
        getNullabilityOfList(dimensions, type, nonNullPrimitives, list);
        return list;
    }

    private static void getNullabilityOfList(int dimensions, AnnotatedType type, boolean nonNullPrimitives, List<Boolean> list) {
        if (dimensions > 0) {
            AnnotatedParameterizedType listType = cast(type);
            list.add(listType.getAnnotation(GraphqlNonNull.class) == null);
            getNullabilityOfList(--dimensions, listType.getAnnotatedActualTypeArguments()[0], nonNullPrimitives, list);
        } else {
            if (nonNullPrimitives) {
                list.add(!isPrimitive(type) && type.getAnnotation(GraphqlNonNull.class) == null);
            } else {
                list.add(type.getAnnotation(GraphqlNonNull.class) == null);
            }
        }
    }

    private static List<Boolean> getNullabilityOfSingle(AnnotatedType type, boolean nonNullPrimitives) {
        if (nonNullPrimitives) {
            return Collections.singletonList(!isPrimitive(type) && type.getAnnotation(GraphqlNonNull.class) == null);
        }
        return Collections.singletonList(type.getAnnotation(GraphqlNonNull.class) == null);
    }

    private static boolean isPrimitive(Type type) {
        return ((Class<?>) type).isPrimitive();
    }

    private static boolean isPrimitive(AnnotatedType type) {
        return isPrimitive(type.getType());
    }
}
