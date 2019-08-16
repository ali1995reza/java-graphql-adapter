package grphaqladapter.adaptedschemabuilder.scalar.impl;


import graphql.schema.Coercing;
import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.scalar.ScalarEntry;

class ScalaEntryImpl implements ScalarEntry {

    private final Class type;
    private final String name;
    private final Coercing coercing;

    ScalaEntryImpl(Class type, String name, Coercing coercing) {
        this.type = type;
        this.name = name;
        this.coercing = coercing;
        validate();
    }


    private final void validate()
    {
        Assert.ifNull(type , "provided type is null");
        Assert.ifConditionTrue("null name" , Assert.isNullString(name));
        Assert.ifNull(coercing , "provided coercing is null");
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
    public Coercing coercing() {
        return coercing;
    }
}
