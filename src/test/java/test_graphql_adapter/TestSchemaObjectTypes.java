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

import graphql.schema.DataFetchingEnvironment;
import graphql_adapter.adaptedschema.AdaptedGraphQLSchema;
import graphql_adapter.adaptedschema.discovered.DiscoveredObjectType;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import graphql_adapter.adaptedschema.mapping.mapped_elements.TypeInformation;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedFieldMethod;
import graphql_adapter.adaptedschema.mapping.mapped_elements.parameter.MappedParameter;
import graphql_adapter.adaptedschema.mapping.mapped_elements.parameter.ParameterModel;
import graphql_adapter.adaptedschema.system_objects.directive.GraphqlDirectivesHolder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test_graphql_adapter.schema.TestSchemaProvider;
import test_graphql_adapter.schema.directives.*;
import test_graphql_adapter.schema.types.*;

import java.util.Arrays;

import static test_graphql_adapter.utils.StaticTests.*;
import static test_graphql_adapter.utils.TestUtils.isParameterNamePresent;

public class TestSchemaObjectTypes {

    @Test
    public void overallTest() {
        Assertions.assertEquals(11, TestSchemaProvider.schema().discoveredObjectTypes().size());
    }

    @Test
    public void testAdminUserType() {
        DiscoveredObjectType type = findAndTestObjectType(AdminUser.class, "Admin", 3);
        assertDescriptionIsNull(type);

        MappedFieldMethod nameField = findFieldAndTest("name", type, TypeInformation.nonNullable(String.class));
        assertDescriptionIsNull(nameField);

        MappedFieldMethod typeField = findFieldAndTest("type", type, TypeInformation.nonNullable(UserType.class));
        assertDescriptionIsNull(typeField);

        MappedFieldMethod tokenField = findFieldAndTest("token", type, TypeInformation.nullable(String.class));
        assertDescriptionIsNull(tokenField);

        assertAndTestNumberOfImplementedInterfaces(1, type);
        assertAndTestNumberOfPossibleUnions(0, type);

        findInterfaceTypeAndTest(UserInterface.class, "UserInterface", type);
    }

    @Test
    public void testBankAccountType() {
        DiscoveredObjectType type = findAndTestObjectType(BankAccount.class, "BankAccount", 3);
        assertDescriptionIsNull(type);

        MappedFieldMethod idField = findFieldAndTest("id", type, TypeInformation.nullable(String.class));
        assertDescriptionIsNull(idField);

        MappedFieldMethod usernameField = findFieldAndTest("username", type, TypeInformation.nullable(String.class));
        assertDescriptionIsNull(usernameField);

        MappedFieldMethod balanceField = findFieldAndTest("balance", type, TypeInformation.nullable(Double.class));
        assertDescriptionEquals(balanceField, "Balance as dollar");

        assertAndTestNumberOfImplementedInterfaces(0, type);
        assertAndTestNumberOfPossibleUnions(0, type);
    }

    @Test
    public void testBusType() {
        DiscoveredObjectType type = findAndTestObjectType(Bus.class, "Bus", 3, 1);
        findAppliedAnnotationAndTest(Since.class, "Since", type, "version", "1.0.1");
        assertDescriptionEquals(type, "Bus");

        MappedFieldMethod modelField = findFieldAndTest("model", type, 0, 3, TypeInformation.nullable(String.class));
        findAppliedAnnotationAndTest(Delay.class, "Delay", modelField, "seconds", 0);
        findAppliedAnnotationAndTest(UpperCase.class, "UpperCase", modelField);
        findAppliedAnnotationAndTest(Hash.class, "Hash", modelField, "salt", "some_salt", "algorithm", "SHA-256");
        assertDescriptionIsNull(modelField);

        MappedFieldMethod produceYearField = findFieldAndTest("produceYear", type, 0, 1, TypeInformation.nullable(Integer.class));
        findAppliedAnnotationAndTest(ToStringDirective.class, "ToString", produceYearField);
        assertDescriptionIsNull(produceYearField);

        MappedFieldMethod sizeField = findFieldAndTest("size", type, TypeInformation.nullable(Integer.class));
        assertDescriptionIsNull(sizeField);

        assertAndTestNumberOfImplementedInterfaces(0, type);
        assertAndTestNumberOfPossibleUnions(1, type);

        findUnionTypeAndTest(Vehicle.class, "Vehicle", type);
    }

