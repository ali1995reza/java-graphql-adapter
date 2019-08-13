package grphaqladapter.adaptedschemabuilder.mapped.impl;

import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;
import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;
import grphaqladapter.adaptedschemabuilder.utils.Utils;
import grphaqladapter.adaptedschemabuilder.validator.TypeValidator;

import java.util.*;

final class MappedClassImpl implements MappedClass {





    private final Class baseClass;
    private final String typeName;
    private final MappedType mappedType;
    private final Map<String , MappedMethod> mappedMethods;

    public MappedClassImpl(Class c, String n , MappedType t , Map<String , MappedMethod> m)
    {
        baseClass = c;
        typeName = n;
        mappedType = t;
        mappedMethods = Utils.nullifyOrGetDefault(m , Collections.EMPTY_MAP);
        TypeValidator.validate(this);

    }



    @Override
    public Class baseClass() {
        return baseClass;
    }

    @Override
    public MappedType mappedType() {
        return mappedType;
    }

    @Override
    public String typeName() {
        return typeName;
    }

    @Override
    public Map<String , MappedMethod> mappedMethods() {
        return mappedMethods;
    }


    @Override
    public String toString() {
        return "[class:"+ baseClass +" , mappedType-argumentName:"+typeName+" , mappedType:"+ mappedType +" , mapped-methods:"+mappedMethods+"]";
    }


    final static MappedClass clone(MappedClass mappedClass)
    {
        return new MappedClassImpl(
                mappedClass.baseClass() ,
                mappedClass.typeName() ,
                mappedClass.mappedType() ,
                mappedClass.mappedMethods()==null?null:Collections.unmodifiableMap(Utils.copy(mappedClass.mappedMethods()))

        );
    }
}
