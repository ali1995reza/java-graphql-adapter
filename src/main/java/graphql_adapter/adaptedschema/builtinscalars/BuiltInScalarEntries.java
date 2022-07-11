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

package graphql_adapter.adaptedschema.builtinscalars;

import graphql.language.*;
import graphql.schema.*;
import graphql_adapter.adaptedschema.scalar.ScalarEntry;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BuiltInScalarEntries {

    private final static GraphQLScalarType GraphQLDouble = GraphQLScalarType.newScalar().name("Double").description("java.lang.Double").coercing(new Coercing<Object, Double>() {
        @Override
        public Double parseLiteral(Object input) {
            if(input instanceof NullValue || input == null) {
                return null;
            }
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
            return this.convert(input);
        }

        @Override
        public Double serialize(Object input) {
            return this.convert(input);
        }

        @Override
        public Value<?> valueToLiteral(Object input) {
            Double converted = convert(input);
            if (converted == null) {
                return NullValue.of();
            }
            return FloatValue.newFloatValue(new BigDecimal(converted)).build();
        }

        private Double convert(Object input) {
            if (input == null) {
                return null;
            }
            if (input instanceof Number || input instanceof String) {
                try {
                    return Double.parseDouble(input.toString());
                } catch (NumberFormatException e) {
                    throw new CoercingSerializeException(e);
                }
            } else {
                throw new CoercingSerializeException("Expected type Number or String but was '" + input.getClass() + "'.");
            }
        }
    }).build();

    private final static GraphQLScalarType GraphQLFloat = GraphQLScalarType.newScalar().name("Float").description("java.lang.Float").coercing(new Coercing<Object, Float>() {
        @Override
        public Float parseLiteral(Object input) {
            if(input instanceof NullValue || input == null) {
                return null;
            }
            if (input instanceof StringValue) {
                return Float.parseFloat(((StringValue) input).getValue());
            } else if (input instanceof IntValue) {
                return ((IntValue) input).getValue().floatValue();
            } else if (input instanceof FloatValue) {
                return ((FloatValue) input).getValue().floatValue();
            } else {
                throw new CoercingParseLiteralException("Expected AST type 'IntValue', 'StringValue' or 'FloatValue' but was '" + input.getClass() + "'.");
            }
        }

        @Override
        public Float parseValue(Object input) {
            return this.convert(input);
        }

        @Override
        public Float serialize(Object input) {
            return this.convert(input);
        }

        @Override
        public Value<?> valueToLiteral(Object input) {
            Float converted = convert(input);
            if (converted == null) {
                return NullValue.of();
            }
            return FloatValue.newFloatValue(new BigDecimal(converted)).build();
        }

        private Float convert(Object input) {
            if (input == null) {
                return null;
            }
            if (input instanceof Number || input instanceof String) {
                try {
                    return Float.parseFloat(input.toString());
                } catch (NumberFormatException e) {
                    throw new CoercingSerializeException(e);
                }
            } else {
                throw new CoercingSerializeException("Expected type Number or String but was '" + input.getClass() + "'.");
            }
        }
    }).build();

    private final static GraphQLScalarType GraphQLLong = GraphQLScalarType.newScalar().name("Long").description("java.lang.Long").coercing(new Coercing<Object, Long>() {
        @Override
        public Long parseLiteral(Object input) {
            if(input instanceof NullValue || input == null) {
                return null;
            }
            if (input instanceof StringValue) {
                return Long.parseLong(((StringValue) input).getValue());
            } else if (input instanceof IntValue) {
                return ((IntValue) input).getValue().longValueExact();
            } else {
                throw new CoercingParseLiteralException("Expected AST type 'IntValue' or 'StringValue' but was '" + input.getClass() + "'.");
            }
        }

        @Override
        public Long parseValue(Object input) {
            return this.convert(input);
        }

        @Override
        public Long serialize(Object input) {
            return this.convert(input);
        }

        @Override
        public Value<?> valueToLiteral(Object input) {
            Long converted = convert(input);
            if (converted == null) {
                return NullValue.of();
            }
            return IntValue.newIntValue(BigInteger.valueOf(converted)).build();
        }

        private Long convert(Object input) {
            if (input == null) {
                return null;
            }
            if (input instanceof Number || input instanceof String) {
                try {
                    return Long.parseLong(input.toString());
                } catch (NumberFormatException e) {
                    throw new CoercingSerializeException(e);
                }
            } else {
                throw new CoercingParseValueException("Expected type 'BigDecimal' but was '" + input.getClass() + "'.");
            }
        }
    }).build();

    private final static GraphQLScalarType GraphQLInt = GraphQLScalarType.newScalar().name("Int").description("java.lang.Integer").coercing(new Coercing<Object, Integer>() {
        @Override
        public Integer parseLiteral(Object input) {
            if(input instanceof NullValue || input == null) {
                return null;
            }
            if (input instanceof StringValue) {
                return Integer.parseInt(((StringValue) input).getValue());
            } else if (input instanceof IntValue) {
                return ((IntValue) input).getValue().intValueExact();
            } else {
                throw new CoercingParseLiteralException("Expected AST type 'IntValue' or 'StringValue' but was '" + input.getClass() + "'.");
            }
        }

        @Override
        public Integer parseValue(Object input) {
            return this.convert(input);
        }

        @Override
        public Integer serialize(Object input) {
            return this.convert(input);
        }

        @Override
        public Value<?> valueToLiteral(Object input) {
            Integer converted = convert(input);
            if(converted == null) {
                return NullValue.of();
            }
            return IntValue.newIntValue(BigInteger.valueOf(converted)).build();
        }

        private Integer convert(Object input) {
            if (input == null) {
                return null;
            }
            if (input instanceof Number || input instanceof String) {
                try {
                    return Integer.parseInt(input.toString());
                } catch (NumberFormatException e) {
                    throw new CoercingSerializeException(e);
                }
            } else {
                throw new CoercingParseValueException("Expected type 'Number' or 'String' but was '" + input.getClass() + "'.");
            }
        }
    }).build();

    private final static GraphQLScalarType GraphQLShort = GraphQLScalarType.newScalar().name("Short").description("java.lang.Short").coercing(new Coercing<Object, Short>() {
        @Override
        public Short parseLiteral(Object input) {
            if(input instanceof NullValue || input == null) {
                return null;
            }
            if (input instanceof StringValue) {
                return Short.parseShort(((StringValue) input).getValue());
            } else if (input instanceof IntValue) {
                return ((IntValue) input).getValue().shortValueExact();
            } else {
                throw new CoercingParseLiteralException("Expected AST type 'IntValue' or 'StringValue' but was '" + input.getClass() + "'.");
            }
        }

        @Override
        public Short parseValue(Object input) {
            return this.convert(input);
        }

        @Override
        public Short serialize(Object input) {
            return this.convert(input);
        }

        @Override
        public Value<?> valueToLiteral(Object input) {
            Short converted = convert(input);
            if(converted == null) {
                return NullValue.of();
            }
            return IntValue.newIntValue(BigInteger.valueOf(converted)).build();
        }

        private Short convert(Object input) {
            if (input == null) {
                return null;
            }
            if (input instanceof Number || input instanceof String) {
                try {
                    return Short.parseShort(input.toString());
                } catch (NumberFormatException e) {
                    throw new CoercingSerializeException(e);
                }
            } else {
                throw new CoercingParseValueException("Expected type 'BigDecimal' but was '" + input.getClass() + "'.");
            }
        }
    }).build();

    private final static GraphQLScalarType GraphQLChar = GraphQLScalarType.newScalar().name("Char").description("java.lang.Char").coercing(new Coercing<Object, Character>() {
        @Override
        public Character parseLiteral(Object input) {
            if(input instanceof NullValue || input == null) {
                return null;
            }
            if (input instanceof StringValue) {
                String value = ((StringValue) input).getValue();
                if (value.length() != 1) {
                    throw new CoercingSerializeException("just single character StringValues can parse to Character");
                }
                return value.charAt(0);
            } else {
                throw new CoercingParseLiteralException("Expected AST type 'StringValue' but was '" + input.getClass() + "'.");
            }
        }

        @Override
        public Character parseValue(Object input) {
            return this.convert(input);
        }

        @Override
        public Character serialize(Object input) {
            return this.convert(input);
        }

        @Override
        public Value<?> valueToLiteral(Object input) {
            Character converted = convert(input);
            if (converted == null) {
                return NullValue.of();
            }
            return StringValue.newStringValue(String.valueOf(converted)).build();
        }

        private Character convert(Object input) {
            if (input == null) {
                return null;
            }
            if (input instanceof String) {
                String value = (String) input;
                if (value.length() != 1) {
                    throw new CoercingSerializeException("just single character StringValues can parse to Character");
                }
                return value.charAt(0);
            } else if (input instanceof Character) {
                return (Character) input;
            } else {
                throw new CoercingSerializeException("Expected type String but was '" + input.getClass() + "'.");
            }
        }
    }).build();

    private final static GraphQLScalarType GraphQLByte = GraphQLScalarType.newScalar().name("Byte").description("java.lang.Byte").coercing(new Coercing<Object, Byte>() {
        @Override
        public Byte parseLiteral(Object input) {
            if(input instanceof NullValue || input == null) {
                return null;
            }
            if (input instanceof StringValue) {
                return Byte.parseByte(((StringValue) input).getValue());
            } else if (input instanceof IntValue) {
                return ((IntValue) input).getValue().byteValueExact();
            } else {
                throw new CoercingParseLiteralException("Expected AST type 'IntValue' or 'StringValue' but was '" + input.getClass() + "'.");
            }
        }

        @Override
        public Byte parseValue(Object input) {
            return this.convert(input);
        }

        @Override
        public Byte serialize(Object input) {
            return this.convert(input);
        }

        @Override
        public Value<?> valueToLiteral(Object input) {
            Byte converted = convert(input);
            if (converted == null) {
                return NullValue.of();
            }
            return IntValue.newIntValue(BigInteger.valueOf(converted)).build();
        }

        private Byte convert(Object input) {
            if (input == null) {
                return null;
            }
            if (input instanceof Number || input instanceof String) {
                try {
                    return Byte.parseByte(input.toString());
                } catch (NumberFormatException e) {
                    throw new IllegalStateException(e);
                }
            } else {
                throw new CoercingSerializeException("Expected type Number or String but was '" + input.getClass() + "'.");
            }
        }
    }).build();

    private final static GraphQLScalarType GraphQLBigDecimal = GraphQLScalarType.newScalar().name("BigDecimal").description("java.math.BigDecimal").coercing(new Coercing<Object, BigDecimal>() {
        @Override
        public BigDecimal parseLiteral(Object input) {
            if(input instanceof NullValue || input == null) {
                return null;
            }
            if (input instanceof StringValue) {
                return new BigDecimal(((StringValue) input).getValue());
            } else if (input instanceof IntValue) {
                return BigDecimal.valueOf(((IntValue) input).getValue().doubleValue());
            } else if (input instanceof FloatValue) {
                return BigDecimal.valueOf(((FloatValue) input).getValue().doubleValue());
            } else {
                throw new CoercingParseLiteralException("Expected AST type 'IntValue', 'StringValue' or 'FloatValue' but was '" + input.getClass() + "'.");
            }
        }

        @Override
        public BigDecimal parseValue(Object input) {
            return this.convert(input);
        }

        @Override
        public BigDecimal serialize(Object input) {
            return this.convert(input);
        }

        @Override
        public Value<?> valueToLiteral(Object input) {
            BigDecimal converted = convert(input);
            if (converted == null) {
                return NullValue.of();
            }
            return FloatValue.newFloatValue(converted).build();
        }

        private BigDecimal convert(Object input) {
            if (input == null) {
                return null;
            }
            if (input instanceof BigDecimal) {
                return (BigDecimal) input;
            } else if (input instanceof Number || input instanceof String) {
                try {
                    return new BigDecimal(input.toString());
                } catch (NumberFormatException e) {
                    throw new CoercingSerializeException(e);
                }
            } else {
                throw new CoercingSerializeException("Expected type Number or String but was '" + input.getClass() + "'.");
            }
        }
    }).build();

    private final static Map<Class<?>, ScalarEntry> ENTRIES;

    static {

        Map<Class<?>, ScalarEntry> entryMap = new HashMap<>();
        entryMap.put(Integer.class, convert(Integer.class, GraphQLInt));
        entryMap.put(Long.class, convert(Long.class, GraphQLLong));
        entryMap.put(Double.class, convert(Double.class, GraphQLDouble));
        entryMap.put(Float.class, convert(Float.class, GraphQLFloat));
        entryMap.put(Character.class, convert(Character.class, GraphQLChar));
        entryMap.put(Byte.class, convert(Byte.class, GraphQLByte));
        entryMap.put(Short.class, convert(Short.class, GraphQLShort));
        entryMap.put(BigDecimal.class, convert(BigDecimal.class, GraphQLBigDecimal));
        ENTRIES = Collections.unmodifiableMap(entryMap);
    }

    public static Map<Class<?>, ScalarEntry> entries() {
        return ENTRIES;
    }

    public static ScalarEntry getBuiltInScalar(Class<?> clazz) {
        return entries().get(clazz);
    }

    private static ScalarEntry convert(Class<?> clazz, GraphQLScalarType type) {
        return ScalarEntry.newScalarEntry()
                .type(clazz)
                .name(type.getName())
                .coercing(type.getCoercing())
                .description(type.getDescription())
                .build();

    }

}
