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

package tests.T1.schema.directives;

import graphql.introspection.Introspection;
import grphaqladapter.annotations.DefaultValue;
import grphaqladapter.annotations.GraphqlDirective;
import grphaqladapter.annotations.GraphqlDirectiveArgument;
import tests.T1.schema.Complex;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@GraphqlDirective(name = "InputProvider", locations = {Introspection.DirectiveLocation.FIELD, Introspection.DirectiveLocation.QUERY})
@Retention(RetentionPolicy.RUNTIME)
public @interface ComplexInputProvider {

    @Since("1.0.20")
    @DefaultValue("{name:'k1', value:'v1', priority: 1, inner: {name:'k2', value:'v2', priority: '2', inner: '{name:\\'k3\\', value:\\'v3\\', priority: 3}'}}")
    @GraphqlDirectiveArgument(type = Complex.class)
    String input();
}
