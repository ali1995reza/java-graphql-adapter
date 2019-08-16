package grphaqladapter.adaptedschemabuilder.scalar;

import graphql.schema.Coercing;

public interface ScalarEntry {


    public Class type();
    public String name();
    public Coercing coercing();
}
