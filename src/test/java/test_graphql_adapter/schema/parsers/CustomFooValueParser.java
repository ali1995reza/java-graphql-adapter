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
package test_graphql_adapter.schema.parsers;

import graphql_adapter.adaptedschema.functions.ValueParser;
import graphql_adapter.adaptedschema.functions.ValueParsingContext;
import graphql_adapter.adaptedschema.mapping.mapped_elements.TypeInformation;
import test_graphql_adapter.schema.types.Foo;

public class CustomFooValueParser implements ValueParser<String, Foo> {
    @Override
    public Foo parse(String input, TypeInformation<?> descriptor, ValueParsingContext context) {
        Foo foo = new Foo();
        foo.setIntValue(-1);
        foo.setIntValue2(-2);
        foo.setIntArray(new int[]{1, 2, 3, 4});
        return foo;
    }
}
