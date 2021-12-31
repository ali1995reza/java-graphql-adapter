package grphaqladapter.parser.filter;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class AnnotationFilter implements ClassFilter {

    public static ClassFilter annotatedWith(Class<? extends Annotation>... annotations) {
        return new AnnotationFilter(annotations);
    }

    private final Set<Class<? extends Annotation>> annotations;

    public AnnotationFilter(Collection<Class<? extends Annotation>> annotations) {
        this.annotations = new HashSet<>(annotations);
    }

    public AnnotationFilter(Class<? extends Annotation>... annotations) {
        this(Arrays.asList(annotations));
    }

    @Override
    public boolean accept(Class cls) {
        for (Annotation annotation : cls.getAnnotations()) {
            if (annotations.contains(annotation.annotationType())) {
                return true;
            }
        }
        return false;
    }
}
