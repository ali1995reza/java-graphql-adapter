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

package graphql_adapter.adaptedschema.mapping.strategy.descriptors.parameter;

import graphql.schema.DataFetchingEnvironment;
import graphql_adapter.adaptedschema.AdaptedGraphQLSchema;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.argument.GraphqlArgumentDescription;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.utils.DescriptorUtils;
import graphql_adapter.adaptedschema.system_objects.directive.GraphqlDirectivesHolder;
import graphql_adapter.adaptedschema.utils.StringUtils;
import graphql_adapter.annotations.GraphqlArgument;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class AnnotationBaseParameterDescriptor implements ParameterDescriptor {

    @Override
    public GraphqlArgumentDescription describeFieldArgument(Method method, Parameter parameter, int parameterIndex) {

        GraphqlArgument argument = ParameterAnnotationLookup.findFirstAppears(method, parameterIndex, GraphqlArgument.class);

        if (argument == null) {
            if (isSystemParameter(parameter)) {
                return GraphqlArgumentDescription.newArgumentDescription()
                        .systemParameter(true)
                        .build();
            }
            return null;
        }

        String name = StringUtils.isNullString(argument.name()) ? parameter.getName() : argument.name();

        return GraphqlArgumentDescription.newArgumentDescription()
                .name(name)
                .nullable(argument.nullable())
                .defaultValue(DescriptorUtils.getDefaultValue(parameter))
                .description(DescriptorUtils.getDescription(parameter))
                .build();
    }

    @Override
    public boolean skipFieldArgument(Method method, Parameter parameter, int parameterIndex) {
        return DescriptorUtils.isSkipElementAnnotationPresent(parameter);
    }

    private boolean isSystemParameter(Parameter parameter) {
        return parameter.getType() == DataFetchingEnvironment.class ||
                parameter.getType() == GraphqlDirectivesHolder.class ||
                parameter.getType() == AdaptedGraphQLSchema.class;
    }
}
