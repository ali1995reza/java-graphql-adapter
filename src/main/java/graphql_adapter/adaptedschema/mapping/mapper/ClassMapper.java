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

import graphql_adapter.adaptedschema.assertion.Assert;
import graphql_adapter.adaptedschema.exceptions.MappingGraphqlTypeException;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElement;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import graphql_adapter.adaptedschema.mapping.mapped_elements.annotation.MappedAnnotation;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedClass;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedInputTypeClass;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedObjectTypeClass;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedScalarClass;
import graphql_adapter.adaptedschema.mapping.mapped_elements.enums.MappedEnum;
import graphql_adapter.adaptedschema.mapping.mapped_elements.interfaces.MappedInterface;
import graphql_adapter.adaptedschema.mapping.mapped_elements.interfaces.MappedUnionInterface;
import graphql_adapter.adaptedschema.mapping.mapper.classes.*;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.annotations.AnnotationBaseAppliedDirectiveDescriptor;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.annotations.AppliedDirectiveDescriptor;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.classes.AnnotationBaseClassDescriptor;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.classes.ClassDescriptor;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.field.AnnotationBaseEnumConstantDescriptor;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.field.AutomaticEnumConstantDescriptor;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.field.EnumConstantDescriptor;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.method.AnnotationBaseMethodDescriptor;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.method.MethodDescriptor;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.method.PojoMethodDescriptor;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.parameter.AnnotationBaseParameterDescriptor;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.parameter.AutomaticParameterDescriptor;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.parameter.ParameterDescriptor;
import graphql_adapter.adaptedschema.scalar.ScalarEntry;
import graphql_adapter.adaptedschema.tools.object_builder.ObjectBuilder;
import graphql_adapter.adaptedschema.utils.ClassUtils;
import graphql_adapter.adaptedschema.utils.CollectionUtils;
import graphql_adapter.adaptedschema.utils.NullifyUtils;
import graphql_adapter.adaptedschema.utils.chain.Chain;
import graphql_adapter.codegenerator.ObjectConstructor;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

import static graphql_adapter.adaptedschema.exceptions.SchemaExceptionBuilder.exception;
import static graphql_adapter.adaptedschema.utils.ClassUtils.cast;

public final class ClassMapper {

    public final static Chain<ClassDescriptor> DEFAULT_CLASS_DESCRIPTORS = Chain.newChain()
            .addToLast(new AnnotationBaseClassDescriptor())
            .build();

    public final static Chain<MethodDescriptor> DEFAULT_METHOD_DESCRIPTORS = Chain.newChain()
            .addToLast(new AnnotationBaseMethodDescriptor())
            .addToLast(new PojoMethodDescriptor(true))
            .build();

    public final static Chain<ParameterDescriptor> DEFAULT_PARAMETER_DESCRIPTORS = Chain.newChain()
            .addToLast(new AnnotationBaseParameterDescriptor())
            .addToLast(AutomaticParameterDescriptor.newBuilder()
                    .argNameIfNotPresent("arg")
                    .build())
            .build();

    public final static Chain<EnumConstantDescriptor> DEFAULT_ENUM_CONSTANT_DESCRIPTORS = Chain.newChain()
            .addToLast(new AnnotationBaseEnumConstantDescriptor())
            .addToLast(new AutomaticEnumConstantDescriptor())
            .build();

    public final static Chain<AppliedDirectiveDescriptor> DEFAULT_APPLIED_DIRECTIVE_DESCRIPTORS = Chain.newChain()
            .addToLast(new AnnotationBaseAppliedDirectiveDescriptor())
            .build();

    private Chain<ClassDescriptor> classDescriptorChain;
    private Chain<MethodDescriptor> methodDescriptorChain;
    private Chain<ParameterDescriptor> parameterDescriptorChain;
    private Chain<EnumConstantDescriptor> enumConstantDescriptorChain;
    private Chain<AppliedDirectiveDescriptor> appliedDirectiveDescriptorChain;

    public ClassMapper() {
        this.setDefaultDescriptors();
    }

