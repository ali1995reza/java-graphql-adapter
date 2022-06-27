package grphaqladapter.adaptedschemabuilder.mapped.impl.method;

import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedInputFieldMethod;
import grphaqladapter.adaptedschemabuilder.mapped.TypeDescriptor;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedMethodImpl;

import java.lang.reflect.Method;

public class MappedInputFieldMethodImpl extends MappedMethodImpl implements MappedInputFieldMethod {

    private final Method setter;

    public MappedInputFieldMethodImpl(String name, String description, Method method, TypeDescriptor type, Method setter) {
        super(name, MappedElementType.INPUT_FIELD, description, method, type);
        this.setter = setter;
    }

    @Override
    public Method setter() {
        return setter;
    }
}
