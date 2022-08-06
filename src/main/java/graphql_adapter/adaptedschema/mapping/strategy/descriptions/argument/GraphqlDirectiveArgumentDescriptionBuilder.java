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
package graphql_adapter.adaptedschema.mapping.strategy.descriptions.argument;

import graphql_adapter.adaptedschema.functions.ValueParser;
import graphql_adapter.adaptedschema.mapping.mapped_elements.DimensionModel;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.DefaultValueContainerElementDescriptionBuilder;

public class GraphqlDirectiveArgumentDescriptionBuilder extends DefaultValueContainerElementDescriptionBuilder<GraphqlDirectiveArgumentDescriptionBuilder, GraphqlDirectiveArgumentDescription> {

    public static GraphqlDirectiveArgumentDescriptionBuilder newBuilder() {
        return new GraphqlDirectiveArgumentDescriptionBuilder();
    }

    private Class<?> type;
    private int dimensions = 0;
    private DimensionModel dimensionModel;
    private Class<? extends ValueParser<?, ?>> valueParser;

    public GraphqlDirectiveArgumentDescription build() {
        return new GraphqlDirectiveArgumentDescriptionImpl(
                name(),
                description(),
                nullability(),
                defaultValue(),
                dimensions(),
                dimensionModel(),
                type(),
                valueParser()
        );
    }

    @Override
    public GraphqlDirectiveArgumentDescriptionBuilder copy(GraphqlDirectiveArgumentDescription graphqlDirectiveArgumentDescription) {
        return super.copy(graphqlDirectiveArgumentDescription)
                .type(graphqlDirectiveArgumentDescription.type())
                .dimensions(graphqlDirectiveArgumentDescription.dimensions())
                .dimensionModel(graphqlDirectiveArgumentDescription.dimensionModel())
                .valueParser(graphqlDirectiveArgumentDescription.valueParser());
    }

    public GraphqlDirectiveArgumentDescriptionBuilder dimensionModel(DimensionModel dimensionModel) {
        this.dimensionModel = dimensionModel;
        return this;
    }

    public DimensionModel dimensionModel() {
        return dimensionModel;
    }

    public GraphqlDirectiveArgumentDescriptionBuilder dimensions(int dimensions) {
        this.dimensions = dimensions;
        return this;
    }

    public int dimensions() {
        return dimensions;
    }

    public GraphqlDirectiveArgumentDescriptionBuilder type(Class<?> type) {
        this.type = type;
        return this;
    }

    public Class<?> type() {
        return type;
    }

    public GraphqlDirectiveArgumentDescriptionBuilder valueParser(Class<? extends ValueParser<?, ?>> valueParser) {
        this.valueParser = valueParser;
        return this;
    }

    public Class<? extends ValueParser<?, ?>> valueParser() {
        return valueParser;
    }
}
