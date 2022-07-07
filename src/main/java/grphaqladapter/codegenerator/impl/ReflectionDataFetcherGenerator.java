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

package grphaqladapter.codegenerator.impl;

import graphql.language.Argument;
import graphql.language.NullValue;
import graphql.schema.DataFetchingEnvironment;
import grphaqladapter.adaptedschema.AdaptedGraphQLSchema;
import grphaqladapter.adaptedschema.discovered.DiscoveredElement;
import grphaqladapter.adaptedschema.mapping.mapped_elements.classes.MappedObjectTypeClass;
import grphaqladapter.adaptedschema.mapping.mapped_elements.method.MappedFieldMethod;
import grphaqladapter.adaptedschema.mapping.mapped_elements.parameter.MappedParameter;
import grphaqladapter.adaptedschema.system_objects.directive.GraphqlDirectivesHolder;
import grphaqladapter.codegenerator.AdaptedDataFetcher;
import grphaqladapter.codegenerator.DataFetcherGenerator;

import java.lang.reflect.Parameter;
import java.util.List;

public class ReflectionDataFetcherGenerator implements DataFetcherGenerator {

    @Override
    public AdaptedDataFetcher generate(MappedObjectTypeClass cls, MappedFieldMethod method) {
        return new ReflectionDataFetcher(cls, method);
    }

    @Override
    public synchronized void init(List<DiscoveredElement> elements) {
    }

    private final static class ReflectionDataFetcher implements AdaptedDataFetcher {

        private final MappedObjectTypeClass mappedClass;
        private final MappedFieldMethod mappedFieldMethod;

        private ReflectionDataFetcher(MappedObjectTypeClass mappedClass, MappedFieldMethod mappedFieldMethod) {
            this.mappedClass = mappedClass;
            this.mappedFieldMethod = mappedFieldMethod;
        }

        @Override
        public Object get(AdaptedGraphQLSchema schema, GraphqlDirectivesHolder directivesHolder, final Object source, DataFetchingEnvironment environment) throws Exception {

            final List<MappedParameter> parameters = mappedFieldMethod.parameters();

            Object result = null;
            if (parameters == null || parameters.size() < 1) {
                result = mappedFieldMethod.method().invoke(source);
            } else {
                Object[] args = new Object[parameters.size()];
                for (int i = 0; i < parameters.size(); i++) {
                    MappedParameter parameter = parameters.get(i);
                    if (parameter.model().isAdaptedSchema()) {
                        args[i] = schema;
                    } else if (parameter.model().isDataFetchingEnvironment()) {
                        args[i] = environment;
                    } else if (parameter.model().isDirectives()) {
                        args[i] = directivesHolder;
                    } else if (parameter.model().isSkipped()) {
                        args[i] = getSkippedValue(parameter.parameter());
                    } else {
                        Object paramValue = environment.getArgument(parameter.name());
                        if (paramValue != null) {
                            args[i] = schema.objectBuilder().buildFromObject(parameter.type(), environment.getArgument(parameter.name()), false);
                        } else if (isNotSetToNull(environment.getField().getArguments(), parameter.name())) {
                            args[i] = parameter.defaultValue();
                        }

                    }
                }

                result = mappedFieldMethod.method().invoke(source, args);
            }

            return result;
        }

        private static boolean isNotSetToNull(List<Argument> arguments, String name) {
            for (int i = 0; i < arguments.size(); i++) {
                Argument argument = arguments.get(i);
                if (argument.getName().equals(name)) {
                    return !(argument.getValue() instanceof NullValue);
                }
            }
            return true;
        }

        private static Object getSkippedValue(Parameter parameter) {
            if (!parameter.getType().isPrimitive()) {
                return null;
            }
            if (parameter.getType() == boolean.class) {
                return false;
            }
            if (parameter.getType() == int.class) {
                return 0;
            }
            if (parameter.getType() == byte.class) {
                return (byte) 0;
            }
            if (parameter.getType() == long.class) {
                return (long) 0;
            }
            if (parameter.getType() == float.class) {
                return (float) 0;
            }
            if (parameter.getType() == double.class) {
                return (double) 0;
            }
            if (parameter.getType() == short.class) {
                return (short) 0;
            }

            return (char) 0;
        }

    }

}
