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
import graphql_adapter.adaptedschema.assertion.Assert;
import graphql_adapter.adaptedschema.mapping.mapper.utils.DimensionsNullabilityUtils;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.argument.GraphqlArgumentDescription;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.utils.DescriptorUtils;
import graphql_adapter.adaptedschema.system_objects.directive.GraphqlDirectivesHolder;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class AutomaticParameterDescriptor implements ParameterDescriptor {

    public static Builder newBuilder() {
        return new Builder();
    }

    private final String argNameIfNotPresent;

    private AutomaticParameterDescriptor(String argNameIfNotPresent) {
        this.argNameIfNotPresent = argNameIfNotPresent;
    }

    @Override
    public GraphqlArgumentDescription describeFieldArgument(Method method, Parameter parameter, int parameterIndex) {

        if (isSystemParameter(parameter)) {
            return GraphqlArgumentDescription.newArgumentDescription()
                    .systemParameter(true)
                    .build();
        }

        return GraphqlArgumentDescription.newArgumentDescription()
                .name(getName(parameter, parameterIndex))
                .nullability(DimensionsNullabilityUtils.getNullabilityOfDimensions(parameter))
                .description(DescriptorUtils.getDescription(parameter))
                .defaultValue(DescriptorUtils.getDefaultValue(parameter))
                .build();
    }

    private String getName(Parameter parameter, int index) {
        Assert.isOneOrMoreTrue(new IllegalStateException("can not find the type-name of parameter because parameter typeName not present"),
                !parameter.isNamePresent(), argNameIfNotPresent == null);
        if (parameter.isNamePresent()) {
            return parameter.getName();
        }
        return argNameIfNotPresent + index;
    }

    private boolean isSystemParameter(Parameter parameter) {
        return parameter.getType() == DataFetchingEnvironment.class ||
                parameter.getType() == GraphqlDirectivesHolder.class ||
                parameter.getType() == AdaptedGraphQLSchema.class;
    }

    public static class Builder {
        private String argNameIfNotPresent;

        public Builder argNameIfNotPresent(String name) {
            this.argNameIfNotPresent = name;
            return this;
        }

        public ParameterDescriptor build() {
            return new AutomaticParameterDescriptor(argNameIfNotPresent);
        }
    }
}
