package grphaqladapter.annotations.impl.type;

import grphaqladapter.adaptedschemabuilder.validator.TypeValidator;
import grphaqladapter.annotations.GraphqlInputTypeAnnotation;

class GraphqlInputTypeAnnotationImpl extends TypeNameContainer implements GraphqlInputTypeAnnotation {

    GraphqlInputTypeAnnotationImpl(String typeName) {
        super(typeName);

        TypeValidator.validate(this);
    }
}
