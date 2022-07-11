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

import graphql_adapter.adaptedschema.functions.impl.RawValueParser;
import graphql_adapter.adaptedschema.mapping.mapped_elements.TypeInformation;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.argument.GraphqlDirectiveArgumentDescription;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.field.GraphqlFieldDescription;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.field.GraphqlInputFieldDescription;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.utils.DescriptorUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class PojoMethodDescriptor implements MethodDescriptor {

    private final static String GET_PREFIX = "get";
    private final static String SET_PREFIX = "set";
    private final static String IS_PREFIX = "is";
    private final static String EMPTY = "";


    private final boolean removePrefix;

    public PojoMethodDescriptor(boolean removePrefix) {
        this.removePrefix = removePrefix;
    }

    public PojoMethodDescriptor() {
        this(true);
    }

    @Override
    public GraphqlDirectiveArgumentDescription describeDirectiveArgument(Method method, Class<?> clazz) {
        if (method.getDeclaringClass() != clazz) {
            return null;
        }
        TypeInformation<?> information = TypeInformation.of(method, !method.getReturnType().isPrimitive());
        return GraphqlDirectiveArgumentDescription.newDirectiveArgumentDescription()
                .name(getName(method))
                .description(DescriptorUtils.getDescription(method))
                .defaultValue(DescriptorUtils.getDefaultValueOfAnnotationMethod(method, RawValueParser.class))
                .dimensions(information.dimensions())
                .type(information.type())
                .nullable(information.isNullable())
                .dimensionModel(information.dimensionModel())
                .build();
    }

    @Override
    public GraphqlFieldDescription describeField(Method method, Class<?> clazz) {
        return outputField(method, clazz);
    }

    @Override
    public GraphqlInputFieldDescription describeInputField(Method method, Class<?> clazz) {
        return inputField(method, clazz);
    }

    private static String firstCharLowerCase(String str) {
        char[] array = str.toCharArray();
        array[0] = Character.toLowerCase(array[0]);
        return new String(array);
    }

    private Method getMethod(Class<?> clazz, String name, Class<?>... params) {
        try {
            return clazz.getMethod(name, params);
        } catch (NoSuchMethodException ignored) {
        }
        try {
            return clazz.getDeclaredMethod(name, params);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private String getName(Method method) {
        return removePrefix ? firstCharLowerCase(removePrefix(method)) : method.getName();
    }

    private String getSetterName(Method method, Class<?> clazz) {
        String name = removePrefix(method);
        Method setter = getMethod(clazz, SET_PREFIX + name, method.getReturnType());
        if (setter == null) {
            return null;
        }
        return setter.getName();
    }

    private GraphqlInputFieldDescription inputField(Method method, Class<?> clazz) {
        if (Modifier.isStatic(method.getModifiers()) || method.getDeclaringClass() == Object.class) {
            return null;
        }
        if (!(method.getName().startsWith(GET_PREFIX) || method.getName().startsWith(IS_PREFIX))) {
            return null;
        }
        String setter = getSetterName(method, clazz);
        if (setter == null) {
            return null;
        }
        return GraphqlInputFieldDescription.newInputFieldDescription()
                .name(getName(method))
                .nullable(isNullable(method))
                .setter(setter)
                .defaultValue(DescriptorUtils.getDefaultValue(method))
                .build();
    }

    private static boolean isNullable(Method method) {
        return !method.getReturnType().isPrimitive();
    }

    private GraphqlFieldDescription outputField(Method method, Class<?> clazz) {
        if (Modifier.isStatic(method.getModifiers()) || method.getDeclaringClass() == Object.class) {
            return null;
        }
        if (!(method.getName().startsWith(GET_PREFIX) || method.getName().startsWith(IS_PREFIX))) {
            return null;
        }
        return GraphqlFieldDescription.newFieldDescription()
                .name(getName(method))
                .nullable(isNullable(method))
                .build();
    }

    private String removePrefix(Method method) {
        String name = method.getName();
        if (name.startsWith(GET_PREFIX)) {
            name = name.replaceFirst(GET_PREFIX, EMPTY);
        } else if (name.startsWith(IS_PREFIX)) {
            name = name.replaceFirst(IS_PREFIX, EMPTY);
        }
        return name;
    }

}
