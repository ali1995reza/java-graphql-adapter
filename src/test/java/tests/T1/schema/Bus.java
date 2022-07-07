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

import grphaqladapter.annotations.GraphqlField;
import grphaqladapter.annotations.GraphqlObjectType;
import tests.T1.schema.directives.*;

@GraphqlObjectType
@Since("1.0.1")
public class Bus implements Vehicle {

    private String model;
    private Integer produceYear;
    private Integer size;

    @Delay(0)
    @UpperCase
    @Hash(salt = "some_salt")
    @GraphqlField(name = "model")
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @ToStringDirective
    @GraphqlField(name = "produceYear")
    public Integer getProduceYear() {
        return produceYear;
    }

    public void setProduceYear(Integer produceYear) {
        this.produceYear = produceYear;
    }

    @GraphqlField(name = "size")
    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
