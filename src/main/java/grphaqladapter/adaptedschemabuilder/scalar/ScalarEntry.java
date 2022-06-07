package grphaqladapter.adaptedschemabuilder.scalar;

import graphql.schema.Coercing;

public interface ScalarEntry {

    Class type();

    String name();

    String description();

    Coercing coercing();
}
