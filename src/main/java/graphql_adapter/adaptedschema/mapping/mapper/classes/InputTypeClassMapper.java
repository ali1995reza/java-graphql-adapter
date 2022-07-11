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

package graphql_adapter.adaptedschema.mapping.mapper.classes;

import graphql_adapter.adaptedschema.mapping.mapped_elements.annotation.MappedAnnotation;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedInputTypeClass;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedInputTypeClassBuilder;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedInputFieldMethod;
import graphql_adapter.adaptedschema.mapping.mapper.ElementMapperWithMethodMapper;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.type.GraphqlInputTypeDescription;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.annotations.AppliedDirectiveDescriptor;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.classes.ClassDescriptor;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.method.MethodDescriptor;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.parameter.ParameterDescriptor;
import graphql_adapter.adaptedschema.mapping.validator.ClassValidator;
import graphql_adapter.adaptedschema.tools.object_builder.ObjectBuilder;
import graphql_adapter.adaptedschema.utils.ClassUtils;
import graphql_adapter.adaptedschema.utils.CollectionUtils;
import graphql_adapter.adaptedschema.utils.chain.Chain;
import graphql_adapter.codegenerator.ObjectConstructor;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class InputTypeClassMapper extends ElementMapperWithMethodMapper {

    public InputTypeClassMapper(Chain<ClassDescriptor> classDescriptorChain, Chain<MethodDescriptor> methodDescriptorChain, Chain<ParameterDescriptor> parameterDescriptorChain, Chain<AppliedDirectiveDescriptor> appliedDirectiveDescriptorChain) {
        super(classDescriptorChain, methodDescriptorChain, parameterDescriptorChain, null, appliedDirectiveDescriptorChain);
    }

    public MappedInputTypeClass mapInputTypeClass(Class<?> clazz, Map<Class<?>, MappedAnnotation> annotations, ObjectConstructor constructor, ObjectBuilder builder) {
        GraphqlInputTypeDescription inputTypeDescription = describeInputType(clazz);
        if (inputTypeDescription == null) {
            return null;
        }

        MappedInputTypeClassBuilder mappedInputTypeClassBuilder = MappedInputTypeClass.newInputTypeClass()
                .name(inputTypeDescription.name())
                .baseClass(clazz)
                .description(inputTypeDescription.description());

        for (Method method : ClassUtils.getAllMethods(clazz)) {

            MappedInputFieldMethod mappedMethod = methodMapper().mapInputFieldMethod(clazz, method, annotations, constructor, builder);

            if (mappedMethod == null) {
                continue;
            }

            mappedInputTypeClassBuilder.addInputFieldMethod(mappedMethod);
        }

        MappedInputTypeClass mappedInputTypeClass = mappedInputTypeClassBuilder.build();
        mappedInputTypeClass = addAppliedAnnotations(MappedInputTypeClass::newInputTypeClass, mappedInputTypeClass, annotations, constructor, builder);

        ClassValidator.validateInputTypeClass(mappedInputTypeClass, clazz);

        return mappedInputTypeClass;
    }

    public MappedInputTypeClass mapInputTypeClass(Class<?> clazz) {
        return mapInputTypeClass(clazz, Collections.emptyMap(), null, null);
    }

    public Map<Class<?>, MappedInputTypeClass> mapInputTypeClasses(Collection<Class<?>> classes, Map<Class<?>, MappedAnnotation> annotations, ObjectConstructor constructor, ObjectBuilder builder) {
        checkDuplicate(classes);
        return CollectionUtils.separateToMap(classes, Class.class, clazz -> clazz, clazz -> mapInputTypeClass(clazz, annotations, constructor, builder), true);
    }

    public Map<Class<?>, MappedInputTypeClass> mapInputTypeClasses(Collection<Class<?>> classes) {
        return mapInputTypeClasses(classes, Collections.emptyMap(), null, null);
    }
}