    @Test
    public void testCarType() {
        DiscoveredObjectType type = findAndTestObjectType(Car.class, "Car", 2);
        assertDescriptionIsNull(type);

        MappedFieldMethod modelField = findFieldAndTest("model", type, TypeInformation.nullable(String.class));
        assertDescriptionIsNull(modelField);

        MappedFieldMethod produceYearField = findFieldAndTest("produceYear", type, 0, 1, TypeInformation.nullable(Integer.class));
        findAppliedAnnotationAndTest(ToStringDirective.class, "ToString", produceYearField);
        assertDescriptionEquals(produceYearField, "D76543");

        assertAndTestNumberOfImplementedInterfaces(0, type);
        assertAndTestNumberOfPossibleUnions(1, type);

        findUnionTypeAndTest(Vehicle.class, "Vehicle", type);
    }

    @Test
    public void testComplexOutputType() {

        DiscoveredObjectType type = findAndTestObjectType(Complex.class, "ComplexOutput", 4, 1);
        findAppliedAnnotationAndTest(Since.class, "Since", type, "version", "1.0.25");
        assertDescriptionEquals(type, "D980");

        MappedFieldMethod nameField = findFieldAndTest("name", type, TypeInformation.nullable(String.class));
        assertDescriptionIsNull(nameField);

        MappedFieldMethod valueField = findFieldAndTest("value", type, TypeInformation.nullable(String.class));
        assertDescriptionEquals(valueField, "D2900");

        MappedFieldMethod priorityField = findFieldAndTest("priority", type, 0, 1, TypeInformation.nonNullable(int.class));
        findAppliedAnnotationAndTest(Since.class, "Since", priorityField, "version", "1.0.21");
        assertDescriptionIsNull(priorityField);

        MappedFieldMethod innerField = findFieldAndTest("inner", type, TypeInformation.nullable(Complex.class));
        assertDescriptionEquals(innerField, "D891");
    }

    @Test
    public void testFooOutputType() {
        DiscoveredObjectType objectType = findAndTestObjectType(Foo.class, "FooOutput", 12);
        assertDescriptionEquals(objectType, "D456");

        MappedFieldMethod stringValueField = findFieldAndTest("stringValue", objectType, 0, TypeInformation.nullable(String.class));
        assertDescriptionIsNull(stringValueField);

        MappedFieldMethod longValueField = findFieldAndTest("longValue", objectType, 0, TypeInformation.nonNullable(long.class));
        assertDescriptionIsNull(longValueField);

        MappedFieldMethod intValueField = findFieldAndTest("intValue", objectType, 0, TypeInformation.nonNullable(int.class));
        assertDescriptionIsNull(intValueField);

        MappedFieldMethod intValue2Field = findFieldAndTest("intValue2", objectType, 0, TypeInformation.nonNullable(int.class));
        assertDescriptionIsNull(intValue2Field);

        MappedFieldMethod doubleValueField = findFieldAndTest("doubleValue", objectType, 0, TypeInformation.nonNullable(double.class));
        assertDescriptionIsNull(doubleValueField);

        MappedFieldMethod floatValueField = findFieldAndTest("floatValue", objectType, 0, TypeInformation.nonNullable(float.class));
        assertDescriptionIsNull(floatValueField);

        MappedFieldMethod byteValueField = findFieldAndTest("byteValue", objectType, 0, TypeInformation.nonNullable(byte.class));
        assertDescriptionIsNull(byteValueField);

        MappedFieldMethod shortValueField = findFieldAndTest("shortValue", objectType, 0, TypeInformation.nonNullable(short.class));
        assertDescriptionIsNull(shortValueField);

        MappedFieldMethod charValueField = findFieldAndTest("charValue", objectType, 0, TypeInformation.nonNullable(char.class));
        assertDescriptionIsNull(charValueField);

        MappedFieldMethod booleanValueField = findFieldAndTest("booleanValue", objectType, 0, TypeInformation.nonNullable(boolean.class));
        assertDescriptionIsNull(booleanValueField);

        MappedFieldMethod booleanValue2Field = findFieldAndTest("booleanValue2", objectType, 0, TypeInformation.nonNullable(boolean.class));
        assertDescriptionIsNull(booleanValue2Field);

        MappedFieldMethod intArrayField = findFieldAndTest("intArray", objectType, 0, TypeInformation.nullableArray(int.class));
        assertDescriptionEquals(intArrayField, "D989");
    }

