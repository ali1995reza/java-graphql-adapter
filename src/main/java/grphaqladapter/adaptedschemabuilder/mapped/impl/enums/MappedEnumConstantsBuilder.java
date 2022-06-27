package grphaqladapter.adaptedschemabuilder.mapped.impl.enums;

import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedEnumConstant;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedElementBuilder;

public class MappedEnumConstantsBuilder extends MappedElementBuilder<MappedEnumConstantsBuilder, MappedEnumConstant> {

    public static MappedEnumConstantsBuilder newBuilder() {
        return new MappedEnumConstantsBuilder();
    }

    public MappedEnumConstantsBuilder() {
        super(MappedElementType.ENUM_VALUE);
    }

    private Enum constant;

    @Override
    public MappedEnumConstantsBuilder copy(MappedEnumConstant element) {
        return super.copy(element)
                .constant(element.constant());
    }

    public MappedEnumConstantsBuilder constant(Enum constant) {
        this.constant = constant;
        return this;
    }

    public Enum constant() {
        return constant;
    }

    @Override
    public MappedEnumConstantsBuilder refresh() {
        this.constant = null;
        return super.refresh();
    }

    @Override
    public MappedEnumConstant build() {
        return new MappedEnumConstantImpl(
                name(),
                description(),
                constant()
        );
    }
}
