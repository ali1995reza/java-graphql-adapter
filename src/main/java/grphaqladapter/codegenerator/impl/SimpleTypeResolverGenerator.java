package grphaqladapter.codegenerator.impl;

import graphql.TypeResolutionEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.TypeResolver;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredInterfaceType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredObjectType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredUnionType;
import grphaqladapter.codegenerator.TypeResolverGenerator;

public class SimpleTypeResolverGenerator implements TypeResolverGenerator {


    @Override
    public TypeResolver generate(DiscoveredUnionType unionType) {

        return new TypeResolverImpl(unionType);
    }

    @Override
    public TypeResolver generate(DiscoveredInterfaceType interfaceType) {
        return new TypeResolverImpl(interfaceType);
    }

    private final static class TypeResolverImpl implements TypeResolver {

        private final DiscoveredObjectType[] possibleTypes;

        private TypeResolverImpl(DiscoveredUnionType unionType) {
            possibleTypes = new DiscoveredObjectType[unionType.possibleTypes().size()];
            for (int i = 0; i < possibleTypes.length; i++) {
                possibleTypes[i] = unionType.possibleTypes().get(i);
            }
        }

        private TypeResolverImpl(DiscoveredInterfaceType interfaceType) {
            possibleTypes = new DiscoveredObjectType[interfaceType.implementors().size()];
            for (int i = 0; i < possibleTypes.length; i++) {
                possibleTypes[i] = interfaceType.implementors().get(i);
            }
        }


        @Override
        public GraphQLObjectType getType(TypeResolutionEnvironment typeResolutionEnvironment) {

            Class objectClass = typeResolutionEnvironment.getObject().getClass();
            for (DiscoveredObjectType objectType : possibleTypes) {
                if (objectType.asMappedClass().baseClass().isAssignableFrom(objectClass))
                    return objectType.asGraphQLType();
            }

            return null;
        }
    }

}
