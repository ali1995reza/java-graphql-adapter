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

package tests.T1.schema;

import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import grphaqladapter.annotations.GraphqlDescription;
import grphaqladapter.annotations.GraphqlScalar;
import tests.T1.schema.directives.Since;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Pattern;

@GraphqlScalar(name = "Period", coercing = IntPeriodScalar.CoercingImpl.class)
@GraphqlDescription("This is an custom description")
@Since("1.0.2")
public class IntPeriodScalar {

    private final static Pattern PeriodPattern =
            Pattern.compile("[\\[(]\\d+[:]\\d+[)\\]]");

    public static IntPeriodScalar of(int lowerBound, int upperBound) {
        return new IntPeriodScalar(new Bound(lowerBound, true), new Bound(upperBound, true));
    }

    public static IntPeriodScalar parse(String asString) {
        if (PeriodPattern.matcher(asString).matches()) {
            String[] split = asString.split(":");
            return new IntPeriodScalar(
                    new Bound(Integer.parseInt(split[0].substring(1)), split[0].charAt(0) == '['),
                    new Bound(Integer.parseInt(split[1].substring(0, split[1].length() - 1)), split[1].charAt(split[1].length() - 1) == ']')
            );
        }


        throw new IllegalStateException("invalid format");
    }

    private final Bound lowBound;
    private final Bound upBound;
    private final String asString;

    private IntPeriodScalar(Bound from, Bound to) {
        this.lowBound = from;
        this.upBound = to;


        if (from.compareTo(to) > 0)
            throw new IllegalStateException("lower bound bigger than upper bound");


        asString = lowBound.asLowerBound() + ":" + upBound.asUpperBound();
    }

    @Override
    public String toString() {
        return asString;
    }

    public void forEach(Consumer<Integer> forEach) {
        int start = lowBound.isContains() ? lowBound.number() : lowBound.number() + 1;
        int end = upBound.isContains() ? upBound.number() : upBound.number() - 1;

        for (int i = start; i <= end; i++) {
            forEach.accept(i);
        }
    }

    public Bound lowBound() {
        return lowBound;
    }

    public Bound upBound() {
        return upBound;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntPeriodScalar that = (IntPeriodScalar) o;
        return Objects.equals(lowBound, that.lowBound) && Objects.equals(upBound, that.upBound) && Objects.equals(asString, that.asString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lowBound, upBound, asString);
    }

    public final static class CoercingImpl implements Coercing {

        @Override
        public Object parseLiteral(Object o) throws CoercingParseLiteralException {

            if (o instanceof StringValue) {
                return IntPeriodScalar.parse(((StringValue) o).getValue());
            } else if (o instanceof IntValue) {
                int bound = ((IntValue) o).getValue().intValue();
                return IntPeriodScalar.of(bound, bound);
            }

            throw new CoercingParseLiteralException("Expect String but found [" + o.getClass() + "]");

        }

        @Override
        public Object parseValue(Object o) throws CoercingParseValueException {
            if (o instanceof String) {
                return IntPeriodScalar.parse((String) o);
            } else if (o instanceof Integer) {
                return new IntPeriodScalar(new Bound((Integer) o, true), new Bound((Integer) o, true));
            } else if (o instanceof Byte) {
                return new IntPeriodScalar(new Bound((Byte) o, true), new Bound((Byte) o, true));
            } else if (o instanceof Short) {
                return new IntPeriodScalar(new Bound((Short) o, true), new Bound((Short) o, true));
            } else if (o instanceof IntPeriodScalar) {
                return o;
            }

            throw new CoercingParseValueException("Expect String or IntPeriodScalar but found [" + o.getClass() + "]");

        }

        @Override
        public Object serialize(Object o) throws CoercingSerializeException {
            if (o instanceof IntPeriodScalar) {
                return o.toString();
            }

            throw new CoercingSerializeException("Expect IntPeriodScalar but found [" + o.getClass() + "]");

        }

        @Override
        public Value valueToLiteral(Object input) {
            if (input instanceof IntPeriodScalar) {
                return StringValue.of(input.toString());
            }

            throw new CoercingSerializeException("Expect IntPeriodScalar but found [" + input.getClass() + "]");

        }
    }
}
