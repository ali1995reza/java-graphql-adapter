package grphaqladapter.adaptedschemabuilder.mapped;

import java.lang.reflect.Parameter;

public interface MappedParameter {

    String argumentName();

    boolean isEnv();

    boolean isNullable();

    Parameter parameter();

    boolean isList();

    int dimensions();

    Class type();
}
