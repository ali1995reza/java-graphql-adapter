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
import grphaqladapter.annotations.GraphqlInputType;
import grphaqladapter.annotations.GraphqlObjectType;
import tests.T1.schema.directives.Since;

import java.util.Objects;

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

    @DefaultValue("{name:'dn', value:'dv', priority:100, inner:{name:'idn', value:'idv', priority:101}}")
    public Complex getInner() {
        return inner;
    }

    public Complex setInner(Complex inner) {
        this.inner = inner;
        return this;
    }

    public String getName() {
        return name;
    }

    public Complex setName(String name) {
        this.name = name;
        return this;
    }

    @Since("1.0.21")
    public int getPriority() {
        return priority;
    }

    public Complex setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Complex setValue(String value) {
        this.value = value;
        return this;
    }
}
