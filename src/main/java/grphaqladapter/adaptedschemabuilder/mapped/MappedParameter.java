package grphaqladapter.adaptedschemabuilder.mapped;

import java.lang.reflect.Parameter;

public interface MappedParameter {



    public String argumentName();

    public boolean isNullable();

    public Parameter parameter();

    public boolean isList();

    public int dimensions();

    public Class type();
}
