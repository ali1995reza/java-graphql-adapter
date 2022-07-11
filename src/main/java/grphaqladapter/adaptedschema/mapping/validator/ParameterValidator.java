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

package grphaqladapter.adaptedschema.mapping.validator;

import grphaqladapter.adaptedschema.assertion.Assert;
import grphaqladapter.adaptedschema.exceptions.MappingGraphqlArgumentException;
import grphaqladapter.adaptedschema.mapping.mapped_elements.TypeInformation;
import grphaqladapter.adaptedschema.mapping.mapped_elements.parameter.MappedParameter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static grphaqladapter.adaptedschema.exceptions.SchemaExceptionBuilder.exception;

public class ParameterValidator {

    private final static Class<MappingGraphqlArgumentException> e = MappingGraphqlArgumentException.class;

    public static void validateParameter(MappedParameter mappedParameter, Class clazz, Method method, Parameter parameter) {
        Assert.isNotNull(mappedParameter.model(), exception(e, "mapped parameter model is null", clazz, method, parameter));
        ElementValidator.validateElement(mappedParameter, clazz, method, parameter, mappedParameter.model().isSchemaArgument(), e);
        if (!mappedParameter.model().isSchemaArgument()) {
            Assert.isNull(mappedParameter.name(), exception(e, "non-schema parameters name must be null", clazz, method, parameter));
        }
        Assert.isNotNull(mappedParameter.parameter(), exception(e, "mapped parameter parameter is null", clazz, method, parameter));
        final boolean nullable = (mappedParameter.type().hasDimensions() || !mappedParameter.type().type().isPrimitive()) && mappedParameter.type().isNullable();
        TypeInformation expectedType = TypeInformation.of(mappedParameter.parameter(), nullable);
        Assert.isEquals(expectedType, mappedParameter.type(), exception(e, "mapped parameter detected type is not valid, expected: " + expectedType + ", actual:" + mappedParameter.type(), clazz, method, parameter));
        Assert.isNotNegative(mappedParameter.index(), exception(e, "mapped parameter index is negative", clazz, method, parameter));
    }
}
