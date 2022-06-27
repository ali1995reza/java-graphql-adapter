package grphaqladapter.adaptedschemabuilder;

import graphql.schema.GraphQLDirective;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredDirective;
import grphaqladapter.adaptedschemabuilder.mapped.MappedAnnotation;

final class DiscoveredDirectiveImpl implements DiscoveredDirective {

    private final MappedAnnotation mappedAnnotation;
    private final String name;
    private final GraphQLDirective graphQLDirective;

    DiscoveredDirectiveImpl(MappedAnnotation mappedAnnotation, String name, GraphQLDirective graphQLDirective) {
        this.mappedAnnotation = mappedAnnotation;
        this.name = name;
        this.graphQLDirective = graphQLDirective;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public GraphQLDirective asGraphqlElement() {
        return graphQLDirective;
    }

    @Override
    public MappedAnnotation asMappedElement() {
        return mappedAnnotation;
    }
}
