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

import graphql_adapter.annotations.*;
import test_graphql_adapter.schema.directives.Since;
import test_graphql_adapter.schema.validators.Match;

import java.util.List;

@GraphqlDescription("Mutation Abstract")
@GraphqlInterface
public interface MutationInterface {

    @GraphqlDescription("D2299")
    @GraphqlField
    String encodeToBase64(@GraphqlDescription("D29876") @Match("\\w{5,}") @Since("1.0.14") @GraphqlArgument(name = "input") String input);

    @GraphqlDescription("D145")
    @Since("1.0.12")
    @GraphqlField
    default List<String> split(@GraphqlDescription("D2019") @GraphqlArgument(name = "input") String input, @Since("1.0.13") @GraphqlArgument(name = "splitor") @GraphqlNonNull Splitor splitor) {
        return splitor.split(input);
    }
}
