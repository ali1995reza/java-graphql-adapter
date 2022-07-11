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

package grphaqladapter.adaptedschema.tools.object_builder;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.language.*;
import grphaqladapter.adaptedschema.discovered.DiscoveredElement;
import grphaqladapter.adaptedschema.discovered.DiscoveredEnumType;
import grphaqladapter.adaptedschema.discovered.DiscoveredInputType;
import grphaqladapter.adaptedschema.discovered.DiscoveredScalarType;
import grphaqladapter.adaptedschema.mapping.mapped_elements.DimensionModel;
import grphaqladapter.adaptedschema.mapping.mapped_elements.MappedElement;
import grphaqladapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import grphaqladapter.adaptedschema.mapping.mapped_elements.TypeInformation;
import grphaqladapter.adaptedschema.mapping.mapped_elements.classes.MappedInputTypeClass;
import grphaqladapter.adaptedschema.mapping.mapped_elements.classes.MappedScalarClass;
import grphaqladapter.adaptedschema.mapping.mapped_elements.enums.MappedEnum;
import grphaqladapter.adaptedschema.mapping.mapped_elements.enums.MappedEnumConstant;
import grphaqladapter.adaptedschema.mapping.mapped_elements.method.MappedInputFieldMethod;
import grphaqladapter.adaptedschema.utils.ClassUtils;
import grphaqladapter.adaptedschema.utils.CollectionUtils;
import grphaqladapter.codegenerator.ObjectConstructor;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ObjectBuilder {

    private final static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }

    public static ObjectBuilder of(List<DiscoveredElement> elements, ObjectConstructor constructor, boolean userPairTypesAsEachOther) {
        Map<Class, MappedScalarClass> scalars = CollectionUtils.separateToMap(elements, DiscoveredScalarType.class
                , discoveredScalarType -> discoveredScalarType.asMappedElement().baseClass(),
                DiscoveredScalarType::asMappedElement);
        Map<Class, MappedEnum> enums = CollectionUtils.separateToMap(elements, DiscoveredEnumType.class
                , discoveredEnumType -> discoveredEnumType.asMappedElement().baseClass(),
                DiscoveredEnumType::asMappedElement);
        Map<Class, MappedInputTypeClass> inputTypes = CollectionUtils.separateToMap(elements, DiscoveredInputType.class
                , discoveredInputType -> discoveredInputType.asMappedElement().baseClass(),
                DiscoveredInputType::asMappedElement);
        if (userPairTypesAsEachOther) {
            ClassUtils.addPairTypesWithSameValue(scalars, enums, inputTypes);
        }
        return new ObjectBuilder(scalars, enums, inputTypes, constructor);
    }

    public static ObjectBuilder of(Map<Class, Map<MappedElementType, MappedElement>> allMappedElements, ObjectConstructor constructor, boolean userPairTypesAsEachOther) {
        Map<Class, MappedScalarClass> scalars = new HashMap<>();
        allMappedElements.values().stream().forEach(
                map -> CollectionUtils.separateToStream(map.values(), MappedScalarClass.class)
                        .forEach(scalar -> scalars.put(scalar.baseClass(), scalar))
        );

        Map<Class, MappedEnum> enums = new HashMap<>();
        allMappedElements.values().stream().forEach(
                map -> CollectionUtils.separateToStream(map.values(), MappedEnum.class)
                        .forEach(mappedEnum -> enums.put(mappedEnum.baseClass(), mappedEnum))
        );

        Map<Class, MappedInputTypeClass> inputTypes = new HashMap<>();
        allMappedElements.values().stream().forEach(
                map -> CollectionUtils.separateToStream(map.values(), MappedInputTypeClass.class)
                        .forEach(inputTypeClass -> inputTypes.put(inputTypeClass.baseClass(), inputTypeClass))
        );

        if (userPairTypesAsEachOther) {
            ClassUtils.addPairTypesWithSameValue(scalars, enums, inputTypes);
        }

        return new ObjectBuilder(scalars, enums, inputTypes, constructor);
    }

    private final Map<Class, MappedScalarClass> scalars;
    private final Map<Class, MappedEnum> enums;
    private final Map<Class, MappedInputTypeClass> inputTypes;
    private final ObjectConstructor objectConstructor;
    private final ConcurrentHashMap<ZeroDimArrayKey, Object> zeroDimArraysCache = new ConcurrentHashMap<>();

    public ObjectBuilder(Map<Class, MappedScalarClass> scalars, Map<Class, MappedEnum> enums, Map<Class, MappedInputTypeClass> inputTypes, ObjectConstructor objectConstructor) {
        this.scalars = scalars;
        this.enums = enums;
        this.inputTypes = inputTypes;
        this.objectConstructor = objectConstructor;

    }

    public Object buildFromObject(Object object, Class type, int dimensions, DimensionModel dimensionModel, BuildingObjectConfig config) {
        try {
            if (object == null) {
                return null;
            }
            if (dimensions > 0) {
                if (object instanceof String) {
                    object = readJsonList((String) object);
                }
                if (dimensionModel.isArray()) {
                    return buildArray((List) object, type, dimensions, config);
                } else {
                    return buildList((List) object, type, dimensions, config);
                }
            } else {
                return buildFromSingleObject(type, object, config);
            }
        } catch (Exception e) {
            throw new IllegalStateException("can not parse object : " + object.getClass().getName() + "{ " + object + " }, class [" + type + "], dimensions [" + dimensions + "], dimension-model [" + dimensionModel + "]", e);
        }
    }

    public <T> T buildFromObject(Object object, Class<T> type, BuildingObjectConfig config) {
        return (T) buildFromObject(object, type, 0, null, config);
    }

    public Object buildFromObject(Object object, TypeInformation descriptor, BuildingObjectConfig config) {
        return buildFromObject(object, descriptor.type(), descriptor.dimensions(), descriptor.dimensionModel(), config);
    }

    public Object buildFromValue(Value value, Class type, int dimensions, DimensionModel dimensionModel, BuildingObjectConfig config) {
        try {
            if (value == null || value instanceof NullValue) {
                return null;
            }
            if (dimensions > 0) {
                if (dimensionModel.isArray()) {
                    return buildArray(value, type, dimensions, config);
                } else {
                    return buildList(value, type, dimensions, config);
                }
            } else {
                return buildFromSingleValue(value, type, config);
            }
        } catch (Exception e) {
            throw new IllegalStateException("can not parse value : " + value + ", class [" + type + "], dimensions [" + dimensions + "], dimension-model [" + dimensionModel + "]", e);
        }
    }

    public <T> T buildFromValue(Value value, Class<T> type, BuildingObjectConfig config) {
        return (T) buildFromValue(value, type, 0, null, config);
    }

    public Object buildFromValue(Value value, TypeInformation descriptor, BuildingObjectConfig config) {
        return buildFromValue(value, descriptor.type(), descriptor.dimensions(), descriptor.dimensionModel(), config);
    }

    private Object buildArray(List list, Class clazz, int dimensions, BuildingObjectConfig config) {
        if (list == null) {
            return null;
        }

        if (list.isEmpty()) {
            return getZeroDimSizeArray(clazz, dimensions);
        }

        Object array = --dimensions == 0 ? Array.newInstance(clazz, list.size()) :
                Array.newInstance(getZeroDimSizeArray(clazz, dimensions).getClass(), list.size());

        for (int i = 0; i < list.size(); i++) {
            Object listMember = list.get(i);
            Object object = buildFromObject(listMember, clazz, dimensions, DimensionModel.ARRAY, config);
            Array.set(array, i, object);
        }

        return array;
    }

    private Object buildArray(Value rawValue, Class clazz, int dimensions, BuildingObjectConfig config) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (rawValue == null || rawValue instanceof NullValue) {
            return null;
        }

        List<Value> valuesList = ((ArrayValue) rawValue).getValues();

        if (CollectionUtils.isEmpty(valuesList)) {
            return getZeroDimSizeArray(clazz, dimensions);
        }

        Object array = --dimensions == 0 ? Array.newInstance(clazz, valuesList.size()) :
                Array.newInstance(getZeroDimSizeArray(clazz, dimensions).getClass(), valuesList.size());

        for (int i = 0; i < valuesList.size(); i++) {
            Value value = valuesList.get(i);
            Object object = buildFromValue(value, clazz, dimensions, DimensionModel.ARRAY, config);
            Array.set(array, i, object);
        }
        return array;
    }

    private Object buildFromMap(Map<String, Object> map, MappedInputTypeClass inputTypeClass, BuildingObjectConfig config) throws InstantiationException, IllegalAccessException, InvocationTargetException {

        if (map == null) {
            return null;
        }

        Object instance = objectConstructor.getInstance(inputTypeClass.baseClass());

        for (MappedInputFieldMethod method : inputTypeClass.inputFiledMethods().values()) {
            Object value = map.get(method.name());
            if (value == null && method.defaultValue() != null && config.useInputFieldsDefaultValues()) {
                method.setter().invoke(instance, (Object) method.defaultValue());
                continue;
            }
            if (value == null) {
                continue;
            }
            method.setter().invoke(instance, buildFromObject(value, method.type(), config));
        }

        return instance;
    }

    private Object buildFromSingleObject(Class clazz, Object value, BuildingObjectConfig config) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (value == null) {
            return null;
        }
        if (isScalar(clazz)) {
            return getScalarValue(clazz, value);
        } else if (isEnum(clazz)) {
            return getEnumValue(clazz, value);
        } else {
            if (value instanceof String) {
                value = readJsonMap((String) value);
            }
            return buildFromMap((Map<String, Object>) value, findInputTypeByClass(clazz), config);
        }
    }

    private Object buildFromObjectValue(Value rawValue, MappedInputTypeClass inputType, BuildingObjectConfig config) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        if (rawValue == null || rawValue instanceof NullValue) {
            return null;
        }

        ObjectValue objectValue = (ObjectValue) rawValue;

        Object instance = objectConstructor.getInstance(inputType.baseClass());

        if (config.useInputFieldsDefaultValues()) {
            for (MappedInputFieldMethod method : inputType.inputFiledMethods().values()) {
                Value value = findValueFromObject(objectValue, method.name());
                if (value == null && method.hasDefaultValue()) {
                    method.setter().invoke(instance, (Object) method.defaultValue());
                    continue;
                }
                if (value == null) {
                    continue;
                }
                method.setter().invoke(instance, buildFromValue(value, method.type(), config));
            }
        } else {
            for (ObjectField field : objectValue.getObjectFields()) {
                MappedInputFieldMethod method = inputType.inputFiledMethods().get(field.getName());
                method.setter().invoke(instance, buildFromValue(field.getValue(), method.type(), config));
            }
        }

        return instance;
    }

    private Object buildFromSingleValue(Value value, Class clazz, BuildingObjectConfig config) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        if (value == null || value instanceof NullValue) {
            return null;
        }
        if (isScalar(clazz)) {
            return findScalarTypeByClass(clazz)
                    .coercing()
                    .parseLiteral(value);
        } else if (isEnum(clazz)) {
            EnumValue enumValue = (EnumValue) value;
            return findEnumTypeByClass(clazz)
                    .constantsByName()
                    .get(enumValue.getName())
                    .constant();
        } else {
            return buildFromObjectValue(value, findInputTypeByClass(clazz), config);
        }
    }

    private List buildList(Value rawValue, Class clazz, int dimensions, BuildingObjectConfig config) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (rawValue == null || rawValue instanceof NullValue) {
            return null;
        }

        List<Value> valuesList = ((ArrayValue) rawValue).getValues();

        if (CollectionUtils.isEmpty(valuesList)) {
            return Collections.emptyList();
        }


        --dimensions;

        List buildingList = new ArrayList();

        for (Value value : valuesList) {
            buildingList.add(buildFromValue(value, clazz, dimensions, DimensionModel.LIST, config));
        }

        return buildingList;
    }

    private List buildList(List list, Class clazz, int dimensions, BuildingObjectConfig config) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (list == null) {
            return null;
        }

        if (list.isEmpty()) {
            return Collections.emptyList();
        }

        if (isScalar(clazz) && config.useExactProvidedListForScalarTypes()) {
            return list;
        }


        --dimensions;
        List buildingList = new ArrayList();

        for (Object listMember : list) {
            buildingList.add(buildFromObject(listMember, clazz, dimensions, DimensionModel.LIST, config));
        }

        return buildingList;
    }

    private Object buildZeroDimArray(ZeroDimArrayKey key) {
        return Array.newInstance(key.type, new int[key.dimensions]);
    }

    private MappedEnum findEnumTypeByClass(Class clazz) {
        return enums.get(clazz);
    }

    private MappedInputTypeClass findInputTypeByClass(Class clazz) {
        return inputTypes.get(clazz);
    }

    private MappedScalarClass findScalarTypeByClass(Class clazz) {
        return scalars.get(clazz);
    }

    private Value findValueFromObject(ObjectValue objectValue, String name) {
        for (int i = 0; i < objectValue.getObjectFields().size(); i++) {
            ObjectField value = objectValue.getObjectFields().get(i);
            if (value.getName().equals(name)) {
                return value.getValue();
            }
        }
        return null;
    }

    private Object getEnumValue(Class clazz, Object value) {
        if (value.getClass() == clazz) {
            return value;
        }
        if (value instanceof String) {
            MappedEnumConstant mappedEnumConstant = findEnumTypeByClass(clazz).constantsByName().get(value);
            if (mappedEnumConstant == null) {
                throw new IllegalStateException("can not find any constant with name [" + value + "] of type [" + clazz + "]");
            }
            return mappedEnumConstant.constant();
        }

        throw new IllegalStateException("can not find parse Enum [" + clazz + "] from value " + value);
    }

    private Object getScalarValue(Class clazz, Object value) {
        return findScalarTypeByClass(clazz).coercing().parseValue(value);
    }

    private Object getZeroDimSizeArray(Class clazz, int dims) {
        return zeroDimArraysCache.computeIfAbsent(ZeroDimArrayKey.of(clazz, dims), this::buildZeroDimArray);
    }

    private boolean isEnum(Class clazz) {
        return enums.containsKey(clazz);
    }

    private boolean isScalar(Class clazz) {
        return scalars.containsKey(clazz);
    }

    private static List readJsonList(String input) {
        try {
            return objectMapper.readValue(input, List.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Map readJsonMap(String input) {
        try {
            return objectMapper.readValue(input, Map.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private static class ZeroDimArrayKey {

        private final Class type;
        private final int dimensions;

        private ZeroDimArrayKey(Class type, int dimensions) {
            this.type = type;
            this.dimensions = dimensions;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ZeroDimArrayKey that = (ZeroDimArrayKey) o;
            return dimensions == that.dimensions && Objects.equals(type, that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, dimensions);
        }

        private static ZeroDimArrayKey of(Class clazz, int dimensions) {
            return new ZeroDimArrayKey(clazz, dimensions);
        }
    }
}
