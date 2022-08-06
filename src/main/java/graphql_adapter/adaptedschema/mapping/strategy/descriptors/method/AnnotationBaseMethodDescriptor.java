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
package graphql_adapter.adaptedschema.mapping.strategy.descriptors.method;

import graphql_adapter.adaptedschema.mapping.mapper.utils.DimensionsNullabilityUtils;
import graphql_adapter.adaptedschema.mapping.mapper.utils.MappingUtils;
import graphql_adapter.adaptedschema.mapping.mapper.utils.TypeDetails;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.argument.GraphqlDirectiveArgumentDescription;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.field.GraphqlFieldDescription;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.field.GraphqlInputFieldDescription;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.utils.DescriptorUtils;
import graphql_adapter.adaptedschema.utils.NullifyUtils;
import graphql_adapter.annotations.GraphqlDirectiveArgument;
import graphql_adapter.annotations.GraphqlField;
import graphql_adapter.annotations.GraphqlInputField;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AnnotationBaseMethodDescriptor implements MethodDescriptor {

    @Override
    public GraphqlDirectiveArgumentDescription describeDirectiveArgument(Method method, Class<?> clazz) {
        MethodAnnotationLookupResult<GraphqlDirectiveArgument> result = MethodAnnotationLookup.findFirstAppears(method, GraphqlDirectiveArgument.class);

        if (result == null) {
            return null;
        }

        GraphqlDirectiveArgument directiveArgumentAnnotation = result.annotation();

        String name = NullifyUtils.getOrDefault(directiveArgumentAnnotation.name(), method.getName());
        TypeDetails details = MappingUtils.findTypeDetails(method);

        if (directiveArgumentAnnotation.type() == Void.class) {
            return GraphqlDirectiveArgumentDescription.newDirectiveArgumentDescription()
                    .name(name)
                    .nullability(DimensionsNullabilityUtils.getNullabilityOfDimensions(method))
                    .type(details.type())
                    .dimensions(details.dimensions())
                    .dimensionModel(details.dimensionModel())
                    .valueParser(directiveArgumentAnnotation.valueParser())
                    .defaultValue(DescriptorUtils.getDefaultValueOfAnnotationMethod(method, directiveArgumentAnnotation.valueParser()))
                    .description(DescriptorUtils.getDescription(method))
                    .build();
        } else {
            return GraphqlDirectiveArgumentDescription.newDirectiveArgumentDescription()
                    .name(name)
                    .nullability(of(directiveArgumentAnnotation.dimensions(), directiveArgumentAnnotation.nullability()))
                    .type(directiveArgumentAnnotation.type())
                    .dimensions(directiveArgumentAnnotation.dimensions())
                    .dimensionModel(directiveArgumentAnnotation.dimensionModel())
                    .valueParser(directiveArgumentAnnotation.valueParser())
                    .defaultValue(DescriptorUtils.getDefaultValueOfAnnotationMethod(method, directiveArgumentAnnotation.valueParser()))
                    .description(DescriptorUtils.getDescription(method))
                    .build();
        }
    }

    @Override
    public GraphqlFieldDescription describeField(Method method, Class<?> clazz) {
        MethodAnnotationLookupResult<GraphqlField> result = MethodAnnotationLookup.findFirstAppears(method, GraphqlField.class);

        if (result == null) {
            return null;
        }

        GraphqlField fieldAnnotation = result.annotation();

        String name = NullifyUtils.getOrDefault(fieldAnnotation.name(), method.getName());

        return GraphqlFieldDescription.newFieldDescription()
                .name(name)
                .nullability(DimensionsNullabilityUtils.getNullabilityOfDimensions(result.method()))
                .description(DescriptorUtils.getDescription(method))
                .build();
    }

    @Override
    public GraphqlInputFieldDescription describeInputField(Method method, Class<?> clazz) {
        MethodAnnotationLookupResult<GraphqlInputField> result = MethodAnnotationLookup.findFirstAppears(method, GraphqlInputField.class);

        if (result == null) {
            return null;
        }

        GraphqlInputField inputFieldAnnotation = result.annotation();

        String name = NullifyUtils.getOrDefault(inputFieldAnnotation.name(), method.getName());

        return GraphqlInputFieldDescription.newInputFieldDescription()
                .name(name)
                .nullability(DimensionsNullabilityUtils.getNullabilityOfDimensions(result.method()))
                .setter(inputFieldAnnotation.setter())
                .defaultValue(DescriptorUtils.getDefaultValue(method))
                .description(DescriptorUtils.getDescription(method))
                .build();
    }

    @Override
    public boolean skipDirectiveArgument(Method method, Class<?> clazz) {
        return DescriptorUtils.isSkipElementAnnotationPresent(method);
    }

    @Override
    public boolean skipField(Method method, Class<?> clazz) {
        return DescriptorUtils.isSkipElementAnnotationPresent(method);
    }

    @Override
    public boolean skipInputField(Method method, Class<?> clazz) {
        return DescriptorUtils.isSkipElementAnnotationPresent(method);
    }

    private static List<Boolean> listOfBooleans(boolean value, int size) {
        List<Boolean> list = new ArrayList<>(size);
        for(int i=0;i<size;i++) {
            list.add(value);
        }
        return list;
    }

    private static List<Boolean> of(int size, boolean... booleans) {
        if(booleans == null || booleans.length == 0) {
            return listOfBooleans(true, size+1);
        }
        List<Boolean> list = new ArrayList<>();
        for (boolean b : booleans) {
            list.add(b);
        }
        return list;
    }
}
