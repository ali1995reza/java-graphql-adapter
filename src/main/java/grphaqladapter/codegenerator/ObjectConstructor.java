package grphaqladapter.codegenerator;

import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;

public interface ObjectConstructor {

    default Object getInstance(MappedClass mappedClass) {
        return getInstance(mappedClass.baseClass());
    }

    <T> T getInstance(Class<T> clazz);

}
