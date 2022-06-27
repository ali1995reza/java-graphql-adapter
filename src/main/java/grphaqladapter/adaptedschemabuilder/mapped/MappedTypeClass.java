package grphaqladapter.adaptedschemabuilder.mapped;

import java.util.Map;

public interface MappedTypeClass extends MappedClass{

    Map<String, MappedFieldMethod> fieldMethods();
}
