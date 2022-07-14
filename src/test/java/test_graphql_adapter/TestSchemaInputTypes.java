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

import static test_graphql_adapter.utils.StaticTests.*;

public class TestSchemaInputTypes {

    @Test
    public void overallTest() {
        Assertions.assertEquals(4, TestSchemaProvider.schema().discoveredInputTypes().size());
    }

    @Test
    public void testComplexInputType() {

        DiscoveredInputType inputType = findAndTestInputType(Complex.class, "ComplexInput", 4, 1);
        findAppliedAnnotationAndTest(Since.class, "Since", inputType, "version", "1.0.25");
        assertDescriptionEquals(inputType, "D980");

        MappedInputFieldMethod nameField = findInputFieldAndTest("name", inputType, TypeInformation.nullable(String.class));
        assertDescriptionIsNull(nameField);

        MappedInputFieldMethod valueField = findInputFieldAndTest("value", inputType, TypeInformation.nullable(String.class));
        assertDescriptionEquals(valueField, "D2900");

        MappedInputFieldMethod priorityField = findInputFieldAndTest("priority", inputType, 1, TypeInformation.nonNullable(int.class));
        findAppliedAnnotationAndTest(Since.class, "Since", priorityField, "version", "1.0.21");
        assertDescriptionIsNull(priorityField);

        MappedInputFieldMethod innerField = findInputFieldAndTest("inner", inputType, 0, TypeInformation.nullable(Complex.class),
                new Complex().setName("dn").setValue("dv").setPriority(100).setInner(new Complex().setName("idn").setValue("idv").setPriority(101)));
        assertDescriptionEquals(innerField, "D891");
    }

    @Test
    public void testFooInputType() {
        DiscoveredInputType inputType = findAndTestInputType(Foo.class, "FooInput", 12);
        assertDescriptionEquals(inputType, "D456");

        MappedInputFieldMethod stringValueField = findInputFieldAndTest("stringValue", inputType, 0, TypeInformation.nullable(String.class), "DV1");
        assertDescriptionIsNull(stringValueField);

        MappedInputFieldMethod longValueField = findInputFieldAndTest("longValue", inputType, 0, TypeInformation.nonNullable(long.class), 9223372036854775807L);
        assertDescriptionIsNull(longValueField);

        MappedInputFieldMethod intValueField = findInputFieldAndTest("intValue", inputType, 0, TypeInformation.nonNullable(int.class), 1);
        assertDescriptionIsNull(intValueField);

        MappedInputFieldMethod intValue2Field = findInputFieldAndTest("intValue2", inputType, 0, TypeInformation.nonNullable(int.class), 10);
        assertDescriptionIsNull(intValue2Field);

        MappedInputFieldMethod doubleValueField = findInputFieldAndTest("doubleValue", inputType, 0, TypeInformation.nonNullable(double.class), 2123455500000.21D);
        assertDescriptionIsNull(doubleValueField);

        MappedInputFieldMethod floatValueField = findInputFieldAndTest("floatValue", inputType, 0, TypeInformation.nonNullable(float.class), 15477542.236F);
        assertDescriptionIsNull(floatValueField);

        MappedInputFieldMethod byteValueField = findInputFieldAndTest("byteValue", inputType, 0, TypeInformation.nonNullable(byte.class), (byte) 120);
        assertDescriptionIsNull(byteValueField);

        MappedInputFieldMethod shortValueField = findInputFieldAndTest("shortValue", inputType, 0, TypeInformation.nonNullable(short.class), (short) 20000);
        assertDescriptionIsNull(shortValueField);

        MappedInputFieldMethod charValueField = findInputFieldAndTest("charValue", inputType, 0, TypeInformation.nonNullable(char.class), 'c');
        assertDescriptionIsNull(charValueField);

        MappedInputFieldMethod booleanValueField = findInputFieldAndTest("booleanValue", inputType, 0, TypeInformation.nonNullable(boolean.class), true);
        assertDescriptionIsNull(booleanValueField);

        MappedInputFieldMethod booleanValue2Field = findInputFieldAndTest("booleanValue2", inputType, 0, TypeInformation.nonNullable(boolean.class), false);
        assertDescriptionIsNull(booleanValue2Field);

        MappedInputFieldMethod intArrayField = findInputFieldAndTest("intArray", inputType, 0, TypeInformation.nullableArray(int.class), new int[]{1, 2, 3});
        assertDescriptionEquals(intArrayField, "D989");
    }

    @Test
    public void testInputUserType() {
        DiscoveredInputType inputType = findAndTestInputType(InputUser.class, "InputUser", 2, 1);
        findAppliedAnnotationAndTest(Since.class, "Since", inputType, "version", "1.0.7");
        assertDescriptionIsNull(inputType);

        MappedInputFieldMethod nameField = findInputFieldAndTest("name", inputType, 0, TypeInformation.nonNullable(String.class), "Anonymous");
        assertDescriptionIsNull(nameField);

        MappedInputFieldMethod typeField = findInputFieldAndTest("type", inputType, 1, TypeInformation.nullable(UserType.class), UserType.Normal);
        findAppliedAnnotationAndTest(Since.class, "Since", typeField, "version", "1.0.8");
        assertDescriptionIsNull(typeField);
    }

    @Test
    public void testPageParametersType() {
        DiscoveredInputType inputType = findAndTestInputType(PageParameters.class, "PageParameters", 2);
        assertDescriptionIsNull(inputType);

        MappedInputFieldMethod pageField = findInputFieldAndTest("page", inputType, TypeInformation.nonNullable(int.class));
        assertDescriptionIsNull(pageField);

        MappedInputFieldMethod sizeField = findInputFieldAndTest("size", inputType, TypeInformation.nonNullable(int.class));
        assertDescriptionIsNull(sizeField);
    }
}
