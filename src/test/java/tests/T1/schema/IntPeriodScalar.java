package tests.T1.schema;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;

import java.util.function.Consumer;
import java.util.regex.Pattern;

public class IntPeriodScalar {


    private final static Pattern PeriodPattern =
            Pattern.compile("[\\[(]\\d+[:]\\d+[)\\]]");

    public final static Coercing Coercing = new Coercing() {


        @Override
        public Object serialize(Object o) throws CoercingSerializeException {
            if (o instanceof IntPeriodScalar) {
                return o.toString();
            }

            throw new CoercingSerializeException("Expect IntPeriodScalar but found [" + o.getClass() + "]");

        }

        @Override
        public Object parseValue(Object o) throws CoercingParseValueException {
            if (o instanceof String) {
                return IntPeriodScalar.parse((String) o);
            }

            throw new CoercingParseValueException("Expect String but found [" + o.getClass() + "]");

        }

        @Override
        public Object parseLiteral(Object o) throws CoercingParseLiteralException {

            if (o instanceof StringValue) {
                return IntPeriodScalar.parse(((StringValue) o).getValue());
            }

            throw new CoercingParseLiteralException("Expect String but found [" + o.getClass() + "]");

        }
    };


    private final Bound lowBound;
    private final Bound upBound;
    private final String asString;


    private IntPeriodScalar(Bound from, Bound to) {
        this.lowBound = from;
        this.upBound = to;


        if (from.compareTo(to) > 0)
            throw new IllegalStateException("lower bound bigger than upper bound");


        asString = lowBound.asLowerBound() + ":" + upBound.asUpperBound();
    }

    public static IntPeriodScalar parse(String asString) {
        if (PeriodPattern.matcher(asString).matches()) {
            String split[] = asString.split(":");
            return new IntPeriodScalar(
                    new Bound(Integer.parseInt(split[0].substring(1)), split[0].charAt(0) == '['),
                    new Bound(Integer.parseInt(split[1].substring(0, split[1].length() - 1)), split[1].charAt(split[1].length() - 1) == ']')
            );
        }


        throw new IllegalStateException("invalid format");
    }

    public Bound lowBound() {
        return lowBound;
    }

    public Bound upBound() {
        return upBound;
    }

    @Override
    public String toString() {
        return asString;
    }

    public void forEach(Consumer<Integer> forEach) {
        int start = lowBound.isContains() ? lowBound.number() : lowBound.number() + 1;
        int end = upBound.isContains() ? upBound.number() : upBound.number() - 1;

        for (int i = start; i <= end; i++) {
            forEach.accept(i);
        }
    }
}
