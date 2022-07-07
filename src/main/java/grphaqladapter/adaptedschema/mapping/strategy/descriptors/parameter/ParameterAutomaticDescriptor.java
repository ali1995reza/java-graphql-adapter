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

package grphaqladapter.adaptedschema.mapping.strategy.descriptors.parameter;

import graphql.schema.DataFetchingEnvironment;
import grphaqladapter.adaptedschema.AdaptedGraphQLSchema;
import grphaqladapter.adaptedschema.assertutil.Assert;
import grphaqladapter.adaptedschema.mapping.strategy.descriptions.argument.GraphqlArgumentDescription;
import grphaqladapter.adaptedschema.mapping.strategy.descriptions.argument.GraphqlArgumentDescriptionBuilder;
import grphaqladapter.adaptedschema.mapping.strategy.descriptors.DescriptorUtils;
import grphaqladapter.adaptedschema.system_objects.directive.GraphqlDirectivesHolder;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class ParameterAutomaticDescriptor implements ParameterDescriptor {

    public static Builder newBuilder() {
        return new Builder();
    }

    private final String argNameIfNotPresent;

    private ParameterAutomaticDescriptor(String argNameIfNotPresent) {
        this.argNameIfNotPresent = argNameIfNotPresent;
    }

    @Override
    public GraphqlArgumentDescription describeFieldArgument(Method method, Parameter parameter, int parameterIndex) {

        if (isSystemParameter(parameter)) {
            return GraphqlArgumentDescriptionBuilder.newBuilder()
                    .systemParameter(true)
                    .build();
        }

        return GraphqlArgumentDescriptionBuilder.newBuilder()
                .name(getName(parameter, parameterIndex))
                .nullable(isNullable(parameter))
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

    private boolean isNullable(Parameter parameter) {
        return !parameter.getType().isPrimitive();
    }

    public static class Builder {
        private String argNameIfNotPresent;

        public Builder argNameIfNotPresent(String name) {
            this.argNameIfNotPresent = name;
            return this;
        }

        public ParameterDescriptor build() {
            return new ParameterAutomaticDescriptor(argNameIfNotPresent);
        }
    }

    private boolean isSystemParameter(Parameter parameter) {
        return parameter.getType() == DataFetchingEnvironment.class ||
                parameter.getType() == GraphqlDirectivesHolder.class ||
                parameter.getType() == AdaptedGraphQLSchema.class;
    }
}
