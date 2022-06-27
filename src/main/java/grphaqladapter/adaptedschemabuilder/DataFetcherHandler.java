package grphaqladapter.adaptedschemabuilder;

import graphql.execution.directives.QueryAppliedDirective;
import graphql.execution.directives.QueryAppliedDirectiveArgument;
import graphql.language.*;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredDirective;
import grphaqladapter.adaptedschemabuilder.discovered.DiscoveredObjectType;
import grphaqladapter.adaptedschemabuilder.mapped.MappedAnnotation;
import grphaqladapter.adaptedschemabuilder.mapped.MappedAnnotationMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedFieldMethod;
import grphaqladapter.adaptedschemabuilder.mapped.MappedTypeClass;
import grphaqladapter.annotations.interfaces.*;
import grphaqladapter.codegenerator.AdaptedDataFetcher;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Supplier;

public class DataFetcherHandler implements DataFetcher<Object> {

    private final AdaptedDataFetcher wrapped;
    private final Supplier<AdaptedGraphQLSchema> schemaSupplier;
    private final MappedTypeClass mappedTypeClass;
    private final MappedFieldMethod mappedFieldMethod;

    public DataFetcherHandler(AdaptedDataFetcher wrapped, MappedTypeClass mappedTypeClass, MappedFieldMethod mappedFieldMethod, Supplier<AdaptedGraphQLSchema> schemaSupplier) {
        this.wrapped = wrapped;
        this.mappedTypeClass = mappedTypeClass;
        this.mappedFieldMethod = mappedFieldMethod;
        this.schemaSupplier = schemaSupplier;
    }

