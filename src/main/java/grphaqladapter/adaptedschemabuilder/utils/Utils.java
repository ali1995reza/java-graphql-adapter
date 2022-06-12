package grphaqladapter.adaptedschemabuilder.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Utils {


    public static <T> List copy(List<T> l) {
        ArrayList<T> list = new ArrayList<>();

        for (T o : l) {
            list.add(o);
        }

        return list;
    }


    public static <K, V> Map<K, V> copy(Map<K, V> m) {
        return new HashMap<>(m);
    }

    public final static <T> T nullifyOrGetDefault(T t, T d) {
        if (t == null)
            return d;

        return t;
    }

    public final static String stringNullifyOrGetDefault(String s, String d) {
        if (s == null || s.isEmpty())
            return d;

        return s;
    }
}
