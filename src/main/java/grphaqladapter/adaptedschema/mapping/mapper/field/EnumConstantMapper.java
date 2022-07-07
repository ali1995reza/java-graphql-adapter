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

package grphaqladapter.adaptedschema.mapping.mapper.field;

import grphaqladapter.adaptedschema.ObjectBuilder;
import grphaqladapter.adaptedschema.assertutil.Assert;
import grphaqladapter.adaptedschema.mapping.mapped_elements.annotation.MappedAnnotation;
import grphaqladapter.adaptedschema.mapping.mapped_elements.enums.MappedEnumConstant;
import grphaqladapter.adaptedschema.mapping.mapped_elements.enums.MappedEnumConstantsBuilder;
import grphaqladapter.adaptedschema.mapping.mapper.AbstractElementMapper;
import grphaqladapter.adaptedschema.mapping.strategy.descriptions.enum_value.GraphqlEnumValueDescription;
import grphaqladapter.adaptedschema.mapping.strategy.descriptors.annotations.AppliedDirectiveDescriptor;
import grphaqladapter.adaptedschema.mapping.strategy.descriptors.field.EnumConstantDescriptor;
import grphaqladapter.adaptedschema.utils.FieldUtils;
import grphaqladapter.adaptedschema.utils.chain.Chain;
import grphaqladapter.codegenerator.ObjectConstructor;

import java.lang.reflect.Field;
import java.util.Map;

public class EnumConstantMapper extends AbstractElementMapper {

    public EnumConstantMapper(Chain<EnumConstantDescriptor> enumValueDescriptorChain, Chain<AppliedDirectiveDescriptor> appliedDirectiveDescriptorChain) {
        super(null, null, null, enumValueDescriptorChain, appliedDirectiveDescriptorChain);
    }

    public MappedEnumConstant mapEnumConstant(Class clazz, Enum enumConstant, Map<Class, MappedAnnotation> annotations, ObjectConstructor constructor, ObjectBuilder builder) {
        Assert.isEquals(clazz, enumConstant.getDeclaringClass(), new IllegalStateException("enum value ["+enumConstant+"] dose not member of class ["+clazz+"]"));
        Field field = FieldUtils.getDeclaredField(clazz, enumConstant.name());
        GraphqlEnumValueDescription description = describeEnumValue(enumConstant, field, clazz);
        if (description == null) {
            return null;
        }

        MappedEnumConstant constant = MappedEnumConstantsBuilder.newBuilder()
                .name(description.name())
                .constant(enumConstant)
                .description(description.description())
                .build();
        constant = addAppliedAnnotations(MappedEnumConstantsBuilder::newBuilder, constant, annotations, constructor, builder);

        //todo validate

        return constant;
    }

}
