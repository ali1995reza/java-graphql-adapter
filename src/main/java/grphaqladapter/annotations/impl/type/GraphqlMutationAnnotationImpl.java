package grphaqladapter.annotations.impl.type;

import grphaqladapter.adaptedschemabuilder.validator.TypeValidator;
import grphaqladapter.annotations.GraphqlMutationAnnotation;

class GraphqlMutationAnnotationImpl extends TypeNameContainer implements GraphqlMutationAnnotation {

    GraphqlMutationAnnotationImpl(String typeName) {
        super(typeName);
        TypeValidator.validate(this);
    }
}
