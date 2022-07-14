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
package test_graphql_adapter.utils;

import graphql_adapter.adaptedschema.discovered.*;
import graphql_adapter.adaptedschema.functions.ValueParser;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElement;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import graphql_adapter.adaptedschema.mapping.mapped_elements.TypeInformation;
import graphql_adapter.adaptedschema.mapping.mapped_elements.annotation.AppliedAnnotation;
import graphql_adapter.adaptedschema.mapping.mapped_elements.annotation.MappedAnnotation;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedInputTypeClass;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedObjectTypeClass;
import graphql_adapter.adaptedschema.mapping.mapped_elements.enums.MappedEnumConstant;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedAnnotationMethod;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedFieldMethod;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedInputFieldMethod;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedMethod;
import graphql_adapter.adaptedschema.mapping.mapped_elements.parameter.MappedParameter;
import graphql_adapter.adaptedschema.mapping.mapped_elements.parameter.ParameterModel;
import graphql_adapter.adaptedschema.utils.NameValidationUtils;
import test_graphql_adapter.schema.TestSchemaProvider;

import java.lang.annotation.Annotation;

import static org.junit.jupiter.api.Assertions.*;

public class StaticTests {

    public static void assertAndTestNumberOfImplementedInterfaces(int expected, DiscoveredObjectType objectType) {
        assertEquals(expected, objectType.implementedInterfaces().size());
        assertEquals(expected, objectType.implementedInterfacesByClass().size());
        assertEquals(expected, objectType.implementedInterfacesByName().size());
        for (DiscoveredInterfaceType interfaceType : objectType.implementedInterfaces()) {
            assertEquals(interfaceType, objectType.implementedInterfacesByClass().get(interfaceType.asMappedElement().baseClass()));
            assertEquals(interfaceType, objectType.implementedInterfacesByName().get(interfaceType.name()));
        }
    }

    public static void assertAndTestNumberOfPossibleUnions(int expected, DiscoveredObjectType objectType) {
        assertEquals(expected, objectType.possibleUnionTypes().size());
        assertEquals(expected, objectType.possibleUnionTypesByClass().size());
        assertEquals(expected, objectType.possibleUnionTypesByName().size());
        for (DiscoveredUnionType unionType : objectType.possibleUnionTypes()) {
            assertEquals(unionType, objectType.possibleUnionTypesByClass().get(unionType.asMappedElement().baseClass()));
            assertEquals(unionType, objectType.possibleUnionTypesByName().get(unionType.name()));
        }
    }

    public static DiscoveredDirective findAndTestDirective(Class<?> clazz, String name, int expectedNumberOfArguments) {
        DiscoveredDirective directiveByClass = TestSchemaProvider.schema().typeFinder().findDirectiveByClass(clazz);
        DiscoveredDirective directiveByName = TestSchemaProvider.schema().typeFinder().findDirectiveByName(name);
        assertNotNull(directiveByClass);
        assertEquals(directiveByName, directiveByClass);
        assertEquals(expectedNumberOfArguments, directiveByClass.asMappedElement().mappedMethods().size());
        return testDirective(directiveByClass);
    }

    public static DiscoveredEnumType findAndTestEnumType(Class<?> clazz, String name, int expectedNumberOfConstants, int expectedNumberOfAppliedAnnotation) {
        DiscoveredEnumType typeByClass = TestSchemaProvider.schema().typeFinder().findEnumTypeByClass(clazz);
        DiscoveredEnumType typeByName = TestSchemaProvider.schema().typeFinder().findEnumTypeByName(name);
        assertNotNull(typeByClass);
        assertEquals(typeByClass, typeByName);
        assertEquals(expectedNumberOfConstants, typeByClass.asMappedElement().constantsByName().size());
        assertEquals(expectedNumberOfAppliedAnnotation, typeByClass.asMappedElement().appliedAnnotations().size());
        return testEnumType(typeByClass);
    }

