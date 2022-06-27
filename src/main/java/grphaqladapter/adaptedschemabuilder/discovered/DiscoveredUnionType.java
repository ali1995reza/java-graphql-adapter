package grphaqladapter.adaptedschemabuilder.discovered;

import graphql.schema.GraphQLUnionType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedUnionInterface;

import java.util.List;

public interface DiscoveredUnionType extends DiscoveredType<GraphQLUnionType, MappedUnionInterface> {


    List<DiscoveredObjectType> possibleTypes();
}
