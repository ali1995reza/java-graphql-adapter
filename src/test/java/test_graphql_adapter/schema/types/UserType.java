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

import graphql_adapter.annotations.GraphqlEnum;
import graphql_adapter.annotations.GraphqlEnumValue;
import graphql_adapter.annotations.SkipElement;
import test_graphql_adapter.schema.directives.Since;

@GraphqlEnum
@Since("1.0.9")
public enum UserType {

    @GraphqlEnumValue(name = "NORMAL")
    Normal,
    @Since("1.0.10")
    @GraphqlEnumValue
    ADMIN,
    @SkipElement
    Unknown;

    public boolean isAdmin() {
        return this == ADMIN;
    }

    public boolean isNormal() {
        return this == Normal;
    }
}
