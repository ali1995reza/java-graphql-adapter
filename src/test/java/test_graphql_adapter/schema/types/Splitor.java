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

package test_graphql_adapter.schema.types;

import graphql.language.NullValue;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import test_graphql_adapter.schema.directives.Since;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Since("1.0.3")
public class Splitor {

    private final String str;

    public Splitor(String str) {
        this.str = str;
    }

    public List<String> split(String s) {
        return Arrays.asList(s.split(str));
    }

    public String getSplitorString() {
        return str;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Splitor splitor = (Splitor) o;
        return Objects.equals(str, splitor.str);
    }

    @Override
    public int hashCode() {
        return Objects.hash(str);
    }

    @Override
    public String toString() {
        return "Splitor{" +
                "str='" + str + '\'' +
                '}';
    }

    public final static class CoercingImpl implements Coercing<Object, Object> {

        @Override
        public Object parseLiteral(Object o) throws CoercingParseLiteralException {
            if (o == null || o instanceof NullValue) {
                return null;
            }
            if (o instanceof StringValue) {
                return new Splitor(((StringValue) o).getValue());
            }

            throw new CoercingParseLiteralException("Expect String but found [" + o.getClass() + "]");

        }

        @Override
        public Object parseValue(Object o) throws CoercingParseValueException {
            if (o == null) {
                return null;
            }
            if (o instanceof String) {
                return new Splitor((String) o);
            } else if (o instanceof Splitor) {
                return o;
            }

            throw new CoercingParseValueException("Expect String but found [" + o.getClass() + "]");

        }

        @Override
        public Object serialize(Object o) throws CoercingSerializeException {
            if (o instanceof Splitor) {
                return ((Splitor) o).str;
            }

            throw new CoercingSerializeException("Expect Splitor but found [" + o.getClass() + "]");

        }

        @Override
        public Value<?> valueToLiteral(Object input) {
            if (input instanceof Splitor) {
                return StringValue.of(((Splitor) input).getSplitorString());
            }

            throw new CoercingSerializeException("Expect Splitor but found [" + input.getClass() + "]");
        }
    }

}
