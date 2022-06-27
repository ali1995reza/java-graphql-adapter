package grphaqladapter.annotations.interfaces;

import java.util.Collections;
import java.util.Map;

public class FragmentDirectives {

    private final static FragmentDirectives EMPTY = new FragmentDirectives(Collections.emptyMap());

    public static FragmentDirectives empty() {
        return EMPTY;
    }

    private final Map<Class, GraphqlDirectivesList> directivesByType;

    public FragmentDirectives(Map<Class, GraphqlDirectivesList> directivesByType) {
        this.directivesByType = directivesByType;
    }


    public Map<Class, GraphqlDirectivesList> directivesByType() {
        return directivesByType;
    }
}
