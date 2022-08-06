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

import graphql_adapter.annotations.DefaultValue;
import graphql_adapter.annotations.GraphqlInputField;
import graphql_adapter.annotations.GraphqlInputType;
import graphql_adapter.annotations.GraphqlNonNull;
import test_graphql_adapter.schema.directives.Since;
import test_graphql_adapter.schema.validators.Match;

import java.util.Objects;

@GraphqlInputType
@Since("1.0.7")
public class InputUser extends InputUserAbstract {

    private String name;
    private UserType type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InputUser user = (InputUser) o;
        return Objects.equals(name, user.name) && type == user.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public String toString() {
        return "InputUser{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }

    @Match("[A-Za-z0-9]+")
    @DefaultValue("Anonymous")
    @GraphqlInputField(name = "name", setter = "setName")
    @GraphqlNonNull
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    @DefaultValue("NORMAL")
    @Since("1.0.8")
    @GraphqlInputField(setter = "setType")
    public UserType type() {
        return type;
    }
}
