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

package grphaqladapter.adaptedschema.mapping.strategy.descriptors.classes;

import grphaqladapter.adaptedschema.mapping.strategy.descriptions.directive.GraphqlDirectiveDescription;
import grphaqladapter.adaptedschema.mapping.strategy.descriptions.directive.GraphqlDirectiveDescriptionBuilder;
import grphaqladapter.adaptedschema.mapping.strategy.descriptions.type.*;
import grphaqladapter.adaptedschema.mapping.strategy.descriptors.DescriptorUtils;
import grphaqladapter.adaptedschema.utils.Utils;
import grphaqladapter.annotations.*;

import java.lang.annotation.Annotation;

public class AnnotationBaseClassDescriptor implements ClassDescriptor {

    public AnnotationBaseClassDescriptor() {
    }

    @Override
    public GraphqlEnumDescription describeEnumType(Class<? extends Enum> clazz) {
        GraphqlEnum annotation = clazz.getAnnotation(GraphqlEnum.class);
        if (annotation != null) {
            return TypesDescriptionBuilder.newBuilder()
                    .name(typeName(annotation.name(), clazz))
                    .description(DescriptorUtils.getDescription(clazz))
                    .buildEnumDescription();
        }
        return null;
    }

    @Override
    public GraphqlObjectTypeDescription describeObjectType(Class<?> clazz) {
        GraphqlObjectType annotation = clazz.getAnnotation(GraphqlObjectType.class);
        if (annotation != null) {
            return TypesDescriptionBuilder.newBuilder()
                    .name(typeName(annotation.name(), clazz))
                    .description(DescriptorUtils.getDescription(clazz))
                    .buildObjectTypeDescription();
        }
        return null;
    }

    @Override
    public GraphqlQueryDescription describeQueryType(Class<?> clazz) {
        GraphqlQuery annotation = clazz.getAnnotation(GraphqlQuery.class);
        if (annotation != null) {
            return TypesDescriptionBuilder.newBuilder()
                    .name(typeName(annotation.name(), clazz))
                    .description(DescriptorUtils.getDescription(clazz))
                    .buildQueryDescription();
        }
        return null;
    }

    @Override
    public GraphqlMutationDescription describeMutationType(Class<?> clazz) {
        GraphqlMutation annotation = clazz.getAnnotation(GraphqlMutation.class);
        if (annotation != null) {
            return TypesDescriptionBuilder.newBuilder()
                    .name(typeName(annotation.name(), clazz))
                    .description(DescriptorUtils.getDescription(clazz))
                    .buildMutationDescription();
        }
        return null;
    }

    @Override
    public GraphqlSubscriptionDescription describeSubscriptionType(Class<?> clazz) {
        GraphqlSubscription annotation = clazz.getAnnotation(GraphqlSubscription.class);
        if (annotation != null) {
            return TypesDescriptionBuilder.newBuilder()
                    .name(typeName(annotation.name(), clazz))
                    .description(DescriptorUtils.getDescription(clazz))
                    .buildSubscriptionDescription();
        }
        return null;
    }

    @Override
    public GraphqlInputTypeDescription describeInputType(Class<?> clazz) {
        GraphqlInputType annotation = clazz.getAnnotation(GraphqlInputType.class);
        if (annotation != null) {
            return TypesDescriptionBuilder.newBuilder()
                    .name(typeName(annotation.name(), clazz))
                    .description(DescriptorUtils.getDescription(clazz))
                    .buildInputTypeDescription();
        }
        return null;
    }

    @Override
    public GraphqlInterfaceDescription describeInterfaceType(Class<?> clazz) {
        GraphqlInterface annotation = clazz.getAnnotation(GraphqlInterface.class);
        if (annotation != null) {
            return TypesDescriptionBuilder.newBuilder()
                    .name(typeName(annotation.name(), clazz))
                    .description(DescriptorUtils.getDescription(clazz))
                    .buildInterfaceDescription();
        }
        return null;
    }

    @Override
    public GraphqlUnionDescription describeUnionType(Class<?> clazz) {
        GraphqlUnion annotation = clazz.getAnnotation(GraphqlUnion.class);
        if (annotation != null) {
            return TypesDescriptionBuilder.newBuilder()
                    .name(typeName(annotation.name(), clazz))
                    .description(DescriptorUtils.getDescription(clazz))
                    .buildUnionDescription();
        }
        return null;
    }

    @Override
    public GraphqlScalarDescription describeScalarType(Class<?> clazz) {
        GraphqlScalar annotation = clazz.getAnnotation(GraphqlScalar.class);
        if (annotation != null) {
            return TypesDescriptionBuilder.newBuilder()
                    .name(typeName(annotation.name(), clazz))
                    .description(DescriptorUtils.getDescription(clazz))
                    .buildScalarDescription(annotation.coercing());
        }
        return null;
    }

    @Override
    public GraphqlDirectiveDescription describeDirective(Class<? extends Annotation> clazz) {
        GraphqlDirective annotation = clazz.getAnnotation(GraphqlDirective.class);
        if (annotation != null) {
            return GraphqlDirectiveDescriptionBuilder.newBuilder()
                    .name(typeName(annotation.name(), clazz))
                    .description(DescriptorUtils.getDescription(clazz))
                    .addLocations(annotation.locations())
                    .functionality(annotation.functionality())
                    .build();
        }
        return null;
    }

    @Override
    public boolean skipType(Class clazz) {
        return DescriptorUtils.isSkipElementAnnotationPresent(clazz);
    }

    private static String typeName(String name, Class clazz) {
        return Utils.getOrDefault(name, clazz.getSimpleName());
    }
}
