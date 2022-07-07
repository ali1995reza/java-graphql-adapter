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

import graphql.language.StringValue;
import graphql.schema.*;

public final class ID {

    public final static GraphQLScalarType ScalarType =
            GraphQLScalarType.newScalar()
                    .name("ID")
                    .coercing(new Coercing() {
                        @Override
                        public Object parseLiteral(Object o) throws CoercingParseLiteralException {
                            if (o instanceof StringValue) {
                                return new ID(((StringValue) o).getValue());
                            }

                            throw new CoercingParseLiteralException("Expect StringValue but found [" + o.getClass() + "]");

                        }

                        @Override
                        public Object parseValue(Object o) throws CoercingParseValueException {
                            if (o instanceof String) {
                                return new ID(o.toString());
                            }


                            throw new CoercingParseValueException("Expect String but found [" + o.getClass() + "]");
                        }

                        @Override
                        public Object serialize(Object o) throws CoercingSerializeException {
                            if (o instanceof ID) {
                                return o.toString();
                            }

                            throw new CoercingSerializeException("Expect ID but found [" + o.getClass() + "]");
                        }
                    }).build();


    private final String id;

    public ID(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }
}
