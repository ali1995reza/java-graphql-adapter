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
import graphql_adapter.adaptedschema.mapping.mapped_elements.interfaces.MappedUnionInterface;
import graphql_adapter.adaptedschema.mapping.mapped_elements.interfaces.MappedUnionInterfaceBuilder;
import graphql_adapter.adaptedschema.mapping.mapper.ElementMapperWithMethodMapper;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.type.GraphqlUnionDescription;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.annotations.AppliedDirectiveDescriptor;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.classes.ClassDescriptor;
import graphql_adapter.adaptedschema.mapping.validator.ClassValidator;
import graphql_adapter.adaptedschema.tools.object_builder.ObjectBuilder;
import graphql_adapter.adaptedschema.utils.CollectionUtils;
import graphql_adapter.adaptedschema.utils.chain.Chain;
import graphql_adapter.codegenerator.ObjectConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class UnionInterfaceMapper extends ElementMapperWithMethodMapper {

    public UnionInterfaceMapper(Chain<ClassDescriptor> classDescriptorChain, Chain<AppliedDirectiveDescriptor> appliedDirectiveDescriptorChain) {
        super(classDescriptorChain, null, null, null, appliedDirectiveDescriptorChain, null);
    }

    public MappedUnionInterface mapUnionInterface(Class<?> clazz, Map<Class<?>, MappedAnnotation> annotations, ObjectConstructor constructor, ObjectBuilder builder) {
        GraphqlUnionDescription unionDescription = describeUnionType(clazz);
        if (unionDescription == null) {
            return null;
        }

        MappedUnionInterfaceBuilder mappedUnionInterfaceBuilder = MappedUnionInterface.newUnionInterface()
                .name(unionDescription.name())
                .baseClass(clazz)
                .description(unionDescription.description());

        MappedUnionInterface unionInterface = mappedUnionInterfaceBuilder.build();
        unionInterface = addAppliedAnnotations(MappedUnionInterface::newUnionInterface, unionInterface, annotations, constructor, builder);

        ClassValidator.validateUnionInterface(unionInterface, clazz);

        return unionInterface;
    }

    public MappedUnionInterface mapUnionInterface(Class<?> clazz) {
        return mapUnionInterface(clazz, Collections.emptyMap(), null, null);
    }

    public Map<Class<?>, MappedUnionInterface> mapUnionInterfaces(Collection<Class<?>> classes, Map<Class<?>, MappedAnnotation> annotations, ObjectConstructor constructor, ObjectBuilder builder) {
        checkDuplicate(classes);
        return CollectionUtils.separateToMap(classes, Class.class, clazz -> clazz, clazz -> mapUnionInterface(clazz, annotations, constructor, builder));
    }

    public Map<Class<?>, MappedUnionInterface> mapUnionInterfaces(Collection<Class<?>> classes) {
        return mapUnionInterfaces(classes, Collections.emptyMap(), null, null);
    }
}
