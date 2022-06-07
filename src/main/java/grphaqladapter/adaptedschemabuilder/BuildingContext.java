package grphaqladapter.adaptedschemabuilder;


import graphql.schema.GraphQLTypeReference;
import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;


interface BuildingContext {

    GraphQLTypeReference getInputObjectTypeFor(Class c);

    GraphQLTypeReference getInputTypeFor(Class c);

    GraphQLTypeReference geOutputTypeFor(Class c);

    GraphQLTypeReference getScalarTypeFor(Class c);

    GraphQLTypeReference getObjectTypeFor(Class c);

    GraphQLTypeReference getInterfaceFor(Class c);

    GraphQLTypeReference getEnumFor(Class c);

    GraphQLTypeReference getUnionTypeFor(Class c);

    boolean isAnInterface(Class cls);

    boolean isAnUnion(Class cls);

    void addToPossibleTypesOf(MappedClass mappedClass, MappedClass possible);

    MappedClass getMappedClassFor(Class cls, MappedClass.MappedType mappedType);
}
