package grphaqladapter.adaptedschemabuilder.mapped.impl.classes;

import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedFieldMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedTypeClass;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedClassImpl;

import java.util.Map;

final class MappedTypeClassImpl extends MappedClassImpl implements MappedTypeClass {

    private final Map<String, MappedFieldMethod> fieldMethods;

    MappedTypeClassImpl(String name, MappedElementType elementType, String description, Class baseClass, Map<String, MappedFieldMethod> fieldMethods) {
        super(name, elementType, description, baseClass);
        Assert.isTrue(elementType.isTopLevelType() || elementType.is(MappedElementType.OBJECT_TYPE) ,
                new IllegalStateException("MappedTypeClass valid element types is [QUERY, MUTATION, SUBSCRIPTION, OBJECT_TYPE]"));
        this.fieldMethods = fieldMethods;
    }

    @Override
    public Map<String, MappedFieldMethod> fieldMethods() {
        return fieldMethods;
    }
}
