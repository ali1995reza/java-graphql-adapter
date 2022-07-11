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
package graphql_adapter.adaptedschema.mapping.mapper.parameter;

import graphql.schema.DataFetchingEnvironment;
import graphql_adapter.adaptedschema.AdaptedGraphQLSchema;
import graphql_adapter.adaptedschema.mapping.mapped_elements.TypeInformation;
import graphql_adapter.adaptedschema.mapping.mapped_elements.annotation.MappedAnnotation;
import graphql_adapter.adaptedschema.mapping.mapped_elements.parameter.MappedParameter;
import graphql_adapter.adaptedschema.mapping.mapped_elements.parameter.MappedParameterBuilder;
import graphql_adapter.adaptedschema.mapping.mapper.AbstractElementMapper;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.argument.GraphqlArgumentDescription;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.annotations.AppliedDirectiveDescriptor;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.parameter.ParameterDescriptor;
import graphql_adapter.adaptedschema.mapping.validator.ParameterValidator;
import graphql_adapter.adaptedschema.tools.object_builder.ObjectBuilder;
import graphql_adapter.adaptedschema.utils.chain.Chain;
import graphql_adapter.codegenerator.ObjectConstructor;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

public class ParameterMapper extends AbstractElementMapper {

    public ParameterMapper(Chain<ParameterDescriptor> parameterDescriptorChain, Chain<AppliedDirectiveDescriptor> appliedDirectiveDescriptorChain) {
        super(null, null, parameterDescriptorChain, null, appliedDirectiveDescriptorChain);
    }

    public MappedParameter mapParameter(Class<?> clazz, Method method, Parameter parameter, int index, Map<Class<?>, MappedAnnotation> annotations, ObjectConstructor constructor, ObjectBuilder builder) {
        GraphqlArgumentDescription argumentDescription = describeFieldArgument(method, parameter, index);

        if (argumentDescription == null) {
            return MappedParameterBuilder.newSkippedParameter(parameter, index);
        }

        if (argumentDescription.isSystemParameter()) {
            return getSystemParameter(parameter, index);
        }

        TypeInformation<?> type = TypeInformation.of(parameter, argumentDescription.nullable());

        MappedParameter mappedParameter = MappedParameter.newParameter()
                .name(argumentDescription.name())
                .parameter(parameter)
                .type(type)
                .index(index)
                .description(argumentDescription.description())
                .defaultValue(parseAndGetDefaultValue(argumentDescription.defaultValue(), type, constructor, builder))
                .build();

        mappedParameter = addAppliedAnnotations(MappedParameter::newParameter, mappedParameter, annotations, constructor, builder);

        ParameterValidator.validateParameter(mappedParameter, clazz, method, parameter);

        return mappedParameter;
    }

    private MappedParameter getSystemParameter(Parameter parameter, int index) {
        if (parameter.getType() == DataFetchingEnvironment.class) {
            return MappedParameterBuilder.newEnvironmentParameter(parameter, index);
        }
        if (parameter.getType() == AdaptedGraphQLSchema.class) {
            return MappedParameterBuilder.newAdaptedSchemaParameter(parameter, index);
        }
        return MappedParameterBuilder.newDirectiveParameter(parameter, index);
    }
}
