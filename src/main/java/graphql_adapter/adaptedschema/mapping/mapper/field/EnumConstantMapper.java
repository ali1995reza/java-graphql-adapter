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
package graphql_adapter.adaptedschema.mapping.mapper.field;

import graphql_adapter.adaptedschema.assertion.Assert;
import graphql_adapter.adaptedschema.mapping.mapped_elements.annotation.MappedAnnotation;
import graphql_adapter.adaptedschema.mapping.mapped_elements.enums.MappedEnumConstant;
import graphql_adapter.adaptedschema.mapping.mapper.AbstractElementMapper;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.enum_value.GraphqlEnumValueDescription;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.annotations.AppliedDirectiveDescriptor;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.field.EnumConstantDescriptor;
import graphql_adapter.adaptedschema.mapping.validator.EnumConstantValidator;
import graphql_adapter.adaptedschema.tools.object_builder.ObjectBuilder;
import graphql_adapter.adaptedschema.utils.FieldUtils;
import graphql_adapter.adaptedschema.utils.chain.Chain;
import graphql_adapter.codegenerator.ObjectConstructor;

import java.lang.reflect.Field;
import java.util.Map;

import static graphql_adapter.adaptedschema.utils.ClassUtils.cast;

public class EnumConstantMapper extends AbstractElementMapper {

    public EnumConstantMapper(Chain<EnumConstantDescriptor> enumValueDescriptorChain, Chain<AppliedDirectiveDescriptor> appliedDirectiveDescriptorChain) {
        super(null, null, null, enumValueDescriptorChain, appliedDirectiveDescriptorChain, null);
    }

    public MappedEnumConstant mapEnumConstant(Class<?> clazz, Enum<?> enumConstant, Map<Class<?>, MappedAnnotation> annotations, ObjectConstructor constructor, ObjectBuilder builder) {
        Assert.isEquals(clazz, enumConstant.getDeclaringClass(), new IllegalStateException("enum value [" + enumConstant + "] dose not member of class [" + clazz + "]"));
        Field field = FieldUtils.getDeclaredField(clazz, enumConstant.name());
        GraphqlEnumValueDescription description = describeEnumValue(enumConstant, field, cast(clazz));
        if (description == null) {
            return null;
        }

        MappedEnumConstant constant = MappedEnumConstant.newEnumConstant()
                .name(description.name())
                .constant(enumConstant)
                .field(field)
                .description(description.description())
                .build();

        constant = addAppliedAnnotations(MappedEnumConstant::newEnumConstant, constant, annotations, constructor, builder);

        EnumConstantValidator.validateEnumConstant(constant, clazz, field);

        return constant;
    }
}
