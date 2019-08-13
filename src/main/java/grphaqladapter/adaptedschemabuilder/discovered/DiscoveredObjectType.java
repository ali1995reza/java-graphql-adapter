package grphaqladapter.adaptedschemabuilder.discovered;

import graphql.schema.GraphQLObjectType;

import java.util.List;

public interface DiscoveredObjectType extends DiscoveredType<GraphQLObjectType> {



    List<DiscoveredInterfaceType> implementedInterfaces();
}
