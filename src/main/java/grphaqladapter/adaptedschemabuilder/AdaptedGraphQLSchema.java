package grphaqladapter.adaptedschemabuilder;

import graphql.schema.GraphQLSchema;
import grphaqladapter.adaptedschemabuilder.discovered.*;
import grphaqladapter.codegenerator.ObjectConstructor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class AdaptedGraphQLSchema {

    private final GraphQLSchema schema;
    private final String sdl;
    private final List<DiscoveredElement> discoveredElements;
    private final List<DiscoveredInputType> discoveredInputTypes;
    private final List<DiscoveredInterfaceType> discoveredInterfacesTypes;
    private final List<DiscoveredObjectType> discoveredObjectTypes;
    private final List<DiscoveredUnionType> discoveredUnionTypes;
    private final List<DiscoveredEnumType> discoveredEnumTypes;
    private final List<DiscoveredScalarType> discoveredScalarTypes;
    private final List<DiscoveredDirective> discoveredDirectives;
    private final ObjectConstructor objectConstructor;
    private final TypeFinder typeFinder;
    private final ObjectBuilder objectBuilder;

    AdaptedGraphQLSchema(GraphQLSchema schema, List<DiscoveredElement> discoveredElements, ObjectConstructor objectConstructor) {
        this.schema = schema;
        this.sdl = SDLStatic.from(schema);
        this.discoveredElements = discoveredElements;
        this.discoveredInputTypes = separate(discoveredElements, DiscoveredInputType.class);
        this.discoveredInterfacesTypes = separate(discoveredElements, DiscoveredInterfaceType.class);
        this.discoveredObjectTypes = separate(discoveredElements, DiscoveredObjectType.class);
        this.discoveredUnionTypes = separate(discoveredElements, DiscoveredUnionType.class);
        this.discoveredEnumTypes = separate(discoveredElements, DiscoveredEnumType.class);
        this.discoveredScalarTypes = separate(discoveredElements, DiscoveredScalarType.class);
        this.discoveredDirectives = separate(discoveredElements, DiscoveredDirective.class);
        this.objectConstructor = objectConstructor;
        this.typeFinder = new TypeFinder(discoveredElements);
        this.objectBuilder = new ObjectBuilder(this.objectConstructor, typeFinder);
    }

    private static <T extends DiscoveredElement> List<T> separate(List<DiscoveredElement> types, Class<T> cls) {
        List<T> list = types.stream().filter(type->cls.isAssignableFrom(type.getClass()))
                .map(type->(T)type).collect(Collectors.toList());

        return Collections.unmodifiableList(list);
    }

    public GraphQLSchema getSchema() {
        return schema;
    }

    public List<DiscoveredEnumType> discoveredEnumTypes() {
        return discoveredEnumTypes;
    }


    public List<DiscoveredInterfaceType> discoveredInterfacesTypes() {
        return discoveredInterfacesTypes;
    }

    public List<DiscoveredObjectType> discoveredObjectTypes() {
        return discoveredObjectTypes;
    }

    public List<DiscoveredInputType> discoveredInputTypes() {
        return discoveredInputTypes;
    }

    public List<DiscoveredScalarType> discoveredScalarTypes() {
        return discoveredScalarTypes;
    }

    public List<DiscoveredElement> discoveredElements() {
        return discoveredElements;
    }

    public List<DiscoveredUnionType> discoveredUnionTypes() {
        return discoveredUnionTypes;
    }

    public List<DiscoveredDirective> discoveredDirectives() {
        return discoveredDirectives;
    }

    public String asSchemaDefinitionLanguage() {
        return sdl;
    }

    public TypeFinder typeFinder() {
        return typeFinder;
    }

    public ObjectBuilder objectBuilder() {
        return objectBuilder;
    }

    public ObjectConstructor objectConstructor() {
        return objectConstructor;
    }
}
