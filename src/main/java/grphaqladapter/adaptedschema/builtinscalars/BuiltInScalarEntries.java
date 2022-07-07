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

package grphaqladapter.adaptedschema.builtinscalars;

import graphql.Scalars;
import graphql.language.FloatValue;
import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.scalars.java.JavaPrimitives;
import graphql.schema.*;
import grphaqladapter.adaptedschema.scalar.ScalarEntry;
import grphaqladapter.adaptedschema.scalar.impl.ScalarEntryBuilder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BuiltInScalarEntries {

    private final static GraphQLScalarType GraphQLDouble = GraphQLScalarType.newScalar().name("Double").description("java.lang.Double").coercing(new Coercing<Object, Double>() {
        @Override
        public Double parseLiteral(Object input) {
            if (input instanceof StringValue) {
                return Double.parseDouble(((StringValue) input).getValue());
            } else if (input instanceof IntValue) {
                return ((IntValue) input).getValue().doubleValue();
            } else if (input instanceof FloatValue) {
                return ((FloatValue) input).getValue().doubleValue();
            } else {
                throw new CoercingParseLiteralException("Expected AST type 'IntValue', 'StringValue' or 'FloatValue' but was '" + input.getClass() + "'.");
            }
        }

        @Override
        public Double parseValue(Object input) {
            Double result = this.convert(input);
            if (result == null) {
                throw new CoercingParseValueException("Expected type 'BigDecimal' but was '" + input.getClass() + "'.");
            } else {
                return result;
            }
        }

        @Override
        public Double serialize(Object input) {
            Double result = this.convert(input);
            if (result == null) {
                throw new CoercingSerializeException("Expected type Number or String but was '" + input.getClass() + "'.");
            } else {
                return result;
            }
        }

        @Override
        public Value<?> valueToLiteral(Object input) {
            Double result = Objects.requireNonNull(this.convert(input));
            return FloatValue.newFloatValue(new BigDecimal(result)).build();
        }

        private Double convert(Object input) {
            if (input instanceof Number || input instanceof String) {
                try {
                    return Double.parseDouble(input.toString());
                } catch (NumberFormatException var3) {
                    return null;
                }
            } else {
                return null;
            }
        }
    }).build();

    private final static Map<Class, ScalarEntry> ENTRIES;

    static {
        Map<Class, ScalarEntry> entryMap = new HashMap<>();
        //entryMap.put(String.class, convert(String.class, Scalars.GraphQLString));
        entryMap.put(Integer.class, convert(Integer.class, Scalars.GraphQLInt));
        //entryMap.put(int.class, convert(int.class, Scalars.GraphQLInt));
        entryMap.put(Long.class, convert(Long.class, JavaPrimitives.GraphQLLong));
        //entryMap.put(long.class, convert(long.class, JavaPrimitives.GraphQLLong));
        entryMap.put(Double.class, convert(Double.class, GraphQLDouble));
        //entryMap.put(double.class, convert(double.class, GraphQLDouble));
        entryMap.put(Float.class, convert(Float.class, Scalars.GraphQLFloat));
        //entryMap.put(float.class, convert(float.class, Scalars.GraphQLFloat));
        //entryMap.put(Boolean.class, convert(Boolean.class, Scalars.GraphQLBoolean));
        //entryMap.put(boolean.class, convert(boolean.class, Scalars.GraphQLBoolean));
        entryMap.put(Character.class, convert(Character.class, JavaPrimitives.GraphQLChar));
        //entryMap.put(char.class, convert(char.class, JavaPrimitives.GraphQLChar));
        entryMap.put(Byte.class, convert(Byte.class, JavaPrimitives.GraphQLByte));
        //entryMap.put(byte.class, convert(byte.class, JavaPrimitives.GraphQLByte));
        entryMap.put(Short.class, convert(Short.class, JavaPrimitives.GraphQLShort));
        //entryMap.put(short.class, convert(short.class, JavaPrimitives.GraphQLShort));
        entryMap.put(BigDecimal.class, convert(BigDecimal.class, JavaPrimitives.GraphQLBigDecimal));
        entryMap.put(ID.class, convert(ID.class, ID.ScalarType));
        ENTRIES = Collections.unmodifiableMap(entryMap);
    }

    public static Map<Class, ScalarEntry> entries() {
        return ENTRIES;
    }

    public static ScalarEntry getBuiltInScalar(Class clazz) {
        return entries().get(clazz);
    }

    private static ScalarEntry convert(Class clazz, GraphQLScalarType type) {
        return ScalarEntryBuilder.newBuilder()
                .type(clazz)
                .name(type.getName())
                .coercing(type.getCoercing())
                .description(type.getDescription())
                .build();

    }

}
