package grphaqladapter.adaptedschemabuilder.mapper.strategy;

import grphaqladapter.adaptedschemabuilder.mapped.MappedAnnotation;
import grphaqladapter.adaptedschemabuilder.mapped.MappedElement;

import java.util.List;
import java.util.Map;

public interface AppliedDirectiveDetector {

    List<DirectiveArgumentsValue> detectDirectives(MappedElement element, Map<Class, MappedAnnotation> annotations);
}
