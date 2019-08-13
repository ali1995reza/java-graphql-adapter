package grphaqladapter.adaptedschemabuilder.mapped.impl;

import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.mapped.MappedParameter;
import grphaqladapter.adaptedschemabuilder.validator.ArgumentValidator;

import java.lang.reflect.Parameter;
import java.util.List;

final class MappedParameterImpl implements MappedParameter {


    private final String argumentName;
    private final boolean nullable;
    private final Parameter parameter;
    private final int dimensions;
    private final Class type;
    private final boolean isList;




    MappedParameterImpl(String name, boolean nullable, Parameter parameter , Class type , int dims) {

        this.argumentName = name;
        this.nullable = nullable;
        this.parameter = parameter;
        this.type = type;
        dimensions = dims;
        isList = dimensions>0;

        ArgumentValidator.validate(this);
    }





    public String argumentName() {
        return argumentName;
    }

    public boolean isNullable() {
        return nullable;
    }

    public Parameter parameter() {
        return parameter;
    }

    public boolean isList()
    {
        return isList;
    }

    public int dimensions() {
        return dimensions;
    }

    public Class type()
    {
        return type;
    }

    @Override
    public String toString() {
        return "[parameter:"+parameter+" , argumentName:"+ argumentName +"]";
    }


    final static MappedParameter clone(MappedParameter parameter)
    {
        return new MappedParameterImpl(
                parameter.argumentName() ,
                parameter.isNullable() ,
                parameter.parameter() ,
                parameter.type() ,
                parameter.dimensions()
        );
    }
}
