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

package tests.T1.schema.parsers;

import grphaqladapter.adaptedschema.functions.ValueParser;
import grphaqladapter.adaptedschema.functions.ValueParsingContext;
import grphaqladapter.adaptedschema.mapping.mapped_elements.TypeInformation;
import tests.T1.schema.Foo;

public class CustomDirectiveArgumentFooValueParser implements ValueParser<int[], Foo> {
    @Override
    public Foo parse(int[] input, TypeInformation descriptor, ValueParsingContext context) {
        Foo foo = new Foo();
        foo.setIntValue(input[0]);
        foo.setIntValue2(input[1]);
        return foo;
    }
}
