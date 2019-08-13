package grphaqladapter.annotations.impl.type;

import grphaqladapter.adaptedschemabuilder.validator.TypeValidator;
import grphaqladapter.annotations.GraphqlTypeAnnotation;

class GraphqlTypeAnnotationImpl extends TypeNameContainer implements GraphqlTypeAnnotation {

    GraphqlTypeAnnotationImpl(String typeName) {

        super(typeName);
        TypeValidator.validate(this);
    }
}