    @Test
    public void testIntListType() {
        DiscoveredObjectType type = findAndTestObjectType(IntList.class, "IntList", 4);
        assertDescriptionIsNull(type);

        MappedFieldMethod dataField = findFieldAndTest("data", type, TypeInformation.nullableList(Integer.class));
        assertDescriptionIsNull(dataField);

        MappedFieldMethod isEmptyField = findFieldAndTest("isEmpty", type, TypeInformation.nonNullable(boolean.class));
        assertDescriptionIsNull(isEmptyField);

        MappedFieldMethod sizeField = findFieldAndTest("size", type, 1, TypeInformation.nonNullable(int.class));
        findParameterAndTest(null, sizeField, ParameterModel.DATA_FETCHING_ENVIRONMENT, 0, TypeInformation.nullable(DataFetchingEnvironment.class));
        assertDescriptionIsNull(sizeField);

        MappedFieldMethod getField = findFieldAndTest("get", type, 1, TypeInformation.nullable(Integer.class));
        findParameterAndTest("index", getField, ParameterModel.SCHEMA_ARGUMENT, 0, 0, TypeInformation.nonNullable(int.class), 0);
        assertDescriptionIsNull(getField);

        assertAndTestNumberOfImplementedInterfaces(0, type);
        assertAndTestNumberOfPossibleUnions(0, type);
    }

