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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test_graphql_adapter.schema.TestSchemaProvider;
import test_graphql_adapter.schema.types.Bus;
import test_graphql_adapter.schema.types.Car;
import test_graphql_adapter.schema.types.Vehicle;
import test_graphql_adapter.utils.StaticTests;

public class TestSchemaUnionTypes {

    @Test
    public void overallTest() {
        Assertions.assertEquals(1, TestSchemaProvider.schema().discoveredUnionTypes().size());
    }

    @Test
    public void testVehicleUnionType() {

        StaticTests.findAndTestUnionType(Vehicle.class, "Vehicle", 1, Bus.class, Car.class);
    }
}
