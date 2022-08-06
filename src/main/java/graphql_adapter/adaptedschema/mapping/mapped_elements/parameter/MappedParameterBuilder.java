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
package graphql_adapter.adaptedschema.mapping.mapped_elements.parameter;

import graphql_adapter.adaptedschema.assertion.Assert;
import graphql_adapter.adaptedschema.mapping.mapped_elements.GraphqlValidator;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElementBuilder;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import graphql_adapter.adaptedschema.mapping.mapped_elements.TypeInformation;
import graphql_adapter.adaptedschema.utils.CollectionUtils;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class MappedParameterBuilder extends MappedElementBuilder<MappedParameterBuilder, MappedParameter> {

    public static MappedParameter newAdaptedSchemaParameter(Parameter parameter, int index) {
        return new MappedParameterImpl(
                null,
                MappedElementType.ARGUMENT,
                null,
                Collections.emptyList(),
                Collections.emptyList()
                , null,
                parameter,
                index, TypeInformation.adaptedSchema(parameter),
                ParameterModel.ADAPTED_SCHEMA
        );
    }

    public static MappedParameterBuilder newBuilder() {
        return new MappedParameterBuilder();
    }

    public static MappedParameter newDirectiveParameter(Parameter parameter, int index) {
        return new MappedParameterImpl(
                null,
                MappedElementType.ARGUMENT,
                null,
                Collections.emptyList(),
                Collections.emptyList(),
                null,
                parameter,
                index,
                TypeInformation.directives(parameter),
                ParameterModel.DIRECTIVES
        );
    }

    public static MappedParameter newEnvironmentParameter(Parameter parameter, int index) {
        return new MappedParameterImpl(
                null,
                MappedElementType.ARGUMENT,
                null,
                Collections.emptyList(),
                Collections.emptyList(),
                null,
                parameter,
                index,
                TypeInformation.environment(parameter),
                ParameterModel.DATA_FETCHING_ENVIRONMENT
        );
    }

    public static MappedParameter newSkippedParameter(Parameter parameter, int index) {
        return new MappedParameterImpl(
                null,
                MappedElementType.ARGUMENT,
                null,
                Collections.emptyList(),
                Collections.emptyList(),
                null,
                parameter,
                index,
                TypeInformation.of(parameter),
                ParameterModel.SKIPPED
        );
    }

    private final List<GraphqlValidator> validators = new ArrayList<>();
    private Parameter parameter;
    private TypeInformation<?> type;
    private Object defaultValue;
    private int index = -1;

    public MappedParameterBuilder() {
        super(MappedElementType.ARGUMENT);
    }

    @Override
    public MappedParameter build() {
        return new MappedParameterImpl(
                name(),
                elementType(),
                description(),
                appliedAnnotations(),
                validators(),
                defaultValue(),
                parameter(),
                index(),
                type(),
                ParameterModel.SCHEMA_ARGUMENT);
    }

    @Override
    public MappedParameterBuilder copy(MappedParameter element) {
        super.copy(element)
                .parameter(element.parameter())
                .index(element.index())
                .type(element.type())
                .defaultValue(element.defaultValue());
        element.validators().forEach(this::addValidator);
        return this;
    }

    @Override
    public MappedParameterBuilder refresh() {
        this.parameter = null;
        this.type = null;
        this.defaultValue = null;
        this.index = -1;
        this.clearValidators();
        return super.refresh();
    }

    public MappedParameterBuilder addValidator(GraphqlValidator validator) {
        Assert.isNotNull(validator, new NullPointerException("validator is null"));
        this.validators.add(validator);
        return this;
    }

    public MappedParameterBuilder addValidators(GraphqlValidator... validators) {
        if (CollectionUtils.isEmpty(validators)) {
            return castThis();
        }
        for (GraphqlValidator validator : validators) {
            this.addValidator(validator);
        }
        return this;
    }

    public MappedParameterBuilder addValidators(Collection<GraphqlValidator> validators) {
        if (CollectionUtils.isEmpty(validators)) {
            return castThis();
        }
        for (GraphqlValidator validator : validators) {
            this.addValidator(validator);
        }
        return this;
    }

    public MappedParameterBuilder clearValidators() {
        this.validators.clear();
        return this;
    }

    public MappedParameterBuilder removeValidator(GraphqlValidator validator) {
        Assert.isNotNull(validator, new NullPointerException("validator is null"));
        this.validators.remove(validator);
        return this;
    }

    public List<GraphqlValidator> validators() {
        return Collections.unmodifiableList(new ArrayList<>(validators));
    }

    public MappedParameterBuilder defaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public Object defaultValue() {
        return defaultValue;
    }

    public MappedParameterBuilder index(int index) {
        this.index = index;
        return this;
    }

    public int index() {
        return index;
    }

    public MappedParameterBuilder parameter(Parameter parameter) {
        this.parameter = parameter;
        return this;
    }

    public Parameter parameter() {
        return parameter;
    }

    public MappedParameterBuilder type(TypeInformation<?> type) {
        this.type = type;
        return this;
    }

    public TypeInformation<?> type() {
        return type;
    }
}
