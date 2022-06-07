package grphaqladapter.adaptedschemabuilder.mapped;


import java.util.Map;

public interface MappedClass {

    Class baseClass();

    MappedType mappedType();

    String typeName();

    String description();

    Map<String, MappedMethod> mappedMethods();

    enum MappedType {
        ENUM,
        INTERFACE,
        OBJECT_TYPE,
        UNION,
        INPUT_TYPE,
        SCALAR,
        QUERY,
        MUTATION,
        SUBSCRIPTION;


        public boolean is(MappedType other) {
            return this == other;
        }

        public boolean isTopLevelType() {
            return this == QUERY || this == MUTATION || this == SUBSCRIPTION;
        }
    }
}
