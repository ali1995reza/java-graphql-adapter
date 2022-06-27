package grphaqladapter.adaptedschemabuilder.mapped.impl;

import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;
import grphaqladapter.adaptedschemabuilder.mapped.TypeDescriptor;

import java.lang.reflect.Method;

public class MappedMethodImpl extends MappedElementImpl implements MappedMethod {

    private final Method method;
    private final TypeDescriptor type;

    public MappedMethodImpl(String name, MappedElementType mappedType, String description, Method method, TypeDescriptor type) {
        super(name, mappedType, description);
        this.method = method;
        this.type = type;
    }

    @Override
    public Method method() {
        return method;
    }

    @Override
    public TypeDescriptor type() {
        return type;
    }
}
