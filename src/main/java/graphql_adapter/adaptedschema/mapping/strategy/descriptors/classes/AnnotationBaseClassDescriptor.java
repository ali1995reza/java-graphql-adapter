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
package graphql_adapter.adaptedschema.mapping.strategy.descriptors.classes;

import graphql_adapter.adaptedschema.mapping.strategy.descriptions.directive.GraphqlDirectiveDescription;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.type.*;
import graphql_adapter.adaptedschema.mapping.strategy.descriptors.utils.DescriptorUtils;
import graphql_adapter.adaptedschema.utils.NullifyUtils;
import graphql_adapter.annotations.*;

import java.lang.annotation.Annotation;

public class AnnotationBaseClassDescriptor implements ClassDescriptor {

    public AnnotationBaseClassDescriptor() {
    }

    @Override
    public GraphqlDirectiveDescription describeDirective(Class<? extends Annotation> clazz) {
        GraphqlDirective annotation = clazz.getAnnotation(GraphqlDirective.class);
        if (annotation != null) {
            return GraphqlDirectiveDescription.newDirectiveDescription()
                    .name(typeName(annotation.name(), clazz))
                    .description(DescriptorUtils.getDescription(clazz))
                    .addLocations(annotation.locations())
                    .functionality(annotation.functionality())
                    .build();
        }
        return null;
    }

    @Override
    public GraphqlEnumDescription describeEnumType(Class<? extends Enum<?>> clazz) {
        GraphqlEnum annotation = clazz.getAnnotation(GraphqlEnum.class);
        if (annotation != null) {
            return GraphqlEnumDescription.newEnumDescription()
                    .name(typeName(annotation.name(), clazz))
                    .description(DescriptorUtils.getDescription(clazz))
                    .build();
        }
        return null;
    }

    @Override
    public GraphqlInputTypeDescription describeInputType(Class<?> clazz) {
        GraphqlInputType annotation = clazz.getAnnotation(GraphqlInputType.class);
        if (annotation != null) {
            return GraphqlInputTypeDescription.newInputTypeDescription()
                    .name(typeName(annotation.name(), clazz))
                    .description(DescriptorUtils.getDescription(clazz))
                    .build();
        }
        return null;
    }

    @Override
    public GraphqlInterfaceDescription describeInterfaceType(Class<?> clazz) {
        GraphqlInterface annotation = clazz.getAnnotation(GraphqlInterface.class);
        if (annotation != null) {
            return GraphqlInterfaceDescription.newInterfaceDescription()
                    .name(typeName(annotation.name(), clazz))
                    .description(DescriptorUtils.getDescription(clazz))
                    .build();
        }
        return null;
    }

    @Override
    public GraphqlMutationDescription describeMutationType(Class<?> clazz) {
        GraphqlMutation annotation = clazz.getAnnotation(GraphqlMutation.class);
        if (annotation != null) {
            return GraphqlMutationDescription.newMutationDescription()
                    .name(typeName(annotation.name(), clazz))
                    .description(DescriptorUtils.getDescription(clazz))
                    .build();
        }
        return null;
    }

    @Override
    public GraphqlObjectTypeDescription describeObjectType(Class<?> clazz) {
        GraphqlObjectType annotation = clazz.getAnnotation(GraphqlObjectType.class);
        if (annotation != null) {
            return GraphqlObjectTypeDescription.newObjectTypeDescription()
                    .name(typeName(annotation.name(), clazz))
                    .description(DescriptorUtils.getDescription(clazz))
                    .build();
        }
        return null;
    }

    @Override
    public GraphqlQueryDescription describeQueryType(Class<?> clazz) {
        GraphqlQuery annotation = clazz.getAnnotation(GraphqlQuery.class);
        if (annotation != null) {
            return GraphqlQueryDescription.newQueryDescription()
                    .name(typeName(annotation.name(), clazz))
                    .description(DescriptorUtils.getDescription(clazz))
                    .build();
        }
        return null;
    }

    @Override
    public GraphqlScalarDescription describeScalarType(Class<?> clazz) {
        GraphqlScalar annotation = clazz.getAnnotation(GraphqlScalar.class);
        if (annotation != null) {
            return GraphqlScalarDescription.newScalarDescription()
                    .name(typeName(annotation.name(), clazz))
                    .description(DescriptorUtils.getDescription(clazz))
                    .coercing(annotation.coercing())
                    .build();
        }
        return null;
    }

    @Override
    public GraphqlSubscriptionDescription describeSubscriptionType(Class<?> clazz) {
        GraphqlSubscription annotation = clazz.getAnnotation(GraphqlSubscription.class);
        if (annotation != null) {
            return GraphqlSubscriptionDescription.newSubscriptionDescription()
                    .name(typeName(annotation.name(), clazz))
                    .description(DescriptorUtils.getDescription(clazz))
                    .build();
        }
        return null;
    }

    @Override
    public GraphqlUnionDescription describeUnionType(Class<?> clazz) {
        GraphqlUnion annotation = clazz.getAnnotation(GraphqlUnion.class);
        if (annotation != null) {
            return GraphqlUnionDescription.newUnionDescription()
                    .name(typeName(annotation.name(), clazz))
                    .description(DescriptorUtils.getDescription(clazz))
                    .build();
        }
        return null;
    }

    @Override
    public boolean skipType(Class<?> clazz) {
        return DescriptorUtils.isSkipElementAnnotationPresent(clazz);
    }

    private static String typeName(String name, Class<?> clazz) {
        return NullifyUtils.getOrDefault(name, clazz.getSimpleName());
    }
}
