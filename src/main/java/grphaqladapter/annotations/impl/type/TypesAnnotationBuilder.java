package grphaqladapter.annotations.impl.type;

import grphaqladapter.annotations.*;

public class TypesAnnotationBuilder {

    public final static TypesAnnotationBuilder newBuilder()
    {
        return new TypesAnnotationBuilder();
    }



    private String typeName;

    private TypesAnnotationBuilder(){}

    public synchronized TypesAnnotationBuilder setTypeName(String typeName) {
        this.typeName = typeName;

        return this;
    }

    public synchronized GraphqlInterfaceAnnotation buildInterfaceAnnotation()
    {
        return new GraphqlInterfaceAnnotationImpl(typeName);
    }

    public synchronized GraphqlUnionAnnotation buildUnionAnnotation()
    {
        return new GraphqlUnionAnnotationImpl(typeName);
    }

    public synchronized GraphqlTypeAnnotation buildTypeAnnotation()
    {
        return new GraphqlTypeAnnotationImpl(typeName);
    }


    public synchronized GraphqlInputTypeAnnotation buildInputTypeAnnotation()
    {
        return new GraphqlInputTypeAnnotationImpl(typeName);
    }

    public synchronized GraphqlEnumAnnotation buildEnumAnnotation()
    {
        return new GraphqlEnumAnnotationImpl(typeName);
    }


    public synchronized GraphqlQueryAnnotation buildQueryAnnotation(){
        return new GraphqlQueryAnnotationImpl(typeName);
    }

    public synchronized GraphqlMutationAnnotation buildMutationAnnotation(){
        return new GraphqlMutationAnnotationImpl(typeName);
    }
    public synchronized GraphqlSubscriptionAnnotation buildSubscriptionAnnotation(){
        return new GraphqlSubscriptionAnnotationImpl(typeName);
    }
}
