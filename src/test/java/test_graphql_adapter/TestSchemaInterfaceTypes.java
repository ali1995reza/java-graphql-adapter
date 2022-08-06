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

import static test_graphql_adapter.utils.StaticTests.*;

public class TestSchemaInterfaceTypes {

    @Test
    public void overallTest() {
        Assertions.assertEquals(2, TestSchemaProvider.schema().discoveredInterfacesTypes().size());
    }

    @Test
    public void testMutationInterfaceType() {
        DiscoveredInterfaceType interfaceType = findAndTestInterfaceType(MutationInterface.class, "MutationInterface", 0, 2, TestMutation.class);
        assertDescriptionEquals(interfaceType, "Mutation Abstract");

        MappedFieldMethod encodeToBase64Field = findFieldAndTest("encodeToBase64", interfaceType, 1, TypeInformation.nullable(String.class));
        MappedParameter inputParameter = findParameterAndTest("input", encodeToBase64Field, ParameterModel.SCHEMA_ARGUMENT, 0, 1, TypeInformation.nullable(String.class));
        findAppliedAnnotationAndTest(Since.class, "Since", inputParameter, "version", "1.0.14");
        assertDescriptionEquals(inputParameter, "D29876");
        assertDescriptionEquals(encodeToBase64Field, "D2299");

        MappedFieldMethod splitField = findFieldAndTest("split", interfaceType, 2, 1, TypeInformation.list(String.class, true, true));
        findAppliedAnnotationAndTest(Since.class, "Since", splitField, "version", "1.0.12");
        MappedParameter inputParameter2 = findParameterAndTest("input", splitField, ParameterModel.SCHEMA_ARGUMENT, 0, TypeInformation.nullable(String.class));
        assertDescriptionEquals(inputParameter2, "D2019");
        MappedParameter splitorParameter = findParameterAndTest("splitor", splitField, ParameterModel.SCHEMA_ARGUMENT, 1, 1, TypeInformation.nonNullable(Splitor.class));
        findAppliedAnnotationAndTest(Since.class, "Since", splitorParameter, "version", "1.0.13");
        assertDescriptionIsNull(splitorParameter);
        assertDescriptionEquals(splitField, "D145");
    }

    @Test
    public void testUserInterfaceInterfaceType() {
        DiscoveredInterfaceType interfaceType = findAndTestInterfaceType(UserInterface.class, "UserInterface", 1, 2, NormalUser.class, AdminUser.class);
        findAppliedAnnotationAndTest(Since.class, "Since", interfaceType, "version", "1.0.4");
        assertDescriptionEquals(interfaceType, "D8999999");

        MappedFieldMethod nameField = findFieldAndTest("name", interfaceType, 0, 1, TypeInformation.nonNullable(String.class));
        findAppliedAnnotationAndTest(Since.class, "Since", nameField, "version", "1.0.11");
        assertDescriptionIsNull(nameField);

        MappedFieldMethod typeField = findFieldAndTest("type", interfaceType, 0, 0, TypeInformation.nonNullable(UserType.class));
        assertDescriptionIsNull(typeField);
    }
}
