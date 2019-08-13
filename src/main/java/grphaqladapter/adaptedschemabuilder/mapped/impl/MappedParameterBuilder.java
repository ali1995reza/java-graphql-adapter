package grphaqladapter.adaptedschemabuilder.mapped.impl;

import grphaqladapter.adaptedschemabuilder.mapped.MappedParameter;

import java.lang.reflect.Parameter;

public final class MappedParameterBuilder {

    public final static MappedParameterBuilder newBuilder()
    {
        return new MappedParameterBuilder();
    }


    public static MappedParameter clone(MappedParameter parameter)
    {
        return MappedParameterImpl.clone(parameter);
    }

    public static MappedParameter cloneIfNotImmutable(MappedParameter parameter)
    {
        if(parameter instanceof MappedParameterImpl)
            return parameter;

        return clone(parameter);
    }


    private Parameter parameter;
    private boolean nullable;
    private String argumentName;
    private Class type;
    private int dimensions = 0;


    private MappedParameterBuilder(){}


    public synchronized MappedParameterBuilder setType(Class type) {
        this.type = type;
        return this;
    }

    public synchronized MappedParameterBuilder setNullable(boolean nullable) {
        this.nullable = nullable;
        return this;
    }

    public synchronized MappedParameterBuilder setDimensions(int dimensions) {
        this.dimensions = dimensions;
        return this;
    }

    public synchronized MappedParameterBuilder setArgumentName(String argumentName) {
        this.argumentName = argumentName;
        return this;
    }

    public synchronized MappedParameterBuilder setParameter(Parameter parameter) {
        this.parameter = parameter;
        return this;
    }

    public synchronized MappedParameter build()
    {
        return new MappedParameterImpl(argumentName , nullable , parameter , type , dimensions);
    }
}
