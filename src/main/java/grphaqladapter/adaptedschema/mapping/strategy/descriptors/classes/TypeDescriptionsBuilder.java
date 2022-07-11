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

public class TypeDescriptionsBuilder {

    public static TypeDescriptionsBuilder newBuilder() {
        return new TypeDescriptionsBuilder();
    }

    private GraphqlObjectTypeDescription objectTypeDescription;
    private GraphqlInputTypeDescription inputTypeDescription;
    private GraphqlInterfaceDescription interfaceDescription;
    private GraphqlUnionDescription unionDescription;
    private GraphqlEnumDescription enumDescription;
    private GraphqlQueryDescription queryDescription;
    private GraphqlMutationDescription mutationDescription;
    private GraphqlSubscriptionDescription subscriptionDescription;
    private GraphqlScalarDescription scalarDescription;
    private GraphqlDirectiveDescription directiveDescription;
    private Description description;

    private TypeDescriptionsBuilder() {
    }

    public TypeDescriptions build() {
        return new TypeDescriptionsImpl(objectTypeDescription,
                inputTypeDescription,
                interfaceDescription,
                unionDescription,
                enumDescription,
                queryDescription,
                mutationDescription,
                subscriptionDescription,
                scalarDescription,
                directiveDescription,
                description);
    }

    public TypeDescriptionsBuilder description(Description description) {
        this.description = description;
        return this;
    }

    public TypeDescriptionsBuilder directiveDescription(GraphqlDirectiveDescription directiveDescription) {
        this.directiveDescription = directiveDescription;
        return this;
    }

    public TypeDescriptionsBuilder enumDescription(GraphqlEnumDescription enumDescription) {
        this.enumDescription = enumDescription;
        return this;
    }

    public TypeDescriptionsBuilder inputTypeDescription(GraphqlInputTypeDescription inputTypeDescription) {
        this.inputTypeDescription = inputTypeDescription;
        return this;
    }

    public TypeDescriptionsBuilder interfaceDescription(GraphqlInterfaceDescription interfaceDescription) {
        this.interfaceDescription = interfaceDescription;
        return this;
    }

    public TypeDescriptionsBuilder mutationDescription(GraphqlMutationDescription mutationDescription) {
        this.mutationDescription = mutationDescription;
        return this;
    }

    public TypeDescriptionsBuilder objectTypeDescription(GraphqlObjectTypeDescription objectTypeDescription) {
        this.objectTypeDescription = objectTypeDescription;
        return this;
    }

    public TypeDescriptionsBuilder queryDescription(GraphqlQueryDescription queryDescription) {
        this.queryDescription = queryDescription;
        return this;
    }

    public TypeDescriptionsBuilder refresh() {
        objectTypeDescription = null;
        inputTypeDescription = null;
        interfaceDescription = null;
        unionDescription = null;
        enumDescription = null;
        queryDescription = null;
        mutationDescription = null;
        subscriptionDescription = null;
        description = null;
        return this;
    }

    public TypeDescriptionsBuilder scalarDescription(GraphqlScalarDescription scalarDescription) {
        this.scalarDescription = scalarDescription;
        return this;
    }

    public TypeDescriptionsBuilder subscriptionDescription(GraphqlSubscriptionDescription subscriptionDescription) {
        this.subscriptionDescription = subscriptionDescription;
        return this;
    }

    public TypeDescriptionsBuilder unionDescription(GraphqlUnionDescription unionDescription) {
        this.unionDescription = unionDescription;
        return this;
    }
}
