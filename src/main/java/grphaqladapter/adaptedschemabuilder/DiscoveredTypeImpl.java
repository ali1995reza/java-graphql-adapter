package grphaqladapter.adaptedschemabuilder;


import graphql.schema.GraphQLType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;


abstract class DiscoveredTypeImpl<T extends GraphQLType, E extends MappedClass> implements DiscoveredType<T, E> {

    private final E mappedElement;
    private final String name;
    private final T graphQLType;

    public DiscoveredTypeImpl(E mappedElement, String name, T graphQLType) {
        this.mappedElement = mappedElement;
        this.name = name;
        this.graphQLType = graphQLType;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public T asGraphqlElement() {
        return graphQLType;
    }

    @Override
    public E asMappedElement() {
        return mappedElement;
    }

    abstract void setUnmodifiable();
}
