package grphaqladapter.adaptedschemabuilder.mapped;

import java.util.Map;

public interface MappedEnum extends MappedClass {

    Map<String, MappedEnumConstant> constants();

    @Override
    Class<? extends Enum> baseClass();
}
