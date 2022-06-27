package grphaqladapter.annotations;

import grphaqladapter.annotations.interfaces.ValueParser;

public interface GraphqlDirectiveArgumentAnnotation extends GraphqlElementAnnotation {

    Class type();

    int dimensions();

    boolean nullable();

    Class<? extends ValueParser> valueParser();
}
