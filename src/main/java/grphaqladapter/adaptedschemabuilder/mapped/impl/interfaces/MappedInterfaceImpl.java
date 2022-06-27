package grphaqladapter.adaptedschemabuilder.mapped.impl.interfaces;

import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedFieldMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedInterface;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedClassImpl;

import java.util.Map;

final class MappedInterfaceImpl extends MappedClassImpl implements MappedInterface {

    private final Map<String, MappedFieldMethod> fieldMethods;

    MappedInterfaceImpl(String name, String description, Class baseClass, Map<String, MappedFieldMethod> fieldMethods) {
        super(name, MappedElementType.INTERFACE, description, baseClass);
        this.fieldMethods = fieldMethods;
    }


    @Override
    public Map<String, MappedFieldMethod> fieldMethods() {
        return fieldMethods;
    }
}
