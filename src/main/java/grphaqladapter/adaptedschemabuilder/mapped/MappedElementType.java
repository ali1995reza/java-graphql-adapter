package grphaqladapter.adaptedschemabuilder.mapped;

public enum MappedElementType {
    ENUM,
    ENUM_VALUE,
    INTERFACE,
    OBJECT_TYPE,
    UNION,
    INPUT_TYPE,
    SCALAR,
    QUERY,
    MUTATION,
    SUBSCRIPTION,
    DIRECTIVE,
    FIELD,
    INPUT_FIELD,
    ARGUMENT;


    public boolean is(MappedElementType other) {
        return this == other;
    }

    public boolean isOneOf(MappedElementType... types) {
        for (MappedElementType type : types) {
            if (this == type) {
                return true;
            }
        }
        return false;
    }

    public boolean isTopLevelType() {
        return this == QUERY || this == MUTATION || this == SUBSCRIPTION;
    }
}
