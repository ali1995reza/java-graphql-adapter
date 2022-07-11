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
package graphql_adapter.adaptedschema.mapping.mapper;

import graphql.introspection.Introspection;
import graphql_adapter.adaptedschema.assertion.Assert;
import graphql_adapter.adaptedschema.exceptions.MappingSchemaElementException;
import graphql_adapter.adaptedschema.functions.ValueParsingContext;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElement;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElementBuilder;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import graphql_adapter.adaptedschema.mapping.mapped_elements.TypeInformation;
import graphql_adapter.adaptedschema.mapping.mapped_elements.annotation.AppliedAnnotation;
import graphql_adapter.adaptedschema.mapping.mapped_elements.annotation.AppliedAnnotationBuilder;
import graphql_adapter.adaptedschema.mapping.mapped_elements.annotation.MappedAnnotation;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedClass;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedAnnotationMethod;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.DefaultValueDetails;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.GraphqlTypeNameDescription;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.argument.GraphqlArgumentDescription;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.argument.GraphqlDirectiveArgumentDescription;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.directive.GraphqlDirectiveDescription;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.enum_value.GraphqlEnumValueDescription;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.field.GraphqlFieldDescription;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.field.GraphqlInputFieldDescription;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.type.*;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.annotations.AppliedDirectiveDescriptor;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.annotations.DirectiveArgumentsValue;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.classes.ClassDescriptor;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.field.EnumConstantDescriptor;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.method.MethodDescriptor;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.parameter.ParameterDescriptor;
import graphql_adapter.adaptedschema.tools.object_builder.ObjectBuilder;
import graphql_adapter.adaptedschema.utils.CollectionUtils;
import graphql_adapter.adaptedschema.utils.NullifyUtils;
import graphql_adapter.adaptedschema.utils.chain.Chain;
import graphql_adapter.codegenerator.ObjectConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static graphql_adapter.adaptedschema.exceptions.SchemaExceptionBuilder.exception;
import static graphql_adapter.adaptedschema.utils.ClassUtils.cast;

public class AbstractElementMapper {

    private final Chain<ClassDescriptor> classDescriptorChain;
    private final Chain<MethodDescriptor> methodDescriptorChain;
    private final Chain<ParameterDescriptor> parameterDescriptorChain;
    private final Chain<EnumConstantDescriptor> enumValueDescriptorChain;
    private final Chain<AppliedDirectiveDescriptor> appliedDirectiveDescriptorChain;

    public AbstractElementMapper(Chain<ClassDescriptor> classDescriptorChain, Chain<MethodDescriptor> methodDescriptorChain, Chain<ParameterDescriptor> parameterDescriptorChain, Chain<EnumConstantDescriptor> enumValueDescriptorChain, Chain<AppliedDirectiveDescriptor> appliedDirectiveDescriptorChain) {
        this.classDescriptorChain = NullifyUtils.getOrDefault(classDescriptorChain, Chain.empty());
        this.methodDescriptorChain = NullifyUtils.getOrDefault(methodDescriptorChain, Chain.empty());
        this.parameterDescriptorChain = NullifyUtils.getOrDefault(parameterDescriptorChain, Chain.empty());
        this.enumValueDescriptorChain = NullifyUtils.getOrDefault(enumValueDescriptorChain, Chain.empty());
        this.appliedDirectiveDescriptorChain = NullifyUtils.getOrDefault(appliedDirectiveDescriptorChain, Chain.empty());
    }

    protected final <T extends MappedElement> T addAppliedAnnotations(Supplier<MappedElementBuilder<?, T>> builderSupplier, T element, Map<Class<?>, MappedAnnotation> annotations, ObjectConstructor constructor, ObjectBuilder builder) {
        if (annotations == null || annotations.isEmpty() || constructor == null || builder == null) {
            return element;
        }
        if (element.name().equals("version")) {
            System.out.println();
        }
        List<DirectiveArgumentsValue> values = describeAppliedDirectives(element, annotations);
        if (values == null || values.isEmpty()) {
            return element;
        }
        MappedElementBuilder<?, T> elementBuilder = builderSupplier.get()
                .copy(element);
        for (DirectiveArgumentsValue value : values) {
            MappedAnnotation annotation = annotations.get(value.annotationClass());
            Assert.isNotNull(annotation, exception(MappingSchemaElementException.class, "can not detect directive for class [" + value.annotationClass() + "]", value.annotationClass()));
            elementBuilder.addAppliedAnnotation(convert(value, annotation, constructor, builder));
        }
        return elementBuilder.build();
    }

    protected final void checkDuplicate(Collection<Class<?>> classes) {
        CollectionUtils.checkDuplicates(clazz -> {
            throw new IllegalStateException("duplicate class [" + clazz + "]");
        }, classes);
    }

