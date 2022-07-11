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

package test_graphql_adapter.schema.directives;

import graphql.introspection.Introspection;
import graphql_adapter.annotations.GraphqlDescription;
import graphql_adapter.annotations.GraphqlDirective;
import graphql_adapter.annotations.GraphqlDirectiveArgument;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@GraphqlDescription("make delay to fetch data as seconds")
@GraphqlDirective(locations = {Introspection.DirectiveLocation.FIELD_DEFINITION, Introspection.DirectiveLocation.FIELD, Introspection.DirectiveLocation.QUERY}, functionality = DelayDirectiveFunction.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Delay {

    @GraphqlDirectiveArgument(nullable = false, name = "seconds")
    int value();
}
