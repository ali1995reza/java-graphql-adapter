package grphaqladapter.annotations.impl.type;

abstract class TypeNameContainer {

    private final String typeName;

    TypeNameContainer(String typeName) {
        this.typeName = typeName;
    }

    public String typeName() {
        return typeName;
    }
}
