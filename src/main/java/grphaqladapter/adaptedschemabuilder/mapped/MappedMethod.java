package grphaqladapter.adaptedschemabuilder.mapped;

import java.lang.reflect.Method;

public interface MappedMethod extends MappedElement {

    Method method();

    TypeDescriptor type();

}
