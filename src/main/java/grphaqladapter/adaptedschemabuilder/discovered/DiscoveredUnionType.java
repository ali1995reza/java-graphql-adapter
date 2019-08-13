package grphaqladapter.adaptedschemabuilder.discovered;

import graphql.schema.GraphQLUnionType;

import java.util.List;

public interface DiscoveredUnionType extends DiscoveredType<GraphQLUnionType> {


    List<DiscoveredObjectType> possibleTypes();
}
