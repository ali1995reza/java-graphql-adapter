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

import graphql_adapter.adaptedschema.discovered.DiscoveredDirective;
import graphql_adapter.adaptedschema.mapping.mapped_elements.TypeInformation;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedAnnotationMethod;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test_graphql_adapter.schema.TestSchemaProvider;
import test_graphql_adapter.schema.directives.*;
import test_graphql_adapter.schema.types.Complex;
import test_graphql_adapter.schema.types.Foo;
import test_graphql_adapter.utils.StaticTests;

import java.util.Arrays;

public class TestSchemaDirectives {

    @Test
    public void overallTest() {
        Assertions.assertEquals(10, TestSchemaProvider.schema().discoveredDirectives().size());
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
    public void testFooProviderDirective() {
        DiscoveredDirective directive = StaticTests.findAndTestDirective(FooProvider.class, "FooProvider", 3);

        StaticTests.findAnnotationMethodAndTest("value", directive, 0, TypeInformation.nonNullable(Foo.class),
                new Foo().setIntValue(-101).setIntValue2(-102).setIntArray(new int[]{1, 2, 3, 4, 5, 6}));

        StaticTests.findAnnotationMethodAndTest("arrayValues", directive, 0, TypeInformation.nullableArray(Foo.class, 1),
                new Foo[]{new Foo().setIntValue(-25), new Foo().setIntValue(-26), new Foo().setIntValue(-27)});

        StaticTests.findAnnotationMethodAndTest("listValues", directive, 0, TypeInformation.nonNullableList(Foo.class, 1),
                Arrays.asList(new Foo().setIntValue(-28), new Foo().setIntValue(-29), new Foo().setIntValue(-30)));
    }

    @Test
    public void testHashDirective() {
        DiscoveredDirective directive = StaticTests.findAndTestDirective(Hash.class, "Hash", 2);

        StaticTests.findAnnotationMethodAndTest("algorithm", directive, 0, TypeInformation.nonNullable(String.class), "SHA-256");
        StaticTests.findAnnotationMethodAndTest("salt", directive, 0, TypeInformation.nullable(String.class), "");
    }

    @Test
    public void testInputProviderDirective() {
        DiscoveredDirective directive = StaticTests.findAndTestDirective(ComplexInputProvider.class, "InputProvider", 1);

        MappedAnnotationMethod versionArgument = StaticTests.findAnnotationMethodAndTest("input", directive, 1, TypeInformation.nullable(Complex.class),
                new Complex("k1", "v1", 1).setInner(new Complex("k2", "v2", 2).setInner(new Complex("k3", "v3", 3))));
        StaticTests.findAppliedAnnotationAndTest(Since.class, "Since", versionArgument, "version", "1.0.20");
    }

    @Test
    public void testReverseDirective() {
        StaticTests.findAndTestDirective(Reverse.class, "Reverse", 0);
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
}
