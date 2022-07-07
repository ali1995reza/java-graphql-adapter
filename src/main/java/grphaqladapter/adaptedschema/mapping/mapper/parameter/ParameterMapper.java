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

package grphaqladapter.adaptedschema.mapping.mapper.parameter;

import graphql.schema.DataFetchingEnvironment;
import grphaqladapter.adaptedschema.AdaptedGraphQLSchema;
import grphaqladapter.adaptedschema.ObjectBuilder;
import grphaqladapter.adaptedschema.mapping.mapped_elements.TypeInformation;
import grphaqladapter.adaptedschema.mapping.mapped_elements.annotation.MappedAnnotation;
import grphaqladapter.adaptedschema.mapping.mapped_elements.parameter.MappedParameter;
import grphaqladapter.adaptedschema.mapping.mapped_elements.parameter.MappedParameterBuilder;
import grphaqladapter.adaptedschema.mapping.mapper.AbstractElementMapper;
import grphaqladapter.adaptedschema.mapping.strategy.descriptions.argument.GraphqlArgumentDescription;
import grphaqladapter.adaptedschema.mapping.strategy.descriptors.annotations.AppliedDirectiveDescriptor;
import grphaqladapter.adaptedschema.mapping.strategy.descriptors.parameter.ParameterDescriptor;
import grphaqladapter.adaptedschema.utils.chain.Chain;
import grphaqladapter.codegenerator.ObjectConstructor;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

public class ParameterMapper extends AbstractElementMapper {

    public ParameterMapper(Chain<ParameterDescriptor> parameterDescriptorChain, Chain<AppliedDirectiveDescriptor> appliedDirectiveDescriptorChain) {
        super(null, null, parameterDescriptorChain, null, appliedDirectiveDescriptorChain);
    }

    public MappedParameter mapParameter(Class clazz, Method method, Parameter parameter, int index, Map<Class, MappedAnnotation> annotations, ObjectConstructor constructor, ObjectBuilder builder) {
        GraphqlArgumentDescription argumentDescription = describeFieldArgument(method, parameter, index);

        if (argumentDescription == null) {
            return MappedParameterBuilder.newSkippedParameter(parameter, index);
        }

        if (argumentDescription.isSystemParameter()) {
            return getSystemParameter(parameter, index);
        }

        TypeInformation type = TypeInformation.of(parameter, argumentDescription.nullable());

        MappedParameter mappedParameter = MappedParameterBuilder
                .newBuilder()
                .name(argumentDescription.name())
                .parameter(parameter)
                .type(type)
                .index(index)
                .description(argumentDescription.description())
                .defaultValue(parseAndGetDefaultValue(argumentDescription.defaultValue(), type, constructor, builder))
                .build();

        mappedParameter = addAppliedAnnotations(MappedParameterBuilder::newBuilder, mappedParameter, annotations, constructor, builder);

        //ArgumentValidator.validate(mappedParameter, clazz, method, parameter);

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
