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
import graphql_adapter.annotations.GraphqlDescription;
import graphql_adapter.annotations.GraphqlInputType;
import graphql_adapter.annotations.GraphqlObjectType;
import test_graphql_adapter.schema.directives.Since;
import test_graphql_adapter.schema.validators.Match;
import test_graphql_adapter.schema.validators.NotOneOf;

import java.util.Objects;

@GraphqlDescription("D980")
@GraphqlInputType(name = "ComplexInput")
@GraphqlObjectType(name = "ComplexOutput")
@Since("1.0.25")
public class Complex {

    private String name;
    private String value;
    private int priority;
    private Complex inner;

    public Complex() {

    }

    public Complex(String name, String value, int priority) {
        this.name = name;
        this.value = value;
        this.priority = priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Complex that = (Complex) o;
        return priority == that.priority && Objects.equals(name, that.name) && Objects.equals(value, that.value) && Objects.equals(inner, that.inner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value, priority, inner);
    }

    @Override
    public String toString() {
        return "Complex {" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", priority=" + priority +
                ", inner=" + inner +
                '}';
    }

    @GraphqlDescription("D891")
    @DefaultValue("{name:'dn', value:'dv', priority:100, inner:{name:'idn', value:'idv', priority:101}}")
    public Complex getInner() {
        return inner;
    }

    public Complex setInner(Complex inner) {
        this.inner = inner;
        return this;
    }

    @Match("[A-Za-z0-9]+")
    public String getName() {
        return name;
    }

    public Complex setName(String name) {
        this.name = name;
        return this;
    }

    @NotOneOf({"-12345", "-123456", "-1234567"})
    @Since("1.0.21")
    public int getPriority() {
        return priority;
    }

    public Complex setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    @GraphqlDescription("D2900")
    public String getValue() {
        return value;
    }

    public Complex setValue(String value) {
        this.value = value;
        return this;
    }
}