    protected final List<DirectiveArgumentsValue> describeAppliedDirectives(MappedElement element, Map<Class<?>, MappedAnnotation> annotations) {
        for (AppliedDirectiveDescriptor descriptor : appliedDirectiveDescriptorChain) {
            List<DirectiveArgumentsValue> value = descriptor.describeAppliedDirectives(element, separateAnnotations(annotations, element.mappedType()));
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    protected final GraphqlDirectiveDescription describeDirective(Class<? extends Annotation> clazz) {
        for (ClassDescriptor descriptor : classDescriptorChain) {
            if (descriptor.skipType(clazz)) {
                return null;
            }
            GraphqlDirectiveDescription description = descriptor.describeDirective(clazz);
            if (description != null) {
                return description;
            }
        }
        return null;
    }

    protected final GraphqlDirectiveArgumentDescription describeDirectiveArgument(Method method, Class<?> clazz) {
        for (MethodDescriptor descriptor : methodDescriptorChain) {
            if (descriptor.skipDirectiveArgument(method, clazz)) {
                return null;
            }
            GraphqlDirectiveArgumentDescription description = descriptor.describeDirectiveArgument(method, clazz);
            if (description != null) {
                return description;
            }
        }
        return null;
    }

    protected final GraphqlEnumDescription describeEnumType(Class<? extends Enum<?>> clazz) {
        for (ClassDescriptor descriptor : classDescriptorChain) {
            if (descriptor.skipType(clazz)) {
                return null;
            }
            GraphqlEnumDescription description = descriptor.describeEnumType(clazz);
            if (description != null) {
                return description;
            }
        }
        return null;
    }

    protected final GraphqlEnumValueDescription describeEnumValue(Enum<?> enumValue, Field field, Class<? extends Enum<?>> clazz) {
        for (EnumConstantDescriptor descriptor : enumValueDescriptorChain) {
            if (descriptor.skipEnumValue(enumValue, field, clazz)) {
                return null;
            }
            GraphqlEnumValueDescription description = descriptor.describeEnumValue(enumValue, field, clazz);
            if (description != null) {
                return description;
            }
        }
        return null;
    }

    protected final GraphqlFieldDescription describeField(Method method, Class<?> clazz) {

        for (MethodDescriptor descriptor : methodDescriptorChain) {
            if (descriptor.skipField(method, clazz)) {
                return null;
            }
            GraphqlFieldDescription description = descriptor.describeField(method, clazz);
            if (description != null) {
                return description;
            }
        }

        return null;
    }

    protected final GraphqlArgumentDescription describeFieldArgument(Method method, Parameter parameter, int index) {
        for (ParameterDescriptor descriptor : parameterDescriptorChain) {
            if (descriptor.skipFieldArgument(method, parameter, index)) {
                return null;
            }
            GraphqlArgumentDescription description = descriptor.describeFieldArgument(method, parameter, index);
            if (description != null) {
                return description;
            }
        }
        return null;
    }

    protected final GraphqlInputFieldDescription describeInputField(Method method, Class<?> clazz) {
        for (MethodDescriptor descriptor : methodDescriptorChain) {
            if (descriptor.skipInputField(method, clazz)) {
                return null;
            }
            GraphqlInputFieldDescription description = descriptor.describeInputField(method, clazz);
            if (description != null) {
                return description;
            }
        }

        return null;
    }

    protected final GraphqlInputTypeDescription describeInputType(Class<?> clazz) {
        for (ClassDescriptor descriptor : classDescriptorChain) {
            if (descriptor.skipType(clazz)) {
                return null;
            }
            GraphqlInputTypeDescription description = descriptor.describeInputType(clazz);
            if (description != null) {
                return description;
            }
        }
        return null;
    }

    protected final GraphqlInterfaceDescription describeInterfaceType(Class<?> clazz) {
        for (ClassDescriptor descriptor : classDescriptorChain) {
            if (descriptor.skipType(clazz)) {
                return null;
            }
            GraphqlInterfaceDescription description = descriptor.describeInterfaceType(clazz);
            if (description != null) {
                return description;
            }
        }
        return null;
    }

    protected final GraphqlMutationDescription describeMutationType(Class<?> clazz) {
        for (ClassDescriptor descriptor : classDescriptorChain) {
            if (descriptor.skipType(clazz)) {
                return null;
            }
            GraphqlMutationDescription description = descriptor.describeMutationType(clazz);
            if (description != null) {
                return description;
            }
        }
        return null;
    }

    protected final GraphqlObjectTypeDescription describeObjectType(Class<?> clazz) {
        for (ClassDescriptor descriptor : classDescriptorChain) {
            if (descriptor.skipType(clazz)) {
                return null;
            }
            GraphqlObjectTypeDescription description = descriptor.describeObjectType(clazz);
            if (description != null) {
                return description;
            }
        }
        return null;
    }

    protected final GraphqlQueryDescription describeQueryType(Class<?> clazz) {
        for (ClassDescriptor descriptor : classDescriptorChain) {
            if (descriptor.skipType(clazz)) {
                return null;
            }
            GraphqlQueryDescription description = descriptor.describeQueryType(clazz);
            if (description != null) {
                return description;
            }
        }
        return null;
    }

    protected final GraphqlScalarDescription describeScalarType(Class<?> clazz) {
        for (ClassDescriptor descriptor : classDescriptorChain) {
            if (descriptor.skipType(clazz)) {
                return null;
            }
            GraphqlScalarDescription description = descriptor.describeScalarType(clazz);
            if (description != null) {
                return description;
            }
        }
        return null;
    }

    protected final GraphqlSubscriptionDescription describeSubscriptionType(Class<?> clazz) {
        for (ClassDescriptor descriptor : classDescriptorChain) {
            if (descriptor.skipType(clazz)) {
                return null;
            }
            GraphqlSubscriptionDescription description = descriptor.describeSubscriptionType(clazz);
            if (description != null) {
                return description;
            }
        }
        return null;
    }

    protected final GraphqlTypeNameDescription describeType(Class<?> clazz, MappedElementType elementType) {
        if (elementType.isObjectType()) {
            return describeObjectType(clazz);
        }
        if (elementType.isQuery()) {
            return describeQueryType(clazz);
        }
        if (elementType.isMutation()) {
            return describeMutationType(clazz);
        }
        if (elementType.isSubscription()) {
            return describeSubscriptionType(clazz);
        }
        if (elementType.isEnum()) {
            return describeEnumType(cast(clazz));
        }
        if (elementType.isInterface()) {
            return describeInterfaceType(clazz);
        }
        if (elementType.isUnion()) {
            return describeUnionType(clazz);
        }
        if (elementType.isScalar()) {
            return describeScalarType(clazz);
        }

        throw new IllegalStateException("can not find any type-name type for [" + elementType + "]");
    }

    protected final GraphqlUnionDescription describeUnionType(Class<?> clazz) {
        for (ClassDescriptor descriptor : classDescriptorChain) {
            if (descriptor.skipType(clazz)) {
                return null;
            }
            GraphqlUnionDescription description = descriptor.describeUnionType(clazz);
            if (description != null) {
                return description;
            }
        }
        return null;
    }

    protected final Object parseAndGetDefaultValue(DefaultValueDetails defaultValueDetails, TypeInformation<?> type, ObjectConstructor constructor, ObjectBuilder builder) {
        if (defaultValueDetails == null || constructor == null || builder == null) {
            return null;
        }
        ValueParsingContext context = new ValueParsingContext(constructor, builder);
        return constructor.getInstance(defaultValueDetails.valueParser()).parse(defaultValueDetails.value(), type, context);
    }

    private AppliedAnnotation convert(DirectiveArgumentsValue argumentsValue, MappedAnnotation annotation, ObjectConstructor constructor, ObjectBuilder builder) {
        AppliedAnnotationBuilder appliedAnnotationBuilder = AppliedAnnotation.newAppliedAnnotation();
        appliedAnnotationBuilder.annotationClass(annotation.baseClass());
        appliedAnnotationBuilder.name(annotation.name());
        ValueParsingContext context = new ValueParsingContext(constructor, builder);
        for (MappedAnnotationMethod method : annotation.mappedMethods().values()) {
            Object value = argumentsValue.getArgumentValue(method.name());
            if (value == null) {
                continue;
            }
            value = constructor.getInstance(method.valueParser())
                    .parse(cast(value), method.type(), context);
            appliedAnnotationBuilder.addArgument(method.name(), value);
        }
        return appliedAnnotationBuilder.build();
    }

    private static Map<Class<?>, MappedAnnotation> separateAnnotations(Map<Class<?>, MappedAnnotation> annotations, Introspection.DirectiveLocation location) {
        return annotations.values().stream().filter(element -> element.locations().contains(location))
                .collect(Collectors.toMap(
                        MappedClass::baseClass,
                        element -> element
                ));
    }

    private static Map<Class<?>, MappedAnnotation> separateAnnotations(Map<Class<?>, MappedAnnotation> annotations, MappedElementType type) {
        switch (type) {
            case OBJECT_TYPE:
            case QUERY:
            case MUTATION:
            case SUBSCRIPTION:
                return separateAnnotations(annotations, Introspection.DirectiveLocation.OBJECT);
            case INPUT_TYPE:
                return separateAnnotations(annotations, Introspection.DirectiveLocation.INPUT_OBJECT);
            case FIELD:
                return separateAnnotations(annotations, Introspection.DirectiveLocation.FIELD_DEFINITION);
            case INPUT_FIELD:
                return separateAnnotations(annotations, Introspection.DirectiveLocation.INPUT_FIELD_DEFINITION);
            case ENUM_VALUE:
                return separateAnnotations(annotations, Introspection.DirectiveLocation.ENUM_VALUE);
            case ENUM:
                return separateAnnotations(annotations, Introspection.DirectiveLocation.ENUM);
            case SCALAR:
                return separateAnnotations(annotations, Introspection.DirectiveLocation.SCALAR);
            case ARGUMENT:
                return separateAnnotations(annotations, Introspection.DirectiveLocation.ARGUMENT_DEFINITION);
            case UNION:
                return separateAnnotations(annotations, Introspection.DirectiveLocation.UNION);
            case INTERFACE:
                return separateAnnotations(annotations, Introspection.DirectiveLocation.INTERFACE);
            default:
                return Collections.emptyMap();
        }
    }
}
