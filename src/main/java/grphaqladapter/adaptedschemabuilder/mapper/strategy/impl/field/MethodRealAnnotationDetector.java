package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.field;

import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.FieldAnnotations;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.MethodAnnotationDetector;
import grphaqladapter.annotations.GraphqlField;
import grphaqladapter.annotations.GraphqlInputField;
import grphaqladapter.annotations.GraphqlInputFieldAnnotation;
import grphaqladapter.annotations.impl.field.GraphqlFieldAnnotationBuilder;
import grphaqladapter.annotations.impl.field.GraphqlInputFieldAnnotationBuilder;

import java.lang.reflect.Method;

public class MethodRealAnnotationDetector implements MethodAnnotationDetector {

    private final FieldAnnotationsBuilder builder;
    private final GraphqlFieldAnnotationBuilder fieldAnnotationBuilder;
    private final GraphqlInputFieldAnnotationBuilder inputFieldAnnotationBuilder;

    public MethodRealAnnotationDetector() {
        builder = FieldAnnotationsBuilder.newBuilder();
        fieldAnnotationBuilder = GraphqlFieldAnnotationBuilder.newBuilder();
        inputFieldAnnotationBuilder = GraphqlInputFieldAnnotationBuilder.newBuilder();
    }


    @Override
    public synchronized FieldAnnotations detectAnnotationFor(Method method) {

        GraphqlField field = MethodAnnotationLookup.findFirstAppears(method , GraphqlField.class);
        GraphqlInputField inputField = MethodAnnotationLookup.findFirstAppears(method , GraphqlField.class);

        if(field==null && inputField==null)
            return null;

        if(field!=null) {
            builder.setFieldAnnotation(
                    fieldAnnotationBuilder
                            .setFieldName(field.fieldName())
                            .setInputField(field.inputField())
                            .setSetter(field.setter())
                            .setNullable(field.nullable())
                            .build()
            );
        }

        if(inputField!=null)
        {
            builder.setInputFieldAnnotation(
                    inputFieldAnnotationBuilder
                    .setInputFieldName(inputField.inputFieldName())
                    .setNullable(inputField.nullable())
                    .setSetter(inputField.setter())
                    .build()
            );
        }

        FieldAnnotations annotations = builder.build();
        refresh();
        return annotations;
    }

    private void refresh()
    {
        builder.setFieldAnnotation(null).setInputFieldAnnotation(null);
        inputFieldAnnotationBuilder.setInputFieldName(null)
                .setSetter(null);
        fieldAnnotationBuilder.setFieldName(null)
                .setInputField(false)
                .setSetter(null);
    }
}
