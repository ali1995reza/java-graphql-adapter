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

import graphql_adapter.adaptedschema.discovered.DiscoveredScalarType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test_graphql_adapter.schema.TestSchemaProvider;
import test_graphql_adapter.schema.directives.Since;
import test_graphql_adapter.schema.types.IntPeriodScalar;
import test_graphql_adapter.schema.types.Splitor;
import test_graphql_adapter.utils.StaticTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSchemaScalarTypes {

    @Test
    public void overallTest() {
        Assertions.assertEquals(12, TestSchemaProvider.schema().discoveredScalarTypes().size());
    }

    @Test
    public void testPeriodScalarType() {
        DiscoveredScalarType scalarType = StaticTests.findAndTestScalarType(IntPeriodScalar.class, "Period");

        assertEquals(scalarType.asMappedElement().baseClass(), IntPeriodScalar.class);
        assertEquals(scalarType.asMappedElement().coercing().getClass(), IntPeriodScalar.CoercingImpl.class);

        StaticTests.findAppliedAnnotationAndTest(Since.class, "Since", scalarType, "version", "1.0.2");
    }

    @Test
    public void testSplitorScalarType() {
        DiscoveredScalarType scalarType = StaticTests.findAndTestScalarType(Splitor.class, "Splitor");

        assertEquals(scalarType.asMappedElement().baseClass(), Splitor.class);
        assertEquals(scalarType.asMappedElement().coercing().getClass(), Splitor.CoercingImpl.class);

        StaticTests.findAppliedAnnotationAndTest(Since.class, "Since", scalarType, "version", "1.0.3");
    }
}
