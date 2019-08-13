package grphaqladapter.adaptedschemabuilder.discovered;

import graphql.schema.GraphQLInterfaceType;

import java.util.List;

public interface DiscoveredInterfaceType extends DiscoveredType<GraphQLInterfaceType> {

    List<DiscoveredObjectType> implementors();
}
