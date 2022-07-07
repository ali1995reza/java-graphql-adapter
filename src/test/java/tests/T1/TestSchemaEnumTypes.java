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

import grphaqladapter.adaptedschema.discovered.DiscoveredEnumType;
import grphaqladapter.adaptedschema.mapping.mapped_elements.enums.MappedEnumConstant;
import org.junit.jupiter.api.Test;
import tests.T1.schema.UserType;
import tests.T1.schema.directives.Since;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSchemaEnumTypes {

    @Test
    public void overallTest() {
        assertEquals(1, TestSchemaProvider.schema().discoveredEnumTypes().size());
    }

    @Test
    public void testUserTypeEnumType() {
        DiscoveredEnumType enumType = StaticTests.findAndTestEnumType(UserType.class, "UserType", 2, 1);
        StaticTests.findAppliedAnnotationAndTest(Since.class, "Since", enumType, "version", "1.0.9");

        MappedEnumConstant adminConstant = StaticTests.findEnumValueAndTest("ADMIN", UserType.ADMIN, enumType, 1);
        StaticTests.findAppliedAnnotationAndTest(Since.class, "Since", adminConstant, "version", "1.0.10");

        StaticTests.findEnumValueAndTest("NORMAL", UserType.Normal, enumType, 0);

    }
}
