package grphaqladapter.adaptedschemabuilder.mapped.impl.parameter;

import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedParameter;
import grphaqladapter.adaptedschemabuilder.mapped.TypeDescriptor;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedElementImpl;

import java.lang.reflect.Parameter;

final class MappedParameterImpl extends MappedElementImpl implements MappedParameter {

    private final Parameter parameter;
    private final TypeDescriptor type;
    private final Model model;

    MappedParameterImpl(String name, MappedElementType mappedType, String description, Parameter parameter, TypeDescriptor type, Model model) {
        super(name, mappedType, description);
        this.parameter = parameter;
        this.type = type;
        this.model = model;
    }
    @Override
    public Model model() {
        return model;
    }

    @Override
    public Parameter parameter() {
        return parameter;
    }

    @Override
    public TypeDescriptor type() {
        return type;
    }
}
