package tests.T1.schema;

import grphaqladapter.codegenerator.ObjectConstructor;
import grphaqladapter.codegenerator.impl.ReflectionObjectConstructor;

public class CustomObjectConstructor implements ObjectConstructor {

    private final ObjectConstructor constructor = new ReflectionObjectConstructor();

    @Override
    public Object getInstance(Class clazz) {
        if (clazz == InputUser.class) {
            return new InputUser();
        }
        return constructor.getInstance(clazz);
    }
}
