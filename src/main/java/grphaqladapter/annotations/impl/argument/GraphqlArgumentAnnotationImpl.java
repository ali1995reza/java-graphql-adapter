package grphaqladapter.annotations.impl.argument;


import grphaqladapter.adaptedschemabuilder.validator.ArgumentValidator;
import grphaqladapter.annotations.GraphqlArgumentAnnotation;

final class GraphqlArgumentAnnotationImpl implements GraphqlArgumentAnnotation {


    private final String argumentName;
    private final boolean nullable;

    GraphqlArgumentAnnotationImpl(String argumentName, boolean nullable) {

        this.argumentName = argumentName;
        this.nullable = nullable;
        ArgumentValidator.validate(this);
    }




    @Override
    public String argumentName() {
        return argumentName;
    }

    @Override
    public boolean nullable() {
        return nullable;
    }

}
