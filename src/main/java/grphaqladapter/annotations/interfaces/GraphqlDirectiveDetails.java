package grphaqladapter.annotations.interfaces;

import grphaqladapter.adaptedschemabuilder.mapped.MappedAnnotation;

import java.util.Map;

public class GraphqlDirectiveDetails {

    private final MappedAnnotation annotation;
    private final Map<String, Object> arguments;

    public GraphqlDirectiveDetails(MappedAnnotation annotation, Map<String, Object> arguments) {
        this.annotation = annotation;
        this.arguments = arguments;
    }

    public MappedAnnotation annotation() {
        return annotation;
    }

    public <T> T getArgument(String name) {
        return (T) arguments.get(name);
    }

    @Override
    public String toString() {
        return "GraphqlDirectiveDetails{" +
                "annotation=" + annotation +
                ", arguments=" + arguments +
                '}';
    }
}