    public void appliedDirectiveDescriptorChain(Chain<AppliedDirectiveDescriptor> appliedDirectiveDescriptorChain) {
        Assert.isNotNull(appliedDirectiveDescriptorChain, new IllegalStateException("provided chain is null"));
        this.appliedDirectiveDescriptorChain = appliedDirectiveDescriptorChain;
    }

    public ClassMapper classDescriptorChain(Chain<ClassDescriptor> classDescriptorChain) {
        Assert.isNotNull(classDescriptorChain, new IllegalStateException("provided chain is null"));
        this.classDescriptorChain = classDescriptorChain;
        return this;
    }

    public ClassMapper enumValueDescriptorChain(Chain<EnumConstantDescriptor> enumValueDescriptorChain) {
        Assert.isNotNull(enumValueDescriptorChain, new IllegalStateException("provided chain is null"));
        this.enumConstantDescriptorChain = enumValueDescriptorChain;
        return this;
    }

    public Map<Class<?>, Map<MappedElementType, MappedElement>> map(Collection<ScalarEntry> scalarEntries, Collection<Class<?>> classes, Collection<MappedClass> existingClasses, ObjectConstructor constructor, boolean usePairTypesAsEachOther) {

        HashMap<Class<?>, Map<MappedElementType, MappedElement>> allMappedClasses = new HashMap<>();

        ObjectBuilder objectBuilder = createObjectBuilder(scalarEntries, classes, existingClasses, constructor, usePairTypesAsEachOther);

        Map<Class<?>, MappedAnnotation> annotations = new AnnotationMapper(classDescriptorChain, methodDescriptorChain, parameterDescriptorChain, Chain.empty())
                .mapAnnotations(classes);

        Map<Class<?>, MappedScalarClass> mappedScalarEntries = new ScalarClassMapper(classDescriptorChain, appliedDirectiveDescriptorChain)
                .mapScalarEntries(scalarEntries, annotations, constructor, objectBuilder);

        mappedScalarEntries.forEach((clazz, mappedClass) -> allMappedClasses.computeIfAbsent(clazz, cls -> new HashMap<>())
                .put(MappedElementType.SCALAR, mappedClass));

        for (Class<?> clazz : classes) {

            List<MappedClass> mappedClasses = mapClass(clazz, annotations, constructor, objectBuilder);

            if (CollectionUtils.isEmpty(mappedClasses)) {
                continue;
            }

            Assert.isFalse(allMappedClasses.containsKey(clazz), exception(MappingGraphqlTypeException.class, "duplicate class", clazz));

            allMappedClasses.put(clazz, mappedClasses.stream().collect(Collectors.toMap(MappedElement::mappedType, x -> x)));
        }

        return allMappedClasses;
    }

    public List<MappedClass> mapClass(Class<?> clazz, Map<Class<?>, MappedAnnotation> annotations, ObjectConstructor constructor, ObjectBuilder builder) {
        if (clazz.isEnum()) {
            return mapEnumClass(cast(clazz), annotations, constructor, builder);
        }
        if (clazz.isAnnotation()) {
            return mapAnnotationClass(cast(clazz), annotations, constructor, builder);
        }
        if (clazz.isInterface()) {
            return mapInterfaceOrInterfaceUnionClass(clazz, annotations, constructor, builder);
        }
        return NullifyUtils.getOrCompute(mapScalarClass(clazz, annotations, constructor, builder),
                () -> mapInputAndObjectType(clazz, annotations, constructor, builder));
    }

