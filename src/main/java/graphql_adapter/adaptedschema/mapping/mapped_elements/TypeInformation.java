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
package graphql_adapter.adaptedschema.mapping.mapped_elements;

import graphql.schema.DataFetchingEnvironment;
import graphql_adapter.adaptedschema.AdaptedGraphQLSchema;
import graphql_adapter.adaptedschema.assertion.Assert;
import graphql_adapter.adaptedschema.exceptions.MappingGraphqlArgumentException;
import graphql_adapter.adaptedschema.mapping.mapper.utils.MappingUtils;
import graphql_adapter.adaptedschema.mapping.mapper.utils.TypeDetails;
import graphql_adapter.adaptedschema.system_objects.directive.GraphqlDirectivesHolder;
import graphql_adapter.adaptedschema.utils.NullifyUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

import static graphql_adapter.adaptedschema.exceptions.SchemaExceptionBuilder.exception;
import static graphql_adapter.adaptedschema.utils.ClassUtils.cast;

public final class TypeInformation<T> {

    private final static TypeInformation<DataFetchingEnvironment> ENVIRONMENT = new TypeInformation<>(DataFetchingEnvironment.class, true, 0, null);
    private final static TypeInformation<GraphqlDirectivesHolder> DIRECTIVES = new TypeInformation<>(GraphqlDirectivesHolder.class, true, 0, null);
    private final static TypeInformation<AdaptedGraphQLSchema> ADAPTED_SCHEMA = new TypeInformation<>(AdaptedGraphQLSchema.class, true, 0, null);

    public static TypeInformation<AdaptedGraphQLSchema> adaptedSchema(Parameter parameter) {
        TypeInformation<?> typeInformation = TypeInformation.of(parameter);
        Assert.isEquals(typeInformation, ADAPTED_SCHEMA, exception(MappingGraphqlArgumentException.class, "can not map parameter to ADAPTED_SCHEMA argument model", null, null, parameter));
        return ADAPTED_SCHEMA;
    }

    public static TypeInformation<GraphqlDirectivesHolder> directives(Parameter parameter) {
        TypeInformation<?> typeInformation = TypeInformation.of(parameter);
        Assert.isEquals(typeInformation, DIRECTIVES, exception(MappingGraphqlArgumentException.class, "can not map parameter to DIRECTIVES argument model", null, null, parameter));
        return DIRECTIVES;
    }

    public static TypeInformation<DataFetchingEnvironment> environment(Parameter parameter) {
        TypeInformation<?> typeInformation = TypeInformation.of(parameter);
        Assert.isEquals(typeInformation, ENVIRONMENT, exception(MappingGraphqlArgumentException.class, "can not map parameter to DATA_FETCHING_ENVIRONMENT argument model", null, null, parameter));
        return ENVIRONMENT;
    }

    public static <T> TypeInformation<T> nonNullable(Class<T> clazz) {
        return new TypeInformation<>(clazz, false, 0, DimensionModel.SINGLE);
    }

    public static <T> TypeInformation<T> nonNullableArray(Class<T> clazz, int dimensions) {
        return new TypeInformation<>(clazz, false, dimensions, DimensionModel.ARRAY);
    }

    public static <T> TypeInformation<T> nonNullableArray(Class<T> clazz) {
        return nonNullableArray(clazz, 1);
    }

    public static <T> TypeInformation<T> nonNullableList(Class<T> clazz, int dimensions) {
        return new TypeInformation<>(clazz, false, dimensions, DimensionModel.LIST);
    }

    public static <T> TypeInformation<T> nonNullableList(Class<T> clazz) {
        return nonNullableList(clazz, 1);
    }

    public static <T> TypeInformation<T> nullable(Class<T> clazz) {
        return new TypeInformation<>(clazz, true, 0, DimensionModel.SINGLE);
    }

    public static <T> TypeInformation<T> nullableArray(Class<T> clazz, int dimensions) {
        return new TypeInformation<>(clazz, true, dimensions, DimensionModel.ARRAY);
    }

    public static <T> TypeInformation<T> nullableArray(Class<T> clazz) {
        return nullableArray(clazz, 1);
    }

    public static <T> TypeInformation<T> nullableList(Class<T> clazz, int dimensions) {
        return new TypeInformation<>(clazz, true, dimensions, DimensionModel.LIST);
    }

    public static <T> TypeInformation<T> nullableList(Class<T> clazz) {
        return nullableList(clazz, 1);
    }

    public static TypeInformation<?> of(TypeDetails typeDetails, boolean nullable) {
        return new TypeInformation<>(
                typeDetails.type(),
                nullable,
                typeDetails.dimensions(),
                typeDetails.dimensionModel()
        );
    }

    public static TypeInformation<?> of(Parameter parameter, boolean nullable) {
        TypeDetails details = MappingUtils.findTypeDetails(parameter);
        return of(details, nullable);
    }

    public static TypeInformation<?> of(Parameter parameter) {
        TypeDetails details = MappingUtils.findTypeDetails(parameter);
        return of(details, details.dimensions() > 0 || !details.type().isPrimitive());
    }

    public static TypeInformation<?> of(Method method, boolean nullable) {
        TypeDetails details = MappingUtils.findTypeDetails(method);
        return of(details, nullable);
    }

    public static TypeInformation<?> of(Method method) {
        return of(method, !method.getReturnType().isPrimitive());
    }

    public static <T> TypeInformation<T> toNonNullable(TypeInformation<T> nullable) {
        return new TypeInformation<>(nullable.type, false, nullable.dimensions, nullable.dimensionModel);
    }

    public static <T> TypeInformation<T> toNullable(TypeInformation<T> nonNullable) {
        return new TypeInformation<>(nonNullable.type, true, nonNullable.dimensions, nonNullable.dimensionModel);
    }

    private final Class<T> type;
    private final boolean nullable;
    private final int dimensions;
    private final DimensionModel dimensionModel;

    public TypeInformation(Class<T> type, boolean nullable, int dimensions, DimensionModel dimensionModel) {
        this.type = type;
        this.nullable = nullable;
        this.dimensions = dimensions;
        this.dimensionModel = NullifyUtils.getOrDefault(dimensionModel, DimensionModel.SINGLE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeInformation<?> that = cast(o);
        return nullable == that.nullable && dimensions == that.dimensions && Objects.equals(type, that.type) && dimensionModel == that.dimensionModel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, nullable, dimensions, dimensionModel);
    }

    @Override
    public String toString() {
        return "TypeDescriptor{" +
                "type=" + type +
                ", nullable=" + nullable +
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

    public boolean hasDimensions() {
        return dimensions > 0;
    }

    public boolean isNullable() {
        return nullable;
    }

    public Class<T> type() {
        return type;
    }
}
