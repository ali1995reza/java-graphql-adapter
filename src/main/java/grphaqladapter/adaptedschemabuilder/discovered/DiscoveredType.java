package grphaqladapter.adaptedschemabuilder.discovered;

import graphql.schema.GraphQLType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;

public interface DiscoveredType<T extends GraphQLType, E extends MappedClass> extends DiscoveredElement<T, E> {

}
