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

import graphql.schema.DataFetchingEnvironment;
import graphql_adapter.annotations.*;
import test_graphql_adapter.schema.validators.NonNegative;

import java.util.Collections;
import java.util.List;

@GraphqlObjectType
public class IntList {

    public static IntList of(List<Integer> integers) {
        IntList list = new IntList();
        list.setData(integers);
        return list;
    }

    private List<Integer> data = Collections.emptyList();

    @GraphqlField
    public List<Integer> data() {
        return data;
    }

    @GraphqlField
    public Integer get(@NonNegative @DefaultValue("0") @GraphqlArgument(name = "index") @GraphqlNonNull int index) {
        return data.get(index);
    }

    @GraphqlField
    public @GraphqlNonNull boolean isEmpty() {
        return data.isEmpty();
    }

    public void setData(List<Integer> data) {
        if (data == null) {
            data = Collections.emptyList();
        }
        this.data = data;
    }

    @GraphqlField
    public @GraphqlNonNull int size(DataFetchingEnvironment environment) {
        return data.size();
    }
}
