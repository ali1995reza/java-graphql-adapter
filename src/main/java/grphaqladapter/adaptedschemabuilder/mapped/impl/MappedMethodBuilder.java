package grphaqladapter.adaptedschemabuilder.mapped.impl;

import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedParameter;
import grphaqladapter.adaptedschemabuilder.utils.Utils;
import grphaqladapter.annotations.GraphqlDescriptionAnnotation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public final class MappedMethodBuilder {


    private final List<MappedParameter> mappedParameters;
    private String fieldName;
    private String description;
    private Method method;
    private boolean nullable;
    private Class type;
    private int dimensions;
    private Method setter;

    private MappedMethodBuilder() {
        mappedParameters = new ArrayList<>();
    }

    public static MappedMethodBuilder newBuilder() {
        return new MappedMethodBuilder();
    }

    public static MappedMethod clone(MappedMethod method) {
        return MappedMethodImpl.clone(method);
    }

    public static MappedMethod cloneIfNotImmutable(grphaqladapter.adaptedschemabuilder.mapped.MappedMethod method) {
        if (method instanceof MappedMethodImpl)
            return method;

        return clone(method);
    }

    public synchronized MappedMethodBuilder setDimensions(int dimensions) {
        this.dimensions = dimensions;
        return this;
    }

    public synchronized MappedMethodBuilder setFieldName(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    public synchronized MappedMethodBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public synchronized MappedMethodBuilder setDescriptionFrom(GraphqlDescriptionAnnotation description) {
        if (description != null) {
            this.description = description.value();
        }
        return this;
    }

    public synchronized MappedMethodBuilder setType(Class type) {
        this.type = type;
        return this;
    }

    public synchronized MappedMethodBuilder setSetter(Method setter) {
        this.setter = setter;
        return this;
    }

    public synchronized MappedMethodBuilder setNullable(boolean nullable) {
        this.nullable = nullable;
        return this;
    }

    public synchronized MappedMethodBuilder setMethod(Method method) {
        this.method = method;
        return this;
    }

    public synchronized MappedMethodBuilder addMappedParameter(MappedParameter mappedParameter) {
        Assert.isOneFalse("a parameter with argument typeName [" + mappedParameter.argumentName()
                        + "] already exist",
                mappedParameters.stream().anyMatch(ParameterNamePredict.of(mappedParameter)));
        this.mappedParameters.add(mappedParameter);
        return this;
    }

    public synchronized MappedMethodBuilder clearMappedParameters() {
        this.mappedParameters.clear();
        return this;
    }

    public synchronized MappedMethodBuilder removeMappedParameter(MappedParameter parameter) {
        this.mappedParameters.remove(parameter);
        return this;
    }

    public synchronized MappedMethodBuilder refresh() {
        nullable = false;
        setter = null;
        fieldName = null;
        type = null;
        dimensions = 0;
        mappedParameters.clear();
        method = null;
        description = null;
        return this;
    }

    private final List<MappedParameter> getParametersList() {
        if (mappedParameters.size() == 0)
            return Collections.EMPTY_LIST;

        return Collections.unmodifiableList(Utils.copy(mappedParameters));
    }

    public synchronized MappedMethod build() {
        return new MappedMethodImpl(fieldName, description, method, nullable, getParametersList(), type, dimensions, setter);
    }

    private static final class ParameterNamePredict implements Predicate<MappedParameter> {

        private final String name;


        private ParameterNamePredict(String name) {
            this.name = name;
        }

        public static ParameterNamePredict of(String name) {
            return new ParameterNamePredict(name);
        }

        public static ParameterNamePredict of(MappedParameter parameter) {
            return of(parameter.argumentName());
        }

        @Override
        public boolean test(MappedParameter parameter) {
            return parameter.argumentName().equals(name);
        }
    }
}
