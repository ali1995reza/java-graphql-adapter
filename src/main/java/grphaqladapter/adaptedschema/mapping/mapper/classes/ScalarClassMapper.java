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

package grphaqladapter.adaptedschema.mapping.mapper.classes;

import grphaqladapter.adaptedschema.mapping.mapped_elements.annotation.MappedAnnotation;
import grphaqladapter.adaptedschema.mapping.mapped_elements.classes.MappedScalarClass;
import grphaqladapter.adaptedschema.mapping.mapper.AbstractElementMapper;
import grphaqladapter.adaptedschema.mapping.strategy.descriptions.type.GraphqlScalarDescription;
import grphaqladapter.adaptedschema.mapping.strategy.descriptors.annotations.AppliedDirectiveDescriptor;
import grphaqladapter.adaptedschema.mapping.strategy.descriptors.classes.ClassDescriptor;
import grphaqladapter.adaptedschema.mapping.validator.ClassValidator;
import grphaqladapter.adaptedschema.scalar.ScalarEntry;
import grphaqladapter.adaptedschema.tools.object_builder.ObjectBuilder;
import grphaqladapter.adaptedschema.utils.CollectionUtils;
import grphaqladapter.adaptedschema.utils.chain.Chain;
import grphaqladapter.codegenerator.ObjectConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class ScalarClassMapper extends AbstractElementMapper {
    public ScalarClassMapper(Chain<ClassDescriptor> classDescriptorChain, Chain<AppliedDirectiveDescriptor> appliedDirectiveDescriptorChain) {
        super(classDescriptorChain, null, null, null, appliedDirectiveDescriptorChain);
    }

    public MappedScalarClass mapScalarClass(Class clazz, Map<Class, MappedAnnotation> annotations, ObjectConstructor constructor, ObjectBuilder builder) {
        GraphqlScalarDescription scalarDescription = describeScalarType(clazz);
        if (scalarDescription == null) {
            return null;
        }

        MappedScalarClass scalarClass = MappedScalarClass.newScalarClass()
                .baseClass(clazz)
                .name(scalarDescription.name())
                .coercing(constructor.getInstance(scalarDescription.coercing()))
                .description(scalarDescription.description())
                .build();

        ClassValidator.validateScalar(scalarClass, clazz);

        return addAppliedAnnotations(MappedScalarClass::newScalarClass, scalarClass, annotations, constructor, builder);
    }

    public MappedScalarClass mapScalarClass(Class clazz, ObjectConstructor constructor) {
        return mapScalarClass(clazz, Collections.emptyMap(), constructor, null);
    }

    public Map<Class, MappedScalarClass> mapScalarClasses(Collection<Class> classes, Map<Class, MappedAnnotation> annotations, ObjectConstructor constructor, ObjectBuilder builder) {
        checkDuplicate(classes);
        return CollectionUtils.separateToMap(classes, Class.class, clazz -> clazz, clazz -> mapScalarClass(clazz, annotations, constructor, builder), true);
    }

    public Map<Class, MappedScalarClass> mapScalarClasses(Collection<Class> classes, ObjectConstructor constructor) {
        return mapScalarClasses(classes, Collections.emptyMap(), constructor, null);
    }

    public Map<Class, MappedScalarClass> mapScalarEntries(Collection<ScalarEntry> entries, Map<Class, MappedAnnotation> annotations, ObjectConstructor constructor, ObjectBuilder builder) {
        CollectionUtils.checkDuplicates(clazz -> {
            throw new IllegalStateException("duplicate class [" + clazz + "]");
        }, entries.stream().map(ScalarEntry::type).collect(Collectors.toList()));
        return CollectionUtils.separateToMap(entries, ScalarEntry.class, ScalarEntry::type, scalarEntry -> mapScalarEntry(scalarEntry, annotations, constructor, builder));
    }

    public Map<Class, MappedScalarClass> mapScalarEntries(Collection<ScalarEntry> entries) {
        return mapScalarEntries(entries, Collections.emptyMap(), null, null);
    }

    public MappedScalarClass mapScalarEntry(ScalarEntry entry, Map<Class, MappedAnnotation> annotations, ObjectConstructor constructor, ObjectBuilder builder) {
        MappedScalarClass mappedClass = MappedScalarClass.newScalarClass()
                .name(entry.name())
                .baseClass(entry.type())
                .description(entry.description())
                .coercing(entry.coercing())
                .build();

        mappedClass = addAppliedAnnotations(MappedScalarClass::newScalarClass, mappedClass, annotations, constructor, builder);

        return mappedClass;
    }

    public MappedScalarClass mapScalarEntry(ScalarEntry entry) {
        return mapScalarEntry(entry, Collections.emptyMap(), null, null);
    }
}
