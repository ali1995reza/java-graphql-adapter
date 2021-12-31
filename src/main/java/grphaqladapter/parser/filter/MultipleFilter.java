package grphaqladapter.parser.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MultipleFilter implements ClassFilter {

    public static ClassFilter of(ClassFilter... filters) {
        return new MultipleFilter(filters);
    }

    private final List<ClassFilter> filterList;

    public MultipleFilter(Collection<ClassFilter> filterList) {
        this.filterList = new ArrayList<>(filterList);
    }

    public MultipleFilter(ClassFilter... filters) {
        this(Arrays.asList(filters));
    }


    @Override
    public boolean accept(Class cls) {
        for (ClassFilter filter : filterList) {
            if (!filter.accept(cls)) {
                return false;
            }
        }
        return true;
    }
}
