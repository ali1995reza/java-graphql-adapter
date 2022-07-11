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

package test_graphql_adapter;

import graphql_adapter.adaptedschema.discovered.DiscoveredInputType;
import graphql_adapter.adaptedschema.mapping.mapped_elements.TypeInformation;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedInputFieldMethod;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test_graphql_adapter.schema.TestSchemaProvider;
import test_graphql_adapter.schema.directives.Since;
import test_graphql_adapter.schema.types.*;
import test_graphql_adapter.utils.StaticTests;

public class TestSchemaInputTypes {

    @Test
    public void overallTest() {
        Assertions.assertEquals(4, TestSchemaProvider.schema().discoveredInputTypes().size());
    }

    @Test
    public void testComplexInputType() {

        DiscoveredInputType inputType = StaticTests.findAndTestInputType(Complex.class, "ComplexInput", 4, 1);
        StaticTests.findAppliedAnnotationAndTest(Since.class, "Since", inputType, "version", "1.0.25");

        StaticTests.findInputFieldAndTest("name", inputType, TypeInformation.nullable(String.class));

        StaticTests.findInputFieldAndTest("value", inputType, TypeInformation.nullable(String.class));

        MappedInputFieldMethod priorityField = StaticTests.findInputFieldAndTest("priority", inputType, 1, TypeInformation.nonNullable(int.class));
        StaticTests.findAppliedAnnotationAndTest(Since.class, "Since", priorityField, "version", "1.0.21");

        StaticTests.findInputFieldAndTest("inner", inputType, 0, TypeInformation.nullable(Complex.class),
                new Complex().setName("dn").setValue("dv").setPriority(100).setInner(new Complex().setName("idn").setValue("idv").setPriority(101)));

    }

    @Test
    public void testInputUserType() {
        DiscoveredInputType inputType = StaticTests.findAndTestInputType(InputUser.class, "InputUser", 2, 1);
        StaticTests.findAppliedAnnotationAndTest(Since.class, "Since", inputType, "version", "1.0.7");

        StaticTests.findInputFieldAndTest("name", inputType, 0, TypeInformation.nonNullable(String.class), "Anonymous");

        MappedInputFieldMethod typeInputField = StaticTests.findInputFieldAndTest("type", inputType, 1, TypeInformation.nullable(UserType.class), UserType.Normal);
        StaticTests.findAppliedAnnotationAndTest(Since.class, "Since", typeInputField, "version", "1.0.8");

    }

    @Test
    public void testPageParametersType() {
        DiscoveredInputType inputType = StaticTests.findAndTestInputType(PageParameters.class, "PageParameters", 2);

        StaticTests.findInputFieldAndTest("page", inputType, TypeInformation.nonNullable(int.class));

        StaticTests.findInputFieldAndTest("size", inputType, TypeInformation.nonNullable(int.class));

    }

    @Test
    public void testFooInputType() {
        DiscoveredInputType inputType = StaticTests.findAndTestInputType(Foo.class, "FooInput", 12);

        StaticTests.findInputFieldAndTest("stringValue", inputType, 0, TypeInformation.nullable(String.class), "DV1");

        StaticTests.findInputFieldAndTest("longValue", inputType, 0, TypeInformation.nonNullable(long.class), 9223372036854775807L);

        StaticTests.findInputFieldAndTest("intValue", inputType, 0, TypeInformation.nonNullable(int.class), 1);

        StaticTests.findInputFieldAndTest("intValue2", inputType, 0, TypeInformation.nonNullable(int.class), 10);

        StaticTests.findInputFieldAndTest("doubleValue", inputType, 0, TypeInformation.nonNullable(double.class), 2123455500000.21D);

        StaticTests.findInputFieldAndTest("floatValue", inputType, 0, TypeInformation.nonNullable(float.class), 15477542.236F);

        StaticTests.findInputFieldAndTest("byteValue", inputType, 0, TypeInformation.nonNullable(byte.class), (byte) 120);

        StaticTests.findInputFieldAndTest("shortValue", inputType, 0, TypeInformation.nonNullable(short.class), (short) 20000);

        StaticTests.findInputFieldAndTest("charValue", inputType, 0, TypeInformation.nonNullable(char.class), 'c');

        StaticTests.findInputFieldAndTest("booleanValue", inputType, 0, TypeInformation.nonNullable(boolean.class), true);

        StaticTests.findInputFieldAndTest("booleanValue2", inputType, 0, TypeInformation.nonNullable(boolean.class), false);

        StaticTests.findInputFieldAndTest("intArray", inputType, 0, TypeInformation.nullableArray(int.class), new int[]{1, 2, 3});

    }

}
