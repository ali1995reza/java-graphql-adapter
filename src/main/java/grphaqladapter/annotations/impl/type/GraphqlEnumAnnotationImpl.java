package grphaqladapter.annotations.impl.type;

import grphaqladapter.adaptedschemabuilder.validator.TypeValidator;
import grphaqladapter.annotations.GraphqlEnumAnnotation;

class GraphqlEnumAnnotationImpl extends TypeNameContainer implements GraphqlEnumAnnotation {

    GraphqlEnumAnnotationImpl(String typeName) {
        super(typeName);

        TypeValidator.validate(this);
    }
}
