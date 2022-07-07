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

package tests.T1;

import grphaqladapter.adaptedschema.discovered.DiscoveredInputType;
import grphaqladapter.adaptedschema.mapping.mapped_elements.TypeInformation;
import grphaqladapter.adaptedschema.mapping.mapped_elements.method.MappedInputFieldMethod;
import org.junit.jupiter.api.Test;
import tests.T1.schema.Complex;
import tests.T1.schema.InputUser;
import tests.T1.schema.PageParameters;
import tests.T1.schema.UserType;
import tests.T1.schema.directives.Since;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSchemaInputTypes {

    @Test
    public void overallTest() {
        assertEquals(3, TestSchemaProvider.schema().discoveredInputTypes().size());
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

}
