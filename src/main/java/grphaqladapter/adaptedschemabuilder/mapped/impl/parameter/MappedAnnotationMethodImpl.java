package grphaqladapter.adaptedschemabuilder.mapped.impl.parameter;

import grphaqladapter.adaptedschemabuilder.mapped.MappedAnnotationMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.adaptedschemabuilder.mapped.TypeDescriptor;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedMethodImpl;
import grphaqladapter.annotations.interfaces.ValueParser;

import java.lang.reflect.Method;

final class MappedAnnotationMethodImpl extends MappedMethodImpl implements MappedAnnotationMethod {

    private final Class<? extends ValueParser> valueParser;

    public MappedAnnotationMethodImpl(String name, String description, Method method, TypeDescriptor type, Class<? extends ValueParser> valueParser) {
        super(name, MappedElementType.ARGUMENT, description, method, type);
        this.valueParser = valueParser;
    }

    @Override
    public Class<? extends ValueParser> valueParser() {
        return valueParser;
    }
}
