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
import grphaqladapter.adaptedschema.mapping.mapped_elements.DimensionModel;
import grphaqladapter.annotations.DefaultValue;
import grphaqladapter.annotations.GraphqlDescription;
import grphaqladapter.annotations.GraphqlDirective;
import grphaqladapter.annotations.GraphqlDirectiveArgument;
import tests.T1.schema.Foo;
import tests.T1.schema.parsers.CustomDirectiveArgumentFooArrayValueParser;
import tests.T1.schema.parsers.CustomDirectiveArgumentFooListValueParser;
import tests.T1.schema.parsers.CustomDirectiveArgumentFooValueParser;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@GraphqlDescription("provide FooInput")
@GraphqlDirective(locations = {Introspection.DirectiveLocation.FIELD, Introspection.DirectiveLocation.FIELD_DEFINITION, Introspection.DirectiveLocation.QUERY})
@Retention(RetentionPolicy.RUNTIME)
public @interface FooProvider {

    @DefaultValue("{intValue:-101, intValue2:-102, intArray:[1,2,3,4,5,6]}")
    @GraphqlDirectiveArgument(valueParser = CustomDirectiveArgumentFooValueParser.class, type = Foo.class, nullable = false)
    int[] value();

    @GraphqlDirectiveArgument(valueParser = CustomDirectiveArgumentFooArrayValueParser.class, type = Foo.class, dimensions = 1, dimensionModel = DimensionModel.ARRAY)
    int[] arrayValues() default {-25, -26, -27};

    @GraphqlDirectiveArgument(valueParser = CustomDirectiveArgumentFooListValueParser.class, type = Foo.class, dimensions = 1, dimensionModel = DimensionModel.LIST, nullable = false)
    int[] listValues() default {-28, -29, -30};
}