    public List<MappedClass> mapInputAndObjectType(Class<?> clazz, Map<Class<?>, MappedAnnotation> annotations, ObjectConstructor constructor, ObjectBuilder builder) {
        List<MappedClass> mappedClasses = new ArrayList<>();

        ObjectTypeClassMapper objectTypeClassMapper = new ObjectTypeClassMapper(classDescriptorChain, methodDescriptorChain, parameterDescriptorChain, appliedDirectiveDescriptorChain);
        InputTypeClassMapper inputTypeClassMapper = new InputTypeClassMapper(classDescriptorChain, methodDescriptorChain, parameterDescriptorChain, appliedDirectiveDescriptorChain);

        MappedObjectTypeClass objectTypeClass = objectTypeClassMapper.mapObjectTypeClass(clazz, MappedElementType.OBJECT_TYPE, annotations, constructor, builder);

        if (objectTypeClass != null) {
            mappedClasses.add(objectTypeClass);
        }

        MappedInputTypeClass inputTypeClass = inputTypeClassMapper.mapInputTypeClass(clazz, annotations, constructor, builder);

        if (inputTypeClass != null) {
            mappedClasses.add(inputTypeClass);
        }

        if (!mappedClasses.isEmpty()) {
            return mappedClasses;
        }

        MappedObjectTypeClass queryObjectType = objectTypeClassMapper.mapObjectTypeClass(clazz, MappedElementType.QUERY, annotations, constructor, builder);

        if (queryObjectType != null) {
            mappedClasses.add(queryObjectType);
            return mappedClasses;
        }

        MappedObjectTypeClass mutationObjectType = objectTypeClassMapper.mapObjectTypeClass(clazz, MappedElementType.MUTATION, annotations, constructor, builder);

        if (mutationObjectType != null) {
            mappedClasses.add(mutationObjectType);
            return mappedClasses;
        }

        MappedObjectTypeClass subscriptionObjectType = objectTypeClassMapper.mapObjectTypeClass(clazz, MappedElementType.MUTATION, annotations, constructor, builder);

        if (subscriptionObjectType != null) {
            mappedClasses.add(subscriptionObjectType);
            return mappedClasses;
        }

        return null;
    }

    public ClassMapper methodDescriptorChain(Chain<MethodDescriptor> methodDescriptorChain) {
        Assert.isNotNull(methodDescriptorChain, new IllegalStateException("provided chain is null"));
        this.methodDescriptorChain = methodDescriptorChain;
        return this;
    }

    public ClassMapper parameterDescriptorChain(Chain<ParameterDescriptor> parameterDescriptorChain) {
        Assert.isNotNull(parameterDescriptorChain, new IllegalStateException("provided chain is null"));
        this.parameterDescriptorChain = parameterDescriptorChain;
        return this;
    }

    public ClassMapper reset() {
        this.setDefaultDescriptors();
        return this;
    }

    private ObjectBuilder createObjectBuilder(Collection<ScalarEntry> scalarEntries, Collection<Class<?>> classes, Collection<MappedClass> defaultClasses, ObjectConstructor constructor, boolean usePairClassesAsEachOther) {
        EnumMapper enumMapper = new EnumMapper(classDescriptorChain, enumConstantDescriptorChain, Chain.empty());
        InputTypeClassMapper inputTypeClassMapper = new InputTypeClassMapper(classDescriptorChain, methodDescriptorChain, parameterDescriptorChain, Chain.empty());
        ScalarClassMapper scalarClassMapper = new ScalarClassMapper(classDescriptorChain, Chain.empty());

        Map<Class<?>, MappedEnum> enums = enumMapper.mapEnums(classes);

        Map<Class<?>, MappedInputTypeClass> inputTypes = inputTypeClassMapper.mapInputTypeClasses(classes);

        Map<Class<?>, MappedScalarClass> mappedScalarEntries = scalarClassMapper.mapScalarEntries(scalarEntries);
        Map<Class<?>, MappedScalarClass> scalars = scalarClassMapper.mapScalarClasses(classes, constructor);

        CollectionUtils.checkDuplicates(clazz -> {
            throw new IllegalStateException("duplicate scalar class found [" + clazz + "]");
        }, scalars.keySet(), mappedScalarEntries.keySet());

        scalars.putAll(mappedScalarEntries);

        for (MappedClass defaultClass : defaultClasses) {
            if (defaultClass instanceof MappedScalarClass) {
                Assert.isFalse(scalars.containsKey(defaultClass.baseClass()), exception(MappingGraphqlTypeException.class, "a class parsed class is exists in default classes", defaultClass.baseClass()));
                scalars.put(defaultClass.baseClass(), (MappedScalarClass) defaultClass);
            }

            if (defaultClass instanceof MappedEnum) {
                Assert.isFalse(enums.containsKey(defaultClass.baseClass()), exception(MappingGraphqlTypeException.class, "a class parsed class is exists in default classes", defaultClass.baseClass()));
                enums.put(defaultClass.baseClass(), (MappedEnum) defaultClass);
            }

            if (defaultClass instanceof MappedInputTypeClass) {
                Assert.isFalse(inputTypes.containsKey(defaultClass.baseClass()), exception(MappingGraphqlTypeException.class, "a class parsed class is exists in default classes", defaultClass.baseClass()));
                inputTypes.put(defaultClass.baseClass(), (MappedInputTypeClass) defaultClass);
            }
        }

        if (usePairClassesAsEachOther) {
            ClassUtils.addPairTypesWithSameValue(scalars, enums, inputTypes);
        }

        return new ObjectBuilder(scalars, enums, inputTypes, constructor);
    }

