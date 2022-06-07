package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.type;

import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.mapper.strategy.TypeAnnotations;
import grphaqladapter.adaptedschemabuilder.validator.TypeValidator;
import grphaqladapter.annotations.*;

class TypeAnnotationsImpl implements TypeAnnotations {

    private final GraphqlTypeAnnotation typeAnnotation;
    private final GraphqlInputTypeAnnotation inputTypeAnnotation;
    private final GraphqlInterfaceAnnotation interfaceAnnotation;
    private final GraphqlUnionAnnotation unionAnnotation;
    private final GraphqlEnumAnnotation enumAnnotation;
    private final GraphqlQueryAnnotation queryAnnotation;
    private final GraphqlMutationAnnotation mutationAnnotation;
    private final GraphqlSubscriptionAnnotation subscriptionAnnotation;
    private final GraphqlDescriptionAnnotation descriptionAnnotation;

    TypeAnnotationsImpl(GraphqlTypeAnnotation typeAnnotation, GraphqlInputTypeAnnotation inputTypeAnnotation, GraphqlInterfaceAnnotation interfaceAnnotation, GraphqlUnionAnnotation unionAnnotation, GraphqlEnumAnnotation enumAnnotation, GraphqlQueryAnnotation queryAnnotation, GraphqlMutationAnnotation mutationAnnotation, GraphqlSubscriptionAnnotation subscriptionAnnotation, GraphqlDescriptionAnnotation descriptionAnnotation) {
        this.typeAnnotation = typeAnnotation;
        this.inputTypeAnnotation = inputTypeAnnotation;
        this.interfaceAnnotation = interfaceAnnotation;
        this.unionAnnotation = unionAnnotation;
        this.enumAnnotation = enumAnnotation;
        this.queryAnnotation = queryAnnotation;
        this.mutationAnnotation = mutationAnnotation;
        this.subscriptionAnnotation = subscriptionAnnotation;
        this.descriptionAnnotation = descriptionAnnotation;

        Assert.isOneFalse("at least one type annotation needed",
                typeAnnotation == null, inputTypeAnnotation == null,
                interfaceAnnotation == null, unionAnnotation == null,
                enumAnnotation == null, queryAnnotation == null,
                mutationAnnotation == null, subscriptionAnnotation == null);

        TypeValidator.validate(typeAnnotation, true);
        TypeValidator.validate(inputTypeAnnotation, true);
        TypeValidator.validate(interfaceAnnotation, true);
        TypeValidator.validate(unionAnnotation, true);
        TypeValidator.validate(queryAnnotation, true);
        TypeValidator.validate(mutationAnnotation, true);
        TypeValidator.validate(subscriptionAnnotation, true);
    }


    @Override
    public GraphqlTypeAnnotation typeAnnotation() {
        return typeAnnotation;
    }

    @Override
    public GraphqlInputTypeAnnotation inputTypeAnnotation() {
        return inputTypeAnnotation;
    }

    @Override
    public GraphqlInterfaceAnnotation interfaceAnnotation() {
        return interfaceAnnotation;
    }

    @Override
    public GraphqlUnionAnnotation unionAnnotation() {
        return unionAnnotation;
    }

    @Override
    public GraphqlEnumAnnotation enumAnnotation() {
        return enumAnnotation;
    }

    @Override
    public GraphqlQueryAnnotation queryAnnotation() {
        return queryAnnotation;
    }

    @Override
    public GraphqlMutationAnnotation mutationAnnotation() {
        return mutationAnnotation;
    }

    @Override
    public GraphqlSubscriptionAnnotation subscriptionAnnotation() {
        return subscriptionAnnotation;
    }

    @Override
    public GraphqlDescriptionAnnotation descriptionAnnotation() {
        return descriptionAnnotation;
    }
}
