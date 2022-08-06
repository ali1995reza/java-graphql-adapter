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
package graphql_adapter.adaptedschema.utils;

import graphql_adapter.adaptedschema.SchemaBuildingContext;
import graphql_adapter.adaptedschema.mapping.mapped_elements.DimensionModel;
import graphql_adapter.adaptedschema.mapping.mapped_elements.GraphqlValidator;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import graphql_adapter.adaptedschema.mapping.mapped_elements.ValidatableMappedElement;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedInputTypeClass;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedInputFieldMethod;
import graphql_adapter.codegenerator.ObjectConstructor;

import java.util.List;

import static graphql_adapter.adaptedschema.utils.ClassUtils.cast;

public class ValidatableElementUtils {

    public static void validate(Object value, ValidatableMappedElement element, ObjectConstructor constructor) {
        if (CollectionUtils.isEmpty(element.validators())) {
            return;
        }
        for (GraphqlValidator validator : element.validators()) {
            constructor.getInstance(validator.validationFunction())
                    .validate(cast(value), validator.validationArgument(), element);
        }
    }

    public static void validateDeeply(Object value, ValidatableMappedElement element, SchemaBuildingContext context) {
        if (context.isInputType(element.type().type())) {
            validateInputType(value, element.type().type(), element.type().dimensions(), element.type().dimensionModel(), context);
        }
        validate(value, element, context.objectConstructor());
    }

    private static void validateArray(Object array, Class<?> type, int dimensions, SchemaBuildingContext context) {
        if (array == null) {
            return;
        }

        CollectionUtils.forEachUnknownArray(array, object -> validateInputType(object, type, dimensions - 1, DimensionModel.ARRAY, context));
    }

    private static void validateInputType(Object value, Class<?> type, int dimensions, DimensionModel dimensionModel, SchemaBuildingContext context) {
        if (value == null) {
            return;
        }
        if (dimensions > 0) {
            if (dimensionModel.isArray()) {
                validateArray(value, type, dimensions, context);
            } else if (dimensionModel.isList()) {
                validateList(cast(value), type, dimensions, context);
            }
        } else {
            MappedInputTypeClass inputType = context.getMappedClassFor(type, MappedElementType.INPUT_TYPE);
            validateSingleInputType(value, inputType, context);
        }
    }

    private static void validateList(List<?> list, Class<?> type, int dimensions, SchemaBuildingContext context) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        list.forEach(object -> validateInputType(object, type, dimensions - 1, DimensionModel.LIST, context));
    }

    private static void validateSingleInputType(Object instance, MappedInputTypeClass inputType, SchemaBuildingContext context) {
        for (MappedInputFieldMethod inputField : inputType.inputFiledMethods().values()) {
            Object value = MethodUtils.invoke(instance, inputField.method());
            validateDeeply(value, inputField, context);
        }
    }
}
