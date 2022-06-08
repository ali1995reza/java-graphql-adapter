package tests.T1.schema;

public class Bound implements Comparable<Bound> {

    private final int number;
    private final boolean contains;

    public Bound(int number, boolean contains) {
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
        if (number > o.number)
            return 1;
        if (number < o.number)
            return -1;

        if (number == o.number) {
            if (contains && !o.contains)
                return 1;
            if (!contains && o.contains)
                return -1;
        }

        return 0;
    }

    public String asUpperBound() {
        return new StringBuffer().append(number)
                .append(contains ? ']' : ')').toString();
    }

    public String asLowerBound() {
        return new StringBuffer().append(contains ? '[' : '(').append(number)
                .toString();
    }
}
