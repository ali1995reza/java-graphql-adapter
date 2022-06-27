package grphaqladapter.adaptedschemabuilder.mapped.impl.enums;

import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedEnum;
import grphaqladapter.adaptedschemabuilder.mapped.MappedEnumConstant;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedClassImpl;

import java.util.Map;

final class MappedEnumImpl extends MappedClassImpl implements MappedEnum {

    private final Map<String, MappedEnumConstant> constants;

    MappedEnumImpl(String name, String description, Class<? extends Enum> baseClass, Map<String, MappedEnumConstant> constants) {
        super(name, MappedElementType.ENUM, description, baseClass);
        this.constants = constants;
    }

    @Override
    public Map<String, MappedEnumConstant> constants() {
        return constants;
    }
}
