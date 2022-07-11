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
package graphql_adapter.adaptedschema.mapping.validator;

import graphql_adapter.adaptedschema.assertion.Assert;
import graphql_adapter.adaptedschema.exceptions.MappingGraphqlArgumentException;
import graphql_adapter.adaptedschema.exceptions.MappingGraphqlFieldException;
import graphql_adapter.adaptedschema.mapping.mapped_elements.TypeInformation;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedAnnotationMethod;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedFieldMethod;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedInputFieldMethod;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedMethod;
import graphql_adapter.adaptedschema.mapping.mapped_elements.parameter.MappedParameter;

import java.lang.reflect.Method;

import static graphql_adapter.adaptedschema.exceptions.SchemaExceptionBuilder.exception;

public class MethodValidator {

    private final static Class<MappingGraphqlFieldException> e = MappingGraphqlFieldException.class;

    public static void validateFieldMethod(MappedFieldMethod fieldMethod, Class<?> clazz, Method method) {
        validateMethod(fieldMethod, clazz, method, true);
        for (MappedParameter parameter : fieldMethod.parameters()) {
            if (parameter.model().isSchemaArgument()) {
                MappedParameter parameterByName = fieldMethod.parametersByName().get(parameter.name());
                Assert.isEquals(parameterByName, parameterByName, exception(e, "resolved paramters not same", clazz, method));
            }
            ParameterValidator.validateParameter(parameter, clazz, method, parameter.parameter());
        }
    }

    public static void validateInputFieldMethod(MappedInputFieldMethod inputFieldMethod, Class<?> clazz, Method method) {
        validateMethod(inputFieldMethod, clazz, method, true);
        Assert.isNotNull(inputFieldMethod.setter(), exception(e, "setter method for input-field method is null", clazz, method));
    }

    public static void validateMappedAnnotationMethod(MappedAnnotationMethod mappedAnnotationMethod, Class<?> clazz, Method method) {
        validateMethod(mappedAnnotationMethod, clazz, method, false);
        Assert.isNotNull(mappedAnnotationMethod.valueParser(), exception(MappingGraphqlArgumentException.class, "provided value-parser class for annotation method is null", clazz, method));
    }

    public static void validateMethod(MappedMethod mappedMethod, Class<?> clazz, Method method, boolean validateType) {
        ElementValidator.validateElement(mappedMethod, clazz, method, true, e);
        Assert.isNotNull(mappedMethod.method(), exception(e, "provided method for mapped method is null", clazz, method));
        if (validateType) {
            final boolean nullable = (mappedMethod.type().hasDimensions() || !mappedMethod.type().type().isPrimitive()) && mappedMethod.type().isNullable();
            TypeInformation<?> expectedType = TypeInformation.of(mappedMethod.method(), nullable);
            Assert.isEquals(expectedType, mappedMethod.type(), exception(e, "mapped method detected type is not valid", clazz, method));
        }
    }
}
