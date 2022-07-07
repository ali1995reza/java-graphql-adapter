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

package grphaqladapter.adaptedschema.mapping.strategy.descriptors.field;

import grphaqladapter.adaptedschema.mapping.strategy.descriptions.enum_value.GraphqlEnumValueDescription;
import grphaqladapter.adaptedschema.mapping.strategy.descriptors.DescriptorUtils;
import grphaqladapter.adaptedschema.utils.Utils;
import grphaqladapter.annotations.GraphqlEnumValue;

import java.lang.reflect.Field;

public class AnnotationBaseEnumConstantDescriptor implements EnumConstantDescriptor {


    @Override
    public GraphqlEnumValueDescription describeEnumValue(Enum value, Field field, Class<? extends Enum> clazz) {
        GraphqlEnumValue annotation = field.getAnnotation(GraphqlEnumValue.class);
        if (annotation == null) {
            return null;
        }
        return GraphqlEnumValueDescription.newValue(Utils.getOrDefault(annotation.name(), value.name()),
                DescriptorUtils.getDescription(field));
    }

    @Override
    public boolean skipEnumValue(Enum enumValue, Field field, Class<? extends Enum> clazz) {
        return DescriptorUtils.isSkipElementAnnotationPresent(field);
    }
}
