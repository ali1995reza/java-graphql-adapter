package grphaqladapter.adaptedschemabuilder.mapped;

import java.util.Map;

public interface MappedInputTypeClass extends MappedClass {

    Map<String, MappedInputFieldMethod> inputFiledMethods();
}