    public static DiscoveredInputType findAndTestInputType(Class<?> clazz, String name, int expectedNumberOfInputFields, int expectedNumberOfAppliedAnnotations) {
        DiscoveredInputType typeByClass = TestSchemaProvider.schema().typeFinder().findInputTypeByClass(clazz);
        DiscoveredInputType typeByName = TestSchemaProvider.schema().typeFinder().findInputTypeByName(name);
        assertEquals(typeByClass, typeByName);
        assertEquals(expectedNumberOfInputFields, typeByClass.asMappedElement().inputFiledMethods().size());
        assertEquals(expectedNumberOfAppliedAnnotations, typeByClass.asMappedElement().appliedAnnotations().size());
        return testInputType(typeByClass);
    }

    public static DiscoveredInputType findAndTestInputType(Class<?> clazz, String name, int expectedNumberOfInputFields) {
        return findAndTestInputType(clazz, name, expectedNumberOfInputFields, 0);
    }

    public static DiscoveredInterfaceType findAndTestInterfaceType(Class<?> clazz, String name, int expectedNumberOfAppliedAnnotation, int expectedNumberOfFields, Class<?>... expectedImplementors) {
        DiscoveredInterfaceType typeByClass = TestSchemaProvider.schema().typeFinder().findInterfaceTypeByClass(clazz);
        DiscoveredInterfaceType typeByName = TestSchemaProvider.schema().typeFinder().findInterfaceTypeByName(name);
        assertNotNull(typeByClass);
        assertEquals(typeByClass, typeByName);
        assertEquals(expectedNumberOfFields, typeByClass.asMappedElement().fieldMethods().size());
        int expectedNumberOfPossibleTypes = expectedImplementors == null ? 0 : expectedImplementors.length;
        assertEquals(expectedNumberOfPossibleTypes, typeByClass.implementors().size());
        assertEquals(expectedNumberOfAppliedAnnotation, typeByClass.asMappedElement().appliedAnnotations().size());
        if (expectedImplementors != null) {
            for (Class<?> expectedImplementor : expectedImplementors) {
                assertNotNull(typeByClass.implementorsByClass().get(expectedImplementor));
            }
        }
        return testInterfaceType(typeByClass);
    }

    public static DiscoveredObjectType findAndTestObjectType(Class<?> clazz, String name, int expectedNumberOfFields, int expectedNumberOfAppliedAnnotations, MappedElementType expectedElementType) {
        DiscoveredObjectType typeByClass = TestSchemaProvider.schema().typeFinder().findObjectTypeByClass(clazz);
        DiscoveredObjectType typeByName = TestSchemaProvider.schema().typeFinder().findObjectTypeByName(name);
        assertEquals(typeByClass, typeByName);
        assertEquals(expectedNumberOfFields, typeByClass.asMappedElement().fieldMethods().size());
        assertEquals(expectedElementType, typeByClass.asMappedElement().mappedType());
        assertEquals(expectedNumberOfAppliedAnnotations, typeByClass.asMappedElement().appliedAnnotations().size());
        return testObjectType(typeByClass);
    }

    public static DiscoveredObjectType findAndTestObjectType(Class<?> clazz, String name, int expectedNumberOfFields, int expectedNumberOfAppliedAnnotations) {
        return findAndTestObjectType(clazz, name, expectedNumberOfFields, expectedNumberOfAppliedAnnotations, MappedElementType.OBJECT_TYPE);
    }

    public static DiscoveredObjectType findAndTestObjectType(Class<?> clazz, String name, int expectedNumberOfFields) {
        return findAndTestObjectType(clazz, name, expectedNumberOfFields, 0);
    }

    public static DiscoveredScalarType findAndTestScalarType(Class<?> clazz, String name) {
        DiscoveredScalarType typeByClass = TestSchemaProvider.schema().typeFinder().findScalarTypeByClass(clazz);
        DiscoveredScalarType typeByName = TestSchemaProvider.schema().typeFinder().findScalarTypeByName(name);
        assertEquals(typeByClass, typeByName);
        return testScalarType(typeByClass);
    }

