package grphaqladapter.annotations.impl.type;

import grphaqladapter.annotations.GraphqlSubscriptionAnnotation;

class GraphqlSubscriptionAnnotationImpl extends TypeNameContainer implements GraphqlSubscriptionAnnotation {
    GraphqlSubscriptionAnnotationImpl(String typeName) {
        super(typeName);
    }
}
