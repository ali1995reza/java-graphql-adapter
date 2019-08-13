package grphaqladapter.codegenerator.impl;

import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredInterfaceType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredObjectType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredUnionType;
import grphaqladapter.codegenerator.TypeResolverGenerator;
import graphql.TypeResolutionEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.TypeResolver;

public class SimpleTypeResolverGenerator implements TypeResolverGenerator {


    private final static class TypeResolverImpl implements TypeResolver{

        private final DiscoveredObjectType[] possibleTypes;

        private TypeResolverImpl(DiscoveredUnionType unionType)
        {
            possibleTypes = new DiscoveredObjectType[unionType.possibleTypes().size()];
            for(int i=0;i<possibleTypes.length;i++)
            {
                possibleTypes[i] = unionType.possibleTypes().get(i);
            }
        }

        private TypeResolverImpl(DiscoveredInterfaceType interfaceType)
        {
            possibleTypes = new DiscoveredObjectType[interfaceType.implementors().size()];
            for(int i=0;i<possibleTypes.length;i++)
            {
                possibleTypes[i] = interfaceType.implementors().get(i);
            }
        }


        @Override
        public GraphQLObjectType getType(TypeResolutionEnvironment typeResolutionEnvironment) {

            Class objectClass = typeResolutionEnvironment.getObject().getClass();
            for(DiscoveredObjectType objectType:possibleTypes)
            {
                if(objectClass == objectType.asMappedClass().baseClass())
                    return objectType.asGraphQLType();
            }

            return null;
        }
    }


    @Override
    public TypeResolver generate(DiscoveredUnionType unionType) {

        return new TypeResolverImpl(unionType);
    }

    @Override
    public TypeResolver generate(DiscoveredInterfaceType interfaceType) {
        return new TypeResolverImpl(interfaceType);
    }


}
