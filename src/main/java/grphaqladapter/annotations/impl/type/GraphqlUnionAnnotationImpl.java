package grphaqladapter.annotations.impl.type;

import grphaqladapter.adaptedschemabuilder.validator.TypeValidator;
import grphaqladapter.annotations.GraphqlUnionAnnotation;

class GraphqlUnionAnnotationImpl extends TypeNameContainer implements GraphqlUnionAnnotation {

    GraphqlUnionAnnotationImpl(String typeName) {
        super(typeName);

        TypeValidator.validate(this);
    }
}
