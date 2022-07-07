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

import grphaqladapter.annotations.GraphqlArgument;
import grphaqladapter.annotations.GraphqlField;
import grphaqladapter.annotations.GraphqlInterface;
import tests.T1.schema.directives.Since;

import java.util.List;

@GraphqlInterface
public interface MutationInterface {

    @GraphqlField
    String encodeToBase64(@Since("1.0.14") @GraphqlArgument(name = "input") String input);

    @Since("1.0.12")
    @GraphqlField
    default List<String> split(@GraphqlArgument(name = "input") String input, @Since("1.0.13") @GraphqlArgument(name = "splitor", nullable = false) Splitor splitor) {
        return splitor.split(input);
    }

}
