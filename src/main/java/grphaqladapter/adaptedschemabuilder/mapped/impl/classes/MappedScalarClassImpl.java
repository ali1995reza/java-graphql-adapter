package grphaqladapter.adaptedschemabuilder.mapped.impl.classes;

import graphql.schema.Coercing;
import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedClassImpl;

public class MappedScalarClassImpl extends MappedClassImpl implements grphaqladapter.adaptedschemabuilder.mapped.MappedScalarClass {

    private final Coercing<?, ?> coercing;

    MappedScalarClassImpl(String name, String description, Class baseClass, Coercing<?, ?> coercing) {
        super(name, MappedElementType.SCALAR, description, baseClass);
        this.coercing = coercing;
    }

    @Override
    public Coercing<?, ?> coercing() {
        return this.coercing;
    }
}
