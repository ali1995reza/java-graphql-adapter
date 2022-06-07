package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.type;

import grphaqladapter.adaptedschemabuilder.mapper.strategy.TypeAnnotations;
import grphaqladapter.annotations.*;

public class TypeAnnotationsBuilder {

    private GraphqlTypeAnnotation typeAnnotation;
    private GraphqlInputTypeAnnotation inputTypeAnnotation;
    private GraphqlInterfaceAnnotation interfaceAnnotation;
    private GraphqlUnionAnnotation unionAnnotation;
    private GraphqlEnumAnnotation enumAnnotation;
    private GraphqlQueryAnnotation queryAnnotation;
    private GraphqlMutationAnnotation mutationAnnotation;
    private GraphqlSubscriptionAnnotation subscriptionAnnotation;
    private GraphqlDescriptionAnnotation descriptionAnnotation;

    private TypeAnnotationsBuilder() {
    }

    public static TypeAnnotationsBuilder newBuilder() {
        return new TypeAnnotationsBuilder();
    }

    public TypeAnnotationsBuilder refresh() {
        typeAnnotation = null;
        inputTypeAnnotation = null;
        interfaceAnnotation = null;
        unionAnnotation = null;
        enumAnnotation = null;
        queryAnnotation = null;
        mutationAnnotation = null;
        subscriptionAnnotation = null;
        descriptionAnnotation = null;
        return this;
    }


    public TypeAnnotationsBuilder setInputTypeAnnotation(GraphqlInputTypeAnnotation inputTypeAnnotation) {
        this.inputTypeAnnotation = inputTypeAnnotation;
        return this;
    }

    public TypeAnnotationsBuilder setInterfaceAnnotation(GraphqlInterfaceAnnotation interfaceAnnotation) {
        this.interfaceAnnotation = interfaceAnnotation;
        return this;
    }

    public TypeAnnotationsBuilder setTypeAnnotation(GraphqlTypeAnnotation typeAnnotation) {
        this.typeAnnotation = typeAnnotation;
        return this;
    }

    public TypeAnnotationsBuilder setUnionAnnotation(GraphqlUnionAnnotation unionAnnotation) {
        this.unionAnnotation = unionAnnotation;
        return this;
    }

    public TypeAnnotationsBuilder setEnumAnnotation(GraphqlEnumAnnotation enumAnnotation) {
        this.enumAnnotation = enumAnnotation;
        return this;
    }

    public TypeAnnotationsBuilder setQueryAnnotation(GraphqlQueryAnnotation queryAnnotation) {
        this.queryAnnotation = queryAnnotation;
        return this;
    }

    public TypeAnnotationsBuilder setMutationAnnotation(GraphqlMutationAnnotation mutationAnnotation) {
        this.mutationAnnotation = mutationAnnotation;
        return this;
    }

    public TypeAnnotationsBuilder setSubscriptionAnnotation(GraphqlSubscriptionAnnotation subscriptionAnnotation) {
        this.subscriptionAnnotation = subscriptionAnnotation;
        return this;
    }

    public TypeAnnotationsBuilder setDescriptionAnnotation(GraphqlDescriptionAnnotation descriptionAnnotation) {
        this.descriptionAnnotation = descriptionAnnotation;
        return this;
    }

    public TypeAnnotations build() {
        return new TypeAnnotationsImpl(typeAnnotation,
                inputTypeAnnotation,
                interfaceAnnotation,
                unionAnnotation,
                enumAnnotation,
                queryAnnotation,
                mutationAnnotation,
                subscriptionAnnotation,
                descriptionAnnotation);
    }
}
