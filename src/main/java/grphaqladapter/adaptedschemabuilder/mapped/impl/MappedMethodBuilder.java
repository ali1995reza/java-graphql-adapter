package grphaqladapter.adaptedschemabuilder.mapped.impl;

import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedParameter;

import java.lang.reflect.Method;
import java.util.*;

public final class MappedMethodBuilder {

    public final static MappedMethodBuilder newBuilder()
    {
        return new MappedMethodBuilder();
    }

    public final static MappedMethod clone(MappedMethod method)
    {
        return MappedMethodImpl.clone(method);
    }

    public final static MappedMethod cloneIfNotImmutable(grphaqladapter.adaptedschemabuilder.mapped.MappedMethod method)
    {
        if(method instanceof MappedMethodImpl)
            return method;

        return clone(method);
    }


    private final Map<String , MappedParameter> mappedParameters = new HashMap<>();
    private String fieldName;
    private Method method;
    private boolean nullable;
    private Class type;
    private int dimensions;
    private Method setter;
    private boolean isQueryHandler;




    private MappedMethodBuilder(){}


    public synchronized MappedMethodBuilder setDimensions(int dimensions) {
        this.dimensions = dimensions;
        return this;
    }

    public synchronized MappedMethodBuilder setFieldName(String fieldName) {
        this.fieldName = fieldName;
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

    public synchronized MappedMethodBuilder addMappedParameter(MappedParameter mappedParameter)
    {
        Assert.ifConditionTrue("a parameter with argument typeName ["+mappedParameter.argumentName()
                +"] already exist - [exist:"+mappedParameters.get(mappedParameter.argumentName())+"]",
                mappedParameters.containsKey(mappedParameter.argumentName()));
        this.mappedParameters.put(mappedParameter.argumentName() , mappedParameter);
        return this;
    }

    public synchronized MappedMethodBuilder clearMappedParameters(){
        this.mappedParameters.clear();
        return this;
    }

    public synchronized MappedMethodBuilder removeMappedParameter(MappedParameter parameter)
    {
        this.mappedParameters.remove(parameter.argumentName() , parameter);
        return this;
    }

    public synchronized MappedMethodBuilder setQueryHandler(boolean queryHandler) {
        isQueryHandler = queryHandler;
        return this;
    }

    private final List<MappedParameter> getParametersList()
    {
        if(mappedParameters.size()==0)
            return Collections.EMPTY_LIST;

        List<MappedParameter> parameters = new ArrayList<>();

        for(MappedParameter parameter:mappedParameters.values())
        {
            parameters.add(parameter);
        }

        return Collections.unmodifiableList(parameters);
    }

    public synchronized MappedMethod build()
    {
        return new MappedMethodImpl(fieldName , method , nullable , getParametersList() , type , dimensions , setter, isQueryHandler);
    }
}