    public static DiscoveredUnionType findAndTestUnionType(Class<?> clazz, String name, int expectedNumberOfAppliedAnnotation, Class<?>... expectedPossibleTypes) {
        DiscoveredUnionType typeByClass = TestSchemaProvider.schema().typeFinder().findUnionTypeByClass(clazz);
        DiscoveredUnionType typeByName = TestSchemaProvider.schema().typeFinder().findUnionTypeByName(name);
        assertNotNull(typeByClass);
        assertEquals(typeByClass, typeByName);
        int expectedNumberOfPossibleTypes = expectedPossibleTypes == null ? 0 : expectedPossibleTypes.length;
        assertEquals(expectedNumberOfPossibleTypes, typeByClass.possibleTypes().size());
        assertEquals(expectedNumberOfAppliedAnnotation, typeByClass.asMappedElement().appliedAnnotations().size());
        if (expectedPossibleTypes != null) {
            for (Class<?> expectedPossibleType : expectedPossibleTypes) {
                assertNotNull(typeByClass.possibleTypesByClass().get(expectedPossibleType));
            }
        }
        return testUnionType(typeByClass);
    }

    public static MappedAnnotationMethod findAnnotationMethodAndTest(String name, MappedAnnotation annotation, int expectedNumberOfAppliedAnnotations, TypeInformation<?> expectedType, Object expectedDefaultValue) {
        MappedAnnotationMethod method = annotation.mappedMethods().get(name);
        assertNotNull(method);
        assertNotNull(name, method.name());
        assertEquals(expectedType, method.type());
        assertEquals(expectedNumberOfAppliedAnnotations, method.appliedAnnotations().size());
        TestUtils.assertEquals(expectedDefaultValue, method.defaultValue());
        return testAnnotationMethod(method);
    }

    public static MappedAnnotationMethod findAnnotationMethodAndTest(String name, DiscoveredDirective directive, int expectedNumberOfAppliedAnnotations, TypeInformation<?> expectedType, Object expectedDefaultValue) {
        return findAnnotationMethodAndTest(name, directive.asMappedElement(), expectedNumberOfAppliedAnnotations, expectedType, expectedDefaultValue);
    }

    public static MappedAnnotationMethod findAnnotationMethodAndTest(String name, DiscoveredDirective directive, int expectedNumberOfAppliedAnnotations, TypeInformation<?> expectedType) {
        return findAnnotationMethodAndTest(name, directive, expectedNumberOfAppliedAnnotations, expectedType, null);
    }

    public static AppliedAnnotation findAppliedAnnotationAndTest(Class<? extends Annotation> clazz, String name, MappedElement element, Object... args) {
        AppliedAnnotation appliedAnnotationByClass = element.appliedAnnotationsByClass().get(clazz);
        AppliedAnnotation appliedAnnotationByName = element.appliedAnnotationsByName().get(name);
        assertNotNull(appliedAnnotationByClass);
        assertEquals(appliedAnnotationByClass, appliedAnnotationByName);
        assertTrue(element.appliedAnnotations().contains(appliedAnnotationByClass));
        if (args != null) {
            assertEquals(0, args.length % 2);
        }
        int expectedNumberOfArgs = args == null ? 0 : args.length / 2;
        assertEquals(expectedNumberOfArgs, appliedAnnotationByClass.arguments().size());
        if (args != null) {
            for (int i = 0; i < args.length; i += 2) {
                String key = (String) args[i];
                Object value = args[i + 1];
                TestUtils.assertEquals(value, appliedAnnotationByClass.arguments().get(key));
            }
        }
        return appliedAnnotationByClass;
    }

    public static AppliedAnnotation findAppliedAnnotationAndTest(Class<? extends Annotation> clazz, String name, DiscoveredElement<?, ?> element, Object... args) {
        return findAppliedAnnotationAndTest(clazz, name, element.asMappedElement(), args);
    }

    public static MappedEnumConstant findEnumValueAndTest(String name, Enum<?> value, DiscoveredEnumType enumType, int expectedNumberOfAppliedAnnotation) {
        MappedEnumConstant constant = enumType.asMappedElement().constantsByName().get(name);
        assertEquals(name, constant.name());
        assertEquals(value, constant.constant());
        assertEquals(expectedNumberOfAppliedAnnotation, constant.appliedAnnotations().size());
        return testMappedEnumConstant(constant);
    }

