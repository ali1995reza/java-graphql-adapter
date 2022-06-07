package grphaqladapter.codegenerator;

import graphql.schema.TypeResolver;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredInterfaceType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredUnionType;

public interface TypeResolverGenerator {

    TypeResolver generate(DiscoveredUnionType unionType);

    TypeResolver generate(DiscoveredInterfaceType interfaceType);
}
