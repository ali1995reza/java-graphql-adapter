package grphaqladapter.adaptedschemabuilder.builtinscalars;

import graphql.language.StringValue;
import graphql.schema.*;

public final class ID {

    public final static GraphQLScalarType ScalarType =
            GraphQLScalarType.newScalar()
                    .name("ID")
                    .coercing(new Coercing() {
                        @Override
                        public Object serialize(Object o) throws CoercingSerializeException {
                            if (o instanceof ID) {
                                return o.toString();
                            }

                            throw new CoercingSerializeException("Expect ID but found [" + o.getClass() + "]");
                        }

                        @Override
                        public Object parseValue(Object o) throws CoercingParseValueException {
                            if (o instanceof String) {
                                return new ID(o.toString());
                            }


                            throw new CoercingParseValueException("Expect String but found [" + o.getClass() + "]");
                        }

                        @Override
                        public Object parseLiteral(Object o) throws CoercingParseLiteralException {
                            if (o instanceof StringValue) {
                                return new ID(((StringValue) o).getValue());
                            }

                            throw new CoercingParseLiteralException("Expect StringValue but found [" + o.getClass() + "]");

                        }
                    }).build();


    private final String id;

    public ID(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }
}
