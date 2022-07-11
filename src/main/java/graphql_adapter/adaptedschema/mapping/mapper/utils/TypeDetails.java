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
package graphql_adapter.adaptedschema.mapping.mapper.utils;

import graphql_adapter.adaptedschema.assertion.Assert;
import graphql_adapter.adaptedschema.mapping.mapped_elements.DimensionModel;

public final class TypeDetails {

    private final Class<?> type;
    private final int dimensions;
    private final DimensionModel dimensionModel;

    TypeDetails(Class<?> type, int dimensions, DimensionModel dimensionModel) {
        this.dimensionModel = dimensionModel;
        Assert.isNotNegative(dimensions, new IllegalStateException("an array dimension can not be <0"));
        Assert.isNotNull(type, new NullPointerException("provided type is null"));
        this.type = type;
        this.dimensions = dimensions;
    }

    @Override
    public String toString() {
        return "TypeDetails{" +
                "type=" + type +
                ", dimensions=" + dimensions +
                ", dimensionModel=" + dimensionModel +
                '}';
    }

    public DimensionModel dimensionModel() {
        return dimensionModel;
    }

    public int dimensions() {
        return dimensions;
    }

    public Class<?> type() {
        return type;
    }
}
