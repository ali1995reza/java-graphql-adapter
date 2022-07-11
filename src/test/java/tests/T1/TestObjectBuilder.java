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

package tests.T1;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import graphql.language.*;
import grphaqladapter.adaptedschema.discovered.DiscoveredEnumType;
import grphaqladapter.adaptedschema.discovered.DiscoveredInputType;
import grphaqladapter.adaptedschema.discovered.DiscoveredScalarType;
import grphaqladapter.adaptedschema.mapping.mapped_elements.DimensionModel;
import grphaqladapter.adaptedschema.mapping.mapped_elements.method.MappedInputFieldMethod;
import grphaqladapter.adaptedschema.tools.object_builder.BuildingObjectConfig;
import grphaqladapter.adaptedschema.tools.object_builder.ObjectBuilder;
import grphaqladapter.adaptedschema.tools.type_finder.TypeFinder;
import grphaqladapter.adaptedschema.utils.ClassUtils;
import grphaqladapter.adaptedschema.utils.MethodUtils;
import org.junit.jupiter.api.Test;
import tests.Randomer;
import tests.T1.schema.IntPeriodScalar;
import tests.T1.schema.Splitor;
import tests.T1.schema.UserType;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.*;
import java.util.function.Function;

public class TestObjectBuilder {

    private final static Random RANDOM = new SecureRandom();
    private final static int ROUND = 100;
    private final static int MAXIMUM_DIMENSIONS = 5;

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        OBJECT_MAPPER.registerModule(new SimpleModule().addSerializer(Splitor.class, new JsonSerializer<Splitor>() {
            @Override
            public void serialize(Splitor splitor, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString(splitor.getSplitorString());
            }
        }).addSerializer(IntPeriodScalar.class, new JsonSerializer<IntPeriodScalar>() {
            @Override
            public void serialize(IntPeriodScalar periodScalar, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString(periodScalar.toString());
            }
        }).addSerializer(UserType.class, new JsonSerializer<UserType>() {
            @Override
            public void serialize(UserType userType, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString(userType.isNormal() ? "NORMAL" : "ADMIN");
            }
        }));
    }

    @Test
    public void testBuildEnumTypes() {
        for (DiscoveredEnumType enumType : TestSchemaProvider.schema().discoveredEnumTypes()) {
            test(enumType.asMappedElement().baseClass(), ROUND);

            Class pairType = ClassUtils.getPairType(enumType.asMappedElement().baseClass());
            if (pairType != null) {
                test(pairType, ROUND);
            }
        }
    }

    @Test
    public void testBuildInputTypes() {
        for (DiscoveredInputType inputType : TestSchemaProvider.schema().discoveredInputTypes()) {
            test(inputType.asMappedElement().baseClass(), ROUND);

            Class pairType = ClassUtils.getPairType(inputType.asMappedElement().baseClass());
            if (pairType != null) {
                test(pairType, ROUND);
            }
        }
    }

    @Test
    public void testBuildScalarTypes() {
        for (DiscoveredScalarType scalarType : TestSchemaProvider.schema().discoveredScalarTypes()) {
            test(scalarType.asMappedElement().baseClass(), ROUND);

            Class pairType = ClassUtils.getPairType(scalarType.asMappedElement().baseClass());
            if (pairType != null) {
                test(pairType, ROUND);
            }
        }
    }

    private static Object buildRandomArray(Class<?> clazz, int dims, Function<Integer, Boolean> chanceCalculator, int depth) {
        if (chanceCalculator.apply(10 + depth)) {
            return RANDOM.nextBoolean() ? null : Array.newInstance(getMultiDimensionClass(clazz, dims - 1), 0);
        }

        final int size = 5 + RANDOM.nextInt(10);

        --dims;

        Object array = Array.newInstance(getMultiDimensionClass(clazz, dims), size);

        ++depth;

        for (int i = 0; i < size; i++) {
            Object randomObject = doBuildRandomObject(clazz, dims, DimensionModel.ARRAY, chanceCalculator, depth);
            Array.set(array, i, randomObject);
        }
        return array;
    }

    private static Object buildRandomInputType(Class<?> clazz, Function<Integer, Boolean> chanceCalculator, int depth) {
        if (chanceCalculator.apply(20 + depth)) {
            return null;
        }
        DiscoveredInputType inputType = finder().findInputTypeByClass(clazz);
        Object instance = construct(clazz);
        for (MappedInputFieldMethod method : inputType.asMappedElement().inputFiledMethods().values()) {
            Object randomObject = doBuildRandomObject(method.type().type(), method.type().dimensions(), method.type().dimensionModel(), chanceCalculator, ++depth);
            MethodUtils.invoke(instance, method.setter(), randomObject);
        }
        return instance;
    }

    private static Object buildRandomList(Class<?> clazz, int dims, Function<Integer, Boolean> chanceCalculator, int depth) {
        if (chanceCalculator.apply(10 + depth)) {
            return RANDOM.nextBoolean() ? null : Collections.emptyList();
        }

        final int size = RANDOM.nextInt(10);

        List list = new ArrayList();

        --dims;

        ++depth;

        for (int i = 0; i < size; i++) {
            list.add(doBuildRandomObject(clazz, dims, DimensionModel.LIST, chanceCalculator, depth));
        }
        return list;

    }

    private static Object buildRandomObject(Class<?> clazz, Function<Integer, Boolean> chanceCalculator) {
        return buildRandomObject(clazz, 0, DimensionModel.SINGLE, chanceCalculator);
    }

    private static Object buildRandomObject(Class<?> clazz, int dimensions, DimensionModel dimensionModel, Function<Integer, Boolean> chanceCalculator) {
        return doBuildRandomObject(clazz, dimensions, dimensionModel, chanceCalculator, 0);
    }

    private static ObjectBuilder builder() {
        return TestSchemaProvider.schema().objectBuilder();
    }

    private static boolean chance(int percentage) {
        int random = RANDOM.nextInt(100);
        return random <= percentage;
    }

    private static Object construct(Class<?> clazz) {
        return TestSchemaProvider.schema().objectConstructor().getInstance(clazz);
    }

    private static Object describeArrayAsList(Object o) {
        if (o == null) {
            return null;
        }
        int size = Array.getLength(o);
        if (size == 0) {
            return Collections.emptyList();
        }
        List list = new ArrayList();
        for (int i = 0; i < size; i++) {
            list.add(describeObjectInMapsAndList(Array.get(o, i)));
        }
        return list;
    }

    private static Value describeArrayAsValue(Object o) {
        if (o == null) {
            if (chance(25)) {
                return NullValue.of();
            }
            return null;
        }
        int size = Array.getLength(o);
        if (size == 0) {
            return ArrayValue.newArrayValue().build();
        }
        ArrayValue.Builder list = ArrayValue.newArrayValue();
        for (int i = 0; i < size; i++) {
            list.value(describeObjectAsValue(Array.get(o, i)));
        }
        return list.build();
    }

    private static Object describeObjectAsMap(Object instance) {
        Map map = new HashMap();
        DiscoveredInputType inputType = finder().findInputTypeByClass(instance.getClass());
        for (MappedInputFieldMethod method : inputType.asMappedElement().inputFiledMethods().values()) {
            Object value = MethodUtils.invoke(instance, method.method());
            if (value != null) {
                map.put(method.name(), describeObjectInMapsAndList(value));
            }
        }
        return map;
    }

    private static Value describeObjectAsObjectValue(Object instance) {
        ObjectValue.Builder objectValue = ObjectValue.newObjectValue();
        DiscoveredInputType inputType = finder().findInputTypeByClass(instance.getClass());
        for (MappedInputFieldMethod method : inputType.asMappedElement().inputFiledMethods().values()) {
            Object value = MethodUtils.invoke(instance, method.method());
            Value v = describeObjectAsValue(value);
            if (v != null) {
                objectValue.objectField(ObjectField.newObjectField().name(method.name())
                        .value(v)
                        .build());
            }
        }
        return objectValue.build();
    }

    private static Value describeObjectAsValue(Object o) {
        if (o == null) {
            return NullValue.of();
        }
        if (finder().isScalar(o.getClass())) {
            return finder().findScalarTypeByClass(o.getClass())
                    .asMappedElement().coercing().valueToLiteral(o);
        } else if (finder().isEnum(o.getClass())) {
            String name = finder().findEnumTypeByClass(o.getClass())
                    .asMappedElement().constantsByEnumValue().get(o).name();
            return EnumValue.of(name);
        } else if (o.getClass().isArray()) {
            return describeArrayAsValue(o);
        } else if (o instanceof List) {
            List list = (List) o;
            ArrayValue.Builder newList = ArrayValue.newArrayValue();
            list.forEach(object -> newList.value(describeObjectAsValue(object)));
            return newList.build();
        } else {
            return describeObjectAsObjectValue(o);
        }
    }

    private static Object describeObjectInMapsAndList(Object o) {
        if (o == null) {
            return null;
        }
        if (finder().isScalarOrEnum(o.getClass())) {
            return o;
        } else if (o.getClass().isArray()) {
            return describeArrayAsList(o);
        } else if (o instanceof List) {
            List list = (List) o;
            List newList = new ArrayList();
            list.forEach(object -> newList.add(describeObjectInMapsAndList(object)));
            return newList;
        } else {
            return describeObjectAsMap(o);
        }
    }

    private static String describeObjectInString(Object o) {
        if (o == null || o instanceof String) {
            return (String) o;
        }
        if (o instanceof Splitor) {
            return ((Splitor) o).getSplitorString();
        }
        if (o instanceof UserType) {
            return ((UserType) o).isNormal() ? "NORMAL" : "ADMIN";
        }
        if (!(o instanceof Map || o instanceof List)) {
            return o.toString();
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Object doBuildRandomObject(Class<?> clazz, int dimensions, DimensionModel dimensionModel, Function<Integer, Boolean> chanceCalculator, int depth) {
        if (dimensions == 0) {
            if (finder().isScalar(clazz)) {
                return randomScalar(clazz, chanceCalculator);
            } else if (finder().isEnum(clazz)) {
                return Randomer.random(finder().findEnumTypeByClass(clazz)
                                .asMappedElement()
                                .constantsByName().values())
                        .constant();
            } else {
                return buildRandomInputType(clazz, chanceCalculator, depth);
            }
        } else {
            if (dimensionModel.isList()) {
                return buildRandomList(clazz, dimensions, chanceCalculator, depth);
            } else if (dimensionModel.isArray()) {
                return buildRandomArray(clazz, dimensions, chanceCalculator, depth);
            }
        }
        throw new IllegalStateException("bad type for create random objects");
    }

    private static TypeFinder finder() {
        return TestSchemaProvider.schema().typeFinder();
    }

    private static Class getMultiDimensionClass(Class<?> component, int dims) {
        if (dims == 0) {
            return component;
        }
        return Array.newInstance(component, new int[dims]).getClass();
    }

    private static Object randomScalar(Class<?> clazz, Function<Integer, Boolean> chanceCalculator) {
        if (!clazz.isPrimitive() && chanceCalculator.apply(10)) {
            return null;
        }
        if (clazz == Boolean.class || clazz == boolean.class) {
            return RANDOM.nextBoolean();
        }
        if (clazz == Integer.class || clazz == int.class) {
            return RANDOM.nextInt();
        }
        if (clazz == Long.class || clazz == long.class) {
            return RANDOM.nextLong();
        }
        if (clazz == Double.class || clazz == double.class) {
            return RANDOM.nextDouble();
        }
        if (clazz == Float.class || clazz == float.class) {
            return RANDOM.nextFloat();
        }
        if (clazz == Short.class || clazz == short.class) {
            return (short) RANDOM.nextInt(65535);
        }
        if (clazz == Byte.class || clazz == byte.class) {
            return (byte) RANDOM.nextInt(255);
        }
        if (clazz == Character.class || clazz == char.class) {
            return (char) RANDOM.nextInt(255);
        }
        if (clazz == String.class) {
            byte[] bytes = new byte[RANDOM.nextInt(20)];
            RANDOM.nextBytes(bytes);
            return Base64.getEncoder().encodeToString(bytes);
        }
        if (clazz == Splitor.class) {
            return new Splitor(String.valueOf((char) RANDOM.nextInt(255)));
        }
        if (clazz == IntPeriodScalar.class) {
            return IntPeriodScalar.of(RANDOM.nextInt(5), 5 + RANDOM.nextInt(10));
        }
        if (clazz == BigDecimal.class) {
            return BigDecimal.valueOf(RANDOM.nextDouble());
        }
        throw new IllegalStateException("unknown scalar type [" + clazz + "]");

    }

    private static void test(Class<?> clazz, int rounds) {
        for (int i = 0; i < rounds; i++) {
            test(clazz, RANDOM.nextInt(MAXIMUM_DIMENSIONS), Randomer.random(DimensionModel.LIST, DimensionModel.ARRAY), TestObjectBuilder::chance);
        }
    }

    private static void test(Class<?> clazz, int dimensions, DimensionModel dimensionModel, Function<Integer, Boolean> chanceCalculator) {
        Object randomObject = buildRandomObject(clazz, dimensions, dimensionModel, chanceCalculator);
        Object describedObjectAsMap = describeObjectInMapsAndList(randomObject);
        Object describeObjectAsString = describeObjectInString(describedObjectAsMap);
        Value describeObjectAsValue = describeObjectAsValue(randomObject);

        TestUtils.assertEquals(randomObject, builder().buildFromObject(describedObjectAsMap, clazz, dimensions, dimensionModel, BuildingObjectConfig.ONLY_USE_EXACT_LIST));
        TestUtils.assertEquals(randomObject, builder().buildFromObject(describeObjectAsString, clazz, dimensions, dimensionModel, BuildingObjectConfig.DISABLE_BOTH));
        TestUtils.assertEquals(randomObject, builder().buildFromValue(describeObjectAsValue, clazz, dimensions, dimensionModel, BuildingObjectConfig.DISABLE_BOTH));
    }

}