    public static MappedFieldMethod findFieldAndTest(String name, MappedObjectTypeClass mappedClass, int expectedNumberOfParameters, int expectedNumberOfAppliedAnnotations, TypeInformation<?> expectedType) {
        MappedFieldMethod field = testFieldMethod(mappedClass.fieldMethods().get(name));
        assertEquals(expectedNumberOfParameters, field.parameters().size());
        assertEquals(expectedNumberOfAppliedAnnotations, field.appliedAnnotations().size());
        assertEquals(expectedNumberOfAppliedAnnotations, field.appliedAnnotationsByClass().size());
        assertEquals(expectedNumberOfAppliedAnnotations, field.appliedAnnotationsByName().size());
        assertEquals(expectedType, field.type());
        return field;
    }

    public static MappedFieldMethod findFieldAndTest(String name, DiscoveredObjectType discoveredObjectType, int expectedNumberOfParameters, int expectedNumberOfAppliedAnnotations, TypeInformation<?> expectedType) {
        return findFieldAndTest(name, discoveredObjectType.asMappedElement(), expectedNumberOfParameters, expectedNumberOfAppliedAnnotations, expectedType);
    }

    public static MappedFieldMethod findFieldAndTest(String name, DiscoveredObjectType discoveredObjectType, int expectedNumberOfParameters, TypeInformation<?> expectedType) {
        return findFieldAndTest(name, discoveredObjectType.asMappedElement(), expectedNumberOfParameters, 0, expectedType);
    }

    public static MappedFieldMethod findFieldAndTest(String name, DiscoveredObjectType discoveredObjectType, TypeInformation<?> expectedType) {
        return findFieldAndTest(name, discoveredObjectType.asMappedElement(), 0, 0, expectedType);
    }

    public static MappedFieldMethod findFieldAndTest(String name, DiscoveredInterfaceType discoveredInterfaceType, int expectedNumberOfParameters, int expectedNumberOfAppliedAnnotations, TypeInformation<?> expectedType) {
        return findFieldAndTest(name, discoveredInterfaceType.asMappedElement(), expectedNumberOfParameters, expectedNumberOfAppliedAnnotations, expectedType);
    }

    public static MappedFieldMethod findFieldAndTest(String name, DiscoveredInterfaceType discoveredInterfaceType, int expectedNumberOfParameters, TypeInformation<?> expectedType) {
        return findFieldAndTest(name, discoveredInterfaceType.asMappedElement(), expectedNumberOfParameters, 0, expectedType);
    }

    public static MappedFieldMethod findFieldAndTest(String name, DiscoveredInterfaceType discoveredInterfaceType, TypeInformation<?> expectedType) {
        return findFieldAndTest(name, discoveredInterfaceType.asMappedElement(), 0, 0, expectedType);
    }

    public static MappedInputFieldMethod findInputFieldAndTest(String name, MappedInputTypeClass mappedClass, int expectedNumberOfAppliedAnnotations, TypeInformation<?> expectedType, Object expectedDefaultValue) {
        MappedInputFieldMethod inputField = testInputFieldMethod(mappedClass.inputFiledMethods().get(name));
        assertEquals(expectedType, inputField.type());
        assertEquals(expectedNumberOfAppliedAnnotations, inputField.appliedAnnotations().size());
        TestUtils.assertEquals(expectedDefaultValue, inputField.defaultValue());
        return testInputFieldMethod(inputField);
    }

    public static MappedInputFieldMethod findInputFieldAndTest(String name, DiscoveredInputType discoveredInputType, int expectedNumberOfAppliedAnnotations, TypeInformation<?> expectedType, Object expectedDefaultValue) {
        return findInputFieldAndTest(name, discoveredInputType.asMappedElement(), expectedNumberOfAppliedAnnotations, expectedType, expectedDefaultValue);
    }

    public static MappedInputFieldMethod findInputFieldAndTest(String name, DiscoveredInputType discoveredInputType, int expectedNumberOfAppliedAnnotations, TypeInformation<?> expectedType) {
        return findInputFieldAndTest(name, discoveredInputType, expectedNumberOfAppliedAnnotations, expectedType, null);
    }

