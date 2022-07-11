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

import graphql_adapter.adaptedschema.mapping.mapped_elements.DimensionModel;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static graphql_adapter.adaptedschema.utils.ClassUtils.cast;

public class MappingUtils {

    public static TypeDetails findTypeDetails(Method method) {
        Type type = method.getGenericReturnType();

        if (CompletableFuture.class.isAssignableFrom(method.getReturnType())) {
            if (type instanceof ParameterizedType) {
                //so handle it please !
                ParameterizedType paraType = (ParameterizedType) type;
                Type inner = paraType.getActualTypeArguments()[0];
                TypeDetails details = findTypeDetails(inner);

                return new TypeDetails(details.type(), details.dimensions(), details.dimensionModel());
            } else {
                return new TypeDetails(Object.class, 0, DimensionModel.SINGLE);
            }
        }
        return findTypeDetails(type);
    }

    public static TypeDetails findTypeDetails(Parameter parameter) {
        return findTypeDetails(parameter.getParameterizedType());
    }

    public static TypeDetails findTypeDetails(Type type) {
        if (type == null)
            return null;

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            if (parameterizedType.getRawType() != List.class)
                return new TypeDetails((Class<?>) parameterizedType.getRawType(), 0, DimensionModel.SINGLE);

            Type innerType = parameterizedType.getActualTypeArguments()[0];
            return detectDimensionsDetails(innerType, 1);
        } else if (type instanceof Class) {
            Class<?> clazz = cast(type);
            if (clazz == List.class)
                return new TypeDetails(Object.class, 1, DimensionModel.LIST);
            else if (clazz.isArray())
                return forArray(clazz);
            else
                return new TypeDetails(clazz, 0, DimensionModel.SINGLE);
        }

        throw new IllegalStateException("unknown type class");
    }

    private static TypeDetails detectDimensionsDetails(Type type, int dims) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            if (parameterizedType.getRawType() == List.class) {
                return detectDimensionsDetails(parameterizedType.getActualTypeArguments()[0], dims + 1);
            } else {
                return new TypeDetails(cast(parameterizedType.getRawType()), dims, DimensionModel.LIST);
            }
        } else if (type instanceof Class) {
            Class<?> clazz = cast(type);
            if (clazz == List.class)
                return new TypeDetails(Object.class, dims + 1, DimensionModel.LIST);
            else
                return new TypeDetails(clazz, dims, DimensionModel.LIST);
        }

        throw new IllegalStateException("unknown type class");
    }

    private static TypeDetails forArray(Class<?> clazz) {
        int dims = 0;
        while (clazz.isArray()) {
            ++dims;
            clazz = clazz.getComponentType();
        }
        return new TypeDetails(clazz, dims, DimensionModel.ARRAY);
    }
}
