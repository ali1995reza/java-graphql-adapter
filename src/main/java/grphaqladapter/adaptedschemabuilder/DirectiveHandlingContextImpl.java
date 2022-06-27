package grphaqladapter.adaptedschemabuilder;

import graphql.schema.TypeResolver;
import grphaqladapter.adaptedschemabuilder.assertutil.Assert;
import grphaqladapter.adaptedschemabuilder.assertutil.StringUtils;
import grphaqladapter.adaptedschemabuilder.mapped.MappedElement;
import grphaqladapter.adaptedschemabuilder.mapped.MappedInterface;
import grphaqladapter.adaptedschemabuilder.mapped.MappedTypeClass;
import grphaqladapter.adaptedschemabuilder.mapped.MappedUnionInterface;
import grphaqladapter.annotations.interfaces.SchemaDirectiveHandlingContext;
import grphaqladapter.codegenerator.AdaptedDataFetcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

final class DirectiveHandlingContextImpl implements SchemaDirectiveHandlingContext {

    private final Map<String, MappedElement> elements;
    private final Map<String, List<Function<AdaptedDataFetcher, AdaptedDataFetcher>>> dataFetchersBehavior = new HashMap<>();
    private final Map<String, List<Function<TypeResolver, TypeResolver>>> typeResolversBehavior = new HashMap<>();

    public DirectiveHandlingContextImpl(Map<String, MappedElement> elements) {
        this.elements = elements;
    }

    @Override
    public <T extends MappedElement> T getElement(String name) {
        return (T) elements.get(name);
    }

    private MappedTypeClass getMappedTypeClass(String name) {
        MappedElement element = elements.get(name);
        if (element == null || !(element instanceof MappedTypeClass)) {
            throw new IllegalStateException("can not find any type with name [" + name + "]");
        }
        return (MappedTypeClass) element;
    }

    @Override
    public synchronized <T> SchemaDirectiveHandlingContext changeDataFetcherBehavior(String parent, String field, Function<AdaptedDataFetcher<T>, AdaptedDataFetcher<T>> function) {
        Assert.isNotNull(function, new IllegalStateException("function is null"));
        MappedTypeClass typeClass = getMappedTypeClass(parent);
        Assert.isTrue(typeClass.fieldMethods().containsKey(field), new IllegalStateException("can not find field [" + field + "] in type [" + parent + "]"));
        List list = dataFetchersBehavior.computeIfAbsent(key(parent, field), x -> new ArrayList<>());
        list.add(function);
        return this;
    }

    @Override
    public synchronized SchemaDirectiveHandlingContext changeTypeResolverBehavior(String typeName, Function<TypeResolver, TypeResolver> function) {
        Assert.isNotNull(function, new IllegalStateException("function is null"));
        MappedElement element = getElement(typeName);
        Assert.isAllTrue(new IllegalStateException("can not find any type with type-resolve behavior with name [" + typeName + "]"),
                element != null,
                element instanceof MappedInterface || element instanceof MappedUnionInterface);
        List<Function<TypeResolver, TypeResolver>> list = typeResolversBehavior.computeIfAbsent(typeName, x -> new ArrayList<>());
        Assert.isNotNull(list, new IllegalStateException("[" + typeName + "] has not type-resolve behavior"));
        list.add(function);
        return this;
    }

    AdaptedDataFetcher<?> applyChanges(String parent, String field, AdaptedDataFetcher<?> dataFetcher) {
        List<Function<AdaptedDataFetcher, AdaptedDataFetcher>> list = dataFetchersBehavior.get(key(parent, field));
        if (list == null) {
            return dataFetcher;
        }
        for (Function<AdaptedDataFetcher, AdaptedDataFetcher> function : list) {
            dataFetcher = function.apply(dataFetcher);
        }
        Assert.isNotNull(dataFetcher, new NullPointerException("data fetcher is null [field:" + field + ", parent:" + parent + "]"));
        return dataFetcher;
    }

    TypeResolver applyChanges(String typeName, TypeResolver typeResolver) {
        List<Function<TypeResolver, TypeResolver>> list = typeResolversBehavior.get(typeName);
        if (list == null) {
            return typeResolver;
        }
        for (Function<TypeResolver, TypeResolver> function : list) {
            typeResolver = function.apply(typeResolver);
        }
        Assert.isNotNull(typeName, new NullPointerException("type resolver is null [type:" + typeName + "]"));
        return typeResolver;
    }

    private static String key(String parent, String field) {
        Assert.isTrue(StringUtils.isNoNullString(parent), new IllegalStateException("parent name is empty or null"));
        Assert.isTrue(StringUtils.isNoNullString(field), new IllegalStateException("field name is empty or null"));
        return parent + "_" + field;

    }
}
