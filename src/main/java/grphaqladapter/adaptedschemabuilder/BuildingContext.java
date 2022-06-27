package grphaqladapter.adaptedschemabuilder;


import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLTypeReference;
import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;
import grphaqladapter.adaptedschemabuilder.mapped.MappedElement;
import grphaqladapter.adaptedschemabuilder.mapped.MappedElementType;
import grphaqladapter.annotations.interfaces.GraphqlDirectiveDetails;
import grphaqladapter.annotations.interfaces.SchemaDirectiveHandlingContext;
import grphaqladapter.codegenerator.ObjectConstructor;

import java.util.List;


interface BuildingContext {

    GraphQLTypeReference getInputObjectTypeFor(Class c);

    GraphQLTypeReference getInputTypeFor(Class c);

    GraphQLTypeReference geOutputTypeFor(Class c);

    GraphQLTypeReference getScalarTypeFor(Class c);

    GraphQLTypeReference getObjectTypeFor(Class c);

    GraphQLTypeReference getInterfaceFor(Class c);

    GraphQLTypeReference getEnumFor(Class c);

    GraphQLTypeReference getUnionTypeFor(Class c);

    GraphQLDirective getDirectiveFor(Class c);

    boolean isAnInterface(Class cls);

    boolean isAnUnion(Class cls);

    void addToPossibleTypesOf(MappedClass mappedClass, MappedClass possible);

    MappedClass getMappedClassFor(Class cls, MappedElementType mappedElementType);

    List<GraphqlDirectiveDetails> resolveDirective(MappedElement element);

    ObjectConstructor objectConstructor();

    SchemaDirectiveHandlingContext directiveHandlingContext();
}
