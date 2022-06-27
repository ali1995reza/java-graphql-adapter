package grphaqladapter.adaptedschemabuilder;

import graphql.schema.GraphQLUnionType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredObjectType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredUnionType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedUnionInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class DiscoveredUnionTypeImpl extends DiscoveredTypeImpl<GraphQLUnionType, MappedUnionInterface> implements DiscoveredUnionType {

    private List<DiscoveredObjectType> possibleTypes;

    public DiscoveredUnionTypeImpl(MappedUnionInterface mappedUnionInterface, String name, GraphQLUnionType graphQLType) {
        super(mappedUnionInterface, name, graphQLType);
        possibleTypes = new ArrayList<>();
    }

    public List<DiscoveredObjectType> possibleTypes() {
        return possibleTypes;
    }

    @Override
    void setUnmodifiable() {
        possibleTypes = Collections.unmodifiableList(possibleTypes);
    }
}
