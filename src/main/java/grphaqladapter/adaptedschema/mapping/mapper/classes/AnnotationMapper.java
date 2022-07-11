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
import grphaqladapter.adaptedschema.mapping.mapped_elements.annotation.MappedAnnotationBuilder;
import grphaqladapter.adaptedschema.mapping.mapped_elements.method.MappedAnnotationMethod;
import grphaqladapter.adaptedschema.mapping.mapper.ElementMapperWithMethodMapper;
import grphaqladapter.adaptedschema.mapping.strategy.descriptions.directive.GraphqlDirectiveDescription;
import grphaqladapter.adaptedschema.mapping.strategy.descriptors.annotations.AppliedDirectiveDescriptor;
import grphaqladapter.adaptedschema.mapping.strategy.descriptors.classes.ClassDescriptor;
import grphaqladapter.adaptedschema.mapping.strategy.descriptors.method.MethodDescriptor;
import grphaqladapter.adaptedschema.mapping.strategy.descriptors.parameter.ParameterDescriptor;
import grphaqladapter.adaptedschema.mapping.validator.ClassValidator;
import grphaqladapter.adaptedschema.tools.object_builder.ObjectBuilder;
import grphaqladapter.adaptedschema.utils.ClassUtils;
import grphaqladapter.adaptedschema.utils.CollectionUtils;
import grphaqladapter.adaptedschema.utils.chain.Chain;
import grphaqladapter.codegenerator.ObjectConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class AnnotationMapper extends ElementMapperWithMethodMapper {
    public AnnotationMapper(Chain<ClassDescriptor> classDescriptorChain, Chain<MethodDescriptor> methodDescriptorChain, Chain<ParameterDescriptor> parameterDescriptorChain, Chain<AppliedDirectiveDescriptor> appliedDirectiveDescriptorChain) {
        super(classDescriptorChain, methodDescriptorChain, parameterDescriptorChain, null, appliedDirectiveDescriptorChain);
    }

    public MappedAnnotation mapAnnotation(Class<? extends Annotation> clazz, Map<Class, MappedAnnotation> annotations, ObjectConstructor constructor, ObjectBuilder builder) {
        GraphqlDirectiveDescription directiveDescription = describeDirective(clazz);
        if (directiveDescription == null) {
            return null;
        }

        MappedAnnotationBuilder mappedAnnotationBuilder = MappedAnnotation.newAnnotation()
                .name(directiveDescription.name())
                .description(directiveDescription.description())
                .baseClass(clazz)
                .functionality(directiveDescription.functionality())
                .addLocations(directiveDescription.locations());

        for (Method method : ClassUtils.getAllMethods(clazz)) {
            MappedAnnotationMethod mappedMethod = methodMapper().mapAnnotationMethod(clazz, method, annotations, constructor, builder);

            if (mappedMethod == null) {
                continue;
            }

            mappedAnnotationBuilder.addMethod(mappedMethod);
        }

        MappedAnnotation mappedAnnotation = mappedAnnotationBuilder.build();

        ClassValidator.validateAnnotation(mappedAnnotation, clazz);

        return mappedAnnotation;
    }

    public MappedAnnotation mapAnnotation(Class<? extends Annotation> clazz) {
        return mapAnnotation(clazz, Collections.emptyMap(), null, null);
    }

    public Map<Class, MappedAnnotation> mapAnnotations(Collection<Class> classes, Map<Class, MappedAnnotation> annotationMap, ObjectConstructor constructor, ObjectBuilder builder) {
        checkDuplicate(classes);
        return CollectionUtils.separateToMap(classes, Class.class, clazz -> clazz, clazz -> mapAnnotation(clazz, annotationMap, constructor, builder), true);
    }

    public Map<Class, MappedAnnotation> mapAnnotations(Collection<Class> classes) {
        return mapAnnotations(classes, Collections.emptyMap(), null, null);
    }

}
