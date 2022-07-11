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
import grphaqladapter.adaptedschema.exceptions.MappingGraphqlEnumValueException;
import grphaqladapter.adaptedschema.mapping.mapped_elements.enums.MappedEnumConstant;

import java.lang.reflect.Field;

import static grphaqladapter.adaptedschema.exceptions.SchemaExceptionBuilder.exception;

public class EnumConstantValidator {

    private final static Class<MappingGraphqlEnumValueException> e = MappingGraphqlEnumValueException.class;

    public static void validateEnumConstant(MappedEnumConstant mappedEnumConstant, Class clazz, Field field) {
        ElementValidator.validateElement(mappedEnumConstant, clazz, true, e);
        Assert.isNotNull(mappedEnumConstant.constant(), exception(e, "constant provided for mapped enum-constant is null", clazz, field));
        Assert.isNotNull(mappedEnumConstant.field(), exception(e, "field provided for mapped enum-constant is null", clazz));
    }
}
