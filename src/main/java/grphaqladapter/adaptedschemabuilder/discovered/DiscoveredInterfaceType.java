package grphaqladapter.adaptedschemabuilder.discovered;

import graphql.schema.GraphQLInterfaceType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedInterface;

import java.util.List;

public interface DiscoveredInterfaceType extends DiscoveredType<GraphQLInterfaceType, MappedInterface> {

    List<DiscoveredObjectType> implementors();
}
