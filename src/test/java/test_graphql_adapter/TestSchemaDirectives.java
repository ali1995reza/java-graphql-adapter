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

import java.util.Arrays;

import static test_graphql_adapter.utils.StaticTests.*;

public class TestSchemaDirectives {

    @Test
    public void overallTest() {
        Assertions.assertEquals(10, TestSchemaProvider.schema().discoveredDirectives().size());
    }

    @Test
    public void testAddPageParametersDirective() {
        DiscoveredDirective directive = findAndTestDirective(AddPageParameters.class, "AddPageParameters", 0);
        assertDescriptionIsNull(directive);
    }

    @Test
    public void testAuthenticationDirective() {
        DiscoveredDirective directive = findAndTestDirective(Authentication.class, "Authentication", 1);
        assertDescriptionEquals(directive, "D1");

        MappedAnnotationMethod tokenArgument = findAnnotationMethodAndTest("token", directive, 0, TypeInformation.nullable(String.class));
        assertDescriptionEquals(tokenArgument, "D2");
    }

    @Test
    public void testDelayDirective() {
        DiscoveredDirective directive = findAndTestDirective(Delay.class, "Delay", 1);

        MappedAnnotationMethod secondsArgument = findAnnotationMethodAndTest("seconds", directive, 0, TypeInformation.nonNullable(int.class));
        assertDescriptionEquals(secondsArgument, "D3");
    }

    @Test
    public void testFooProviderDirective() {
        DiscoveredDirective directive = findAndTestDirective(FooProvider.class, "FooProvider", 3);
        assertDescriptionEquals(directive, "provide FooInput");

        MappedAnnotationMethod valueArgument = findAnnotationMethodAndTest("value", directive, 0, TypeInformation.nonNullable(Foo.class),
                new Foo().setIntValue(-101).setIntValue2(-102).setIntArray(new int[]{1, 2, 3, 4, 5, 6}));
        assertDescriptionEquals(valueArgument, "D5");

        MappedAnnotationMethod arrayValuesArgument = findAnnotationMethodAndTest("arrayValues", directive, 0, TypeInformation.nullableArray(Foo.class, 1),
                new Foo[]{new Foo().setIntValue(-25), new Foo().setIntValue(-26), new Foo().setIntValue(-27)});
        assertDescriptionEquals(arrayValuesArgument, "D4");

        MappedAnnotationMethod listValuesArgument = findAnnotationMethodAndTest("listValues", directive, 0, TypeInformation.nonNullableList(Foo.class, 1),
                Arrays.asList(new Foo().setIntValue(-28), new Foo().setIntValue(-29), new Foo().setIntValue(-30)));
        assertDescriptionIsNull(listValuesArgument);
    }

    @Test
    public void testHashDirective() {
        DiscoveredDirective directive = findAndTestDirective(Hash.class, "Hash", 2);
        assertDescriptionIsNull(directive);

        MappedAnnotationMethod algorithmArgument = findAnnotationMethodAndTest("algorithm", directive, 0, TypeInformation.nonNullable(String.class), "SHA-256");
        assertDescriptionEquals(algorithmArgument, "the algorithm that directive will use to hash value");

        MappedAnnotationMethod saltArgument = findAnnotationMethodAndTest("salt", directive, 0, TypeInformation.nullable(String.class), "");
        assertDescriptionIsNull(saltArgument);
    }

    @Test
    public void testInputProviderDirective() {
        DiscoveredDirective directive = findAndTestDirective(ComplexInputProvider.class, "InputProvider", 1);
        assertDescriptionIsNull(directive);

        MappedAnnotationMethod versionArgument = findAnnotationMethodAndTest("input", directive, 1, TypeInformation.nullable(Complex.class),
                new Complex("k1", "v1", 1).setInner(new Complex("k2", "v2", 2).setInner(new Complex("k3", "v3", 3))));
        findAppliedAnnotationAndTest(Since.class, "Since", versionArgument, "version", "1.0.20");
        assertDescriptionIsNull(versionArgument);
    }

    @Test
    public void testReverseDirective() {
        DiscoveredDirective directive = findAndTestDirective(Reverse.class, "Reverse", 0);
        assertDescriptionEquals(directive, "D77");
    }

    @Test
    public void testSinceDirective() {
        DiscoveredDirective directive = findAndTestDirective(Since.class, "Since", 1);
        assertDescriptionIsNull(directive);

        MappedAnnotationMethod versionArgument = findAnnotationMethodAndTest("version", directive, 1, TypeInformation.nonNullable(String.class));
        findAppliedAnnotationAndTest(Since.class, "Since", versionArgument, "version", "1.0.16");
        assertDescriptionIsNull(versionArgument);
    }

    @Test
    public void testToStringDirective() {
        DiscoveredDirective directive = findAndTestDirective(ToStringDirective.class, "ToString", 0);
        assertDescriptionEquals(directive, "D99");
    }

    @Test
    public void testUpperCaseDirective() {
        DiscoveredDirective directive = findAndTestDirective(UpperCase.class, "UpperCase", 0);
        assertDescriptionIsNull(directive);
    }
}