    private List<MappedClass> mapAnnotationClass(Class<? extends Annotation> clazz, Map<Class<?>, MappedAnnotation> annotations, ObjectConstructor constructor, ObjectBuilder builder) {
        MappedAnnotation mappedAnnotation = new AnnotationMapper(classDescriptorChain, methodDescriptorChain, parameterDescriptorChain, appliedDirectiveDescriptorChain)
                .mapAnnotation(clazz, annotations, constructor, builder);
        if (mappedAnnotation != null) {
            return Arrays.asList(mappedAnnotation);
        }
        return null;
    }

    private List<MappedClass> mapEnumClass(Class<? extends Enum<?>> clazz, Map<Class<?>, MappedAnnotation> annotations, ObjectConstructor constructor, ObjectBuilder builder) {
        MappedEnum mappedEnum = new EnumMapper(classDescriptorChain, enumConstantDescriptorChain, appliedDirectiveDescriptorChain)
                .mapEnum(clazz, annotations, constructor, builder);
        if (mappedEnum != null) {
            return Arrays.asList(mappedEnum);
        }
        return null;
    }

    private List<MappedClass> mapInterfaceOrInterfaceUnionClass(Class<?> clazz, Map<Class<?>, MappedAnnotation> annotations, ObjectConstructor constructor, ObjectBuilder builder) {
        MappedInterface mappedInterface = new InterfaceMapper(classDescriptorChain, methodDescriptorChain, parameterDescriptorChain, appliedDirectiveDescriptorChain)
                .mapInterface(clazz, annotations, constructor, builder);
        if (mappedInterface != null) {
            return Arrays.asList(mappedInterface);
        }

        MappedUnionInterface unionInterface = new UnionInterfaceMapper(classDescriptorChain, appliedDirectiveDescriptorChain)
                .mapUnionInterface(clazz, annotations, constructor, builder);
        if (unionInterface != null) {
            return Arrays.asList(unionInterface);
        }
        return null;
    }

    private List<MappedClass> mapScalarClass(Class<?> clazz, Map<Class<?>, MappedAnnotation> annotations, ObjectConstructor constructor, ObjectBuilder builder) {
        MappedScalarClass scalarClass = new ScalarClassMapper(classDescriptorChain, appliedDirectiveDescriptorChain)
                .mapScalarClass(clazz, annotations, constructor, builder);
        if (scalarClass != null) {
            return Arrays.asList(scalarClass);
        }
        return null;
    }

    private void setDefaultDescriptors() {
        this.classDescriptorChain = DEFAULT_CLASS_DESCRIPTORS;
        this.methodDescriptorChain = DEFAULT_METHOD_DESCRIPTORS;
        this.parameterDescriptorChain = DEFAULT_PARAMETER_DESCRIPTORS;
        this.appliedDirectiveDescriptorChain = DEFAULT_APPLIED_DIRECTIVE_DESCRIPTORS;
        this.enumConstantDescriptorChain = DEFAULT_ENUM_CONSTANT_DESCRIPTORS;
    }
}
