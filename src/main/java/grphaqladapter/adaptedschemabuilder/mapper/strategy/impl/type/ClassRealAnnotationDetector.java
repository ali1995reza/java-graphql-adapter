package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.type;

import grphaqladapter.adaptedschemabuilder.mapper.strategy.ClassAnnotationDetector;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.TypeAnnotations;
import grphaqladapter.adaptedschemabuilder.utils.Utils;
import grphaqladapter.annotations.*;
import grphaqladapter.annotations.impl.GraphqlDescriptionAnnotationImpl;
import grphaqladapter.annotations.impl.directive.GraphqlDirectiveAnnotationBuilder;
import grphaqladapter.annotations.impl.type.TypesAnnotationBuilder;

public class ClassRealAnnotationDetector implements ClassAnnotationDetector {

    public ClassRealAnnotationDetector() {
    }


    private static String typeName(String name, Class clazz) {
        return Utils.stringNullifyOrGetDefault(name, clazz.getSimpleName());
    }

    @Override
    public boolean skipType(Class clazz) {
        return clazz.getAnnotation(SkipElement.class) != null;
    }

    @Override
    public TypeAnnotations detectAnnotationFor(Class clazz) {

        GraphqlInterface interfaceAnnotation = (GraphqlInterface) clazz.getAnnotation(GraphqlInterface.class);
        GraphqlInputType inputTypeAnnotation = (GraphqlInputType) clazz.getAnnotation(GraphqlInputType.class);
        GraphqlType typeAnnotation = (GraphqlType) clazz.getAnnotation(GraphqlType.class);
        GraphqlUnion unionAnnotation = (GraphqlUnion) clazz.getAnnotation(GraphqlUnion.class);
        GraphqlEnum enumAnnotation = (GraphqlEnum) clazz.getAnnotation(GraphqlEnum.class);
        GraphqlQuery queryAnnotation = (GraphqlQuery) clazz.getAnnotation(GraphqlQuery.class);
        GraphqlMutation mutationAnnotation = (GraphqlMutation) clazz.getAnnotation(GraphqlMutation.class);
        GraphqlSubscription subscriptionAnnotation = (GraphqlSubscription) clazz.getAnnotation(GraphqlSubscription.class);
        GraphqlDirective directiveAnnotation = (GraphqlDirective) clazz.getAnnotation(GraphqlDirective.class);
        GraphqlDescription descriptionAnnotation = (GraphqlDescription) clazz.getAnnotation(GraphqlDescription.class);

        TypeAnnotationsBuilder builder = TypeAnnotationsBuilder.newBuilder();
        TypesAnnotationBuilder typeBuilder = TypesAnnotationBuilder.newBuilder();

        if (interfaceAnnotation != null) {
            builder.interfaceAnnotation(
                    typeBuilder.name(typeName(interfaceAnnotation.name(), clazz))
                            .buildInterfaceAnnotation()
            );
        }

        if (inputTypeAnnotation != null) {
            builder.inputTypeAnnotation(
                    typeBuilder.name(typeName(inputTypeAnnotation.name(), clazz))
                            .buildInputTypeAnnotation()
            );
        }

        if (typeAnnotation != null) {
            builder.typeAnnotation(
                    typeBuilder.name(typeName(typeAnnotation.name(), clazz))
                            .buildTypeAnnotation()
            );
        }

        if (unionAnnotation != null) {
            builder.unionAnnotation(
                    typeBuilder.name(typeName(unionAnnotation.name(), clazz))
                            .buildUnionAnnotation()
            );
        }


        if (enumAnnotation != null) {
            builder.enumAnnotation(
                    typeBuilder.name(typeName(enumAnnotation.name(), clazz))
                            .buildEnumAnnotation()
            );
        }

        if (queryAnnotation != null) {
            builder.queryAnnotation(
                    typeBuilder.name(typeName(queryAnnotation.name(), clazz))
                            .buildQueryAnnotation()
            );
        }

        if (mutationAnnotation != null) {
            builder.mutationAnnotation(
                    typeBuilder.name(typeName(mutationAnnotation.name(), clazz))
                            .buildMutationAnnotation()
            );
        }

        if (subscriptionAnnotation != null) {
            builder.subscriptionAnnotation(
                    typeBuilder.name(typeName(subscriptionAnnotation.name(), clazz))
                            .buildSubscriptionAnnotation()
            );
        }

        if(directiveAnnotation != null) {
            builder.directiveAnnotation(
                    GraphqlDirectiveAnnotationBuilder.newBuilder()
                            .addLocations(directiveAnnotation.locations())
                            .functionality(directiveAnnotation.functionality())
                            .name(typeName(directiveAnnotation.name(), clazz))
                            .build()
            );
        }

        if (descriptionAnnotation != null) {
            builder.descriptionAnnotation(
                    new GraphqlDescriptionAnnotationImpl(descriptionAnnotation.value())
            );
        }


        TypeAnnotations typeAnnotations = builder.build();

        return typeAnnotations;
    }
}
