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
import grphaqladapter.adaptedschema.mapping.strategy.descriptions.type.*;

import java.lang.annotation.Annotation;

public interface ClassDescriptor {

    GraphqlEnumDescription describeEnumType(Class<? extends Enum> clazz);

    GraphqlObjectTypeDescription describeObjectType(Class<?> clazz);

    GraphqlQueryDescription describeQueryType(Class<?> clazz);

    GraphqlMutationDescription describeMutationType(Class<?> clazz);

    GraphqlSubscriptionDescription describeSubscriptionType(Class<?> clazz);

    GraphqlInputTypeDescription describeInputType(Class<?> clazz);

    GraphqlInterfaceDescription describeInterfaceType(Class<?> clazz);

    GraphqlUnionDescription describeUnionType(Class<?> clazz);

    GraphqlScalarDescription describeScalarType(Class<?> clazz);

    GraphqlDirectiveDescription describeDirective(Class<? extends Annotation> clazz);

    default boolean skipType(Class clazz) {
        return false;
    }
}
