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

import graphql_adapter.adaptedschema.mapping.mapped_elements.GraphqlValidator;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import graphql_adapter.adaptedschema.mapping.mapped_elements.TypeInformation;
import graphql_adapter.adaptedschema.mapping.mapped_elements.ValidatableMappedMethodImpl;
import graphql_adapter.adaptedschema.mapping.mapped_elements.annotation.AppliedAnnotation;

import java.lang.reflect.Method;
import java.util.List;

import static graphql_adapter.adaptedschema.utils.ClassUtils.cast;

public class MappedInputFieldMethodImpl extends ValidatableMappedMethodImpl implements MappedInputFieldMethod {

    private final Object defaultValue;
    private final Method setter;

    public MappedInputFieldMethodImpl(String name, String description, List<AppliedAnnotation> appliedAnnotations, Method method, TypeInformation<?> type, List<GraphqlValidator> validators, Object defaultValue, Method setter) {
        super(name, MappedElementType.INPUT_FIELD, description, appliedAnnotations, method, type, validators);
        this.defaultValue = defaultValue;
        this.setter = setter;
    }

    @Override
    public <T> T defaultValue() {
        return cast(defaultValue);
    }

    @Override
    public Method setter() {
        return setter;
    }
}
