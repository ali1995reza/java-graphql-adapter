package grphaqladapter.adaptedschemabuilder.mapped;

import java.lang.reflect.Method;
import java.util.List;

public interface MappedMethod {

    Method method();

    String fieldName();

    String description();

    boolean isInputField();

    Method setter();

    boolean isNullable();

    boolean isList();

    int dimensions();

    Class type();

    List<MappedParameter> parameters();
}
