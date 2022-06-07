package grphaqladapter.adaptedschemabuilder.scalar.impl;


import graphql.schema.Coercing;
import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.scalar.ScalarEntry;

class ScalaEntryImpl implements ScalarEntry {

    private final Class type;
    private final String name;
    private final String description;
    private final Coercing coercing;

    ScalaEntryImpl(Class type, String name, String description, Coercing coercing) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.coercing = coercing;
        validate();
    }


    private final void validate() {
        Assert.isNotNull(type, "provided type is null");
        Assert.isOneFalse("null name", Assert.isNullString(name));
        Assert.isNotNull(coercing, "provided coercing is null");
    }

    @Override
    public Class type() {
        return type;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public Coercing coercing() {
        return coercing;
    }
}
