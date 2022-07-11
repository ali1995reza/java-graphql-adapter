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

import graphql.schema.DataFetchingEnvironment;
import grphaqladapter.adaptedschema.AdaptedGraphQLSchema;
import grphaqladapter.adaptedschema.discovered.DiscoveredObjectType;
import grphaqladapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import grphaqladapter.adaptedschema.mapping.mapped_elements.TypeInformation;
import grphaqladapter.adaptedschema.mapping.mapped_elements.method.MappedFieldMethod;
import grphaqladapter.adaptedschema.mapping.mapped_elements.parameter.MappedParameter;
import grphaqladapter.adaptedschema.mapping.mapped_elements.parameter.ParameterModel;
import grphaqladapter.adaptedschema.system_objects.directive.GraphqlDirectivesHolder;
import org.junit.jupiter.api.Test;
import tests.T1.schema.*;
import tests.T1.schema.directives.*;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tests.T1.TestUtils.isParameterNamePresent;

public class TestSchemaObjectTypes {

    @Test
    public void overallTest() {
        assertEquals(11, TestSchemaProvider.schema().discoveredObjectTypes().size());
    }

    @Test
    public void testAdminUserType() {
        DiscoveredObjectType type = StaticTests.findAndTestObjectType(AdminUser.class, "Admin", 3);

        StaticTests.findFieldAndTest("name", type, TypeInformation.nonNullable(String.class));

        StaticTests.findFieldAndTest("type", type, TypeInformation.nonNullable(UserType.class));

        StaticTests.findFieldAndTest("token", type, TypeInformation.nullable(String.class));

        StaticTests.assertAndTestNumberOfImplementedInterfaces(1, type);
        StaticTests.assertAndTestNumberOfPossibleUnions(0, type);

        StaticTests.findInterfaceTypeAndTest(UserInterface.class, "UserInterface", type);
    }

    @Test
    public void testBankAccountType() {
        DiscoveredObjectType type = StaticTests.findAndTestObjectType(BankAccount.class, "BankAccount", 3);

        StaticTests.findFieldAndTest("id", type, TypeInformation.nullable(String.class));

        StaticTests.findFieldAndTest("username", type, TypeInformation.nullable(String.class));

        StaticTests.findFieldAndTest("balance", type, TypeInformation.nullable(Double.class));

        StaticTests.assertAndTestNumberOfImplementedInterfaces(0, type);
        StaticTests.assertAndTestNumberOfPossibleUnions(0, type);
    }

    @Test
    public void testBusType() {
        DiscoveredObjectType type = StaticTests.findAndTestObjectType(Bus.class, "Bus", 3, 1);
        StaticTests.findAppliedAnnotationAndTest(Since.class, "Since", type, "version", "1.0.1");

        MappedFieldMethod modelField = StaticTests.findFieldAndTest("model", type, 0, 3, TypeInformation.nullable(String.class));
        StaticTests.findAppliedAnnotationAndTest(Delay.class, "Delay", modelField, "seconds", 0);
        StaticTests.findAppliedAnnotationAndTest(UpperCase.class, "UpperCase", modelField);
        StaticTests.findAppliedAnnotationAndTest(Hash.class, "Hash", modelField, "salt", "some_salt", "algorithm", "SHA-256");

        MappedFieldMethod produceYearField = StaticTests.findFieldAndTest("produceYear", type, 0, 1, TypeInformation.nullable(Integer.class));
        StaticTests.findAppliedAnnotationAndTest(ToStringDirective.class, "ToString", produceYearField);

        StaticTests.findFieldAndTest("size", type, TypeInformation.nullable(Integer.class));

        StaticTests.assertAndTestNumberOfImplementedInterfaces(0, type);
        StaticTests.assertAndTestNumberOfPossibleUnions(1, type);

        StaticTests.findUnionTypeAndTest(Vehicle.class, "Vehicle", type);
    }

