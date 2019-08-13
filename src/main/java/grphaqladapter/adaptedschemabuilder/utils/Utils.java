package grphaqladapter.adaptedschemabuilder.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Utils {



    public final static List copy(List l)
    {
        ArrayList list = new ArrayList();

        for(Object o:l)
        {
            list.add(o);
        }

        return list;
    }


    public final static Map copy(Map m)
    {
        HashMap map = new HashMap();

        for(Object o :m.keySet())
        {
            map.put(o , m.get(o));
        }

        return map;
    }

    public final static <T> T nullifyOrGetDefault(T t , T d)
    {
        if(t==null)
            return d;

        return t;
    }

    public final static String stringNullifyOrGetDefault(String s , String d)
    {
        if(s==null || s.isEmpty())
            return d;

        return s;
    }
}
