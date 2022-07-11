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

package grphaqladapter.adaptedschema.mapping.mapper.classes;

import grphaqladapter.adaptedschema.mapping.mapped_elements.annotation.MappedAnnotation;
import grphaqladapter.adaptedschema.mapping.mapped_elements.enums.MappedEnum;
import grphaqladapter.adaptedschema.mapping.mapped_elements.enums.MappedEnumBuilder;
import grphaqladapter.adaptedschema.mapping.mapped_elements.enums.MappedEnumConstant;
import grphaqladapter.adaptedschema.mapping.mapper.AbstractElementMapper;
import grphaqladapter.adaptedschema.mapping.mapper.field.EnumConstantMapper;
import grphaqladapter.adaptedschema.mapping.strategy.descriptions.type.GraphqlEnumDescription;
import grphaqladapter.adaptedschema.mapping.strategy.descriptors.annotations.AppliedDirectiveDescriptor;
import grphaqladapter.adaptedschema.mapping.strategy.descriptors.classes.ClassDescriptor;
import grphaqladapter.adaptedschema.mapping.strategy.descriptors.field.EnumConstantDescriptor;
import grphaqladapter.adaptedschema.mapping.validator.ClassValidator;
import grphaqladapter.adaptedschema.tools.object_builder.ObjectBuilder;
import grphaqladapter.adaptedschema.utils.CollectionUtils;
import grphaqladapter.adaptedschema.utils.chain.Chain;
import grphaqladapter.codegenerator.ObjectConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class EnumMapper extends AbstractElementMapper {

    private final EnumConstantMapper enumConstantMapper;

    public EnumMapper(Chain<ClassDescriptor> classDescriptorChain, Chain<EnumConstantDescriptor> enumValueDescriptorChain, Chain<AppliedDirectiveDescriptor> appliedDirectiveDescriptorChain) {
        super(classDescriptorChain, null, null, enumValueDescriptorChain, appliedDirectiveDescriptorChain);
        this.enumConstantMapper = new EnumConstantMapper(enumValueDescriptorChain, appliedDirectiveDescriptorChain);
    }

    public EnumConstantMapper enumConstantMapper() {
        return enumConstantMapper;
    }

    public MappedEnum mapEnum(Class<? extends Enum> clazz) {
        return mapEnum(clazz, Collections.emptyMap(), null, null);
    }

    public MappedEnum mapEnum(Class<? extends Enum> clazz, Map<Class, MappedAnnotation> annotations, ObjectConstructor constructor, ObjectBuilder builder) {
        GraphqlEnumDescription enumDescription = describeEnumType(clazz);
        if (enumDescription == null) {
            return null;
        }

        MappedEnumBuilder mappedEnumBuilder = MappedEnum.newEnum()
                .name(enumDescription.name())
                .baseClass(clazz)
                .description(enumDescription.description());

        for (Object c : clazz.getEnumConstants()) {
            Enum constant = (Enum) c;
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

    public Map<Class, MappedEnum> mapEnums(Collection<Class> classes) {
        return mapEnums(classes, Collections.emptyMap(), null, null);
    }

    public Map<Class, MappedEnum> mapEnums(Collection<Class> classes, Map<Class, MappedAnnotation> annotations, ObjectConstructor constructor, ObjectBuilder builder) {
        checkDuplicate(classes);
        return CollectionUtils.separateToMap(classes, Class.class, clazz -> clazz, clazz -> mapEnum(clazz, annotations, constructor, builder), true);
    }
}
