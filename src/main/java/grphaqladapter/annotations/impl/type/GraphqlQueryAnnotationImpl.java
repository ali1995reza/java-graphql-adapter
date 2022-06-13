package grphaqladapter.annotations.impl.type;

import grphaqladapter.annotations.GraphqlQueryAnnotation;

public class GraphqlQueryAnnotationImpl extends TypeNameContainer implements GraphqlQueryAnnotation {

    GraphqlQueryAnnotationImpl(String typeName) {
        super(typeName);
    }
}
