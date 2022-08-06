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
import graphql_adapter.adaptedschema.mapping.mapped_elements.interfaces.MappedInterface;
import graphql_adapter.adaptedschema.mapping.mapped_elements.interfaces.MappedInterfaceBuilder;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedFieldMethod;
import graphql_adapter.adaptedschema.mapping.mapper.ElementMapperWithMethodMapper;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.type.GraphqlInterfaceDescription;
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

public class InterfaceMapper extends ElementMapperWithMethodMapper {

    public InterfaceMapper(Chain<ClassDescriptor> classDescriptorChain, Chain<MethodDescriptor> methodDescriptorChain, Chain<ParameterDescriptor> parameterDescriptorChain, Chain<AppliedDirectiveDescriptor> appliedDirectiveDescriptorChain) {
        super(classDescriptorChain, methodDescriptorChain, parameterDescriptorChain, null, appliedDirectiveDescriptorChain, null);
    }

    public MappedInterface mapInterface(Class<?> clazz, Map<Class<?>, MappedAnnotation> annotations, ObjectConstructor constructor, ObjectBuilder builder) {
        GraphqlInterfaceDescription interfaceDescription = describeInterfaceType(clazz);
        if (interfaceDescription == null) {
            return null;
        }

        MappedInterfaceBuilder mappedInterfaceBuilder = MappedInterface.newInterface()
                .name(interfaceDescription.name())
                .baseClass(clazz)
                .description(interfaceDescription.description());

        for (Method method : ClassUtils.getAllMethods(clazz)) {
            MappedFieldMethod mappedMethod = methodMapper().mapFieldMethod(clazz, method, annotations, constructor, builder);

            if (mappedMethod == null) continue;

            mappedInterfaceBuilder.addFieldMethod(mappedMethod);
        }

        MappedInterface mappedInterface = mappedInterfaceBuilder.build();
        mappedInterface = addAppliedAnnotations(MappedInterface::newInterface, mappedInterface, annotations, constructor, builder);

        ClassValidator.validateInterface(mappedInterface, clazz);

        return mappedInterface;
    }

    public MappedInterface mapInterface(Class clazz) {
        return mapInterface(clazz, Collections.emptyMap(), null, null);
    }

    public Map<Class<?>, MappedInterface> mapInterfaces(Collection<Class<?>> classes, Map<Class<?>, MappedAnnotation> annotations, ObjectConstructor constructor, ObjectBuilder builder) {
        checkDuplicate(classes);
        return CollectionUtils.separateToMap(classes, Class.class, clazz -> clazz, clazz -> mapInterface(clazz, annotations, constructor, builder));
    }

    public Map<Class<?>, MappedInterface> mapInterfaces(Collection<Class<?>> classes) {
        return mapInterfaces(classes, Collections.emptyMap(), null, null);
    }
}
