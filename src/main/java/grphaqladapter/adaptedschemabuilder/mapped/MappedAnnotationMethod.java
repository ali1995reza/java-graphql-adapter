package grphaqladapter.adaptedschemabuilder.mapped;

import grphaqladapter.annotations.interfaces.ValueParser;

public interface MappedAnnotationMethod extends MappedMethod {

    Class<? extends ValueParser> valueParser();

}