    @Test
    public void testMutation() {
        DiscoveredObjectType type = findAndTestObjectType(TestMutation.class, "TestMutation", 6, 0, MappedElementType.MUTATION);
        assertDescriptionIsNull(type);

        MappedFieldMethod encodeToBase64Field = findFieldAndTest("encodeToBase64", type, 1, TypeInformation.nullable(String.class));
        MappedParameter inputParameter = findParameterAndTest("input", encodeToBase64Field, ParameterModel.SCHEMA_ARGUMENT, 0, 0, TypeInformation.nullable(String.class));
        assertDescriptionIsNull(inputParameter);
        assertDescriptionIsNull(encodeToBase64Field);

        MappedFieldMethod splitField = findFieldAndTest("split", type, 2, 1, TypeInformation.nullableList(String.class));
        findAppliedAnnotationAndTest(Since.class, "Since", splitField, "version", "1.0.12");
        MappedParameter inputParameter2 = findParameterAndTest("input", splitField, ParameterModel.SCHEMA_ARGUMENT, 0, TypeInformation.nullable(String.class));
        assertDescriptionEquals(inputParameter2, "D2019");
        MappedParameter splitorParameter = findParameterAndTest("splitor", splitField, ParameterModel.SCHEMA_ARGUMENT, 1, 1, TypeInformation.nonNullable(Splitor.class));
        findAppliedAnnotationAndTest(Since.class, "Since", splitorParameter, "version", "1.0.13");
        assertDescriptionIsNull(splitorParameter);
        assertDescriptionEquals(splitField, "D145");

        MappedFieldMethod combineInto3DMatrixField = findFieldAndTest("combineInto3DMatrix", type, 2, 0, TypeInformation.nullableArray(int.class, 3));
        MappedParameter aParameter = findParameterAndTest("a", combineInto3DMatrixField, ParameterModel.SCHEMA_ARGUMENT, 0, TypeInformation.nullableArray(int.class, 2));
        assertDescriptionIsNull(aParameter);
        MappedParameter bParameter = findParameterAndTest(isParameterNamePresent() ? "b" : "arg1", combineInto3DMatrixField, ParameterModel.SCHEMA_ARGUMENT, 1, TypeInformation.nullableList(Integer.class, 2));
        assertDescriptionIsNull(bParameter);
        assertDescriptionIsNull(combineInto3DMatrixField);

        MappedFieldMethod listToArrayField = findFieldAndTest("listToArray", type, 1, 0, TypeInformation.nullableArray(int.class));
        MappedParameter listParameter = findParameterAndTest(isParameterNamePresent() ? "list" : "arg0", listToArrayField, ParameterModel.SCHEMA_ARGUMENT, 0, TypeInformation.nullableList(Integer.class));
        assertDescriptionIsNull(listParameter);
        assertDescriptionIsNull(listToArrayField);

        MappedFieldMethod inputToOutputField = findFieldAndTest("inputToOutput", type, 1, 1, TypeInformation.nullable(Foo.class));
        findAppliedAnnotationAndTest(FooProvider.class, "FooProvider", inputToOutputField, "value", new Foo().setIntValue(-100).setIntValue2(-200),
                "arrayValues", new Foo[]{new Foo().setIntValue(-25), new Foo().setIntValue(-26), new Foo().setIntValue(-27)},
                "listValues", Arrays.asList(new Foo().setIntValue(-1000), new Foo().setIntValue(-2000), new Foo().setIntValue(-3000)));
        MappedParameter inputParameter3 = findParameterAndTest("input", inputToOutputField, ParameterModel.SCHEMA_ARGUMENT, 0, 0, TypeInformation.nullable(Foo.class),
                new Foo().setIntValue(-1).setIntValue2(-2).setIntArray(new int[]{1, 2, 3, 4}));
        assertDescriptionIsNull(inputParameter3);
        assertDescriptionIsNull(inputToOutputField);

        MappedFieldMethod inputToOutputFromDirectiveField = findFieldAndTest("inputToOutputFromDirective", type, 2, 0, TypeInformation.nullable(Foo.class));
        MappedParameter indexParameter = findParameterAndTest("index", inputToOutputFromDirectiveField, ParameterModel.SCHEMA_ARGUMENT, 0, 0, TypeInformation.nonNullable(int.class), -1);
        assertDescriptionIsNull(indexParameter);
        assertDescriptionIsNull(inputToOutputFromDirectiveField);

        assertAndTestNumberOfImplementedInterfaces(1, type);
        assertAndTestNumberOfPossibleUnions(0, type);

        findInterfaceTypeAndTest(MutationInterface.class, "MutationInterface", type);
    }

    @Test
    public void testNormalUserType() {
        DiscoveredObjectType type = findAndTestObjectType(NormalUser.class, "User", 2);
        assertDescriptionIsNull(type);

        MappedFieldMethod nameField = findFieldAndTest("name", type, TypeInformation.nonNullable(String.class));
        assertDescriptionIsNull(nameField);

        MappedFieldMethod typeField = findFieldAndTest("type", type, TypeInformation.nonNullable(UserType.class));
        assertDescriptionEquals(typeField, "D8776");

        assertAndTestNumberOfImplementedInterfaces(1, type);
        assertAndTestNumberOfPossibleUnions(0, type);

        findInterfaceTypeAndTest(UserInterface.class, "UserInterface", type);
    }

