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

import grphaqladapter.adaptedschema.discovered.DiscoveredDirective;
import grphaqladapter.adaptedschema.mapping.mapped_elements.TypeInformation;
import grphaqladapter.adaptedschema.mapping.mapped_elements.method.MappedAnnotationMethod;
import org.junit.jupiter.api.Test;
import tests.T1.schema.Complex;
import tests.T1.schema.directives.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSchemaDirectives {

    @Test
    public void overallTest() {
        assertEquals(9, TestSchemaProvider.schema().discoveredDirectives().size());
    }

    @Test
    public void testAddPageParametersDirective() {
        StaticTests.findAndTestDirective(AddPageParameters.class, "AddPageParameters", 0);
    }

    @Test
    public void testAuthenticationDirective() {
        DiscoveredDirective directive = StaticTests.findAndTestDirective(Authentication.class, "Authentication", 1);

        StaticTests.findAnnotationMethodAndTest("token", directive, 0, TypeInformation.nullable(String.class));
    }

    @Test
    public void testDelayDirective() {
        DiscoveredDirective directive = StaticTests.findAndTestDirective(Delay.class, "Delay", 1);

        StaticTests.findAnnotationMethodAndTest("seconds", directive, 0, TypeInformation.nonNullable(int.class));
    }


    @Test
    public void testHashDirective() {
        DiscoveredDirective directive = StaticTests.findAndTestDirective(Hash.class, "Hash", 2);

        StaticTests.findAnnotationMethodAndTest("algorithm", directive, 0, TypeInformation.nonNullable(String.class));
        StaticTests.findAnnotationMethodAndTest("salt", directive, 0, TypeInformation.nullable(String.class));
    }

    @Test
    public void testSinceDirective() {
        DiscoveredDirective directive = StaticTests.findAndTestDirective(Since.class, "Since", 1);

        MappedAnnotationMethod versionArgument = StaticTests.findAnnotationMethodAndTest("version", directive, 1, TypeInformation.nonNullable(String.class));
        StaticTests.findAppliedAnnotationAndTest(Since.class, "Since", versionArgument, "version", "1.0.16");
    }

    @Test
    public void testToStringDirective() {
        StaticTests.findAndTestDirective(ToStringDirective.class, "ToString", 0);
    }

    @Test
    public void testUpperCaseDirective() {
        StaticTests.findAndTestDirective(UpperCase.class, "UpperCase", 0);
    }

    @Test
    public void testReverseDirective() {
        StaticTests.findAndTestDirective(Reverse.class, "Reverse", 0);
    }

    @Test
    public void testInputProviderDirective() {
        DiscoveredDirective directive = StaticTests.findAndTestDirective(ComplexInputProvider.class, "InputProvider", 1);

        MappedAnnotationMethod versionArgument = StaticTests.findAnnotationMethodAndTest("input", directive, 1, TypeInformation.nullable(Complex.class));
        StaticTests.findAppliedAnnotationAndTest(Since.class, "Since", versionArgument, "version", "1.0.20");
    }

}
