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
import graphql_adapter.adaptedschema.mapping.mapped_elements.enums.MappedEnum;
import graphql_adapter.adaptedschema.mapping.mapped_elements.enums.MappedEnumBuilder;
import graphql_adapter.adaptedschema.mapping.mapped_elements.enums.MappedEnumConstant;
import graphql_adapter.adaptedschema.mapping.mapper.AbstractElementMapper;
import graphql_adapter.adaptedschema.mapping.mapper.field.EnumConstantMapper;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.type.GraphqlEnumDescription;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.annotations.AppliedDirectiveDescriptor;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.classes.ClassDescriptor;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.field.EnumConstantDescriptor;
import graphql_adapter.adaptedschema.mapping.validator.ClassValidator;
import graphql_adapter.adaptedschema.tools.object_builder.ObjectBuilder;
import graphql_adapter.adaptedschema.utils.CollectionUtils;
import graphql_adapter.adaptedschema.utils.chain.Chain;
import graphql_adapter.codegenerator.ObjectConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static graphql_adapter.adaptedschema.utils.ClassUtils.cast;

public class EnumMapper extends AbstractElementMapper {

    private final EnumConstantMapper enumConstantMapper;

    public EnumMapper(Chain<ClassDescriptor> classDescriptorChain, Chain<EnumConstantDescriptor> enumValueDescriptorChain, Chain<AppliedDirectiveDescriptor> appliedDirectiveDescriptorChain) {
        super(classDescriptorChain, null, null, enumValueDescriptorChain, appliedDirectiveDescriptorChain);
        this.enumConstantMapper = new EnumConstantMapper(enumValueDescriptorChain, appliedDirectiveDescriptorChain);
    }

    public EnumConstantMapper enumConstantMapper() {
        return enumConstantMapper;
    }

    public MappedEnum mapEnum(Class<? extends Enum<?>> clazz) {
        return mapEnum(clazz, Collections.emptyMap(), null, null);
    }

    public MappedEnum mapEnum(Class<? extends Enum<?>> clazz, Map<Class<?>, MappedAnnotation> annotations, ObjectConstructor constructor, ObjectBuilder builder) {
        GraphqlEnumDescription enumDescription = describeEnumType(clazz);
        if (enumDescription == null) {
            return null;
        }

        MappedEnumBuilder mappedEnumBuilder = MappedEnum.newEnum()
                .name(enumDescription.name())
                .baseClass(clazz)
                .description(enumDescription.description());

        for (Object c : clazz.getEnumConstants()) {
            Enum<?> constant = cast(c);
            MappedEnumConstant mappedEnumConstant = enumConstantMapper().mapEnumConstant(clazz, constant, annotations, constructor, builder);

            if (mappedEnumConstant == null) {
                continue;
            }

            mappedEnumBuilder.addEnumConstant(mappedEnumConstant);
        }

        MappedEnum mappedEnum = mappedEnumBuilder.build();
        mappedEnum = addAppliedAnnotations(MappedEnum::newEnum, mappedEnum, annotations, constructor, builder);

        ClassValidator.validateEnum(mappedEnum, clazz);

        return mappedEnum;
    }

    public Map<Class<?>, MappedEnum> mapEnums(Collection<Class<?>> classes) {
        return mapEnums(classes, Collections.emptyMap(), null, null);
    }

    public Map<Class<?>, MappedEnum> mapEnums(Collection<Class<?>> classes, Map<Class<?>, MappedAnnotation> annotations, ObjectConstructor constructor, ObjectBuilder builder) {
        checkDuplicate(classes);
        return CollectionUtils.separateToMap(classes, Class.class, clazz -> clazz, clazz -> mapEnum(clazz, annotations, constructor, builder), true);
    }
}