    @Test
    public void testPageDetailsType() {
        DiscoveredObjectType type = findAndTestObjectType(PageDetails.class, "PageDetails", 2);
        assertDescriptionIsNull(type);

        MappedFieldMethod pageField = findFieldAndTest("page", type, TypeInformation.nonNullable(int.class));
        assertDescriptionIsNull(pageField);

        MappedFieldMethod sizeField = findFieldAndTest("size", type, TypeInformation.nonNullable(int.class));
        assertDescriptionEquals(sizeField, "D428");

        assertAndTestNumberOfImplementedInterfaces(0, type);
        assertAndTestNumberOfPossibleUnions(0, type);
    }

    @Test
    public void testQueryType() {
        DiscoveredObjectType type = findAndTestObjectType(Query.class, "TestQuery", 11, 1, MappedElementType.QUERY);
        findAppliedAnnotationAndTest(Since.class, "Since", type, "version", "1.0.6");
        assertDescriptionIsNull(type);

        MappedFieldMethod getListField = findFieldAndTest("getList", type, 1, TypeInformation.nullable(IntList.class));
        MappedParameter periodParameter = findParameterAndTest("period", getListField, ParameterModel.SCHEMA_ARGUMENT, 0, TypeInformation.nullable(IntPeriodScalar.class));
        assertDescriptionIsNull(getListField);
        assertDescriptionIsNull(periodParameter);

        MappedFieldMethod multiplyMatricesField = findFieldAndTest("multiplyMatrices", type, 2, TypeInformation.nullableList(Integer.class, 2));
        MappedParameter m1Parameter = findParameterAndTest("m1", multiplyMatricesField, ParameterModel.SCHEMA_ARGUMENT, 0, 0, TypeInformation.nonNullableList(Integer.class, 2),
                Arrays.asList(Arrays.asList(1, 2, 3), Arrays.asList(4, 5, 6), Arrays.asList(7, 8, 9)));
        assertDescriptionIsNull(m1Parameter);
        MappedParameter m2Parameter = findParameterAndTest("m2", multiplyMatricesField, ParameterModel.SCHEMA_ARGUMENT, 1, 0, TypeInformation.nonNullableArray(Integer.class, 2),
                new Integer[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}});
        assertDescriptionIsNull(m2Parameter);
        assertDescriptionIsNull(multiplyMatricesField);

        MappedFieldMethod getUserField = findFieldAndTest("getUser", type, 1, TypeInformation.nullable(UserInterface.class));
        MappedParameter userParameter = findParameterAndTest("user", getUserField, ParameterModel.SCHEMA_ARGUMENT, 0, TypeInformation.nonNullable(InputUser.class));
        assertDescriptionIsNull(getUserField);
        assertDescriptionIsNull(userParameter);

        MappedFieldMethod getVehicleField = findFieldAndTest("getVehicle", type, 2, TypeInformation.nullable(Vehicle.class));
        MappedParameter isCarParameter = findParameterAndTest("isCar", getVehicleField, ParameterModel.SCHEMA_ARGUMENT, 0, 0, TypeInformation.nullable(Boolean.class), true);
        findParameterAndTest(null, getVehicleField, ParameterModel.DATA_FETCHING_ENVIRONMENT, 1, TypeInformation.nullable(DataFetchingEnvironment.class));
        assertDescriptionIsNull(isCarParameter);
        assertDescriptionIsNull(getVehicleField);

        MappedFieldMethod getBankAccountField = findFieldAndTest("getBankAccount", type, 2, TypeInformation.nullable(BankAccount.class));
        MappedParameter usernameParameter = findParameterAndTest(isParameterNamePresent() ? "username" : "arg0", getBankAccountField, ParameterModel.SCHEMA_ARGUMENT, 0, TypeInformation.nullable(String.class));
        findParameterAndTest(null, getBankAccountField, ParameterModel.DATA_FETCHING_ENVIRONMENT, 1, TypeInformation.nullable(DataFetchingEnvironment.class));
        assertDescriptionEquals(usernameParameter, "username of bank account owner");
        assertDescriptionIsNull(getBankAccountField);

