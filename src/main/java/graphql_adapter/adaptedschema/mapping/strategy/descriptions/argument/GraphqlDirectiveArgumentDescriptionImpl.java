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
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.DefaultValueDetails;
import graphql_adapter.adaptedschema.mapping.strategy.descriptions.GraphqlDefaultValueContainerElementDescriptionImpl;
import graphql_adapter.adaptedschema.utils.NullifyUtils;

public class GraphqlDirectiveArgumentDescriptionImpl extends GraphqlDefaultValueContainerElementDescriptionImpl implements GraphqlDirectiveArgumentDescription {

    private final int dimensions;
    private final DimensionModel dimensionModel;
    private final Class<?> type;
    private final Class<? extends ValueParser<?, ?>> valueParser;

    public GraphqlDirectiveArgumentDescriptionImpl(String name, String description, boolean nullable, DefaultValueDetails defaultValue, int dimensions, DimensionModel dimensionModel, Class<?> type, Class<? extends ValueParser<?, ?>> valueParser) {
        super(name, description, nullable, defaultValue);
        this.dimensions = dimensions;
        this.dimensionModel = NullifyUtils.getOrDefault(dimensionModel, DimensionModel.SINGLE);
        this.type = type;
        this.valueParser = valueParser;
    }

    @Override
    public DimensionModel dimensionModel() {
        return dimensionModel;
    }

    @Override
    public int dimensions() {
        return dimensions;
    }

    @Override
    public Class<?> type() {
        return type;
    }

    @Override
    public Class<? extends ValueParser<?, ?>> valueParser() {
        return valueParser;
    }
}
