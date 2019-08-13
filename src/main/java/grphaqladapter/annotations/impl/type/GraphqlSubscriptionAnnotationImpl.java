package grphaqladapter.annotations.impl.type;

import grphaqladapter.adaptedschemabuilder.validator.TypeValidator;
import grphaqladapter.annotations.GraphqlSubscriptionAnnotation;

class GraphqlSubscriptionAnnotationImpl extends TypeNameContainer implements GraphqlSubscriptionAnnotation {
    GraphqlSubscriptionAnnotationImpl(String typeName) {
        super(typeName);
        TypeValidator.validate(this);
    }
}