    @Test
    public void testCarType() {
        DiscoveredObjectType type = StaticTests.findAndTestObjectType(Car.class, "Car", 2);

        StaticTests.findFieldAndTest("model", type, TypeInformation.nullable(String.class));

        MappedFieldMethod produceYearField = StaticTests.findFieldAndTest("produceYear", type, 0, 1, TypeInformation.nullable(Integer.class));
        StaticTests.findAppliedAnnotationAndTest(ToStringDirective.class, "ToString", produceYearField);

        StaticTests.assertAndTestNumberOfImplementedInterfaces(0, type);
        StaticTests.assertAndTestNumberOfPossibleUnions(1, type);

        StaticTests.findUnionTypeAndTest(Vehicle.class, "Vehicle", type);
    }

    @Test
    public void testComplexOutputType() {

        DiscoveredObjectType type = StaticTests.findAndTestObjectType(Complex.class, "ComplexOutput", 4, 1);
        StaticTests.findAppliedAnnotationAndTest(Since.class, "Since", type, "version", "1.0.25");


        StaticTests.findFieldAndTest("name", type, TypeInformation.nullable(String.class));

        StaticTests.findFieldAndTest("value", type, TypeInformation.nullable(String.class));

        MappedFieldMethod priorityField = StaticTests.findFieldAndTest("priority", type, 0, 1, TypeInformation.nonNullable(int.class));
        StaticTests.findAppliedAnnotationAndTest(Since.class, "Since", priorityField, "version", "1.0.21");

        StaticTests.findFieldAndTest("inner", type, TypeInformation.nullable(Complex.class));

    }

    @Test
    public void testIntListType() {
        DiscoveredObjectType type = StaticTests.findAndTestObjectType(IntList.class, "IntList", 4);

        StaticTests.findFieldAndTest("data", type, TypeInformation.nullableList(Integer.class));

        StaticTests.findFieldAndTest("isEmpty", type, TypeInformation.nonNullable(boolean.class));

        MappedFieldMethod sizeField = StaticTests.findFieldAndTest("size", type, 1, TypeInformation.nonNullable(int.class));
        StaticTests.findParameterAndTest(null, sizeField, ParameterModel.DATA_FETCHING_ENVIRONMENT, 0, TypeInformation.nullable(DataFetchingEnvironment.class));

        MappedFieldMethod getField = StaticTests.findFieldAndTest("get", type, 1, TypeInformation.nullable(Integer.class));
        StaticTests.findParameterAndTest("index", getField, ParameterModel.SCHEMA_ARGUMENT, 0, 0, TypeInformation.nonNullable(int.class), 0);

        StaticTests.assertAndTestNumberOfImplementedInterfaces(0, type);
        StaticTests.assertAndTestNumberOfPossibleUnions(0, type);
    }

