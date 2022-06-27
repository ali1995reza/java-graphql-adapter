package grphaqladapter.adaptedschemabuilder.mapped.impl.classes;

import graphql.schema.Coercing;
import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedScalarClass;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedClassBuilder;

public class MappedScalarClassBuilder extends MappedClassBuilder<MappedScalarClassBuilder, MappedScalarClass> {

    public static MappedScalarClassBuilder newBuilder() {
        return new MappedScalarClassBuilder();
    }

    private Coercing<?, ?> coercing;

    MappedScalarClassBuilder() {
        super(MappedElementType.SCALAR);
    }

    public MappedScalarClassBuilder coercing(Coercing<?, ?> coercing) {
        this.coercing = coercing;
        return this;
    }

    public Coercing<?, ?> coercing() {
        return coercing;
    }

    @Override
    public MappedScalarClass build() {
        return new MappedScalarClassImpl(
                name(),
                description(),
                baseClass(),
                coercing());
    }
}
