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

import grphaqladapter.annotations.DefaultValue;
import grphaqladapter.annotations.GraphqlInputField;
import grphaqladapter.annotations.GraphqlInputType;
import tests.T1.schema.directives.Since;

@GraphqlInputType
@Since("1.0.7")
public class InputUser {

    private String name;
    private UserType type;


    @DefaultValue("Anonymous")
    @GraphqlInputField(name = "name", setter = "setName", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @DefaultValue("NORMAL")
    @Since("1.0.8")
    @GraphqlInputField(setter = "setType")
    public UserType type() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "InputUser{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
