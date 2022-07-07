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

import grphaqladapter.adaptedschema.mapping.strategy.descriptions.Description;
import grphaqladapter.adaptedschema.mapping.strategy.descriptions.directive.GraphqlDirectiveDescription;
import grphaqladapter.adaptedschema.mapping.strategy.descriptions.type.*;

class TypeDescriptionsImpl implements TypeDescriptions {

    private final GraphqlObjectTypeDescription typeAnnotation;
    private final GraphqlInputTypeDescription inputTypeAnnotation;
    private final GraphqlInterfaceDescription interfaceAnnotation;
    private final GraphqlUnionDescription unionAnnotation;
    private final GraphqlEnumDescription enumAnnotation;
    private final GraphqlQueryDescription queryAnnotation;
    private final GraphqlMutationDescription mutationAnnotation;
    private final GraphqlSubscriptionDescription subscriptionAnnotation;
    private final GraphqlScalarDescription scalarDescription;
    private final GraphqlDirectiveDescription directiveAnnotation;
    private final Description descriptionAnnotation;

    TypeDescriptionsImpl(GraphqlObjectTypeDescription typeAnnotation, GraphqlInputTypeDescription inputTypeAnnotation, GraphqlInterfaceDescription interfaceAnnotation, GraphqlUnionDescription unionAnnotation, GraphqlEnumDescription enumAnnotation, GraphqlQueryDescription queryAnnotation, GraphqlMutationDescription mutationAnnotation, GraphqlSubscriptionDescription subscriptionAnnotation, GraphqlScalarDescription scalarDescription, GraphqlDirectiveDescription directiveAnnotation, Description descriptionAnnotation) {
        this.typeAnnotation = typeAnnotation;
        this.inputTypeAnnotation = inputTypeAnnotation;
        this.interfaceAnnotation = interfaceAnnotation;
        this.unionAnnotation = unionAnnotation;
        this.enumAnnotation = enumAnnotation;
        this.queryAnnotation = queryAnnotation;
        this.mutationAnnotation = mutationAnnotation;
        this.subscriptionAnnotation = subscriptionAnnotation;
        this.scalarDescription = scalarDescription;
        this.directiveAnnotation = directiveAnnotation;
        this.descriptionAnnotation = descriptionAnnotation;
    }

    @Override
    public Description description() {
        return descriptionAnnotation;
    }

    @Override
    public GraphqlDirectiveDescription directiveDescription() {
        return directiveAnnotation;
    }

    @Override
    public GraphqlEnumDescription enumDescription() {
        return enumAnnotation;
    }

    @Override
    public GraphqlInputTypeDescription inputTypeDescription() {
        return inputTypeAnnotation;
    }

    @Override
    public GraphqlInterfaceDescription interfaceDescription() {
        return interfaceAnnotation;
    }

    @Override
    public GraphqlMutationDescription mutationDescription() {
        return mutationAnnotation;
    }

    @Override
    public GraphqlObjectTypeDescription objectTypeDescription() {
        return typeAnnotation;
    }

    @Override
    public GraphqlQueryDescription queryDescription() {
        return queryAnnotation;
    }

    @Override
    public GraphqlScalarDescription scalarDescription() {
        return scalarDescription;
    }

    @Override
    public GraphqlSubscriptionDescription subscriptionDescription() {
        return subscriptionAnnotation;
    }

    @Override
    public GraphqlUnionDescription unionDescription() {
        return unionAnnotation;
    }
}
