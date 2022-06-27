package grphaqladapter.adaptedschemabuilder.mapped.impl.classes;

import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedInputFieldMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedInputTypeClass;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedClassImpl;

import java.util.Map;

public class MappedInputTypeClassImpl extends MappedClassImpl implements MappedInputTypeClass {

    private final Map<String, MappedInputFieldMethod> inputFieldMethods;

    protected MappedInputTypeClassImpl(String name, String description, Class baseClass, Map<String, MappedInputFieldMethod> inputFieldMethods) {
        super(name, MappedElementType.INPUT_TYPE, description, baseClass);
        this.inputFieldMethods = inputFieldMethods;
    }

    @Override
    public Map<String, MappedInputFieldMethod> inputFiledMethods() {
        return inputFieldMethods;
    }
}
