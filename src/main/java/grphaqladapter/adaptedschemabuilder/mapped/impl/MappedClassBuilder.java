package grphaqladapter.adaptedschemabuilder.mapped.impl;

import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;
import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;

public abstract class MappedClassBuilder<T extends MappedClassBuilder<T, E>, E extends MappedClass> extends MappedElementBuilder<T, E> {

    private Class baseClass;

    public MappedClassBuilder(MappedElementType elementType) {
        super(elementType);
    }

    public T baseClass(Class baseClass) {
        this.baseClass = baseClass;
        return (T) this;
    }

    @Override
    public T refresh() {
        this.baseClass = null;
        return super.refresh();
    }

    public Class baseClass() {
        return baseClass;
    }
}
