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

package grphaqladapter.adaptedschema.mapping.strategy.descriptions.type;

import graphql.schema.Coercing;

public class TypesDescriptionBuilder {

    public static TypesDescriptionBuilder newBuilder() {
        return new TypesDescriptionBuilder();
    }

    private String name;
    private String description;

    private TypesDescriptionBuilder() {
    }

    public GraphqlEnumDescription buildEnumDescription() {
        return new GraphqlEnumDescriptionImpl(name(), description());
    }

    public GraphqlInputTypeDescription buildInputTypeDescription() {
        return new GraphqlInputTypeDescriptionImpl(name(), description());
    }

    public GraphqlInterfaceDescription buildInterfaceDescription() {
        return new GraphqlInterfaceDescriptionImpl(name(), description());
    }

    public GraphqlMutationDescription buildMutationDescription() {
        return new GraphqlMutationDescriptionImpl(name(), description());
    }

    public GraphqlObjectTypeDescription buildObjectTypeDescription() {
        return new GraphqlObjectTypeDescriptionImpl(name(), description());
    }

    public GraphqlQueryDescription buildQueryDescription() {
        return new GraphqlQueryDescriptionImpl(name(), description());
    }

    public GraphqlScalarDescription buildScalarDescription(Class<? extends Coercing> coercing) {
        return new GraphqlScalarDescriptionImpl(name(), description(), coercing);
    }

    public GraphqlSubscriptionDescription buildSubscriptionDescription() {
        return new GraphqlSubscriptionDescriptionImpl(name(), description());
    }

    public GraphqlUnionDescription buildUnionDescription() {
        return new GraphqlUnionDescriptionImpl(name(), description());
    }

    public TypesDescriptionBuilder name(String name) {
        this.name = name;
        return this;
    }

    public String name() {
        return name;
    }

    public TypesDescriptionBuilder description(String description) {
        this.description = description;
        return this;
    }

    public String description() {
        return description;
    }
}
