package grphaqladapter.adaptedschemabuilder;

import graphql.schema.GraphQLUnionType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredObjectType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredUnionType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class DiscoveredUnionTypeImpl extends DiscoveredTypeImpl<GraphQLUnionType> implements DiscoveredUnionType {

    private List<DiscoveredObjectType> possibleTypes;

    public DiscoveredUnionTypeImpl(MappedClass mappedClass, String name, GraphQLUnionType graphQLType) {
        super(mappedClass, name, graphQLType);
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
