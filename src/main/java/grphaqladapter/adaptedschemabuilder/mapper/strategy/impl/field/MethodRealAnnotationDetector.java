package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.field;

import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.adaptedschemabuilder.mapper.MappingStatics;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.MethodAnnotationDetector;
import grphaqladapter.adaptedschemabuilder.utils.Utils;
import grphaqladapter.annotations.*;
import grphaqladapter.annotations.impl.GraphqlDescriptionAnnotationImpl;
import grphaqladapter.annotations.impl.argument.GraphqlDirectiveArgumentAnnotationBuilder;
import grphaqladapter.annotations.impl.field.GraphqlFieldAnnotationBuilder;
import grphaqladapter.annotations.impl.field.GraphqlInputFieldAnnotationBuilder;

import java.lang.reflect.Method;

public class MethodRealAnnotationDetector implements MethodAnnotationDetector {

    private boolean skip(Method method) {
        return method.getAnnotation(SkipElement.class) != null;
    }

    @Override
    public boolean skipDirectiveArgument(Method method, Class clazz, MappedElementType mappedElementType) {
        return skip(method);
    }

    @Override
    public boolean skipField(Method method, Class clazz, MappedElementType mappedElementType) {
        return skip(method);
    }

    @Override
    public boolean skipInputField(Method method, Class clazz, MappedElementType mappedElementType) {
        return skip(method);
    }

    @Override
    public GraphqlFieldAnnotation detectFieldAnnotation(Method method, Class clazz, MappedElementType mappedElementType) {
        GraphqlField fieldAnnotation = MethodAnnotationLookup.findFirstAppears(method, GraphqlField.class);

        if (fieldAnnotation == null) {
            return null;
        }

        String name = Utils.stringNullifyOrGetDefault(fieldAnnotation.name(), method.getName());

        return GraphqlFieldAnnotationBuilder.newBuilder()
                .name(name)
                .nullable(fieldAnnotation.nullable())
                .build();
    }

    @Override
    public GraphqlInputFieldAnnotation detectInputFieldAnnotation(Method method, Class clazz, MappedElementType mappedElementType) {
        GraphqlInputField inputFieldAnnotation = MethodAnnotationLookup.findFirstAppears(method, GraphqlInputField.class);

        if (inputFieldAnnotation == null) {
            return null;
        }

        String name = Utils.stringNullifyOrGetDefault(inputFieldAnnotation.name(), method.getName());

        return GraphqlInputFieldAnnotationBuilder.newBuilder()
                .name(name)
                .nullable(inputFieldAnnotation.nullable())
                .setter(inputFieldAnnotation.setter())
                .build();
    }

    @Override
    public GraphqlDirectiveArgumentAnnotation detectDirectiveArgumentAnnotation(Method method, Class clazz, MappedElementType mappedElementType) {
        GraphqlDirectiveArgument directiveArgumentAnnotation = MethodAnnotationLookup.findFirstAppears(method, GraphqlDirectiveArgument.class);

        if (directiveArgumentAnnotation == null) {
            return null;
        }

        String name = Utils.stringNullifyOrGetDefault(directiveArgumentAnnotation.name(), method.getName());
        MappingStatics.TypeDetails details = MappingStatics.findTypeDetails(method);

        Class type = directiveArgumentAnnotation.type() == Void.class ? details.type() : directiveArgumentAnnotation.type();
        int dimensions = directiveArgumentAnnotation.dimensions() == -1 ? details.dimension() : directiveArgumentAnnotation.dimensions();

        return GraphqlDirectiveArgumentAnnotationBuilder.newBuilder()
                .name(name)
                .nullable(directiveArgumentAnnotation.nullable())
                .type(type)
                .dimensions(dimensions)
                .valueParser(directiveArgumentAnnotation.valueParser())
                .build();
    }

    @Override
    public GraphqlDescriptionAnnotation detectDescriptionAnnotation(Method method, Class clazz, MappedElementType mappedElementType) {
        GraphqlDescription description = MethodAnnotationLookup.findFirstAppears(method, GraphqlDescription.class);

        if (description == null) {
            return null;
        }

        return new GraphqlDescriptionAnnotationImpl(description.value());
    }
}
