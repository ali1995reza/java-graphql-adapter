package grphaqladapter.adaptedschemabuilder.scalar.impl;

import graphql.schema.Coercing;
import grphaqladapter.adaptedschemabuilder.scalar.ScalarEntry;

public final class ScalarEntryBuilder {

    private Class type;
    private String name;
    private String description;
    private Coercing coercing;

    private ScalarEntryBuilder() {
    }

    public static ScalarEntryBuilder newBuilder() {
        return new ScalarEntryBuilder();
    }

    public ScalarEntryBuilder setType(Class type) {
        this.type = type;
        return this;
    }

    public ScalarEntryBuilder setCoercing(Coercing coercing) {
        this.coercing = coercing;
        return this;
    }

    public ScalarEntryBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ScalarEntryBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public ScalarEntry build() {
        return new ScalaEntryImpl(type, name, description, coercing);
    }
}
