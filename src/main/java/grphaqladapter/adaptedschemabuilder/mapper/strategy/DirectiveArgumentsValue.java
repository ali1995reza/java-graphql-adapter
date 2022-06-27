package grphaqladapter.adaptedschemabuilder.mapper.strategy;

public interface DirectiveArgumentsValue {

    <T> T getArgumentValue(String argument);

    Class annotationClass();
}