    @Test
    public void testMutation() {
        DiscoveredObjectType type = StaticTests.findAndTestObjectType(TestMutation.class, "TestMutation", 6, 0, MappedElementType.MUTATION);

        MappedFieldMethod encodeToBase64Field = StaticTests.findFieldAndTest("encodeToBase64", type, 1, TypeInformation.nullable(String.class));
        StaticTests.findParameterAndTest("input", encodeToBase64Field, ParameterModel.SCHEMA_ARGUMENT, 0, 0, TypeInformation.nullable(String.class));

        MappedFieldMethod splitField = StaticTests.findFieldAndTest("split", type, 2, 1, TypeInformation.nullableList(String.class));
        StaticTests.findAppliedAnnotationAndTest(Since.class, "Since", splitField, "version", "1.0.12");
        StaticTests.findParameterAndTest("input", splitField, ParameterModel.SCHEMA_ARGUMENT, 0, TypeInformation.nullable(String.class));
        MappedParameter splitorParameter = StaticTests.findParameterAndTest("splitor", splitField, ParameterModel.SCHEMA_ARGUMENT, 1, 1, TypeInformation.nonNullable(Splitor.class));
        StaticTests.findAppliedAnnotationAndTest(Since.class, "Since", splitorParameter, "version", "1.0.13");

        MappedFieldMethod combineInto3DMatrixField = StaticTests.findFieldAndTest("combineInto3DMatrix", type, 2, 0, TypeInformation.nullableArray(int.class, 3));
        StaticTests.findParameterAndTest("a", combineInto3DMatrixField, ParameterModel.SCHEMA_ARGUMENT, 0, TypeInformation.nullableArray(int.class, 2));
        StaticTests.findParameterAndTest(isParameterNamePresent() ? "b" : "arg1", combineInto3DMatrixField, ParameterModel.SCHEMA_ARGUMENT, 1, TypeInformation.nullableList(Integer.class, 2));

        MappedFieldMethod listToArrayField = StaticTests.findFieldAndTest("listToArray", type, 1, 0, TypeInformation.nullableArray(int.class));
        StaticTests.findParameterAndTest(isParameterNamePresent() ? "list" : "arg0", listToArrayField, ParameterModel.SCHEMA_ARGUMENT, 0, TypeInformation.nullableList(Integer.class));

        MappedFieldMethod inputToOutputField = StaticTests.findFieldAndTest("inputToOutput", type, 1, 1, TypeInformation.nullable(Foo.class));
        StaticTests.findParameterAndTest("input", inputToOutputField, ParameterModel.SCHEMA_ARGUMENT, 0, 0, TypeInformation.nullable(Foo.class),
                new Foo().setIntValue(-1).setIntValue2(-2).setIntArray(new int[]{1, 2, 3, 4}));
        StaticTests.findAppliedAnnotationAndTest(FooProvider.class, "FooProvider", inputToOutputField, "value", new Foo().setIntValue(-100).setIntValue2(-200),
                "arrayValues", new Foo[]{new Foo().setIntValue(-25), new Foo().setIntValue(-26), new Foo().setIntValue(-27)},
                "listValues", Arrays.asList(new Foo().setIntValue(-1000), new Foo().setIntValue(-2000), new Foo().setIntValue(-3000)));

        MappedFieldMethod inputToOutputFromDirectiveField = StaticTests.findFieldAndTest("inputToOutputFromDirective", type, 2, 0, TypeInformation.nullable(Foo.class));
        StaticTests.findParameterAndTest("index", inputToOutputFromDirectiveField, ParameterModel.SCHEMA_ARGUMENT, 0, 0, TypeInformation.nonNullable(int.class), -1);


        StaticTests.assertAndTestNumberOfImplementedInterfaces(1, type);
        StaticTests.assertAndTestNumberOfPossibleUnions(0, type);

        StaticTests.findInterfaceTypeAndTest(MutationInterface.class, "MutationInterface", type);

    }

    @Test
    public void testNormalUserType() {
        DiscoveredObjectType type = StaticTests.findAndTestObjectType(NormalUser.class, "User", 2);

        StaticTests.findFieldAndTest("name", type, TypeInformation.nonNullable(String.class));

        StaticTests.findFieldAndTest("type", type, TypeInformation.nonNullable(UserType.class));

        StaticTests.assertAndTestNumberOfImplementedInterfaces(1, type);
        StaticTests.assertAndTestNumberOfPossibleUnions(0, type);

        StaticTests.findInterfaceTypeAndTest(UserInterface.class, "UserInterface", type);
    }

