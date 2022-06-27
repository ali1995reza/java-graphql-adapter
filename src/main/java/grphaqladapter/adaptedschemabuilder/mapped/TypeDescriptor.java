package grphaqladapter.adaptedschemabuilder.mapped;

import graphql.schema.DataFetchingEnvironment;
import grphaqladapter.adaptedschemabuilder.AdaptedGraphQLSchema;
import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.exceptions.MappingGraphqlArgumentException;
import grphaqladapter.adaptedschemabuilder.mapper.MappingStatics;
import grphaqladapter.annotations.interfaces.GraphqlDirectivesHolder;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

import static grphaqladapter.adaptedschemabuilder.exceptions.SchemaExceptionBuilder.exception;

public final class TypeDescriptor<T> {

    private final static TypeDescriptor ENVIRONMENT = new TypeDescriptor(DataFetchingEnvironment.class, true, 0, null);
    private final static TypeDescriptor DIRECTIVES = new TypeDescriptor(GraphqlDirectivesHolder.class, true, 0, null);
    private final static TypeDescriptor ADAPTED_SCHEMA = new TypeDescriptor(AdaptedGraphQLSchema.class, true, 0, null);

    public static TypeDescriptor adaptedSchema(Parameter parameter) {
        TypeDescriptor typeDescriptor = TypeDescriptor.of(parameter);
        Assert.isEquals(typeDescriptor, ADAPTED_SCHEMA, exception(MappingGraphqlArgumentException.class, "can not map parameter to ADAPTED_SCHEMA argument model", null, null, parameter));
        return ADAPTED_SCHEMA;
    }

    public static TypeDescriptor environment(Parameter parameter) {
        TypeDescriptor typeDescriptor = TypeDescriptor.of(parameter);
        Assert.isEquals(typeDescriptor, ENVIRONMENT, exception(MappingGraphqlArgumentException.class, "can not map parameter to DATA_FETCHING_ENVIRONMENT argument model", null, null, parameter));
        return ENVIRONMENT;
    }

    public static TypeDescriptor directives(Parameter parameter) {
        TypeDescriptor typeDescriptor = TypeDescriptor.of(parameter);
        Assert.isEquals(typeDescriptor, DIRECTIVES, exception(MappingGraphqlArgumentException.class, "can not map parameter to DIRECTIVES argument model", null, null, parameter));
        return DIRECTIVES;
    }

    public static TypeDescriptor of(MappingStatics.TypeDetails typeDetails, boolean nullable) {
        return new TypeDescriptor(
                typeDetails.type(),
                nullable,
                typeDetails.dimension(),
                typeDetails.dimension() > 0 ? (typeDetails.isArray() ? TypeDescriptor.DimensionModel.ARRAY :
                        TypeDescriptor.DimensionModel.LIST) : null
        );
    }

    public static TypeDescriptor of(Parameter parameter, boolean nullable) {
        MappingStatics.TypeDetails details = MappingStatics.findTypeDetails(parameter);
        return of(details, nullable);
    }

    public static TypeDescriptor of(Parameter parameter) {
        MappingStatics.TypeDetails details = MappingStatics.findTypeDetails(parameter);
        return of(details, details.dimension() > 0 || !details.type().isPrimitive());
    }

    public static TypeDescriptor of(Method method, boolean nullable) {
        MappingStatics.TypeDetails details = MappingStatics.findTypeDetails(method);
        return of(details, nullable);
    }

    public enum DimensionModel {
        ARRAY, LIST;

        public boolean is(DimensionModel other) {
            return this == other;
        }

        public boolean isArray() {
            return is(ARRAY);
        }

        public boolean isList() {
            return is(LIST);
        }
    }


    private final Class<T> type;
    private final boolean nullable;
    private final int dimensions;
    private final DimensionModel dimensionModel;

    public TypeDescriptor(Class<T> type, boolean nullable, int dimensions, DimensionModel dimensionModel) {
        this.type = type;
        this.nullable = nullable;
        this.dimensions = dimensions;
        this.dimensionModel = dimensionModel;
    }


    public Class<T> type() {
        return type;
    }


    public boolean isNullable() {
        return nullable;
    }


    public int dimensions() {
        return dimensions;
    }

    public DimensionModel dimensionModel() {
        return dimensionModel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeDescriptor that = (TypeDescriptor) o;
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
}
