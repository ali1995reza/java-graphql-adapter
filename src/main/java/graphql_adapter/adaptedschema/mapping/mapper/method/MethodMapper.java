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
package graphql_adapter.adaptedschema.mapping.mapper.method;

import graphql_adapter.adaptedschema.exceptions.MappingGraphqlFieldException;
import graphql_adapter.adaptedschema.mapping.mapped_elements.TypeInformation;
import graphql_adapter.adaptedschema.mapping.mapped_elements.annotation.MappedAnnotation;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.*;
import graphql_adapter.adaptedschema.mapping.mapper.AbstractElementMapper;
import graphql_adapter.adaptedschema.mapping.mapper.parameter.ParameterMapper;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.argument.GraphqlDirectiveArgumentDescription;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.field.GraphqlFieldDescription;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.field.GraphqlInputFieldDescription;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.annotations.AppliedDirectiveDescriptor;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.method.MethodDescriptor;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.parameter.ParameterDescriptor;
import graphql_adapter.adaptedschema.mapping.validator.MethodValidator;
import graphql_adapter.adaptedschema.tools.object_builder.ObjectBuilder;
import graphql_adapter.adaptedschema.utils.chain.Chain;
import graphql_adapter.codegenerator.ObjectConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

import static graphql_adapter.adaptedschema.exceptions.SchemaExceptionBuilder.exception;

public class MethodMapper extends AbstractElementMapper {

    private final ParameterMapper parameterMapper;

    public MethodMapper(Chain<MethodDescriptor> methodDescriptorChain, Chain<ParameterDescriptor> parameterDescriptorChain, Chain<AppliedDirectiveDescriptor> appliedDirectiveDescriptorChain) {
        super(null, methodDescriptorChain, parameterDescriptorChain, null, appliedDirectiveDescriptorChain);
        this.parameterMapper = new ParameterMapper(parameterDescriptorChain, appliedDirectiveDescriptorChain);
    }

    public MappedAnnotationMethod mapAnnotationMethod(Class<? extends Annotation> clazz, Method method, Map<Class<?>, MappedAnnotation> annotations, ObjectConstructor constructor, ObjectBuilder builder) {
        GraphqlDirectiveArgumentDescription argumentDescription = describeDirectiveArgument(method, clazz);
        if (argumentDescription == null) {
            return null;
        }
        //ArgumentValidator.validate(); //todo

        TypeInformation<?> type = new TypeInformation<>(
                argumentDescription.type(),
                argumentDescription.nullable(),
                argumentDescription.dimensions(),
                argumentDescription.dimensionModel()
        );

        MappedAnnotationMethod mappedMethod = MappedAnnotationMethod.newAnnotationMethod()
                .name(argumentDescription.name())
                .method(method)
                .type(type)
                .valueParser(argumentDescription.valueParser())
                .defaultValue(parseAndGetDefaultValue(argumentDescription.defaultValue(), type, constructor, builder))
                .description(argumentDescription.description())
                .build();

        MethodValidator.validateMappedAnnotationMethod(mappedMethod, clazz, method);

        return addAppliedAnnotations(MappedAnnotationMethod::newAnnotationMethod, mappedMethod, annotations, constructor, builder);
    }

    public MappedFieldMethod mapFieldMethod(Class<?> clazz, Method method, Map<Class<?>, MappedAnnotation> annotations, ObjectConstructor constructor, ObjectBuilder builder) {

        GraphqlFieldDescription description = describeField(method, clazz);

        if (description == null) {
            return null;
        }

        MappedFieldMethodBuilder methodBuilder = MappedFieldMethod.newFieldMethod();

        for (int index = 0; index < method.getParameters().length; index++) {
            methodBuilder.addParameter(
                    parameterMapper.mapParameter(clazz, method, method.getParameters()[index], index, annotations, constructor, builder)
            );
        }

        MappedFieldMethod mappedMethod = methodBuilder.method(method)
                .description(description.description())
                .name(description.name())
                .type(TypeInformation.of(method, description.nullable()))
                .build();

        mappedMethod = addAppliedAnnotations(MappedFieldMethod::newFieldMethod, mappedMethod, annotations, constructor, builder);

        MethodValidator.validateFieldMethod(mappedMethod, clazz, method);

        return mappedMethod;
    }

    public MappedInputFieldMethod mapInputFieldMethod(Class<?> clazz, Method method, Map<Class<?>, MappedAnnotation> annotations, ObjectConstructor constructor, ObjectBuilder builder) {
        GraphqlInputFieldDescription inputFieldDescription = describeInputField(method, clazz);
        if (inputFieldDescription == null) {
            return null;
        }

        Method setterMethod = detectSetter(inputFieldDescription.setter(), clazz, method);
        MappedInputFieldMethodBuilder methodBuilder = MappedInputFieldMethod.newInputFieldMethod();
        TypeInformation<?> type = TypeInformation.of(method, inputFieldDescription.nullable());

        MappedInputFieldMethod mappedMethod = methodBuilder.method(method)
                .description(inputFieldDescription.description())
                .name(inputFieldDescription.name())
                .type(type)
                .defaultValue(parseAndGetDefaultValue(inputFieldDescription.defaultValue(), type, constructor, builder))
                .setter(setterMethod)
                .build();

        mappedMethod = addAppliedAnnotations(MappedInputFieldMethod::newInputFieldMethod, mappedMethod, annotations, constructor, builder);

        MethodValidator.validateInputFieldMethod(mappedMethod, clazz, method);

        return mappedMethod;
    }

    public ParameterMapper parameterMapper() {
        return parameterMapper;
    }

    private Method detectSetter(String setter, Class<?> clazz, Method method) {
        try {
            return clazz.getMethod(setter, method.getReturnType());
        } catch (NoSuchMethodException e) {
            throw exception(MappingGraphqlFieldException.class, "can not find setter method", clazz, method);
        }
    }
}
