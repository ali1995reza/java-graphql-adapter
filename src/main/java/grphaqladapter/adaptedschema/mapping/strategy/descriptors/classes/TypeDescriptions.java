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

public interface TypeDescriptions {

    Description description();

    GraphqlDirectiveDescription directiveDescription();

    GraphqlEnumDescription enumDescription();

    GraphqlInputTypeDescription inputTypeDescription();

    GraphqlInterfaceDescription interfaceDescription();

    GraphqlMutationDescription mutationDescription();

    GraphqlObjectTypeDescription objectTypeDescription();

    GraphqlQueryDescription queryDescription();

    GraphqlScalarDescription scalarDescription();

    GraphqlSubscriptionDescription subscriptionDescription();

    GraphqlUnionDescription unionDescription();
}
