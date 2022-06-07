package grphaqladapter.adaptedschemabuilder.mapped.impl;

import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;
import grphaqladapter.adaptedschemabuilder.mapped.MappedMethod;
import grphaqladapter.adaptedschemabuilder.utils.Utils;
import grphaqladapter.adaptedschemabuilder.validator.TypeValidator;

import java.util.Collections;
import java.util.Map;

final class MappedClassImpl implements MappedClass {

    private final Class baseClass;
    private final String typeName;
    private final String description;
    private final MappedType mappedType;
    private final Map<String, MappedMethod> mappedMethods;

    MappedClassImpl(Class baseClass, String typeName, String description, MappedType mappedType, Map<String, MappedMethod> mappedMethods) {
        this.baseClass = baseClass;
        this.typeName = typeName;
        this.description = description;
        this.mappedType = mappedType;
        this.mappedMethods = Utils.nullifyOrGetDefault(mappedMethods, Collections.EMPTY_MAP);
        TypeValidator.validate(this);
    }

    final static MappedClass clone(MappedClass mappedClass) {
        return new MappedClassImpl(
                mappedClass.baseClass(),
                mappedClass.typeName(),
                mappedClass.description(),
                mappedClass.mappedType(),
                mappedClass.mappedMethods() == null ? null :
                        Collections.unmodifiableMap(Utils.copy(mappedClass.mappedMethods()))

        );
    }

    @Override
    public Class baseClass() {
        return baseClass;
    }

    @Override
    public MappedType mappedType() {
        return mappedType;
    }

    @Override
    public String typeName() {
        return typeName;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public Map<String, MappedMethod> mappedMethods() {
        return mappedMethods;
    }

    @Override
    public String toString() {
        return "[class:" + baseClass + " , mappedType-argumentName:" + typeName + " , mappedType:" + mappedType + " , mapped-methods:" + mappedMethods + "]";
    }
}
