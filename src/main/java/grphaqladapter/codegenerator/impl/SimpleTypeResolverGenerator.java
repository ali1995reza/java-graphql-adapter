package grphaqladapter.codegenerator.impl;

import graphql.TypeResolutionEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.TypeResolver;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredInterfaceType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredObjectType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredUnionType;
import grphaqladapter.codegenerator.TypeResolverGenerator;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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

        private final List<DiscoveredObjectType> possibleTypes;
        private ConcurrentHashMap<Class, GraphQLObjectType> resolvedTypesCache = new ConcurrentHashMap<>();

        private TypeResolverImpl(List<DiscoveredObjectType> possibleTypes) {
            this.possibleTypes = possibleTypes;
            for (DiscoveredObjectType objectType : this.possibleTypes) {
                resolvedTypesCache.put(objectType.asMappedElement().baseClass(),
                        objectType.asGraphqlElement());
            }
        }

        private TypeResolverImpl(DiscoveredUnionType unionType) {
            this(unionType.possibleTypes());
        }

        private TypeResolverImpl(DiscoveredInterfaceType interfaceType) {
            this(interfaceType.implementors());
        }

        private GraphQLObjectType findType(Class clazz) {
            for (DiscoveredObjectType objectType : possibleTypes) {
                if (objectType.asMappedElement().baseClass().isAssignableFrom(clazz))
                    return objectType.asGraphqlElement();
            }
            return null;
        }

        @Override
        public GraphQLObjectType getType(TypeResolutionEnvironment typeResolutionEnvironment) {
            Class clazz = typeResolutionEnvironment.getObject().getClass();
            return resolvedTypesCache.computeIfAbsent(clazz, this::findType);
        }
    }

}
