package grphaqladapter.adaptedschemabuilder.mapped.impl.method;

import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedFieldMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedParameter;
import grphaqladapter.adaptedschemabuilder.mapped.TypeDescriptor;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedMethodImpl;

import java.lang.reflect.Method;
import java.util.List;

final class MappedFieldMethodImpl extends MappedMethodImpl implements MappedFieldMethod {

    private final List<MappedParameter> parameters;

    MappedFieldMethodImpl(String name, String description, Method method, TypeDescriptor type, List<MappedParameter> parameters) {
        super(name, MappedElementType.FIELD, description, method, type);
        this.parameters = parameters;
    }


    @Override
    public List<MappedParameter> parameters() {
        return parameters;
    }
}