    @Test
    public void testFooOutputType() {
        DiscoveredObjectType objectType = StaticTests.findAndTestObjectType(Foo.class, "FooOutput", 12);

        StaticTests.findFieldAndTest("stringValue", objectType, 0, TypeInformation.nullable(String.class));

        StaticTests.findFieldAndTest("longValue", objectType, 0, TypeInformation.nonNullable(long.class));

        StaticTests.findFieldAndTest("intValue", objectType, 0, TypeInformation.nonNullable(int.class));

        StaticTests.findFieldAndTest("intValue2", objectType, 0, TypeInformation.nonNullable(int.class));

        StaticTests.findFieldAndTest("doubleValue", objectType, 0, TypeInformation.nonNullable(double.class));

        StaticTests.findFieldAndTest("floatValue", objectType, 0, TypeInformation.nonNullable(float.class));

        StaticTests.findFieldAndTest("byteValue", objectType, 0, TypeInformation.nonNullable(byte.class));

        StaticTests.findFieldAndTest("shortValue", objectType, 0, TypeInformation.nonNullable(short.class));

        StaticTests.findFieldAndTest("charValue", objectType, 0, TypeInformation.nonNullable(char.class));

        StaticTests.findFieldAndTest("booleanValue", objectType, 0, TypeInformation.nonNullable(boolean.class));

        StaticTests.findFieldAndTest("booleanValue2", objectType, 0, TypeInformation.nonNullable(boolean.class));

        StaticTests.findFieldAndTest("intArray", objectType, 0, TypeInformation.nullableArray(int.class));

    }

    @Test
    public void testPageDetailsType() {
        DiscoveredObjectType type = StaticTests.findAndTestObjectType(PageDetails.class, "PageDetails", 2);

        StaticTests.findFieldAndTest("page", type, TypeInformation.nonNullable(int.class));

        StaticTests.findFieldAndTest("size", type, TypeInformation.nonNullable(int.class));

        StaticTests.assertAndTestNumberOfImplementedInterfaces(0, type);
        StaticTests.assertAndTestNumberOfPossibleUnions(0, type);
    }

    @Test
    public void testQueryType() {
        DiscoveredObjectType type = StaticTests.findAndTestObjectType(Query.class, "TestQuery", 11, 1, MappedElementType.QUERY);
        StaticTests.findAppliedAnnotationAndTest(Since.class, "Since", type, "version", "1.0.6");

        MappedFieldMethod getListField = StaticTests.findFieldAndTest("getList", type, 1, TypeInformation.nullable(IntList.class));
        StaticTests.findParameterAndTest("period", getListField, ParameterModel.SCHEMA_ARGUMENT, 0, TypeInformation.nullable(IntPeriodScalar.class));

        MappedFieldMethod multiplyMatrixField = StaticTests.findFieldAndTest("multiplyMatrices", type, 2, TypeInformation.nullableList(Integer.class, 2));
        StaticTests.findParameterAndTest("m1", multiplyMatrixField, ParameterModel.SCHEMA_ARGUMENT, 0, 0, TypeInformation.nonNullableList(Integer.class, 2),
                Arrays.asList(Arrays.asList(1, 2, 3), Arrays.asList(4, 5, 6), Arrays.asList(7, 8, 9)));
        StaticTests.findParameterAndTest("m2", multiplyMatrixField, ParameterModel.SCHEMA_ARGUMENT, 1, 0, TypeInformation.nonNullableArray(Integer.class, 2),
                new Integer[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}});

        MappedFieldMethod getUserField = StaticTests.findFieldAndTest("getUser", type, 1, TypeInformation.nullable(UserInterface.class));
        StaticTests.findParameterAndTest("user", getUserField, ParameterModel.SCHEMA_ARGUMENT, 0, TypeInformation.nonNullable(InputUser.class));

        MappedFieldMethod getVehicleField = StaticTests.findFieldAndTest("getVehicle", type, 2, TypeInformation.nullable(Vehicle.class));
        StaticTests.findParameterAndTest("isCar", getVehicleField, ParameterModel.SCHEMA_ARGUMENT, 0, 0, TypeInformation.nullable(Boolean.class), true);
        StaticTests.findParameterAndTest(null, getVehicleField, ParameterModel.DATA_FETCHING_ENVIRONMENT, 1, TypeInformation.nullable(DataFetchingEnvironment.class));

