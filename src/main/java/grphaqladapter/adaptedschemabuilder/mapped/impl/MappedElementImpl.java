package grphaqladapter.adaptedschemabuilder.mapped.impl;

import grphaqladapter.adaptedschemabuilder.mapped.MappedElement;
import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;

public class MappedElementImpl implements MappedElement {

    private final String name;
    private final MappedElementType mappedType;
    private final String description;

    public MappedElementImpl(String name, MappedElementType mappedType, String description) {
        this.name = name;
        this.mappedType = mappedType;
        this.description = description;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public MappedElementType mappedType() {
        return mappedType;
    }

    @Override
    public String description() {
        return description;
    }
}
