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

package grphaqladapter.adaptedschema.mapping.strategy.descriptors.method;

import grphaqladapter.adaptedschema.mapping.mapped_elements.DimensionModel;
import grphaqladapter.adaptedschema.mapping.mapper.MappingStatics;
import grphaqladapter.adaptedschema.mapping.strategy.descriptions.argument.GraphqlDirectiveArgumentDescription;
import grphaqladapter.adaptedschema.mapping.strategy.descriptions.argument.GraphqlDirectiveArgumentDescriptionBuilder;
import grphaqladapter.adaptedschema.mapping.strategy.descriptions.field.GraphqlFieldDescription;
import grphaqladapter.adaptedschema.mapping.strategy.descriptions.field.GraphqlFieldDescriptionBuilder;
import grphaqladapter.adaptedschema.mapping.strategy.descriptions.field.GraphqlInputFieldDescription;
import grphaqladapter.adaptedschema.mapping.strategy.descriptions.field.GraphqlInputFieldDescriptionBuilder;
import grphaqladapter.adaptedschema.mapping.strategy.descriptors.DescriptorUtils;
import grphaqladapter.adaptedschema.utils.Utils;
import grphaqladapter.annotations.GraphqlDirectiveArgument;
import grphaqladapter.annotations.GraphqlField;
import grphaqladapter.annotations.GraphqlInputField;

import java.lang.reflect.Method;

public class AnnotationBaseMethodDescriptor implements MethodDescriptor {

    @Override
    public GraphqlDirectiveArgumentDescription describeDirectiveArgument(Method method, Class clazz) {
        GraphqlDirectiveArgument directiveArgumentAnnotation = MethodAnnotationLookup.findFirstAppears(method, GraphqlDirectiveArgument.class);

        if (directiveArgumentAnnotation == null) {
            return null;
        }

        String name = Utils.getOrDefault(directiveArgumentAnnotation.name(), method.getName());
        MappingStatics.TypeDetails details = MappingStatics.findTypeDetails(method);

        Class type = directiveArgumentAnnotation.type() == Void.class ? details.type() : directiveArgumentAnnotation.type();
        int dimensions = directiveArgumentAnnotation.dimensions() == -1 ? details.dimension() : directiveArgumentAnnotation.dimensions();
        DimensionModel dimensionModel = dimensions > 0 ? (directiveArgumentAnnotation.dimensionModel().isSet() ?
                directiveArgumentAnnotation.dimensionModel() : DimensionModel.ARRAY) : DimensionModel.NOT_SET;

        return GraphqlDirectiveArgumentDescriptionBuilder.newBuilder()
                .name(name)
                .nullable(directiveArgumentAnnotation.nullable())
                .type(type)
                .dimensions(dimensions)
                .dimensionModel(dimensionModel)
                .valueParser(directiveArgumentAnnotation.valueParser())
                .defaultValue(DescriptorUtils.getDefaultValueOfAnnotationMethod(method, directiveArgumentAnnotation.valueParser()))
                .description(DescriptorUtils.getDescription(method))
                .build();
    }

    @Override
    public GraphqlFieldDescription describeField(Method method, Class clazz) {
        GraphqlField fieldAnnotation = MethodAnnotationLookup.findFirstAppears(method, GraphqlField.class);

        if (fieldAnnotation == null) {
            return null;
        }

        String name = Utils.getOrDefault(fieldAnnotation.name(), method.getName());

        return GraphqlFieldDescriptionBuilder.newBuilder()
                .name(name)
                .nullable(fieldAnnotation.nullable())
                .description(DescriptorUtils.getDescription(method))
                .build();
    }

    @Override
    public GraphqlInputFieldDescription describeInputField(Method method, Class clazz) {
        GraphqlInputField inputFieldAnnotation = MethodAnnotationLookup.findFirstAppears(method, GraphqlInputField.class);

        if (inputFieldAnnotation == null) {
            return null;
        }

        String name = Utils.getOrDefault(inputFieldAnnotation.name(), method.getName());

        return GraphqlInputFieldDescriptionBuilder.newBuilder()
                .name(name)
                .nullable(inputFieldAnnotation.nullable())
                .setter(inputFieldAnnotation.setter())
                .defaultValue(DescriptorUtils.getDefaultValue(method))
                .description(DescriptorUtils.getDescription(method))
                .build();
    }

    @Override
    public boolean skipDirectiveArgument(Method method, Class clazz) {
        return DescriptorUtils.isSkipElementAnnotationPresent(method);
    }

    @Override
    public boolean skipField(Method method, Class clazz) {
        return DescriptorUtils.isSkipElementAnnotationPresent(method);
    }

    @Override
    public boolean skipInputField(Method method, Class clazz) {
        return DescriptorUtils.isSkipElementAnnotationPresent(method);
    }
}
