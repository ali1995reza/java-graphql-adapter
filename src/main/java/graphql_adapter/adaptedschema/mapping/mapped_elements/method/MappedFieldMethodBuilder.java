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
package graphql_adapter.adaptedschema.mapping.mapped_elements.method;

import graphql_adapter.adaptedschema.assertion.Assert;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedMethodBuilder;
import graphql_adapter.adaptedschema.mapping.mapped_elements.parameter.MappedParameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class MappedFieldMethodBuilder extends MappedMethodBuilder<MappedFieldMethodBuilder, MappedFieldMethod> {

    public static MappedFieldMethodBuilder newBuilder() {
        return new MappedFieldMethodBuilder();
    }

    private final List<MappedParameter> parameters = new ArrayList<>();

    public MappedFieldMethodBuilder() {
        super(MappedElementType.FIELD);
    }

    @Override
    public MappedFieldMethod build() {
        return new MappedFieldMethodImpl(
                name(),
                description(),
                appliedAnnotations(),
                method(),
                type(),
                parameters()
        );
    }

    @Override
    public MappedFieldMethodBuilder copy(MappedFieldMethod element) {
        super.copy(element);
        element.parameters().forEach(this::addParameter);
        return this;
    }

    @Override
    public MappedFieldMethodBuilder refresh() {
        this.clearParameters();
        return super.refresh();
    }

    public MappedFieldMethodBuilder addParameter(MappedParameter parameter) {
        Assert.isFalse(parameters.stream().anyMatch(ParameterNamePredict.of(parameter)), new IllegalStateException("parameter with name [" + parameter.name() + "] already exists"));
        parameters.add(parameter);
        return this;
    }

    public MappedFieldMethodBuilder clearParameters() {
        this.parameters.clear();
        return this;
    }

    public List<MappedParameter> parameters() {
        return Collections.unmodifiableList(new ArrayList<>(parameters));
    }

    public MappedFieldMethodBuilder removeParameter(String parameter) {
        for (MappedParameter param : parameters) {
            if (param.name().equals(parameter)) {
                parameters.remove(param);
                break;
            }
        }
        return this;
    }

    public MappedFieldMethodBuilder removeParameter(MappedParameter parameter) {
        parameters.remove(parameter);
        return this;
    }

    private final static class ParameterNamePredict implements Predicate<MappedParameter> {

        private final String parameterName;

        private ParameterNamePredict(String parameterName) {
            this.parameterName = parameterName;
        }

        @Override
        public boolean test(MappedParameter parameter) {
            if (parameter.name() == null) {
                return false;
            }
            return parameter.name().equals(parameterName);
        }

        private static Predicate<MappedParameter> of(String name) {
            return new ParameterNamePredict(name);
        }

        private static Predicate<MappedParameter> of(MappedParameter parameter) {
            return of(parameter.name());
        }
    }
}
