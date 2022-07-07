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

package grphaqladapter.adaptedschema;

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
import grphaqladapter.adaptedschema.mapping.mapped_elements.method.MappedInputFieldMethod;
import grphaqladapter.adaptedschema.utils.CollectionUtils;
import grphaqladapter.adaptedschema.utils.TypoUtils;
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

    static ObjectBuilder of(List<DiscoveredElement> elements, ObjectConstructor constructor, boolean userPairTypesAsEachOther) {
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
            TypoUtils.addPairTypesWithSameValue(scalars, enums, inputTypes);
        }
        return new ObjectBuilder(scalars, enums, inputTypes, constructor);
    }

    static ObjectBuilder of(Map<Class, Map<MappedElementType, MappedElement>> allMappedElements, ObjectConstructor constructor, boolean userPairTypesAsEachOther) {
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
            TypoUtils.addPairTypesWithSameValue(scalars, enums, inputTypes);
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

    public Object buildFromObject(Class type, int dimensions, DimensionModel dimensionModel, Object object, boolean useInputFieldsDefaultValues) {
        try {
            if (dimensions > 0) {
                if (object instanceof String) {
                    object = readJsonList((String) object);
                }
                if (dimensionModel.isArray()) {
                    return buildArray((List) object, type, dimensions, useInputFieldsDefaultValues);
                } else {
                    return buildList((List) object, type, dimensions, useInputFieldsDefaultValues);
                }
            } else {
                return buildFromObject(object, type, useInputFieldsDefaultValues);
            }
        } catch (Exception e) {
            throw new IllegalStateException("can not parse value : " + object, e);
        }
    }

    public <T> T buildFromObject(Class<T> type, Object object, boolean useInputFieldsDefaultValues) {
        return (T) buildFromObject(type, 0, null, object, useInputFieldsDefaultValues);
    }

    public Object buildFromObject(TypeInformation descriptor, Object object, boolean useInputFieldsDefaultValues) {
        return buildFromObject(descriptor.type(), descriptor.dimensions(), descriptor.dimensionModel(), object, useInputFieldsDefaultValues);
    }

    public Object buildFromValue(Class type, int dimensions, DimensionModel dimensionModel, Value value, boolean useInputFieldsDefaultValues) {
        try {
            if (dimensions > 0) {
                if(dimensionModel.isList()) {
                    return buildList((ArrayValue) value, type, dimensions, useInputFieldsDefaultValues);
                } else {
                    return null; //todo fix it here please !
                }
            } else {
                return buildFromValue(value, type, useInputFieldsDefaultValues);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public <T> T buildFromValue(Class<T> type, Value value, boolean useInputFieldsDefaultValues) {
        return (T) buildFromValue(type, 0, null, value, useInputFieldsDefaultValues);
    }

    public Object buildFromValue(TypeInformation descriptor, Value value, boolean useInputFieldsDefaultValues) {
        return buildFromValue(descriptor.type(), descriptor.dimensions(), descriptor.dimensionModel(), value, useInputFieldsDefaultValues);
    }

    private Object buildArray(List argList, Class clazz, int dim, boolean useInputFieldsDefaultValues) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (argList == null)
            return null;

        if (argList.isEmpty()) {
            return getZeroDimSizeArray(clazz, dim);
        }

        --dim;
        if (dim == 0) {
            Object array = Array.newInstance(clazz, argList.size());
            for (int i = 0; i < argList.size(); i++) {
                Object o = argList.get(i);
                Object object = buildFromObject(o, clazz, useInputFieldsDefaultValues);
                Array.set(array, i, object);
            }
            return array;
        } else {
            Object array = Array.newInstance(getZeroDimSizeArray(clazz, dim).getClass(), argList.size());
            for (int i = 0; i < argList.size(); i++) {
                List list = (List) argList.get(i);
                Object object = buildArray(list, clazz, dim, useInputFieldsDefaultValues);
                Array.set(array, i, object);
            }
            return array;
        }
    }

    private Object buildFromMap(Map<String, Object> map, MappedInputTypeClass inputTypeClass, boolean useInputFieldsDefaultValues) throws InstantiationException, IllegalAccessException, InvocationTargetException {

        if (map == null) return null;

        Object instance = objectConstructor.getInstance(inputTypeClass.baseClass());

        for (MappedInputFieldMethod method : inputTypeClass.inputFiledMethods().values()) {
            Object value = map.get(method.name());
            if (value == null && method.defaultValue() != null && useInputFieldsDefaultValues) {
                method.setter().invoke(instance, (Object) method.defaultValue());
                continue;
            }
            if (value == null) {
                continue;
            }
            method.setter().invoke(instance, buildFromObject(method.type(), value, useInputFieldsDefaultValues));
        }

        return instance;
    }

    private boolean isScalar(Class clazz) {
        return scalars.containsKey(clazz);
    }

    private boolean isEnum(Class clazz) {
        return enums.containsKey(clazz);
    }

    private MappedInputTypeClass findInputTypeByClass(Class clazz) {
        return inputTypes.get(clazz);
    }

    private MappedScalarClass findScalarTypeByClass(Class clazz) {
        return scalars.get(clazz);
    }

    private MappedEnum findEnumTypeByClass(Class clazz) {
        return enums.get(clazz);
    }

    private Object buildFromObject(Object o, Class clazz, boolean useInputFieldsDefaultValues) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (isScalar(clazz)) {
            return getScalarValue(clazz, o);
        } else if (isEnum(clazz)) {
            return getEnumValue(clazz, o);
        } else {
            if (o instanceof String) {
                o = readJsonMap((String) o);
            }
            return buildFromMap((Map<String, Object>) o, findInputTypeByClass(clazz), useInputFieldsDefaultValues);
        }
    }

    private Object buildFromObjectValue(ObjectValue objectValue, MappedInputTypeClass inputType, boolean useInputFieldsDefaultValues) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        if (objectValue == null) {
            return null;
        }

        Object instance = objectConstructor.getInstance(inputType.baseClass());

        if (useInputFieldsDefaultValues) {
            for (MappedInputFieldMethod method : inputType.inputFiledMethods().values()) {
                Value value = findValueFromObject(objectValue, method.name());
                if (value == null && method.hasDefaultValue()) {
                    method.setter().invoke(instance, (Object) method.defaultValue());
                    continue;
                }
                if (value == null) {
                    continue;
                }
                method.setter().invoke(instance, buildFromValue(method.type(), value, useInputFieldsDefaultValues));
            }
        } else {
            for (ObjectField field : objectValue.getObjectFields()) {
                MappedInputFieldMethod method = inputType.inputFiledMethods().get(field.getName());
                method.setter().invoke(instance, buildFromValue(method.type(), field.getValue(), useInputFieldsDefaultValues));
            }
        }

        return instance;
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

    private Object getScalarValue(Class clazz, Object value) {
        return findScalarTypeByClass(clazz).coercing().parseValue(value);
    }

    private Object getEnumValue(Class clazz, Object value) {
        if (value.getClass() == clazz) {
            return value;
        }
        if (value instanceof String) {
            return findEnumTypeByClass(clazz).constantsByName().get(value).constant();
        }

        throw new IllegalStateException("can not find parse Enum [" + clazz + "] from value " + value);
    }

    private Object buildFromValue(Value value, Class clazz, boolean useInputFieldsDefaultValues) throws InvocationTargetException, IllegalAccessException, InstantiationException {
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
            return buildFromObjectValue((ObjectValue) value, findInputTypeByClass(clazz), useInputFieldsDefaultValues);
        }
    }

    private List buildList(ArrayValue arrayValue, Class clazz, int dim, boolean useInputFieldsDefaultValues) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (arrayValue == null)
            return null;

        if (arrayValue.getValues().isEmpty()) {
            return Collections.emptyList();
        }


        --dim;
        List buildingList = new ArrayList();

        if (dim == 0) {
            for (Value value : arrayValue.getValues()) {
                buildingList.add(buildFromValue(value, clazz, useInputFieldsDefaultValues));
            }
        } else {
            for (Value value : arrayValue.getValues()) {
                buildingList.add(buildList((ArrayValue) value, clazz, dim, useInputFieldsDefaultValues));
            }
        }

        return buildingList;
    }

    private List buildList(List argList, Class clazz, int dim, boolean useInputFieldsDefaultValues) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (argList == null)
            return null;

        if (argList.isEmpty()) {
            return Collections.emptyList();
        }

        if (isScalar(clazz)) {
            return argList;
        }


        --dim;
        List buildingList = new ArrayList();

        if (dim == 0) {
            for (Object o : argList) {
                buildingList.add(buildFromMap((Map<String, Object>) o, findInputTypeByClass(clazz), useInputFieldsDefaultValues));
            }
        } else {
            for (Object o : argList) {
                buildingList.add(buildList((List) o, clazz, dim, useInputFieldsDefaultValues));
            }
        }

        return buildingList;
    }

    private Object buildZeroDimArray(ZeroDimArrayKey key) {
        int[] dimensions = new int[key.dimensions];
        for (int i = 0; i < key.dimensions; i++) {
            dimensions[i] = 0;
        }
        return Array.newInstance(key.type, dimensions);
    }

    private Object getZeroDimSizeArray(Class clazz, int dims) {
        return zeroDimArraysCache.computeIfAbsent(ZeroDimArrayKey.of(clazz, dims), this::buildZeroDimArray);
    }

    private static class ZeroDimArrayKey {

        private static ZeroDimArrayKey of(Class clazz, int dimensions) {
            return new ZeroDimArrayKey(clazz, dimensions);
        }

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
    }
}
