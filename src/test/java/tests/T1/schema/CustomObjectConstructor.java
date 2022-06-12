package tests.T1.schema;

import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;
import grphaqladapter.codegenerator.ObjectConstructor;
import grphaqladapter.codegenerator.impl.ReflectionObjectConstructor;

public class CustomObjectConstructor implements ObjectConstructor {

    private final ObjectConstructor constructor = new ReflectionObjectConstructor();

    @Override
    public Object getInstance(MappedClass mappedClass) {
        if (mappedClass.baseClass() == InputUser.class) {
            return new InputUser();
        }
        return constructor.getInstance(mappedClass);
    }
}
