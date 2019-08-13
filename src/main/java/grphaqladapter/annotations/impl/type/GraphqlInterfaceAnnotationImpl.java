package grphaqladapter.annotations.impl.type;

import grphaqladapter.adaptedschemabuilder.validator.TypeValidator;
import grphaqladapter.annotations.GraphqlInterfaceAnnotation;

class GraphqlInterfaceAnnotationImpl extends TypeNameContainer implements GraphqlInterfaceAnnotation {

    GraphqlInterfaceAnnotationImpl(String typeName) {
        super(typeName);

        TypeValidator.validate(this);
    }
}
