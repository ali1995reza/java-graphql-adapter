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

public class GraphqlScalarDescriptionBuilder extends TypesDescriptionBuilder<GraphqlScalarDescriptionBuilder, GraphqlScalarDescription> {

    public static GraphqlScalarDescriptionBuilder newBuilder() {
        return new GraphqlScalarDescriptionBuilder();
    }


    private Class<? extends Coercing> coercing;

    GraphqlScalarDescriptionBuilder() {
        super(null);
    }

    @Override
    public GraphqlScalarDescriptionBuilder refresh() {
        this.coercing = null;
        super.refresh();
        return this;
    }

    @Override
    public GraphqlScalarDescriptionBuilder copy(GraphqlScalarDescription graphqlScalarDescription) {
        super.copy(graphqlScalarDescription);
        coercing(graphqlScalarDescription.coercing());
        return this;
    }

    @Override
    public GraphqlScalarDescription build() {
        return new GraphqlScalarDescriptionImpl(name(), description(), coercing());
    }

    public GraphqlScalarDescriptionBuilder coercing(Class<? extends Coercing> coercing) {
        this.coercing = coercing;
        return this;
    }

    public Class<? extends Coercing> coercing() {
        return coercing;
    }
}
