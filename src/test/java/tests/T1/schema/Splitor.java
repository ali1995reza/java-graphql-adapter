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

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import tests.T1.schema.directives.Since;

import java.util.Arrays;
import java.util.List;

@Since("1.0.3")
public class Splitor {

    private final String str;

    public Splitor(String str) {
        this.str = str;
    }

    public List<String> split(String s) {
        return Arrays.asList(s.split(str));
    }

    public final static class CoercingImpl implements Coercing {

        @Override
        public Object parseLiteral(Object o) throws CoercingParseLiteralException {

            if (o instanceof StringValue) {
                return new Splitor(((StringValue) o).getValue());
            }

            throw new CoercingParseLiteralException("Expect String but found [" + o.getClass() + "]");

        }

        @Override
        public Object parseValue(Object o) throws CoercingParseValueException {
            if (o instanceof String) {
                return new Splitor((String) o);
            }

            throw new CoercingParseValueException("Expect String but found [" + o.getClass() + "]");

        }

        @Override
        public Object serialize(Object o) throws CoercingSerializeException {
            if (o instanceof Splitor) {
                return "$plitor(" + ((Splitor) o).str + ")";
            }

            throw new CoercingSerializeException("Expect Splitor but found [" + o.getClass() + "]");

        }
    }

}
