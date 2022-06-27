package grphaqladapter.adaptedschemabuilder.mapped.impl;

import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;
import grphaqladapter.adaptedschemabuilder.mapped.TypeDescriptor;

import java.lang.reflect.Method;

public abstract class MappedMethodBuilder<T extends MappedMethodBuilder<T, E>, E extends MappedMethod> extends MappedElementBuilder<T, E> {

    private Method method;
    private TypeDescriptor type;

    public MappedMethodBuilder(MappedElementType elementType) {
        super(elementType);
    }

    @Override
    public T copy(E element) {
        return super.copy(element)
                .method(element.method())
                .type(element.type());

    }

    public T method(Method method) {
        this.method = method;
        return (T) this;
    }

    public Method method() {
        return method;
    }

    public T type(TypeDescriptor type) {
        this.type = type;
        return (T) this;
    }

    public TypeDescriptor type() {
        return type;
    }

    @Override
    public T refresh() {
        this.method = null;
        this.type = null;
        return super.refresh();
    }
}
