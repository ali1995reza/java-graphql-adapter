package grphaqladapter.adaptedschemabuilder.discovered;

import graphql.schema.GraphQLObjectType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedTypeClass;

import java.util.List;

public interface DiscoveredObjectType extends DiscoveredType<GraphQLObjectType, MappedTypeClass> {

    List<DiscoveredInterfaceType> implementedInterfaces();
}