        MappedFieldMethod isSystemParamsHealthyField = findFieldAndTest("isSystemParamsHealthy", type, 4, TypeInformation.nonNullable(boolean.class));
        findParameterAndTest(null, isSystemParamsHealthyField, ParameterModel.ADAPTED_SCHEMA, 0, TypeInformation.nullable(AdaptedGraphQLSchema.class));
        findParameterAndTest(null, isSystemParamsHealthyField, ParameterModel.ADAPTED_SCHEMA, 1, TypeInformation.nullable(AdaptedGraphQLSchema.class));
        findParameterAndTest(null, isSystemParamsHealthyField, ParameterModel.DATA_FETCHING_ENVIRONMENT, 2, TypeInformation.nullable(DataFetchingEnvironment.class));
        findParameterAndTest(null, isSystemParamsHealthyField, ParameterModel.DIRECTIVES, 3, TypeInformation.nullable(GraphqlDirectivesHolder.class));
        assertDescriptionIsNull(isSystemParamsHealthyField);

        MappedFieldMethod isDirectivesHealthyField = findFieldAndTest("isDirectivesHealthy", type, 1, TypeInformation.nonNullable(boolean.class));
        findParameterAndTest(null, isDirectivesHealthyField, ParameterModel.DIRECTIVES, 0, TypeInformation.nullable(GraphqlDirectivesHolder.class));
        assertDescriptionIsNull(isDirectivesHealthyField);

        MappedFieldMethod getPageDetailsField = findFieldAndTest("getPageDetails", type, 2, 1, TypeInformation.nullable(PageDetails.class));
        findAppliedAnnotationAndTest(AddPageParameters.class, "AddPageParameters", getPageDetailsField);
        findParameterAndTest(null, getPageDetailsField, ParameterModel.ADAPTED_SCHEMA, 0, TypeInformation.nullable(AdaptedGraphQLSchema.class));
        findParameterAndTest(null, getPageDetailsField, ParameterModel.DATA_FETCHING_ENVIRONMENT, 1, TypeInformation.nullable(DataFetchingEnvironment.class));
        assertDescriptionIsNull(getPageDetailsField);

        MappedFieldMethod getDeveloperNameField = findFieldAndTest("getDeveloperName", type, TypeInformation.nullable(String.class));
        assertDescriptionIsNull(getDeveloperNameField);

        MappedFieldMethod serializeToStringField = findFieldAndTest("serializeToString", type, 2, 0, TypeInformation.nullable(String.class));
        MappedParameter inputParameter = findParameterAndTest("input", serializeToStringField, ParameterModel.SCHEMA_ARGUMENT, 0, 0, TypeInformation.nullable(Complex.class),
                new Complex("k1", "v1", 1).setInner(new Complex("k2", "v2", 2).setInner(new Complex("k3", "v3", 3))));
        assertDescriptionIsNull(inputParameter);
        MappedParameter separatorParameter = findParameterAndTest("separator", serializeToStringField, ParameterModel.SCHEMA_ARGUMENT, 1, 0, TypeInformation.nonNullable(char.class), ',');
        assertDescriptionIsNull(separatorParameter);
        assertDescriptionIsNull(serializeToStringField);

        MappedFieldMethod serializeToStringFromDirective = findFieldAndTest("serializeToStringFromDirective", type, 2, 0, TypeInformation.nullable(String.class));
        MappedParameter separatorParameter2 = findParameterAndTest("separator", serializeToStringFromDirective, ParameterModel.SCHEMA_ARGUMENT, 0, 0, TypeInformation.nonNullable(char.class), ',');
        assertDescriptionIsNull(separatorParameter2);
        findParameterAndTest(null, serializeToStringFromDirective, ParameterModel.DIRECTIVES, 1, TypeInformation.nullable(GraphqlDirectivesHolder.class));
        assertDescriptionIsNull(serializeToStringFromDirective);

        assertAndTestNumberOfImplementedInterfaces(0, type);
        assertAndTestNumberOfPossibleUnions(0, type);
    }
}
