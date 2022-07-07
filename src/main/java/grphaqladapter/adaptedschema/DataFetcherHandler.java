/*
 * Copyright 2022 Alireza Akhoundi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package grphaqladapter.adaptedschema;

import graphql.execution.directives.QueryAppliedDirective;
import graphql.execution.directives.QueryAppliedDirectiveArgument;
import graphql.language.*;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import grphaqladapter.adaptedschema.discovered.DiscoveredDirective;
import grphaqladapter.adaptedschema.discovered.DiscoveredObjectType;
import grphaqladapter.adaptedschema.functions.GraphqlDirectiveFunction;
import grphaqladapter.adaptedschema.mapping.mapped_elements.annotation.MappedAnnotation;
import grphaqladapter.adaptedschema.mapping.mapped_elements.classes.MappedObjectTypeClass;
import grphaqladapter.adaptedschema.mapping.mapped_elements.method.MappedAnnotationMethod;
import grphaqladapter.adaptedschema.mapping.mapped_elements.method.MappedFieldMethod;
import grphaqladapter.adaptedschema.system_objects.directive.*;
import grphaqladapter.adaptedschema.utils.CollectionUtils;
import grphaqladapter.adaptedschema.utils.Utils;
import grphaqladapter.codegenerator.AdaptedDataFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DataFetcherHandler implements DataFetcher<Object> {

    private final static Logger LOGGER = LoggerFactory.getLogger(DataFetcherHandler.class);

    private final AdaptedDataFetcher wrapped;
    private final Supplier<AdaptedGraphQLSchema> schemaSupplier;
    private final MappedObjectTypeClass mappedObjectTypeClass;
    private final MappedFieldMethod mappedFieldMethod;

    public DataFetcherHandler(AdaptedDataFetcher wrapped, MappedObjectTypeClass mappedObjectTypeClass, MappedFieldMethod mappedFieldMethod, Supplier<AdaptedGraphQLSchema> schemaSupplier) {
        this.wrapped = wrapped;
        this.mappedObjectTypeClass = mappedObjectTypeClass;
        this.mappedFieldMethod = mappedFieldMethod;
        this.schemaSupplier = schemaSupplier;
    }

    @Override
    public Object get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
        try {
            AdaptedGraphQLSchema schema = schemaSupplier.get();
            final Object source = dataFetchingEnvironment.getSource() != null ? dataFetchingEnvironment.getSource() : schema.objectConstructor().getInstance(mappedObjectTypeClass.baseClass());
            GraphqlDirectivesHolder directives = getDirectivesAndExecutePreHandlers(source, schema, dataFetchingEnvironment);
            Object result = wrapped.get(schema, directives, source, dataFetchingEnvironment);
            result = handleValueDirectives(schema, result, source, directives, dataFetchingEnvironment);
            return result;
        } catch (Exception e) {
            LOGGER.error("An exception occurs when handling data fetcher", e);
            e.printStackTrace();
            throw e;
        }
    }

    private GraphqlDirectivesHolder getDirectivesAndExecutePreHandlers(Object source, AdaptedGraphQLSchema schema, DataFetchingEnvironment environment) {

        OperationDirectives operationDirectives = getOperationDirectivesAndExecutePreHandlers(source, schema, environment);
        FragmentDirectives fragmentDirectives = getFragmentDirectivesAndExecutePreHandlers(source, schema, environment);
        FieldDirectives fieldDirectives = getFieldDirectivesAndExecutePreHandlers(source, schema, environment);

        return new GraphqlDirectivesHolder(operationDirectives, fragmentDirectives, fieldDirectives);
    }

    private OperationDirectives getOperationDirectivesAndExecutePreHandlers(Object source, AdaptedGraphQLSchema schema, DataFetchingEnvironment environment) {
        if (isNotEmpty(environment.getOperationDefinition().getDirectives())) {

            List<GraphqlDirectiveDetails> directivesList = getDirectives(environment.getOperationDefinition().getDirectives(), schema,
                    directive -> directive.function.preHandleOperationDirective(directive, source, environment.getOperationDefinition(), mappedFieldMethod, environment));

            return new OperationDirectives(Collections.unmodifiableList(directivesList), environment.getOperationDefinition());
        }
        return null;
    }

    private FragmentDirectives getFragmentDirectivesAndExecutePreHandlers(Object source, AdaptedGraphQLSchema schema, DataFetchingEnvironment environment) {

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
                    List<GraphqlDirectiveDetails> detailsList = getDirectives(fragment.getDirectives(), schema,
                            directive -> directive.function.preHandleFragmentDirective(directive, source, mappedFieldMethod, environment));

                    if (directivesListMap == null) {
                        directivesListMap = new HashMap<>();
                    }
                    directivesListMap.put(objectType.asMappedElement().baseClass(), new GraphqlDirectivesList(Collections.unmodifiableList(detailsList)));
                }
            }

            if (isNotEmpty(fragmentSpreads)) {
                for (FragmentSpread fragment : fragmentSpreads) {
                    FragmentDefinition fragmentDefinition = environment.getFragmentsByName().get(fragment.getName());
                    DiscoveredObjectType objectType = schema.typeFinder().findObjectTypeByName(fragmentDefinition.getTypeCondition().getName());
                    List<GraphqlDirectiveDetails> detailsList = null;
                    if (isNotEmpty(fragmentDefinition.getDirectives())) {
                        detailsList = getDirectives(fragmentDefinition.getDirectives(), schema,
                                directive -> directive.function.preHandleFragmentDirective(directive, source, mappedFieldMethod, environment));
                    }
                    if (isNotEmpty(fragment.getDirectives())) {
                        if (detailsList == null) {
                            detailsList = new ArrayList<>();
                        }
                        getDirectives(fragment.getDirectives(), schema,
                                directive -> directive.function.preHandleFragmentDirective(directive, source, mappedFieldMethod, environment),
                                detailsList
                        );
                    }
                    if (detailsList == null) {
                        continue;
                    }

                    if (directivesListMap == null) {
                        directivesListMap = new HashMap<>();
                    }
                    directivesListMap.put(objectType.asMappedElement().baseClass(), new GraphqlDirectivesList(Collections.unmodifiableList(detailsList)));
                }
            }

            if (directivesListMap != null) {
                return new FragmentDirectives(Collections.unmodifiableMap(directivesListMap));
            }
        }

        return null;
    }

    private FieldDirectives getFieldDirectivesAndExecutePreHandlers(Object source, AdaptedGraphQLSchema schema, DataFetchingEnvironment environment) {

        if (environment.getQueryDirectives() != null) {

            List<QueryAppliedDirective> appliedDirectives = environment.getQueryDirectives().getImmediateAppliedDirectivesByField().get(environment.getField());

            List<GraphqlDirectiveDetails> detailsList = getQueryDirectives(appliedDirectives, schema,
                    directive -> directive.function.preHandleFieldDirective(directive, source, mappedFieldMethod, environment));

            return new FieldDirectives(Collections.unmodifiableList(detailsList), mappedFieldMethod);
        }

        return null;
    }

    private DirectiveDetailsWithFunction getDirective(Directive directive, AdaptedGraphQLSchema schema, Consumer<DirectiveDetailsWithFunction> directiveHandler) {
        DiscoveredDirective discoveredDirective = schema.typeFinder().findDirectiveByName(directive.getName());
        GraphqlDirectiveFunction function = schema.objectConstructor().getInstance(discoveredDirective.asMappedElement()
                .functionality());
        DirectiveDetailsWithFunction details = new DirectiveDetailsWithFunction(
                discoveredDirective.asMappedElement(),
                getDirectiveArguments(directive, discoveredDirective, schema),
                function
        );
        directiveHandler.accept(details);
        return details;
    }

    private List<GraphqlDirectiveDetails> getDirectives(List<Directive> directives, AdaptedGraphQLSchema schema, Consumer<DirectiveDetailsWithFunction> directiveHandler, List<GraphqlDirectiveDetails> list) {
        if (CollectionUtils.isEmpty(directives)) {
            return list;
        }

        for (Directive directive : directives) {
            list.add(getDirective(directive, schema, directiveHandler));
        }
        return list;
    }

    private List<GraphqlDirectiveDetails> getDirectives(List<Directive> directives, AdaptedGraphQLSchema schema, Consumer<DirectiveDetailsWithFunction> directiveHandler) {
        return getDirectives(directives, schema, directiveHandler, new ArrayList<>());
    }

    private GraphqlDirectiveDetails getQueryDirective(QueryAppliedDirective appliedDirective, AdaptedGraphQLSchema schema, Consumer<DirectiveDetailsWithFunction> directiveHandler) {
        DiscoveredDirective discoveredDirective = schema.typeFinder().findDirectiveByName(appliedDirective.getName());
        GraphqlDirectiveFunction function = schema.objectConstructor().getInstance(discoveredDirective.asMappedElement()
                .functionality());
        DirectiveDetailsWithFunction details = new DirectiveDetailsWithFunction(
                discoveredDirective.asMappedElement(),
                getDirectiveArguments(appliedDirective, discoveredDirective, schema),
                function
        );
        directiveHandler.accept(details);
        return details;
    }

    private List<GraphqlDirectiveDetails> getQueryDirectives(List<QueryAppliedDirective> directives, AdaptedGraphQLSchema schema, Consumer<DirectiveDetailsWithFunction> directiveHandler) {
        if (CollectionUtils.isEmpty(directives)) {
            return Collections.emptyList();
        }

        List<GraphqlDirectiveDetails> detailsList = new ArrayList<>();
        for (QueryAppliedDirective appliedDirective : directives) {
            detailsList.add(getQueryDirective(appliedDirective, schema, directiveHandler));
        }

        return detailsList;
    }

    private static Map<String, Object> getDirectiveArguments(Directive directive, DiscoveredDirective discoveredDirective, AdaptedGraphQLSchema schema) {
        Map<String, Argument> argumentByName = Utils.getOrDefault(directive.getArgumentsByName(), Collections.emptyMap());
        Map<String, Object> arguments = new HashMap<>();
        for (MappedAnnotationMethod method : discoveredDirective.asMappedElement().mappedMethods().values()) {
            Argument argument = argumentByName.get(method.name());
            if (argument == null) {
                if (method.hasDefaultValue()) {
                    arguments.put(method.name(), method.defaultValue());
                }
            } else {
                Object val = schema.objectBuilder().buildFromValue(method.type(), argument.getValue(), true);
                if (val != null) {
                    arguments.put(method.name(), val);
                }
            }
        }

        return arguments;
    }

    private static Map<String, Object> getDirectiveArguments(QueryAppliedDirective directive, DiscoveredDirective discoveredDirective, AdaptedGraphQLSchema schema) {
        List<QueryAppliedDirectiveArgument> appliedArguments = Utils.getOrDefault(directive.getArguments(), Collections.emptyList());

        if (appliedArguments.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Object> arguments = new HashMap<>();
        for (QueryAppliedDirectiveArgument argument : appliedArguments) {
            if (argument.getArgumentValue().isSet() && argument.getArgumentValue().getValue() == null) {
                continue;
            }
            MappedAnnotationMethod method = discoveredDirective.asMappedElement()
                    .mappedMethods().get(argument.getName());
            Object val = schema.objectBuilder().buildFromObject(method.type(), argument.getValue(), false);
            if (val == null && method.hasDefaultValue()) {
                val = method.defaultValue();
            }
            if (val == null) {
                continue;
            }
            arguments.put(method.name(), val);
        }

        return arguments;
    }

    private Object handleValueDirectives(AdaptedGraphQLSchema schema, Object result, Object source, GraphqlDirectivesHolder directivesHolder, DataFetchingEnvironment environment) {
        if (directivesHolder.isOperationDirectivesPresent()) {
            for (GraphqlDirectiveDetails details : directivesHolder.operationDirectives().directives()) {
                DirectiveDetailsWithFunction detailsWithFunction = (DirectiveDetailsWithFunction) details;
                result = detailsWithFunction.function().handleOperationDirective(details, result, source, environment.getOperationDefinition(), mappedFieldMethod, environment);
            }
        }

        if (directivesHolder.isFragmentDirectivesPresent()) {
            DiscoveredObjectType objectType = schema.typeFinder().findObjectTypeByClassDeeply(result.getClass());
            for (GraphqlDirectiveDetails details : directivesHolder.fragmentDirectives().directivesByType()
                    .get(objectType.asMappedElement().baseClass()).directives()) {
                DirectiveDetailsWithFunction detailsWithFunction = (DirectiveDetailsWithFunction) details;
                result = detailsWithFunction.function().handleFragmentDirective(details, result, source, objectType.asMappedElement(), mappedFieldMethod, environment);
            }
        }

        if (directivesHolder.isFieldDirectivesPresent()) {
            for (GraphqlDirectiveDetails details : directivesHolder.fieldDirectives().directives()) {
                DirectiveDetailsWithFunction detailsWithFunction = (DirectiveDetailsWithFunction) details;
                result = detailsWithFunction.function().handleFieldDirective(details, result, source, mappedFieldMethod, environment);
            }
        }

        return result;
    }

    private static boolean isNotEmpty(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
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
