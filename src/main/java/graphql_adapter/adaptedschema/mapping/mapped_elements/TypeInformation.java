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
import java.util.*;

import static graphql_adapter.adaptedschema.exceptions.SchemaExceptionBuilder.exception;

public final class TypeInformation<T> {

    private final static TypeInformation<DataFetchingEnvironment> ENVIRONMENT = new TypeInformation<>(DataFetchingEnvironment.class, booleanListOf(true), 0, null);
    private final static TypeInformation<GraphqlDirectivesHolder> DIRECTIVES = new TypeInformation<>(GraphqlDirectivesHolder.class, booleanListOf(true), 0, null);
    private final static TypeInformation<AdaptedGraphQLSchema> ADAPTED_SCHEMA = new TypeInformation<>(AdaptedGraphQLSchema.class, booleanListOf(true), 0, null);

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

    public static <T> TypeInformation<T> array(Class<T> clazz, Boolean... nullability) {
        return new TypeInformation<>(
                clazz,
                Arrays.asList(nullability),
                nullability.length - 1,
                DimensionModel.ARRAY
        );
    }

    public static <T> TypeInformation<T> list(Class<T> clazz, Boolean... nullability) {
        return new TypeInformation<>(
                clazz,
                Arrays.asList(nullability),
                nullability.length - 1,
                DimensionModel.LIST
        );
    }

    public static <T> TypeInformation<T> nonNullable(Class<T> clazz) {
        return single(clazz, false);
    }

    public static <T> TypeInformation<T> nullable(Class<T> clazz) {
        return single(clazz, true);
    }

    public static TypeInformation<?> of(TypeDetails typeDetails, List<Boolean> nullability) {
        return new TypeInformation<>(
                typeDetails.type(),
                nullability,
                typeDetails.dimensions(),
                typeDetails.dimensionModel()
        );
    }

    public static TypeInformation<?> of(Parameter parameter, List<Boolean> nullability) {
        TypeDetails details = MappingUtils.findTypeDetails(parameter);
        return of(details, nullability);
    }

    public static TypeInformation<?> of(Parameter parameter) {
        TypeDetails details = MappingUtils.findTypeDetails(parameter);
        return of(details, !details.type().isPrimitive() ? booleanListOf(true, details.dimensions() + 1) : booleanListOf(true, details.dimensions() + 1, false));
    }

    public static TypeInformation<?> of(Method method, List<Boolean> nullability) {
        TypeDetails details = MappingUtils.findTypeDetails(method);
        return of(details, nullability);
    }

    public static TypeInformation<?> of(Method method) {
        TypeDetails details = MappingUtils.findTypeDetails(method);
        return of(details, !details.type().isPrimitive() ? booleanListOf(true, details.dimensions() + 1) : booleanListOf(true, details.dimensions() + 1, false));
    }

    public static <T> TypeInformation<T> single(Class<T> clazz, boolean nullability) {
        return new TypeInformation<>(
                clazz,
                Collections.singletonList(nullability),
                0,
                DimensionModel.SINGLE
        );
    }
    private final List<Boolean> nullability;

    public TypeInformation(Class<T> type, List<Boolean> nullability, int dimensions, DimensionModel dimensionModel) {
        this.type = type;
        this.nullability = Collections.unmodifiableList(new ArrayList<>(nullability));
        this.dimensions = dimensions;
        this.dimensionModel = NullifyUtils.getOrDefault(dimensionModel, DimensionModel.SINGLE);
        Assert.isEquals(this.nullability.size(), this.dimensions + 1, new IllegalStateException("nullability list and dimensions has difference size [" + this + "]"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeInformation<?> that = (TypeInformation<?>) o;
        return dimensions == that.dimensions && Objects.equals(type, that.type) && Objects.equals(nullability, that.nullability) && dimensionModel == that.dimensionModel;
    }

    private final Class<T> type;

    @Override
    public int hashCode() {
        return Objects.hash(type, nullability, dimensions, dimensionModel);
    }
    private final int dimensions;
    private final DimensionModel dimensionModel;

    @Override
    public String toString() {
        return "TypeInformation{" +
                "type=" + type +
                ", nullability=" + nullability +
                ", dimensions=" + dimensions +
                ", dimensionModel=" + dimensionModel +
                '}';
    }

    public boolean canBeEqualsTo(TypeInformation<T> that) {
        if (this == that) return true;
        return dimensions == that.dimensions && Objects.equals(type, that.type) && (lastDimensionNullability() || !that.lastDimensionNullability()) && dimensionModel == that.dimensionModel;
    }

    public boolean lastDimensionNullability() {
        return nullability(dimensions);
    }

    public boolean nullability(int dimension) {
        return nullability.get(dimension);
    }

    public List<Boolean> nullability() {
        return nullability;
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

    private static List<Boolean> booleanListOf(boolean value, int size) {
        return booleanListOf(value, size, value);
    }

    private static List<Boolean> booleanListOf(boolean value, int size, boolean lastOne) {
        List<Boolean> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (i == size - 1) {
                list.add(lastOne);
            } else {
                list.add(value);
            }
        }
        return list;
    }

    private static List<Boolean> booleanListOf(boolean value) {
        return Collections.singletonList(value);
    }

    public Class<T> type() {
        return type;
    }
}
