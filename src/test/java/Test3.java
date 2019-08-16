import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import grphaqladapter.adaptedschemabuilder.AdaptedGraphQLSchema;
import grphaqladapter.adaptedschemabuilder.AdaptedSchemaBuilder;
import grphaqladapter.adaptedschemabuilder.scalar.ScalarEntry;
import grphaqladapter.adaptedschemabuilder.scalar.impl.ScalarEntryBuilder;
import grphaqladapter.annotations.GraphqlArgument;
import grphaqladapter.annotations.GraphqlField;
import grphaqladapter.annotations.GraphqlQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class Test3 {

    public static class IntPeriodScalar{


        private final static Pattern PeriodPattern  =
                Pattern.compile("[\\[(]\\d+[:]\\d+[)\\]]");

        private final static Coercing coercing = new Coercing() {


            @Override
            public Object serialize(Object o) throws CoercingSerializeException {
                if(o instanceof IntPeriodScalar)
                {
                    return o.toString();
                }

                throw new CoercingSerializeException("Expect IntPeriodScalar but found ["+o.getClass()+"]");

            }

            @Override
            public Object parseValue(Object o) throws CoercingParseValueException {
                if(o instanceof String)
                {
                    return IntPeriodScalar.parse((String)o);
                }

                throw new CoercingParseValueException("Expect String but found ["+o.getClass()+"]");

            }

            @Override
            public Object parseLiteral(Object o) throws CoercingParseLiteralException {

                if(o instanceof StringValue)
                {
                    return IntPeriodScalar.parse(((StringValue) o).getValue());
                }

                throw new CoercingParseLiteralException("Expect String but found ["+o.getClass()+"]");

            }
        };


        private final Bound lowBound;
        private final Bound upBound;
        private final String asString;


        private IntPeriodScalar(Bound from, Bound to) {
            this.lowBound = from;
            this.upBound = to;


            if(from.compareTo(to)>0)
                throw new IllegalStateException("lower bound bigger than upper bound");


            asString = lowBound.asLowerBound()+":"+upBound.asUpperBound();
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
        public void forEach(Consumer<Integer> forEach)
        {
            int start = lowBound.isContains()?lowBound.number():lowBound.number()+1;
            int end = upBound.isContains()?upBound.number():upBound.number()-1;

            for(int i = start;i<=end;i++)
            {
                forEach.accept(i);
            }
        }


        public static IntPeriodScalar parse(String asString)
        {
            if(PeriodPattern.matcher(asString).matches())
            {
                String split[] = asString.split(":");
                return new IntPeriodScalar(
                        new Bound(Integer.parseInt(split[0].substring(1)) , split[0].charAt(0)=='[') ,
                        new Bound(Integer.parseInt(split[1].substring(0 , split[1].length()-1)) , split[1].charAt(split[1].length()-1)==']')
                );
            }


            throw new IllegalStateException("invalid format");
        }
    }

    private static class Bound implements Comparable<Bound>{

        private final int number;
        private final boolean contains;

        private Bound(int number, boolean contains) {
            this.number = number;
            this.contains = contains;
        }

        public int number() {
            return number;
        }

        public boolean isContains() {
            return contains;
        }

        @Override
        public int compareTo(Bound o) {
            if(number>o.number)
                return 1;
            if(number<o.number)
                return -1;

            if(number==o.number)
            {
                if(contains && !o.contains)
                    return 1;
                if(!contains && o.contains)
                    return -1;
            }

            return 0;
        }

        public String asUpperBound()
        {
            return new StringBuffer().append(number)
                    .append(contains?']':')').toString();
        }

        public String asLowerBound()
        {
            return new StringBuffer().append(contains?'[':'(').append(number)
                    .toString();
        }
    }


    @GraphqlQuery
    public static class MyQuery{


        @GraphqlField
        public List<Integer> get(@GraphqlArgument(argumentName = "period") IntPeriodScalar periodScalar)
        {
            List<Integer> list = new ArrayList<>();

            periodScalar.forEach(new Consumer<Integer>() {
                @Override
                public void accept(Integer integer) {
                    list.add(integer);
                }
            });

            return list;
        }
    }


    public static void main(String[] args)
    {

        AdaptedGraphQLSchema schema = AdaptedSchemaBuilder
                .newBuilder()
                .add(MyQuery.class)
                .addScalar(
                        ScalarEntryBuilder.newBuilder()
                        .setName("Period")
                        .setCoercing(IntPeriodScalar.coercing)
                        .setType(IntPeriodScalar.class)
                        .build()
                        )
                .build();

        System.out.println(schema.asSchemaDefinitionLanguage());


        System.out.println("===========================================================");


        GraphQL graphQL = GraphQL.newGraphQL(schema.getSchema()).build();

        ExecutionResult result = graphQL.execute("\n" +
                "    query{\n" +
                "        get(period:\"(10:20]\")\n" +
                "    }");

        System.out.println(result);
    }
}