    public static MappedInputFieldMethod findInputFieldAndTest(String name, DiscoveredInputType discoveredInputType, TypeInformation<?> expectedType) {
        return findInputFieldAndTest(name, discoveredInputType, 0, expectedType);
    }

    public static DiscoveredInterfaceType findInterfaceTypeAndTest(Class<?> clazz, String name, DiscoveredObjectType objectType) {
        DiscoveredInterfaceType interfaceByClass = objectType.implementedInterfacesByClass().get(clazz);
        DiscoveredInterfaceType interfaceByName = objectType.implementedInterfacesByName().get(name);
        assertEquals(interfaceByClass, interfaceByName);
        return testInterfaceType(interfaceByClass);
    }

    public static MappedParameter findParameterAndTest(String name, MappedFieldMethod field, ParameterModel expectedModel, int expectedIndex, int expectedNumberOfAppliedAnnotations, TypeInformation<?> expectedType, Object expectedDefaultValue) {
        MappedParameter parameter = field.parameters().get(expectedIndex);
        assertEquals(name, parameter.name());
        assertEquals(expectedModel, parameter.model());
        assertEquals(expectedType, parameter.type());
        assertEquals(expectedNumberOfAppliedAnnotations, parameter.appliedAnnotations().size());
        assertEquals(expectedIndex, parameter.index());
        TestUtils.assertEquals(expectedDefaultValue, parameter.defaultValue());
        return testParameter(parameter);
    }

    public static MappedParameter findParameterAndTest(String name, MappedFieldMethod field, ParameterModel expectedModel, int expectedIndex, int expectedNumberOfAppliedAnnotations, TypeInformation<?> expectedType) {
        return findParameterAndTest(name, field, expectedModel, expectedIndex, expectedNumberOfAppliedAnnotations, expectedType, null);
    }

    public static MappedParameter findParameterAndTest(String name, MappedFieldMethod field, ParameterModel expectedModel, int expectedIndex, TypeInformation<?> expectedType) {
        return findParameterAndTest(name, field, expectedModel, expectedIndex, 0, expectedType, null);
    }

    public static DiscoveredUnionType findUnionTypeAndTest(Class<?> clazz, String name, DiscoveredObjectType objectType) {
        DiscoveredUnionType unionTypeByClass = objectType.possibleUnionTypesByClass().get(clazz);
        DiscoveredUnionType unionTypeByName = objectType.possibleUnionTypesByName().get(name);
        assertEquals(unionTypeByClass, unionTypeByName);
        return testUnionType(unionTypeByClass);
    }

    public static MappedAnnotationMethod testAnnotationMethod(MappedAnnotationMethod method) {
        testMethod(method);
        assertNotNull(method.valueParser());
        assertTrue(ValueParser.class.isAssignableFrom(method.valueParser()));
        return method;
    }

    public static DiscoveredDirective testDirective(DiscoveredDirective directive) {
        return testDiscoveredElement(directive);
    }

    public static <T extends DiscoveredElement<?, ?>> T testDiscoveredElement(T discoveredElement) {
        testMappedElement(discoveredElement.asMappedElement());
        return discoveredElement;
    }

    public static DiscoveredEnumType testEnumType(DiscoveredEnumType enumType) {
        testDiscoveredElement(enumType);
        return enumType;
    }

    public static MappedFieldMethod testFieldMethod(MappedFieldMethod method) {
        testMethod(method);
        return method;
    }

    public static MappedInputFieldMethod testInputFieldMethod(MappedInputFieldMethod method) {
        testMethod(method);
        assertNotNull(method.setter());
        return method;
    }

    public static DiscoveredInputType testInputType(DiscoveredInputType inputType) {
        return testDiscoveredElement(inputType);
    }

    public static DiscoveredInterfaceType testInterfaceType(DiscoveredInterfaceType interfaceType) {
        testDiscoveredElement(interfaceType);
        int expectedNumberOfImplementors = interfaceType.implementors().size();
        assertEquals(expectedNumberOfImplementors, interfaceType.implementorsByClass().size());
        assertEquals(expectedNumberOfImplementors, interfaceType.implementorsByName().size());
        for (DiscoveredObjectType objectType : interfaceType.implementors()) {
            assertEquals(objectType, interfaceType.implementorsByClass().get(objectType.asMappedElement().baseClass()));
            assertEquals(objectType, interfaceType.implementorsByName().get(objectType.name()));
        }
        return interfaceType;
    }

