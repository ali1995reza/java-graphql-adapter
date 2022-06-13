package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.type;

import grphaqladapter.adaptedschemabuilder.mapper.strategy.ClassAnnotationDetector;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.TypeAnnotations;
import grphaqladapter.adaptedschemabuilder.utils.Utils;
import grphaqladapter.annotations.*;
import grphaqladapter.annotations.impl.GraphqlDescriptionAnnotationImpl;
import grphaqladapter.annotations.impl.type.TypesAnnotationBuilder;

public class ClassRealAnnotationDetector implements ClassAnnotationDetector {

    private final TypeAnnotationsBuilder builder;
    private final TypesAnnotationBuilder typeBuilder;

    public ClassRealAnnotationDetector() {
        this.builder = TypeAnnotationsBuilder.newBuilder();
        typeBuilder = TypesAnnotationBuilder.newBuilder();
    }


    private static String typeName(String name, Class clazz) {
        return Utils.stringNullifyOrGetDefault(name, clazz.getSimpleName());
    }

    @Override
    public synchronized TypeAnnotations detectAnnotationFor(Class cls) {

        GraphqlInterface interfaceAnnotation = (GraphqlInterface) cls.getAnnotation(GraphqlInterface.class);
        GraphqlInputType inputTypeAnnotation = (GraphqlInputType) cls.getAnnotation(GraphqlInputType.class);
        GraphqlType typeAnnotation = (GraphqlType) cls.getAnnotation(GraphqlType.class);
        GraphqlUnion unionAnnotation = (GraphqlUnion) cls.getAnnotation(GraphqlUnion.class);
        GraphqlEnum enumAnnotation = (GraphqlEnum) cls.getAnnotation(GraphqlEnum.class);
        GraphqlQuery queryAnnotation = (GraphqlQuery) cls.getAnnotation(GraphqlQuery.class);
        GraphqlMutation mutationAnnotation = (GraphqlMutation) cls.getAnnotation(GraphqlMutation.class);
        GraphqlSubscription subscriptionAnnotation = (GraphqlSubscription) cls.getAnnotation(GraphqlSubscription.class);
        GraphqlDescription descriptionAnnotation = (GraphqlDescription) cls.getAnnotation(GraphqlDescription.class);

        builder.refresh();

        if (interfaceAnnotation != null) {
            builder.setInterfaceAnnotation(
                    typeBuilder.setTypeName(typeName(interfaceAnnotation.typeName(), cls))
                            .buildInterfaceAnnotation()
            );
        }

        if (inputTypeAnnotation != null) {
            builder.setInputTypeAnnotation(
                    typeBuilder.setTypeName(typeName(inputTypeAnnotation.typeName(), cls))
                            .buildInputTypeAnnotation()
            );
        }

        if (typeAnnotation != null) {
            builder.setTypeAnnotation(
                    typeBuilder.setTypeName(typeName(typeAnnotation.typeName(), cls))
                            .buildTypeAnnotation()
            );
        }

        if (unionAnnotation != null) {
            builder.setUnionAnnotation(
                    typeBuilder.setTypeName(typeName(unionAnnotation.typeName(), cls))
                            .buildUnionAnnotation()
            );
        }


        if (enumAnnotation != null) {
            builder.setEnumAnnotation(
                    typeBuilder.setTypeName(typeName(enumAnnotation.typeName(), cls))
                            .buildEnumAnnotation()
            );
        }

        if (queryAnnotation != null) {
            builder.setQueryAnnotation(
                    typeBuilder.setTypeName(typeName(queryAnnotation.typeName(), cls))
                            .buildQueryAnnotation()
            );
        }

        if (mutationAnnotation != null) {
            builder.setMutationAnnotation(
                    typeBuilder.setTypeName(typeName(mutationAnnotation.typeName(), cls))
                            .buildMutationAnnotation()
            );
        }

        if (subscriptionAnnotation != null) {
            builder.setSubscriptionAnnotation(
                    typeBuilder.setTypeName(typeName(subscriptionAnnotation.typeName(), cls))
                            .buildSubscriptionAnnotation()
            );
        }

        if (descriptionAnnotation != null) {
            builder.setDescriptionAnnotation(
                    new GraphqlDescriptionAnnotationImpl(descriptionAnnotation.value())
            );
        }

        TypeAnnotations typeAnnotations = builder.build();

        return typeAnnotations;
    }
}
