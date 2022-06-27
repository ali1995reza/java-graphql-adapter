package grphaqladapter.adaptedschemabuilder.mapped.impl.interfaces;

import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedUnionInterface;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedClassBuilder;

public class MappedUnionInterfaceBuilder extends MappedClassBuilder<MappedUnionInterfaceBuilder, MappedUnionInterface> {

    public static MappedUnionInterfaceBuilder newBuilder() {
        return new MappedUnionInterfaceBuilder();
    }

    public MappedUnionInterfaceBuilder() {
        super(MappedElementType.UNION);
    }

    @Override
    public MappedUnionInterface build() {
        return new MappedUnionInterfaceImpl(
                name(),
                description(),
                baseClass()
        );
    }
}
