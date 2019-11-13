package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.field;

import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.FieldAnnotations;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.MethodAnnotationDetector;
import grphaqladapter.annotations.GraphqlField;
import grphaqladapter.annotations.GraphqlFieldAnnotation;
import grphaqladapter.annotations.GraphqlInputField;
import grphaqladapter.annotations.GraphqlInputFieldAnnotation;
import grphaqladapter.annotations.impl.field.GraphqlFieldAnnotationBuilder;
import grphaqladapter.annotations.impl.field.GraphqlInputFieldAnnotationBuilder;

import java.lang.reflect.Method;

public class MethodRealAnnotationDetector implements MethodAnnotationDetector {

    private final FieldAnnotationsBuilder builder;
    private final GraphqlFieldAnnotationBuilder fieldAnnotationBuilder;
    private final GraphqlInputFieldAnnotationBuilder inputFieldAnnotationBuilder;
    private final boolean validateAnnotations;

    public MethodRealAnnotationDetector(boolean validateAnnotations) {
        this.validateAnnotations = validateAnnotations;
        builder = FieldAnnotationsBuilder.newBuilder();
        fieldAnnotationBuilder = GraphqlFieldAnnotationBuilder.newBuilder();
        inputFieldAnnotationBuilder = GraphqlInputFieldAnnotationBuilder.newBuilder();
    }

    public MethodRealAnnotationDetector() {
        this(true);
    }


    @Override
    public synchronized FieldAnnotations detectAnnotationFor(Method method, Class clazz, MappedClass.MappedType mappedType) {
        if(validateAnnotations)
            return buildAndValidate(method, clazz, mappedType);


        return build(method, clazz, mappedType);

    }


    private FieldAnnotations buildAndValidate(Method method , Class cls , MappedClass.MappedType mappedType)
    {
        GraphqlField field = MethodAnnotationLookup.findFirstAppears(method , GraphqlField.class);
        GraphqlInputField inputField = MethodAnnotationLookup.findFirstAppears(method , GraphqlInputField.class);

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
        builder.refresh();
        inputFieldAnnotationBuilder.refresh();
        fieldAnnotationBuilder.refresh();
        return annotations;
    }

    private FieldAnnotations build(Method method , Class cls , MappedClass.MappedType mappedType)
    {
        GraphqlField field = MethodAnnotationLookup.findFirstAppears(method , GraphqlField.class);
        GraphqlInputField inputField = MethodAnnotationLookup.findFirstAppears(method , GraphqlInputField.class);

        if(field==null && inputField==null)
            return null;

        if(field!=null) {
            builder.setFieldAnnotation(
                    new UnValidatedFiledAnnotation(
                            field.fieldName() ,
                            field.nullable() ,
                            field.inputField() ,
                            field.setter()
                    )
            );
        }

        if(inputField!=null)
        {
            builder.setInputFieldAnnotation(
                    new UnValidatedInputFieldAnnotation(
                            inputField.inputFieldName() ,
                            inputField.nullable() ,
                            inputField.setter()
                    )
            );
        }

        FieldAnnotations annotations = builder.build();
        builder.refresh();
        return annotations;
    }




    private static class UnValidatedInputFieldAnnotation implements GraphqlInputFieldAnnotation {

        private final String inputFieldName;
        private final boolean nullable;
        private final String setter;

        private UnValidatedInputFieldAnnotation(String inputFieldName, boolean nullable, String setter) {
            this.inputFieldName = inputFieldName;
            this.nullable = nullable;
            this.setter = setter;
        }


        @Override
        public String inputFieldName() {
            return inputFieldName;
        }

        @Override
        public boolean nullable() {
            return nullable;
        }

        @Override
        public String setter() {
            return setter;
        }
    }

    private static class UnValidatedFiledAnnotation implements GraphqlFieldAnnotation{

        private final String fieldName;
        private final boolean nullable;
        private final boolean inputFiled;
        private final String setter;

        private UnValidatedFiledAnnotation(String fieldName, boolean nullable, boolean inputFiled, String setter) {
            this.fieldName = fieldName;
            this.nullable = nullable;
            this.inputFiled = inputFiled;
            this.setter = setter;
        }


        @Override
        public String fieldName() {
            return fieldName;
        }

        @Override
        public boolean nullable() {
            return nullable;
        }

        @Override
        public boolean inputField() {
            return inputFiled;
        }

        @Override
        public String setter() {
            return setter;
        }
    }
}
