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

import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import graphql_adapter.adaptedschema.mapping.mapped_elements.annotation.MappedAnnotation;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedObjectTypeClass;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedObjectTypeClassBuilder;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedFieldMethod;
import graphql_adapter.adaptedschema.mapping.mapper.ElementMapperWithMethodMapper;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.GraphqlTypeNameDescription;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.annotations.AppliedDirectiveDescriptor;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.classes.ClassDescriptor;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.method.MethodDescriptor;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.parameter.ParameterDescriptor;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.validators.ValidatorDescriptor;
import graphql_adapter.adaptedschema.mapping.validator.ClassValidator;
import graphql_adapter.adaptedschema.tools.object_builder.ObjectBuilder;
import graphql_adapter.adaptedschema.utils.ClassUtils;
import graphql_adapter.adaptedschema.utils.CollectionUtils;
import graphql_adapter.adaptedschema.utils.chain.Chain;
import graphql_adapter.codegenerator.ObjectConstructor;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

public class ObjectTypeClassMapper extends ElementMapperWithMethodMapper {

    public ObjectTypeClassMapper(Chain<ClassDescriptor> classDescriptorChain, Chain<MethodDescriptor> methodDescriptorChain, Chain<ParameterDescriptor> parameterDescriptorChain, Chain<AppliedDirectiveDescriptor> appliedDirectiveDescriptorChain, Chain<ValidatorDescriptor> validatorDescriptorChain) {
        super(classDescriptorChain, methodDescriptorChain, parameterDescriptorChain, null, appliedDirectiveDescriptorChain, validatorDescriptorChain);
    }

    public MappedObjectTypeClass mapObjectTypeClass(Class<?> clazz, MappedElementType elementType, Map<Class<?>, MappedAnnotation> annotations, ObjectConstructor constructor, ObjectBuilder builder) {
        GraphqlTypeNameDescription description = describeType(clazz, elementType);
        if (description == null) {
            return null;
        }

        MappedObjectTypeClassBuilder mappedObjectTypeClassBuilder = MappedObjectTypeClass.newObjectTypeClass(elementType)
                .name(description.name())
                .baseClass(clazz)
                .description(description.description());

        for (Method method : ClassUtils.getAllMethods(clazz)) {
            MappedFieldMethod mappedMethod = methodMapper().mapFieldMethod(clazz, method, annotations, constructor, builder);

            if (mappedMethod == null) continue;

            mappedObjectTypeClassBuilder.addFieldMethod(mappedMethod);
        }

        MappedObjectTypeClass mappedObjectTypeClass = addAppliedAnnotations(() -> MappedObjectTypeClass.newObjectTypeClass(elementType), mappedObjectTypeClassBuilder.build(), annotations, constructor, builder);

        ClassValidator.validateObjectTypeClass(mappedObjectTypeClass, clazz);

        return mappedObjectTypeClass;
    }

    public MappedObjectTypeClass mapObjectTypeClass(Class clazz, MappedElementType elementType) {
        return mapObjectTypeClass(clazz, elementType, Collections.emptyMap(), null, null);
    }

    public Map<Class, MappedObjectTypeClass> mapObjectTypeClasses(Map<Class<?>, MappedElementType> classes, Map<Class<?>, MappedAnnotation> annotations, ObjectConstructor constructor, ObjectBuilder builder) {
        checkDuplicate(classes.keySet());
        return CollectionUtils.separateToMap(classes.keySet(), Class.class, clazz -> clazz, clazz -> mapObjectTypeClass(clazz, classes.get(clazz), annotations, constructor, builder));
    }

    public Map<Class, MappedObjectTypeClass> mapObjectTypeClasses(Map<Class<?>, MappedElementType> classes) {
        return mapObjectTypeClasses(classes, Collections.emptyMap(), null, null);
    }
}
