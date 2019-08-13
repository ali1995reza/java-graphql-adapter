package grphaqladapter.adaptedschemabuilder;

import graphql.schema.GraphQLSchema;
import grphaqladapter.adaptedschemabuilder.discovered.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AdaptedGraphQLSchema {

    private final GraphQLSchema schema;
    private final String sdl;
    private final List<DiscoveredType> discoveredTypes;
    private final List<DiscoveredInterfaceType> discoveredInterfacesTypes;
    private final List<DiscoveredObjectType> discoveredObjectTypes;
    private final List<DiscoveredUnionType> discoveredUnionTypes;
    private final List<DiscoveredEnumType> discoveredEnumTypes;

    AdaptedGraphQLSchema(GraphQLSchema schema  , List<DiscoveredType> types)
    {
        this.schema = schema;
        sdl = SDLStatic.from(schema);
        discoveredTypes = Collections.unmodifiableList(types);
        discoveredInterfacesTypes = separateAllInterfaceTypes();
        discoveredObjectTypes = separateAllObjectTypes();
        discoveredUnionTypes = separateAllUnionTypes();
        discoveredEnumTypes = separateAllEnumTypes();

    }

    private List<DiscoveredInterfaceType> separateAllInterfaceTypes()
    {
        List<DiscoveredInterfaceType> list = new ArrayList<>();
        for(DiscoveredType type:discoveredTypes)
        {
            if(type instanceof DiscoveredInterfaceType)
            {
                list.add((DiscoveredInterfaceTypeImpl)type);
            }
        }

        return Collections.unmodifiableList(list);
    }

    private List<DiscoveredObjectType> separateAllObjectTypes()
    {
        List<DiscoveredObjectType> list = new ArrayList<>();
        for(DiscoveredType type:discoveredTypes)
        {
            if(type instanceof DiscoveredObjectType)
            {
                list.add((DiscoveredObjectType)type);
            }
        }

        return Collections.unmodifiableList(list);
    }

    private List<DiscoveredUnionType> separateAllUnionTypes()
    {
        List<DiscoveredUnionType> list = new ArrayList<>();
        for(DiscoveredType type:discoveredTypes)
        {
            if(type instanceof DiscoveredUnionType)
            {
                list.add((DiscoveredUnionType)type);
            }
        }

        return Collections.unmodifiableList(list);
    }

    private List<DiscoveredEnumType> separateAllEnumTypes()
    {
        List<DiscoveredEnumType> list = new ArrayList<>();
        for(DiscoveredType type:discoveredTypes)
        {
            if(type instanceof DiscoveredEnumType)
            {
                list.add((DiscoveredEnumType)type);
            }
        }

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
