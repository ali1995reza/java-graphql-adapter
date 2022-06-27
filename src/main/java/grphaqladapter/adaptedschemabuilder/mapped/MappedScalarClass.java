package grphaqladapter.adaptedschemabuilder.mapped;

import graphql.schema.Coercing;

public interface MappedScalarClass extends MappedClass {

    Coercing<?, ?> coercing();
}
