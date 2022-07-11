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

package test_graphql_adapter.schema.types;


import graphql_adapter.annotations.GraphqlDescription;
import graphql_adapter.annotations.GraphqlObjectType;

@GraphqlObjectType
public class BankAccount {

    private final String id;
    private final String username;
    private final Double balance;

    public BankAccount(String id, String username, Double balance) {
        this.id = id;
        this.username = username;
        this.balance = balance;
    }

    @GraphqlDescription("Balance as dollar")
    public Double getBalance() {
        return balance;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
