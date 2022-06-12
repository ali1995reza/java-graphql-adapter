package grphaqladapter.codegenerator;

import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;

public interface ObjectConstructor {

    Object getInstance(MappedClass mappedClass);

}
