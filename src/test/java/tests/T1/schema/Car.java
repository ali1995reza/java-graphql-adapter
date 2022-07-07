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
import tests.T1.schema.directives.ToStringDirective;

@GraphqlObjectType
public class Car implements Vehicle {

    private String model;
    private Integer produceYear;

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
}
