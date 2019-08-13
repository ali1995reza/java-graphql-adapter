package grphaqladapter.adaptedschemabuilder.mapper.strategy;


import java.lang.reflect.Method;

public interface MethodAnnotationDetector {

    FieldAnnotations detectAnnotationFor(Method method);

}
