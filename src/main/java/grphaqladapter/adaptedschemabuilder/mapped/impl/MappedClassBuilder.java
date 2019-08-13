package grphaqladapter.adaptedschemabuilder.mapped.impl;

import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;
import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;
import grphaqladapter.adaptedschemabuilder.utils.Utils;


import java.util.*;

public final class MappedClassBuilder {

    public final static MappedClassBuilder newBuilder()
    {
        return new MappedClassBuilder();
    }

    public final static MappedClass clone(MappedClass mappedClass)
    {
        return MappedClassImpl.clone(mappedClass);
    }

    public final static MappedClass cloneIfNotImmutable(MappedClass mappedClass)
    {
        if(mappedClass instanceof MappedClassImpl)
            return mappedClass;

        return clone(mappedClass);
    }

    private final Map<String , MappedMethod> mappedMethods = new HashMap<>();
    private Class baseClass;
    private MappedClass.MappedType mappedType;
    private String typeName;


    private MappedClassBuilder(){}

    public synchronized MappedClassBuilder setBaseClass(Class baseClass) {
        this.baseClass = baseClass;
        return this;
    }

    public synchronized MappedClassBuilder setMappedType(MappedClass.MappedType mappedType) {
        this.mappedType = mappedType;
        return this;
    }
    public synchronized MappedClassBuilder addMappedMethod(MappedMethod method)
    {
        Assert.ifConditionTrue("already a method with field typeName ["+method.fieldName()+
                        "] exist - [exist:"+mappedMethods.get(method.fieldName())+"]",
                mappedMethods.containsKey(method.fieldName()));

        mappedMethods.put(method.fieldName() , method);
        return this;
    }

    public synchronized MappedClassBuilder removeMappedMethod(MappedMethod method)
    {
        mappedMethods.remove(method.fieldName() , method);
        return this;
    }

    public synchronized MappedClassBuilder clearMappedMethods()
    {
        mappedMethods.clear();
        return this;
    }

    public synchronized MappedClassBuilder setTypeName(String typeName) {
        this.typeName = typeName;
        return this;
    }


    private final Map<String , MappedMethod> getMappedMethods()
    {
        if(mappedMethods.size()==0)
            return Collections.EMPTY_MAP;


        return Collections.unmodifiableMap(Utils.copy(mappedMethods));
    }


    public synchronized MappedClass build()
    {
        //so validate and build
        return new MappedClassImpl(
                baseClass ,
                typeName ,
                mappedType ,
                getMappedMethods()
        );
    }


}
