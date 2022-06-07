package grphaqladapter.adaptedschemabuilder;


import graphql.schema.GraphQLType;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedClass;


abstract class DiscoveredTypeImpl<T extends GraphQLType> implements DiscoveredType<T> {

    private final MappedClass mappedClass;
    private final String name;
    private final T graphQLType;

    public DiscoveredTypeImpl(MappedClass mappedClass, String name, T graphQLType) {
        this.mappedClass = mappedClass;
        this.name = name;
        this.graphQLType = graphQLType;
    }

    @Override
    public String typeName() {
        return name;
    }

    @Override
    public T asGraphQLType() {
        return graphQLType;
    }

    @Override
    public MappedClass asMappedClass() {
        return mappedClass;
    }

    abstract void setUnmodifiable();
}
