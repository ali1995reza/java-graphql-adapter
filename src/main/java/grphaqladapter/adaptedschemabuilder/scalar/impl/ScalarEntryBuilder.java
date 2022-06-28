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

    public ScalarEntryBuilder type(Class type) {
        this.type = type;
        return this;
    }

    public Class type() {
        return type;
    }

    public ScalarEntryBuilder coercing(Coercing coercing) {
        this.coercing = coercing;
        return this;
    }

    public Coercing coercing() {
        return coercing;
    }

    public ScalarEntryBuilder name(String name) {
        this.name = name;
        return this;
    }

    public String name() {
        return name;
    }

    public ScalarEntryBuilder description(String description) {
        this.description = description;
        return this;
    }

    public String description() {
        return description;
    }

    public ScalarEntry build() {
        return new ScalaEntryImpl(type(),
                name(),
                description(),
                coercing()
        );
    }
}
