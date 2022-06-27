package grphaqladapter.annotations.interfaces;

import grphaqladapter.adaptedschemabuilder.mapped.MappedFieldMethod;

import java.lang.annotation.Annotation;
import java.util.Map;

public class FieldDirectives extends GraphqlDirectivesList {

    private final MappedFieldMethod field;

    public FieldDirectives(Map<Class<? extends Annotation>, GraphqlDirectiveDetails> directives, MappedFieldMethod field) {
        super(directives);
        this.field = field;
    }

    public MappedFieldMethod field() {
        return field;
    }
}