    @Override
    public Object get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
        AdaptedGraphQLSchema schema = schemaSupplier.get();
        final Object source = dataFetchingEnvironment.getSource() != null ? dataFetchingEnvironment.getSource() : schema.objectConstructor().getInstance(mappedTypeClass);
        GraphqlDirectivesHolder directives = directives(source, schema, dataFetchingEnvironment);
        Object result = wrapped.get(schema, directives, source, dataFetchingEnvironment);
        result = handleDirectives(schema, result, source, directives, dataFetchingEnvironment);
        return result;
    }


    private Object handleDirectives(AdaptedGraphQLSchema schema, Object result, Object source, GraphqlDirectivesHolder directivesHolder, DataFetchingEnvironment environment) {
        if (directivesHolder.operationDirectives() != null) {
            for (GraphqlDirectiveDetails details : directivesHolder.operationDirectives().directives().values()) {
                DirectiveDetailsWithFunction detailsWithFunction = (DirectiveDetailsWithFunction) details;
                result = detailsWithFunction.function().handleOperationDirective(result, source, environment.getOperationDefinition(), mappedFieldMethod, environment);
            }
        }

        if (directivesHolder.fragmentDirectives() != null) {
            DiscoveredObjectType objectType = schema.typeFinder().findObjectTypeByObject(result);
            for (GraphqlDirectiveDetails details : directivesHolder.fragmentDirectives().directivesByType()
                    .get(objectType.asMappedElement().baseClass()).directives().values()) {
                DirectiveDetailsWithFunction detailsWithFunction = (DirectiveDetailsWithFunction) details;
                result = detailsWithFunction.function().handleFragmentDirective(result, source, objectType.asMappedElement(), mappedFieldMethod, environment);
            }
        }

        if (directivesHolder.fieldDirectives() != null) {
            for (GraphqlDirectiveDetails details : directivesHolder.fieldDirectives().directives().values()) {
                DirectiveDetailsWithFunction detailsWithFunction = (DirectiveDetailsWithFunction) details;
                result = detailsWithFunction.function().handleFieldDirective(result, source, mappedFieldMethod, environment);
            }
        }

        return result;
    }

    private static boolean isNotEmpty(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    private GraphqlDirectivesHolder directives(Object source, AdaptedGraphQLSchema schema, DataFetchingEnvironment environment) {
        OperationDirectives operationDirectives = null;
        if (isNotEmpty(environment.getOperationDefinition().getDirectives())) {
            Map<Class<? extends Annotation>, GraphqlDirectiveDetails> detailsMap = new HashMap<>();
            for (Directive directive : environment.getOperationDefinition().getDirectives()) {
                DiscoveredDirective discoveredDirective = schema.typeFinder().findDirectiveByName(directive.getName());
                GraphqlDirectiveFunction function = schema.objectConstructor().getInstance(discoveredDirective.asMappedElement()
                        .functionality());
                DirectiveDetailsWithFunction details = new DirectiveDetailsWithFunction(
                        discoveredDirective.asMappedElement(),
                        getDirectiveArguments(directive, discoveredDirective, schema),
                        function
                );
                function.setDirective(details);
                function.preHandleOperationDirective(source, environment.getOperationDefinition(), mappedFieldMethod, environment);
                detailsMap.put(discoveredDirective.asMappedElement().baseClass(), details);
            }
            operationDirectives = new OperationDirectives(Collections.unmodifiableMap(detailsMap), environment.getOperationDefinition());
        }

        FragmentDirectives fragmentDirectives = null;
        if (environment.getField().getSelectionSet() != null) {
            List<InlineFragment> inlineFragments = environment.getField()
                    .getSelectionSet().getSelectionsOfType(InlineFragment.class);
            List<FragmentSpread> fragmentSpreads = environment.getField()
                    .getSelectionSet().getSelectionsOfType(FragmentSpread.class);
            Map<Class, GraphqlDirectivesList> directivesListMap = null;
            if (isNotEmpty(inlineFragments)) {
                for (InlineFragment fragment : inlineFragments) {
                    if (!isNotEmpty(fragment.getDirectives())) {
                        continue;
                    }
                    DiscoveredObjectType objectType = schema.typeFinder().findObjectTypeByName(fragment.getTypeCondition().getName());
                    Map<Class<? extends Annotation>, GraphqlDirectiveDetails> detailsMap = new HashMap<>();
                    for (Directive directive : fragment.getDirectives()) {
                        DiscoveredDirective discoveredDirective = schema.typeFinder().findDirectiveByName(directive.getName());
                        GraphqlDirectiveFunction function = schema.objectConstructor().getInstance(discoveredDirective.asMappedElement()
                                .functionality());
                        DirectiveDetailsWithFunction details = new DirectiveDetailsWithFunction(
                                discoveredDirective.asMappedElement(),
                                getDirectiveArguments(directive, discoveredDirective, schema),
                                function
                        );
                        function.setDirective(details);
                        function.preHandleFragmentDirective(source, mappedFieldMethod, environment);
                        detailsMap.put(discoveredDirective.asMappedElement().baseClass(), details);
                    }
                    if (directivesListMap == null) {
                        directivesListMap = new HashMap<>();
                    }
                    directivesListMap.put(objectType.asMappedElement().baseClass(),
                            new GraphqlDirectivesList(Collections.unmodifiableMap(detailsMap)));
                }
            }
            if (isNotEmpty(fragmentSpreads)) {
                for (FragmentSpread fragment : fragmentSpreads) {
                    FragmentDefinition fragmentDefinition = environment.getFragmentsByName().get(fragment.getName());
                    DiscoveredObjectType objectType = schema.typeFinder().findObjectTypeByName(fragmentDefinition.getTypeCondition().getName());
                    Map<Class<? extends Annotation>, GraphqlDirectiveDetails> detailsMap = new HashMap<>();
                    if (isNotEmpty(fragment.getDirectives())) {
                        for (Directive directive : fragment.getDirectives()) {
                            DiscoveredDirective discoveredDirective = schema.typeFinder().findDirectiveByName(directive.getName());
                            GraphqlDirectiveFunction function = schema.objectConstructor().getInstance(discoveredDirective.asMappedElement()
                                    .functionality());
                            DirectiveDetailsWithFunction details = new DirectiveDetailsWithFunction(
                                    discoveredDirective.asMappedElement(),
                                    getDirectiveArguments(directive, discoveredDirective, schema),
                                    function
                            );
                            function.setDirective(details);
                            function.preHandleFragmentDirective(source, mappedFieldMethod, environment);
                            detailsMap.put(discoveredDirective.asMappedElement().baseClass(), details);
                        }
                    }
                    if (isNotEmpty(fragmentDefinition.getDirectives())) {
                        for (Directive directive : fragmentDefinition.getDirectives()) {
                            DiscoveredDirective discoveredDirective = schema.typeFinder().findDirectiveByName(directive.getName());
                            GraphqlDirectiveFunction function = schema.objectConstructor().getInstance(discoveredDirective.asMappedElement()
                                    .functionality());
                            DirectiveDetailsWithFunction details = new DirectiveDetailsWithFunction(
                                    discoveredDirective.asMappedElement(),
                                    getDirectiveArguments(directive, discoveredDirective, schema),
                                    function
                            );
                            function.setDirective(details);
                            function.preHandleFragmentDirective(source, mappedFieldMethod, environment);
                            detailsMap.put(discoveredDirective.asMappedElement().baseClass(), details);
                        }
                    }
                    if (directivesListMap == null) {
                        directivesListMap = new HashMap<>();
                    }
                    directivesListMap.put(objectType.asMappedElement().baseClass(),
                            new GraphqlDirectivesList(Collections.unmodifiableMap(detailsMap)));
                }
            }

            if (directivesListMap != null) {
                fragmentDirectives = new FragmentDirectives(Collections.unmodifiableMap(directivesListMap));
            }
        }

        FieldDirectives fieldDirectives = null;
        if (environment.getQueryDirectives() != null) {
            Map<Class<? extends Annotation>, GraphqlDirectiveDetails> detailsMap = new HashMap<>();
            for (QueryAppliedDirective appliedDirective : environment.getQueryDirectives().getImmediateAppliedDirectivesByField().get(environment.getField())) {
                DiscoveredDirective discoveredDirective = schema.typeFinder().findDirectiveByName(appliedDirective.getName());
                GraphqlDirectiveFunction function = schema.objectConstructor().getInstance(discoveredDirective.asMappedElement()
                        .functionality());
                DirectiveDetailsWithFunction details = new DirectiveDetailsWithFunction(
                        discoveredDirective.asMappedElement(),
                        getDirectiveArguments(appliedDirective, discoveredDirective, schema),
                        function
                );
                function.setDirective(details);
                function.preHandleFieldDirective(source, mappedFieldMethod, environment);
                detailsMap.put(discoveredDirective.asMappedElement().baseClass(), details);
            }
            fieldDirectives = new FieldDirectives(Collections.unmodifiableMap(detailsMap), mappedFieldMethod);
        }

        if (fieldDirectives == null && operationDirectives == null && fragmentDirectives == null) {
            return GraphqlDirectivesHolder.empty();
        }

        return new GraphqlDirectivesHolder(operationDirectives, fragmentDirectives, fieldDirectives);

    }

    private static Map<String, Object> getDirectiveArguments(Directive directive, DiscoveredDirective discoveredDirective, AdaptedGraphQLSchema schema) {
        Map<String, Argument> argumentByName = directive.getArgumentsByName();
        if (argumentByName.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Object> arguments = new HashMap<>();
        for (Argument argument : argumentByName.values()) {
            MappedAnnotationMethod method = discoveredDirective.asMappedElement()
                    .mappedMethods().get(argument.getName());
            Object val = schema.objectBuilder().buildFromValue(method.type(), argument.getValue());
            if (val == null) {
                continue;
            }
            arguments.put(method.name(), val);
        }
        return arguments;
    }

    private static Map<String, Object> getDirectiveArguments(QueryAppliedDirective directive, DiscoveredDirective discoveredDirective, AdaptedGraphQLSchema schema) {
        List<QueryAppliedDirectiveArgument> appliedArguments = directive.getArguments();

        if (appliedArguments.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Object> arguments = new HashMap<>();
        for (QueryAppliedDirectiveArgument argument : appliedArguments) {
            MappedAnnotationMethod method = discoveredDirective.asMappedElement()
                    .mappedMethods().get(argument.getName());
            Object val = schema.objectBuilder().buildFromObject(method.type(), argument.getValue());
            if (val == null) {
                continue;
            }
            arguments.put(method.name(), val);
        }
        return arguments;
    }


    private final static class DirectiveDetailsWithFunction extends GraphqlDirectiveDetails {

        private final GraphqlDirectiveFunction function;

        public DirectiveDetailsWithFunction(MappedAnnotation annotation, Map<String, Object> arguments, GraphqlDirectiveFunction function) {
            super(annotation, arguments);
            this.function = function;
        }

        GraphqlDirectiveFunction function() {
            return function;
        }
    }
}
