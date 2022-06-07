package grphaqladapter.adaptedschemabuilder;

import graphql.schema.GraphQLSchema;
import grphaqladapter.adaptedschemabuilder.discovered.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class AdaptedGraphQLSchema {

    private final GraphQLSchema schema;
    private final String sdl;
    private final List<DiscoveredType> discoveredTypes;
    private final List<DiscoveredInputType> discoveredInputTypes;
    private final List<DiscoveredInterfaceType> discoveredInterfacesTypes;
    private final List<DiscoveredObjectType> discoveredObjectTypes;
    private final List<DiscoveredUnionType> discoveredUnionTypes;
    private final List<DiscoveredEnumType> discoveredEnumTypes;
    private final List<DiscoveredScalarType> discoveredScalarTypes;

    AdaptedGraphQLSchema(GraphQLSchema schema, List<DiscoveredType> types) {
        this.schema = schema;
        this.sdl = SDLStatic.from(schema);
        this.discoveredTypes = types;
        this.discoveredInputTypes = separate(discoveredTypes, DiscoveredInputType.class);
        this.discoveredInterfacesTypes = separate(discoveredTypes, DiscoveredInterfaceType.class);
        this.discoveredObjectTypes = separate(discoveredTypes, DiscoveredObjectType.class);
        this.discoveredUnionTypes = separate(discoveredTypes, DiscoveredUnionType.class);
        this.discoveredEnumTypes = separate(discoveredTypes, DiscoveredEnumType.class);
        this.discoveredScalarTypes = separate(discoveredTypes, DiscoveredScalarType.class);

    }

    private static <T extends DiscoveredType> List<T> separate(List<DiscoveredType> types, Class<T> cls) {
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

    public List<DiscoveredType> discoveredTypes() {
        return discoveredTypes;
    }

    public List<DiscoveredUnionType> discoveredUnionTypes() {
        return discoveredUnionTypes;
    }

    public String asSchemaDefinitionLanguage() {
        return sdl;
    }
}
