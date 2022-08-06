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
import graphql_adapter.adaptedschema.mapping.mapped_elements.DimensionModel;
import graphql_adapter.annotations.DefaultValue;
import graphql_adapter.annotations.GraphqlDescription;
import graphql_adapter.annotations.GraphqlDirective;
import graphql_adapter.annotations.GraphqlDirectiveArgument;
import test_graphql_adapter.schema.parsers.CustomDirectiveArgumentFooArrayValueParser;
import test_graphql_adapter.schema.parsers.CustomDirectiveArgumentFooListValueParser;
import test_graphql_adapter.schema.parsers.CustomDirectiveArgumentFooValueParser;
import test_graphql_adapter.schema.types.Foo;
import test_graphql_adapter.schema.validators.FooValidation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@GraphqlDescription("provide FooInput")
@GraphqlDirective(locations = {Introspection.DirectiveLocation.FIELD, Introspection.DirectiveLocation.FIELD_DEFINITION, Introspection.DirectiveLocation.QUERY})
@Retention(RetentionPolicy.RUNTIME)
public @interface FooProvider {

    @FooValidation
    @GraphqlDescription("D4")
    @GraphqlDirectiveArgument(valueParser = CustomDirectiveArgumentFooArrayValueParser.class, type = Foo.class, dimensions = 1, dimensionModel = DimensionModel.ARRAY, nullability = {false, false})
    int[] arrayValues() default {-25, -26, -27};

    @FooValidation
    @GraphqlDirectiveArgument(valueParser = CustomDirectiveArgumentFooListValueParser.class, type = Foo.class, dimensions = 1, dimensionModel = DimensionModel.LIST)
    int[] listValues() default {-28, -29, -30};

    @GraphqlDescription("D5")
    @DefaultValue("{intValue:-101, intValue2:-102, intArray:[1,2,3,4,5,6]}")
    @GraphqlDirectiveArgument(valueParser = CustomDirectiveArgumentFooValueParser.class, type = Foo.class, nullability = false)
    int[] value();
}
