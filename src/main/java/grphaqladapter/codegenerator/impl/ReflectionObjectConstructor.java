package grphaqladapter.codegenerator.impl;

import grphaqladapter.codegenerator.ObjectConstructor;

public class ReflectionObjectConstructor implements ObjectConstructor {

    @Override
    public <T> T getInstance(Class<T> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