    public static <T extends MappedElement> T testMappedElement(T element) {
        if (element instanceof MappedParameter) {
            MappedParameter parameter = (MappedParameter) element;
            if (parameter.model().isSchemaArgument()) {
                assertTrue(NameValidationUtils.isNameValid(element.name()));
            } else {
                assertNull(element.name());
            }
        } else {
            assertTrue(NameValidationUtils.isNameValid(element.name()));
        }
        assertNotNull(element.mappedType());
        int expectedAppliedAnnotations = element.appliedAnnotations().size();
        assertEquals(expectedAppliedAnnotations, element.appliedAnnotationsByClass().size());
        assertEquals(expectedAppliedAnnotations, element.appliedAnnotationsByName().size());
        for (AppliedAnnotation annotation : element.appliedAnnotations()) {
            assertEquals(annotation, element.appliedAnnotationsByClass().get(annotation.annotationClass()));
            assertEquals(annotation, element.appliedAnnotationsByName().get(annotation.name()));
        }
        return element;
    }

    public static MappedEnumConstant testMappedEnumConstant(MappedEnumConstant constant) {
        testMappedElement(constant);
        return constant;
    }

    public static MappedMethod testMethod(MappedMethod method) {
        testMappedElement(method);
        assertNotNull(method);
        assertNotNull(method.method());
        assertTrue(NameValidationUtils.isNameValid(method.name()));
        assertNotNull(method.type());
        if (method instanceof MappedInputFieldMethod) {
            assertEquals(method.mappedType(), MappedElementType.INPUT_FIELD);
        }

        if (method instanceof MappedFieldMethod) {
            assertEquals(method.mappedType(), MappedElementType.FIELD);
        }

        if (method instanceof MappedAnnotationMethod) {
            assertEquals(method.mappedType(), MappedElementType.ARGUMENT);
        }
        return method;
    }

    public static DiscoveredObjectType testObjectType(DiscoveredObjectType objectType) {
        testDiscoveredElement(objectType);
        assertTrue(objectType.asMappedElement().mappedType().isTopLevelType() ||
                objectType.asMappedElement().mappedType().isObjectType());
        return objectType;
    }

    public static MappedParameter testParameter(MappedParameter parameter) {
        testMappedElement(parameter);
        return parameter;
    }

    public static DiscoveredScalarType testScalarType(DiscoveredScalarType scalarType) {
        testDiscoveredElement(scalarType);
        return scalarType;
    }

    public static DiscoveredUnionType testUnionType(DiscoveredUnionType unionType) {
        testDiscoveredElement(unionType);
        int expectedNumberPossibleTypes = unionType.possibleTypes().size();
        assertEquals(expectedNumberPossibleTypes, unionType.possibleTypesByClass().size());
        assertEquals(expectedNumberPossibleTypes, unionType.possibleTypesByName().size());
        for (DiscoveredObjectType objectType : unionType.possibleTypes()) {
            assertEquals(objectType, unionType.possibleTypesByClass().get(objectType.asMappedElement().baseClass()));
            assertEquals(objectType, unionType.possibleTypesByName().get(objectType.name()));
        }
        return unionType;
    }

    public static void assertDescriptionEquals(MappedElement element, String expectedDescription) {
        assertEquals(expectedDescription, element.description(), "description is not equals as expected");
    }

    public static void assertDescriptionEquals(DiscoveredElement<?, ?> element, String expectedDescription) {
        assertDescriptionEquals(element.asMappedElement(), expectedDescription);
    }

    public static void assertDescriptionIsNull(MappedElement element) {
        assertNull(element.description(), "description is not equals as expected");
    }

    public static void assertDescriptionIsNull(DiscoveredElement<?, ?> element) {
        assertNull(element.asMappedElement().description());
    }
}
