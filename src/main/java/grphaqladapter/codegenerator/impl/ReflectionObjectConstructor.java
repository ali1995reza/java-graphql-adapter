package grphaqladapter.codegenerator.impl;

import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;
import grphaqladapter.codegenerator.ObjectConstructor;

public class ReflectionObjectConstructor implements ObjectConstructor {
    @Override
    public Object getInstance(MappedClass mappedClass) {
        try {
            return mappedClass.baseClass().getConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
