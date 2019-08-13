package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.type;

import grphaqladapter.adaptedschemabuilder.mapper.strategy.ClassAnnotationDetector;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.TypeAnnotations;
import grphaqladapter.annotations.*;
import grphaqladapter.annotations.impl.type.TypesAnnotationBuilder;

import java.net.StandardSocketOptions;

public class ClassRealAnnotationDetector implements ClassAnnotationDetector {

    private final TypeAnnotationsBuilder builder;
    private final TypesAnnotationBuilder typeBuilder;

    public ClassRealAnnotationDetector() {
        this.builder = TypeAnnotationsBuilder.newBuilder();
        typeBuilder = TypesAnnotationBuilder.newBuilder();
    }


    @Override
    public synchronized TypeAnnotations detectAnnotationFor(Class cls) {
        refresh();

        GraphqlInterface interfaceAnnotation = (GraphqlInterface) cls.getAnnotation(GraphqlInterface.class);
        GraphqlInputType inputTypeAnnotation = (GraphqlInputType) cls.getAnnotation(GraphqlInputType.class);
        GraphqlType typeAnnotation = (GraphqlType)cls.getAnnotation(GraphqlType.class);
        GraphqlUnion unionAnnotation = (GraphqlUnion) cls.getAnnotation(GraphqlUnion.class);
        GraphqlEnum enumAnnotation = (GraphqlEnum) cls.getAnnotation(GraphqlEnum.class);
        GraphqlQuery queryAnnotation = (GraphqlQuery) cls.getAnnotation(GraphqlQuery.class);
        GraphqlMutation mutationAnnotation = (GraphqlMutation)cls.getAnnotation(GraphqlMutation.class);
        GraphqlSubscription subscriptionAnnotation = (GraphqlSubscription) cls.getAnnotation(GraphqlSubscription.class);


        if(interfaceAnnotation!=null)
        {
            builder.setInterfaceAnnotation(
                    typeBuilder.setTypeName(interfaceAnnotation.typeName())
                            .buildInterfaceAnnotation()
            );
        }

        if(inputTypeAnnotation!=null)
        {
            builder.setInputTypeAnnotation(
                    typeBuilder.setTypeName(inputTypeAnnotation.typeName())
                            .buildInputTypeAnnotation()
            );
        }

        if(typeAnnotation!=null)
        {
            builder.setTypeAnnotation(
                    typeBuilder.setTypeName(typeAnnotation.typeName())
                            .buildTypeAnnotation()
            );
        }

        if(unionAnnotation!=null)
        {
            builder.setUnionAnnotation(
                    typeBuilder.setTypeName(unionAnnotation.typeName())
                            .buildUnionAnnotation()
            );
        }


        if(enumAnnotation!=null)
        {
            builder.setEnumAnnotation(
                    typeBuilder.setTypeName(enumAnnotation.typeName())
                            .buildEnumAnnotation()
            );
        }

        if(queryAnnotation!=null)
        {
            builder.setQueryAnnotation(
                    typeBuilder.setTypeName(queryAnnotation.typeName())
                    .buildQueryAnnotation()
            );
        }

        if(mutationAnnotation!=null)
        {
            builder.setMutationAnnotation(
                    typeBuilder.setTypeName(mutationAnnotation.typeName())
                            .buildMutationAnnotation()
            );
        }

        if(subscriptionAnnotation!=null)
        {
            builder.setSubscriptionAnnotation(
                    typeBuilder.setTypeName(subscriptionAnnotation.typeName())
                            .buildSubscriptionAnnotation()
            );
        }


        TypeAnnotations typeAnnotations = builder.build();

        return typeAnnotations;
    }


    public void refresh()
    {
        builder.setEnumAnnotation(null)
                .setTypeAnnotation(null)
                .setInputTypeAnnotation(null)
                .setInterfaceAnnotation(null)
                .setQueryAnnotation(null)
                .setMutationAnnotation(null)
                .setSubscriptionAnnotation(null)
                .setUnionAnnotation(null);
    }
}
