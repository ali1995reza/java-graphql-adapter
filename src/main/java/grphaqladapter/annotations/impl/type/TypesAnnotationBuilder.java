package grphaqladapter.annotations.impl.type;

import grphaqladapter.annotations.*;

public class TypesAnnotationBuilder {

    private String name;

    public static TypesAnnotationBuilder newBuilder() {
        return new TypesAnnotationBuilder();
    }

    private TypesAnnotationBuilder() {
    }

    public TypesAnnotationBuilder name(String name) {
        this.name = name;
        return this;
    }

    public String name() {
        return name;
    }

    public GraphqlInterfaceAnnotation buildInterfaceAnnotation() {
        return new GraphqlInterfaceAnnotationImpl(name);
    }

    public GraphqlUnionAnnotation buildUnionAnnotation() {
        return new GraphqlUnionAnnotationImpl(name);
    }

    public GraphqlTypeAnnotation buildTypeAnnotation() {
        return new GraphqlTypeAnnotationImpl(name);
    }


    public GraphqlInputTypeAnnotation buildInputTypeAnnotation() {
        return new GraphqlInputTypeAnnotationImpl(name);
    }

    public GraphqlEnumAnnotation buildEnumAnnotation() {
        return new GraphqlEnumAnnotationImpl(name);
    }


    public GraphqlQueryAnnotation buildQueryAnnotation() {
        return new GraphqlQueryAnnotationImpl(name);
    }

    public GraphqlMutationAnnotation buildMutationAnnotation() {
        return new GraphqlMutationAnnotationImpl(name);
    }

    public GraphqlSubscriptionAnnotation buildSubscriptionAnnotation() {
        return new GraphqlSubscriptionAnnotationImpl(name);
    }
}
