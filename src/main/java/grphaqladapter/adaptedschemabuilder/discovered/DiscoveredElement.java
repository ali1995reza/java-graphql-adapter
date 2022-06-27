package grphaqladapter.adaptedschemabuilder.discovered;

import graphql.schema.GraphQLSchemaElement;
import grphaqladapter.adaptedschemabuilder.mapped.MappedElement;

public interface DiscoveredElement <T extends GraphQLSchemaElement, M extends MappedElement> {

    String name();

    T asGraphqlElement();

    M asMappedElement();
}
