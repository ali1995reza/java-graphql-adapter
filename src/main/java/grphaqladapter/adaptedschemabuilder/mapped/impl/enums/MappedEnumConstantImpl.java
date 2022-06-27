package grphaqladapter.adaptedschemabuilder.mapped.impl.enums;

import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedEnumConstant;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedElementImpl;

final class MappedEnumConstantImpl extends MappedElementImpl implements MappedEnumConstant {

    private final Enum constant;

    MappedEnumConstantImpl(String name, String description, Enum constant) {
        super(name, MappedElementType.ENUM_VALUE, description);
        this.constant = constant;
    }

    @Override
    public Enum constant() {
        return constant;
    }
}
