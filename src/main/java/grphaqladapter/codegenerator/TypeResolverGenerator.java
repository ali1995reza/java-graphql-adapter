package grphaqladapter.codegenerator;

import graphql.schema.TypeResolver;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredInterfaceType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredUnionType;

public interface TypeResolverGenerator {

    public TypeResolver generate(DiscoveredUnionType unionType);
    public TypeResolver generate(DiscoveredInterfaceType interfaceType);
}
