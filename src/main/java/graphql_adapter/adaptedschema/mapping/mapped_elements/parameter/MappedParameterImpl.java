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

import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElementImpl;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import graphql_adapter.adaptedschema.mapping.mapped_elements.TypeInformation;
import graphql_adapter.adaptedschema.mapping.mapped_elements.annotation.AppliedAnnotation;

import java.lang.reflect.Parameter;
import java.util.List;

import static graphql_adapter.adaptedschema.utils.ClassUtils.cast;

final class MappedParameterImpl extends MappedElementImpl implements MappedParameter {

    private final Object defaultValue;
    private final Parameter parameter;
    private final int index;
    private final TypeInformation<?> type;
    private final ParameterModel model;

    MappedParameterImpl(String name, MappedElementType mappedType, String description, List<AppliedAnnotation> appliedAnnotations, Object defaultValue, Parameter parameter, int index, TypeInformation<?> type, ParameterModel model) {
        super(name, mappedType, description, appliedAnnotations);
        this.defaultValue = defaultValue;
        this.parameter = parameter;
        this.index = index;
        this.type = type;
        this.model = model;
    }

    @Override
    public <T> T defaultValue() {
        return cast(defaultValue);
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public ParameterModel model() {
        return model;
    }

    @Override
    public Parameter parameter() {
        return parameter;
    }

    @Override
    public TypeInformation<?> type() {
        return type;
    }
}
