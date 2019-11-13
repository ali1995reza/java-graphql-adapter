package grphaqladapter.adaptedschemabuilder.mapper.strategy;


import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;

import java.lang.reflect.Method;

public interface MethodAnnotationDetector {

    FieldAnnotations detectAnnotationFor(Method method, Class clazz, MappedClass.MappedType mappedType);

}
