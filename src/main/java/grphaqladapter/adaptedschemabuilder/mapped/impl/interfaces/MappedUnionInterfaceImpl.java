package grphaqladapter.adaptedschemabuilder.mapped.impl.interfaces;


import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedUnionInterface;
import grphaqladapter.adaptedschemabuilder.mapped.impl.MappedClassImpl;

final class MappedUnionInterfaceImpl extends MappedClassImpl implements MappedUnionInterface {

    MappedUnionInterfaceImpl(String name, String description, Class baseClass) {
        super(name, MappedElementType.UNION, description, baseClass);
    }
}