        MappedFieldMethod getBankAccountField = StaticTests.findFieldAndTest("getBankAccount", type, 2, TypeInformation.nullable(BankAccount.class));
        StaticTests.findParameterAndTest(isParameterNamePresent() ? "username" : "arg0", getBankAccountField, ParameterModel.SCHEMA_ARGUMENT, 0, TypeInformation.nullable(String.class));
        StaticTests.findParameterAndTest(null, getBankAccountField, ParameterModel.DATA_FETCHING_ENVIRONMENT, 1, TypeInformation.nullable(DataFetchingEnvironment.class));

        MappedFieldMethod isSystemParamsHealthyField = StaticTests.findFieldAndTest("isSystemParamsHealthy", type, 4, TypeInformation.nonNullable(boolean.class));
        StaticTests.findParameterAndTest(null, isSystemParamsHealthyField, ParameterModel.ADAPTED_SCHEMA, 0, TypeInformation.nullable(AdaptedGraphQLSchema.class));
        StaticTests.findParameterAndTest(null, isSystemParamsHealthyField, ParameterModel.ADAPTED_SCHEMA, 1, TypeInformation.nullable(AdaptedGraphQLSchema.class));
        StaticTests.findParameterAndTest(null, isSystemParamsHealthyField, ParameterModel.DATA_FETCHING_ENVIRONMENT, 2, TypeInformation.nullable(DataFetchingEnvironment.class));
        StaticTests.findParameterAndTest(null, isSystemParamsHealthyField, ParameterModel.DIRECTIVES, 3, TypeInformation.nullable(GraphqlDirectivesHolder.class));

        MappedFieldMethod isDirectivesHealthyField = StaticTests.findFieldAndTest("isDirectivesHealthy", type, 1, TypeInformation.nonNullable(boolean.class));
        StaticTests.findParameterAndTest(null, isDirectivesHealthyField, ParameterModel.DIRECTIVES, 0, TypeInformation.nullable(GraphqlDirectivesHolder.class));

        MappedFieldMethod getPageDetailsField = StaticTests.findFieldAndTest("getPageDetails", type, 2, 1, TypeInformation.nullable(PageDetails.class));
        StaticTests.findParameterAndTest(null, getPageDetailsField, ParameterModel.ADAPTED_SCHEMA, 0, TypeInformation.nullable(AdaptedGraphQLSchema.class));
        StaticTests.findParameterAndTest(null, getPageDetailsField, ParameterModel.DATA_FETCHING_ENVIRONMENT, 1, TypeInformation.nullable(DataFetchingEnvironment.class));
        StaticTests.findAppliedAnnotationAndTest(AddPageParameters.class, "AddPageParameters", getPageDetailsField);

        StaticTests.findFieldAndTest("getDeveloperName", type, TypeInformation.nullable(String.class));

        MappedFieldMethod serializeToStringField = StaticTests.findFieldAndTest("serializeToString", type, 2, 0, TypeInformation.nullable(String.class));
        StaticTests.findParameterAndTest("input", serializeToStringField, ParameterModel.SCHEMA_ARGUMENT, 0, 0, TypeInformation.nullable(Complex.class),
                new Complex("k1", "v1", 1).setInner(new Complex("k2", "v2", 2).setInner(new Complex("k3", "v3", 3))));
        StaticTests.findParameterAndTest("separator", serializeToStringField, ParameterModel.SCHEMA_ARGUMENT, 1, 0, TypeInformation.nonNullable(char.class), ',');

        MappedFieldMethod serializeToStringFromDirective = StaticTests.findFieldAndTest("serializeToStringFromDirective", type, 2, 0, TypeInformation.nullable(String.class));
        StaticTests.findParameterAndTest("separator", serializeToStringFromDirective, ParameterModel.SCHEMA_ARGUMENT, 0, 0, TypeInformation.nonNullable(char.class), ',');
        StaticTests.findParameterAndTest(null, serializeToStringFromDirective, ParameterModel.DIRECTIVES, 1, TypeInformation.nullable(GraphqlDirectivesHolder.class));


        StaticTests.assertAndTestNumberOfImplementedInterfaces(0, type);
        StaticTests.assertAndTestNumberOfPossibleUnions(0, type);

    }
}
