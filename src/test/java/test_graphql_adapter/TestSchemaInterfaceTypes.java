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


import graphql_adapter.adaptedschema.discovered.DiscoveredInterfaceType;
import graphql_adapter.adaptedschema.mapping.mapped_elements.TypeInformation;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedFieldMethod;
import graphql_adapter.adaptedschema.mapping.mapped_elements.parameter.MappedParameter;
import graphql_adapter.adaptedschema.mapping.mapped_elements.parameter.ParameterModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test_graphql_adapter.schema.TestSchemaProvider;
import test_graphql_adapter.schema.directives.Since;
import test_graphql_adapter.schema.types.*;
import test_graphql_adapter.utils.StaticTests;

public class TestSchemaInterfaceTypes {

    @Test
    public void overallTest() {
        Assertions.assertEquals(2, TestSchemaProvider.schema().discoveredInterfacesTypes().size());
    }

    @Test
    public void testMutationInterfaceType() {
        DiscoveredInterfaceType interfaceType = StaticTests.findAndTestInterfaceType(MutationInterface.class, "MutationInterface", 0, 2, TestMutation.class);

        MappedFieldMethod encodeToBase64Field = StaticTests.findFieldAndTest("encodeToBase64", interfaceType, 1, TypeInformation.nullable(String.class));
        MappedParameter inputParameter = StaticTests.findParameterAndTest("input", encodeToBase64Field, ParameterModel.SCHEMA_ARGUMENT, 0, 1, TypeInformation.nullable(String.class));
        StaticTests.findAppliedAnnotationAndTest(Since.class, "Since", inputParameter, "version", "1.0.14");

        MappedFieldMethod splitField = StaticTests.findFieldAndTest("split", interfaceType, 2, 1, TypeInformation.nullableList(String.class));
        StaticTests.findAppliedAnnotationAndTest(Since.class, "Since", splitField, "version", "1.0.12");
        StaticTests.findParameterAndTest("input", splitField, ParameterModel.SCHEMA_ARGUMENT, 0, TypeInformation.nullable(String.class));
        MappedParameter splitorParameter = StaticTests.findParameterAndTest("splitor", splitField, ParameterModel.SCHEMA_ARGUMENT, 1, 1, TypeInformation.nonNullable(Splitor.class));
        StaticTests.findAppliedAnnotationAndTest(Since.class, "Since", splitorParameter, "version", "1.0.13");

    }

    @Test
    public void testUserInterfaceInterfaceType() {
        DiscoveredInterfaceType interfaceType = StaticTests.findAndTestInterfaceType(UserInterface.class, "UserInterface", 1, 2, NormalUser.class, AdminUser.class);
        StaticTests.findAppliedAnnotationAndTest(Since.class, "Since", interfaceType, "version", "1.0.4");

        MappedFieldMethod nameField = StaticTests.findFieldAndTest("name", interfaceType, 0, 1, TypeInformation.nonNullable(String.class));
        StaticTests.findAppliedAnnotationAndTest(Since.class, "Since", nameField, "version", "1.0.11");

        StaticTests.findFieldAndTest("type", interfaceType, 0, 0, TypeInformation.nonNullable(UserType.class));
    }
}
