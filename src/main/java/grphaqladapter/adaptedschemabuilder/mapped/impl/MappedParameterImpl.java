package grphaqladapter.adaptedschemabuilder.mapped.impl;

import grphaqladapter.adaptedschemabuilder.mapped.MappedParameter;

import java.lang.reflect.Parameter;

final class MappedParameterImpl implements MappedParameter {


    private final String argumentName;
    private final boolean isEnv;
    private final boolean nullable;
    private final Parameter parameter;
    private final int dimensions;
    private final Class type;
    private final boolean isList;


    MappedParameterImpl(String name, boolean isEnv, boolean nullable, Parameter parameter, Class type, int dims) {

        this.argumentName = name;
        this.isEnv = isEnv;
        this.nullable = nullable;
        this.parameter = parameter;
        this.type = type;
        dimensions = dims;
        isList = dimensions > 0;

    }

    static MappedParameter clone(MappedParameter parameter) {
        return new MappedParameterImpl(
                parameter.argumentName(),
                parameter.isEnv(),
                parameter.isNullable(),
                parameter.parameter(),
                parameter.type(),
                parameter.dimensions()
        );
    }

    public String argumentName() {
        return argumentName;
    }

    @Override
    public boolean isEnv() {
        return isEnv;
    }

    public boolean isNullable() {
        return nullable;
    }

    public Parameter parameter() {
        return parameter;
    }

    public boolean isList() {
        return isList;
    }

    public int dimensions() {
        return dimensions;
    }

    public Class type() {
        return type;
    }

    @Override
    public String toString() {
        return "[parameter:" + parameter + " , argumentName:" + argumentName + "]";
    }
}
