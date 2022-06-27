package grphaqladapter.adaptedschemabuilder.mapper.strategy;

import grphaqladapter.annotations.*;

public interface TypeAnnotations {

    GraphqlTypeAnnotation typeAnnotation();

    GraphqlInputTypeAnnotation inputTypeAnnotation();

    GraphqlInterfaceAnnotation interfaceAnnotation();

    GraphqlUnionAnnotation unionAnnotation();

    GraphqlEnumAnnotation enumAnnotation();

    GraphqlQueryAnnotation queryAnnotation();

    GraphqlMutationAnnotation mutationAnnotation();

    GraphqlSubscriptionAnnotation subscriptionAnnotation();

    GraphqlDirectiveAnnotation directiveAnnotation();

    GraphqlDescriptionAnnotation descriptionAnnotation();
}
