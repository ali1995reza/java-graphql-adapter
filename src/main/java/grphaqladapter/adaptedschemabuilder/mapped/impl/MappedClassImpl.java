package grphaqladapter.adaptedschemabuilder.mapped.impl;

import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;
import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;

public abstract class MappedClassImpl extends MappedElementImpl implements MappedClass {

    private final String name;
    private final Class baseClass;
    private final String description;

    protected MappedClassImpl(String name, MappedElementType mappedType, String description, Class baseClass) {
        super(name, mappedType, description);
        this.name = name;
        this.baseClass = baseClass;
        this.description = description;
    }

    @Override
    public Class baseClass() {
        return baseClass;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public String name() {
        return name;
    }
}
