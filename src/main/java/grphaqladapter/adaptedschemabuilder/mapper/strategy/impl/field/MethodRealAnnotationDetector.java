package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.field;

import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.MethodAnnotationDetector;
import grphaqladapter.adaptedschemabuilder.utils.Utils;
import grphaqladapter.annotations.*;
import grphaqladapter.annotations.impl.GraphqlDescriptionAnnotationImpl;
import grphaqladapter.annotations.impl.field.GraphqlFieldAnnotationBuilder;
import grphaqladapter.annotations.impl.field.GraphqlInputFieldAnnotationBuilder;

import java.lang.reflect.Method;

public class MethodRealAnnotationDetector implements MethodAnnotationDetector {

    private final GraphqlFieldAnnotationBuilder fieldAnnotationBuilder;
    private final GraphqlInputFieldAnnotationBuilder inputFieldAnnotationBuilder;

    public MethodRealAnnotationDetector() {
        fieldAnnotationBuilder = GraphqlFieldAnnotationBuilder.newBuilder();
        inputFieldAnnotationBuilder = GraphqlInputFieldAnnotationBuilder.newBuilder();
    }

    @Override
    public GraphqlFieldAnnotation detectFieldAnnotation(Method method, Class clazz, MappedClass.MappedType mappedType) {
        GraphqlField fieldAnnotation = MethodAnnotationLookup.findFirstAppears(method, GraphqlField.class);

        if (fieldAnnotation == null) {
            return null;
        }

        String name = Utils.stringNullifyOrGetDefault(fieldAnnotation.fieldName(), method.getName());

        return fieldAnnotationBuilder
                .refresh()
                .setFieldName(name)
                .setNullable(fieldAnnotation.nullable())
                .build();
    }

    @Override
    public GraphqlInputFieldAnnotation detectInputFieldAnnotation(Method method, Class clazz, MappedClass.MappedType mappedType) {
        GraphqlInputField inputFieldAnnotation = MethodAnnotationLookup.findFirstAppears(method, GraphqlInputField.class);

        if (inputFieldAnnotation == null) {
            return null;
        }

        String name = Utils.stringNullifyOrGetDefault(inputFieldAnnotation.inputFieldName(), method.getName());

        return inputFieldAnnotationBuilder
                .refresh()
                .setInputFieldName(name)
                .setNullable(inputFieldAnnotation.nullable())
                .setSetter(inputFieldAnnotation.setter())
                .build();
    }

    @Override
    public GraphqlDescriptionAnnotation detectDescriptionAnnotation(Method method, Class clazz, MappedClass.MappedType mappedType) {
        GraphqlDescription description = MethodAnnotationLookup.findFirstAppears(method, GraphqlDescription.class);

        if (description == null) {
            return null;
        }

        return new GraphqlDescriptionAnnotationImpl(description.value());
    }

}
