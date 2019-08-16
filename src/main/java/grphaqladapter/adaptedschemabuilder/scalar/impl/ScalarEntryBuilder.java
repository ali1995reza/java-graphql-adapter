package grphaqladapter.adaptedschemabuilder.scalar.impl;

import graphql.schema.Coercing;
import grphaqladapter.adaptedschemabuilder.scalar.ScalarEntry;

public final class ScalarEntryBuilder {

    public ScalarEntryBuilder newBuilder()
    {
        return new ScalarEntryBuilder();
    }


    private Class type;
    private String name;
    private Coercing coercing;


    private ScalarEntryBuilder(){}


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


    public ScalarEntry build()
    {
        return new ScalaEntryImpl(type , name , coercing);
    }
}
