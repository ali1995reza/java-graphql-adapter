package grphaqladapter.annotations.interfaces;

import graphql.language.OperationDefinition;

import java.lang.annotation.Annotation;
import java.util.Map;

public class OperationDirectives extends GraphqlDirectivesList {

    private final OperationDefinition operation;

    public OperationDirectives(Map<Class<? extends Annotation>, GraphqlDirectiveDetails> directives, OperationDefinition operation) {
        super(directives);
        this.operation = operation;
    }

    public OperationDefinition operation() {
        return operation;
    }
}
