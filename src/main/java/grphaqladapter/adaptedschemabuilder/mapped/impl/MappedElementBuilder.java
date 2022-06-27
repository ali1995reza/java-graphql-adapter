package grphaqladapter.adaptedschemabuilder.mapped.impl;

import grphaqladapter.adaptedschemabuilder.mapped.MappedElement;
import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;

public abstract class MappedElementBuilder<T extends MappedElementBuilder<T, E>, E extends MappedElement> {

    private final MappedElementType elementType;
    private String name;
    private String description;

    protected MappedElementBuilder(MappedElementType elementType) {
        this.elementType = elementType;
    }

    public T copy(E element) {
        if(!element.mappedType().is(elementType())) {
            throw new IllegalStateException("can not copy element - difference types ["+element.mappedType()+" , "+elementType()+"]");
        }
        this.refresh();
        return name(element.name())
                .description(element.description());
    }

    public T name(String name) {
        this.name = name;
        return (T) this;
    }

    public T description(String description) {
        this.description = description;
        return (T) this;
    }

    public String description() {
        return description;
    }

    public final MappedElementType elementType() {
        return elementType;
    }

    public String name() {
        return name;
    }

    public T refresh() {
        this.name = null;
        this.description = null;
        return (T) this;
    }

    public abstract E build();

}
