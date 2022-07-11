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
import graphql_adapter.adaptedschema.exceptions.MappingGraphqlTypeException;
import graphql_adapter.adaptedschema.mapping.mapped_elements.annotation.MappedAnnotation;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedClass;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedInputTypeClass;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedObjectTypeClass;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedScalarClass;
import graphql_adapter.adaptedschema.mapping.mapped_elements.enums.MappedEnum;
import graphql_adapter.adaptedschema.mapping.mapped_elements.enums.MappedEnumConstant;
import graphql_adapter.adaptedschema.mapping.mapped_elements.interfaces.MappedInterface;
import graphql_adapter.adaptedschema.mapping.mapped_elements.interfaces.MappedUnionInterface;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedAnnotationMethod;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedFieldMethod;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedInputFieldMethod;

import static graphql_adapter.adaptedschema.exceptions.SchemaExceptionBuilder.exception;

public class ClassValidator {

    private final static Class<MappingGraphqlTypeException> e = MappingGraphqlTypeException.class;

    public static void validateAnnotation(MappedAnnotation annotation, Class<?> clazz) {
        validateMappedClass(annotation, clazz);
        Assert.isTrue(clazz.isAnnotation(),
                exception(e, "just an annotation class can be base class of MappedAnnotation", clazz));
        Assert.isNotNull(annotation.functionality(), exception(e, "provided functionality for mapped annotation is null", clazz));
        for (MappedAnnotationMethod annotationMethod : annotation.mappedMethods().values()) {
            MethodValidator.validateMappedAnnotationMethod(annotationMethod, clazz, annotationMethod.method());
        }
    }

    public static void validateEnum(MappedEnum mappedEnum, Class<?> clazz) {
        validateMappedClass(mappedEnum, clazz);
        Assert.isTrue(clazz.isEnum(),
                exception(e, "just an Enum class can be base class of MappedEnum", clazz));
        Assert.isEquals(mappedEnum.constantsByName().values().size(),
                mappedEnum.constantsByName().size(), exception(e, "enum constants size not same in difference formats", clazz));
        for (MappedEnumConstant constant : mappedEnum.constantsByName().values()) {
            MappedEnumConstant mappedEnumConstantByValue = mappedEnum.constantsByEnumValue().get(constant.constant());
            Assert.isEquals(mappedEnumConstantByValue, constant, exception(e, "resolved enum constant are not the same", clazz, constant.field()));
            EnumConstantValidator.validateEnumConstant(constant, clazz, constant.field());
        }
    }

    public static void validateInputTypeClass(MappedInputTypeClass inputTypeClass, Class<?> clazz) {
        validateMappedClass(inputTypeClass, clazz);
        for (MappedInputFieldMethod inputFieldMethod : inputTypeClass.inputFiledMethods().values()) {
            MethodValidator.validateInputFieldMethod(inputFieldMethod, clazz, inputFieldMethod.method());
        }
    }

    public static void validateInterface(MappedInterface mappedInterface, Class<?> clazz) {
        validateMappedClass(mappedInterface, clazz);
        Assert.isTrue(clazz.isInterface() && !clazz.isAnnotation(),
                exception(e, "just a non-annotation interface class can be base class of MappedInterface", clazz));
        for (MappedFieldMethod fieldMethod : mappedInterface.fieldMethods().values()) {
            MethodValidator.validateFieldMethod(fieldMethod, clazz, fieldMethod.method());
        }
    }

    public static void validateObjectTypeClass(MappedObjectTypeClass mappedObjectTypeClass, Class<?> clazz) {
        validateMappedClass(mappedObjectTypeClass, clazz);
        for (MappedFieldMethod fieldMethod : mappedObjectTypeClass.fieldMethods().values()) {
            MethodValidator.validateFieldMethod(fieldMethod, clazz, fieldMethod.method());
        }
    }

    public static void validateScalar(MappedScalarClass scalarClass, Class<?> clazz) {
        validateMappedClass(scalarClass, clazz);
        Assert.isNotNull(scalarClass.coercing(), exception(e, "provided coercing for scalar class in null", clazz));
    }

    public static void validateUnionInterface(MappedUnionInterface unionInterface, Class<?> clazz) {
        validateMappedClass(unionInterface, clazz);
        Assert.isTrue(clazz.isInterface() && !clazz.isAnnotation(),
                exception(e, "just a non-annotation interface class can be base class of MappedUnionInterface", clazz));
    }

    private static void validateMappedClass(MappedClass mappedClass, Class<?> clazz) {
        ElementValidator.validateElement(mappedClass, clazz, true, e);
        Assert.isNotNull(mappedClass.baseClass(), exception(e, "provided base class for mapped class is null", clazz));
    }

}
