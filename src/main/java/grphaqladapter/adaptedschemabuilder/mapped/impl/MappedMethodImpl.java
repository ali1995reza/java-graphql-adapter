package grphaqladapter.adaptedschemabuilder.mapped.impl;

import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedParameter;
import grphaqladapter.adaptedschemabuilder.utils.Utils;
import grphaqladapter.adaptedschemabuilder.validator.FieldValidator;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;


final class MappedMethodImpl implements MappedMethod {

    private final String fieldName;
    private final Method method;
    private final boolean nullable;
    private final List<MappedParameter> parameters;
    private final int dimensions;
    private final Class type;
    private final boolean isList;
    private final Method setter;
    private final boolean isInputField;
    private final boolean isQueryHandler;


    public MappedMethodImpl(String f, Method m, boolean n, List<MappedParameter> p, Class t, int dims, Method setter, boolean isQueryHandler)
    {
        fieldName  = f;
        method = m;
        nullable = n;
        this.parameters = Utils.nullifyOrGetDefault(p , Collections.EMPTY_LIST);
        type = t;
        dimensions = dims;
        this.setter = setter;
        this.isQueryHandler = isQueryHandler;
        isList = dimensions>0;
        isInputField = setter!=null;

        FieldValidator.validate(this);
    }



    public Method method() {
        return method;
    }

    public String fieldName() {
        return fieldName;
    }

    public boolean isInputField(){ return isInputField;}

    public Method setter(){return setter;}

    public boolean isNullable() {
        return nullable;
    }

    public boolean isList()
    {
        return isList;
    }

    public int dimensions() {
        return dimensions;
    }

    @Override
    public boolean isQueryHandler() {
        return isQueryHandler;
    }

    public Class type() {
        return type;
    }

    public List<MappedParameter>  parameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "[method:"+method+" , field-argumentName:"+fieldName+" , parameters:"+parameters+"]";
    }


    final static grphaqladapter.adaptedschemabuilder.mapped.MappedMethod clone(grphaqladapter.adaptedschemabuilder.mapped.MappedMethod method)
    {
        Assert.ifConditionTrue("an input mapped method must contains setter method" , method.isInputField()
                , method.setter()==null);

        return new MappedMethodImpl(
                method.fieldName() ,
                method.method() ,
                method.isNullable() ,
                method.parameters()==null?null:Collections.unmodifiableList(Utils.copy(method.parameters())),
                method.type() ,
                method.dimensions() ,
                method.setter(),
                method.isQueryHandler());
    }
}
