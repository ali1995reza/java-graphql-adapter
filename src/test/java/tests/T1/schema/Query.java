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

package tests.T1.schema;

import graphql.schema.DataFetchingEnvironment;
import grphaqladapter.adaptedschema.AdaptedGraphQLSchema;
import grphaqladapter.adaptedschema.system_objects.directive.GraphqlDirectiveDetails;
import grphaqladapter.adaptedschema.system_objects.directive.GraphqlDirectivesHolder;
import grphaqladapter.adaptedschema.tools.object_builder.BuildingObjectConfig;
import grphaqladapter.annotations.*;
import tests.Randomer;
import tests.T1.schema.directives.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@GraphqlQuery(name = "TestQuery")
@Since("1.0.6")
public class Query {

    @GraphqlField
    public BankAccount getBankAccount(@GraphqlDescription("username of bank account owner") String username, DataFetchingEnvironment environment) {
        String token = environment.getGraphQlContext().get("auth");
        if (!Objects.equals(username, token)) {
            throw new IllegalStateException("authentication failed");
        }
        return new BankAccount(
                Base64.getEncoder().encodeToString(username.getBytes(StandardCharsets.UTF_8)),
                username,
                Randomer.random(10.567, 200000.23, 0.0)
        );
    }

    @GraphqlField
    public CompletableFuture<String> getDeveloperName() {
        return CompletableFuture.completedFuture("Alireza Akhoundi");
    }

    @GraphqlField
    public IntList getList(@GraphqlArgument(name = "period") IntPeriodScalar periodScalar) {

        List<Integer> list = new ArrayList<>();
        periodScalar.forEach(list::add);

        return IntList.of(list);

    }

    @AddPageParameters
    @GraphqlField
    public PageDetails getPageDetails(AdaptedGraphQLSchema schema, DataFetchingEnvironment environment) {
        PageParameters parameters = schema.objectBuilder()
                .buildFromObject(environment.getArgument("pageParameters"), PageParameters.class, BuildingObjectConfig.ONLY_USE_EXACT_LIST);
        return new PageDetails(parameters.getPage(), parameters.getSize());
    }

    @GraphqlField
    public UserInterface getUser(@GraphqlArgument(name = "user", nullable = false) InputUser user) {
        if (user.type().isNormal()) {
            return NormalUser.create(user.getName());
        }
        return AdminUser.create(user.getName());
    }

    @GraphqlField
    public Vehicle getVehicle(@DefaultValue("true") @GraphqlArgument(name = "isCar") Boolean isCar, DataFetchingEnvironment environment) {
        if (isCar) {
            Car car = new Car();
            car.setModel(Randomer.random("Ferrari", "Lamborghini", "Bugatti"));
            car.setProduceYear(Randomer.random(1998, 2001, 2012, 2021));
            return car;
        } else {
            Bus bus = new Bus();
            bus.setModel(Randomer.random("Benz", "Volvo", "Nissan"));
            bus.setSize(Randomer.random(10, 20, 30, 40));
            bus.setProduceYear(Randomer.random(1998, 2001, 2012, 2021));
            return bus;
        }
    }

    @GraphqlField(nullable = false)
    public boolean isDirectivesHealthy(GraphqlDirectivesHolder directives) {
        GraphqlDirectiveDetails upperCase = directives.fieldDirectives().directivesByClass()
                .get(UpperCase.class);
        GraphqlDirectiveDetails auth = directives.operationDirectives().directivesByClass()
                .get(Authentication.class);
        GraphqlDirectiveDetails delay = directives.operationDirectives().directivesByClass()
                .get(Delay.class);
        if (upperCase == null || auth == null || delay == null) {
            return false;
        }
        return auth.getArgument("token").equals("Peter") &&
                delay.getArgument("seconds").equals(0);
    }

    @GraphqlField(nullable = false)
    public boolean isSystemParamsHealthy(AdaptedGraphQLSchema schema, AdaptedGraphQLSchema duplicateSchema, DataFetchingEnvironment environment, GraphqlDirectivesHolder directives) {
        return schema != null && environment != null && directives != null && schema == duplicateSchema;
    }

    @GraphqlField
    public List<List<Integer>> multiplyMatrices(@DefaultValue("[[1, 2, 3], [4, 5, 6], [7, 8, 9]]") @GraphqlArgument(name = "m1", nullable = false) List<List<Integer>> first,
                                                @DefaultValue("[[1, 2, 3], [4, 5, 6], [7, 8, 9]]") @GraphqlArgument(name = "m2", nullable = false) Integer[][] second) {
        int i, j, k;

        int row1 = first.size();
        int col1 = first.get(0).size();

        int row2 = second.length;
        int col2 = second[0].length;

        if (row2 != col1) {
            throw new IllegalStateException("Multiplication Not Possible");
        }

        List<List<Integer>> result = new ArrayList<>();

        for (i = 0; i < row1; i++) {
            result.add(new ArrayList<>());
            for (j = 0; j < col2; j++) {
                int sum = 0;
                for (k = 0; k < row2; k++)
                    sum += first.get(i).get(k) * second[k][j];
                result.get(i).add(sum);
            }
        }
        return result;
    }

    @GraphqlField
    public String serializeToString(
            @DefaultValue("{name:'k1', value:'v1', priority: 1, inner: {name:'k2', value:'v2', priority: '2', inner: '{name:\\'k3\\', value:\\'v3\\', priority: 3}'}}")
            @GraphqlArgument(name = "input") Complex input,
            @DefaultValue(",")
            @GraphqlArgument(name = "separator", nullable = false) char separator) {
        return serialize(input, separator);
    }

    @GraphqlField
    public String serializeToStringFromDirective(
            @DefaultValue(",")
            @GraphqlArgument(name = "separator", nullable = false) char separator, GraphqlDirectivesHolder directives) {
        if (directives.isOperationDirectivesPresent()) {
            GraphqlDirectiveDetails directive = directives.operationDirectives().directivesByClass().get(ComplexInputProvider.class);
            if (directive != null) {
                return serialize(directive.getArgument("input"), separator);
            }
        }

        if (directives.isFieldDirectivesPresent()) {
            GraphqlDirectiveDetails directive = directives.fieldDirectives().directivesByClass().get(ComplexInputProvider.class);
            if (directive != null) {
                return serialize(directive.getArgument("input"), separator);
            }
        }

        return serialize(null, separator);
    }


    private String serialize(Complex input, char separator) {
        StringBuffer buffer = new StringBuffer();
        Complex current = input;
        while (current != null) {
            if (current.getName() != null) {
                buffer.append(current.getName()).append(separator);
            }
            if (current.getValue() != null) {
                buffer.append(current.getValue()).append(separator);
            }
            buffer.append(current.getPriority());
            current = current.getInner();
            if (current != null) {
                buffer.append(separator);
            }
        }
        return buffer.toString();
    }
}
