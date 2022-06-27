package grphaqladapter.annotations.interfaces;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Map;

public class GraphqlDirectivesList {

    private final static GraphqlDirectivesList EMPTY = new GraphqlDirectivesList(Collections.emptyMap());

    public static GraphqlDirectivesList empty() {
        return EMPTY;
    }

    private final Map<Class< ? extends Annotation> , GraphqlDirectiveDetails> directives;

    public GraphqlDirectivesList(Map<Class<? extends Annotation>, GraphqlDirectiveDetails> directives) {
        this.directives = directives;
    }

    public Map<Class<? extends Annotation>, GraphqlDirectiveDetails> directives() {
        return directives;
    }
}
