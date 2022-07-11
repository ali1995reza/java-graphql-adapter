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

import graphql_adapter.adaptedschema.functions.ValueParser;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedMethodImpl;
import graphql_adapter.adaptedschema.mapping.mapped_elements.TypeInformation;
import graphql_adapter.adaptedschema.mapping.mapped_elements.annotation.AppliedAnnotation;

import java.lang.reflect.Method;
import java.util.List;

import static graphql_adapter.adaptedschema.utils.ClassUtils.cast;

final class MappedAnnotationMethodImpl extends MappedMethodImpl implements MappedAnnotationMethod {

    private final Object defaultValue;
    private final Class<? extends ValueParser<?, ?>> valueParser;

    public MappedAnnotationMethodImpl(String name, String description, List<AppliedAnnotation> appliedAnnotations, Method method, TypeInformation<?> type, Object defaultValue, Class<? extends ValueParser<?, ?>> valueParser) {
        super(name, MappedElementType.ARGUMENT, description, appliedAnnotations, method, type);
        this.defaultValue = defaultValue;
        this.valueParser = valueParser;
    }

    @Override
    public <T> T defaultValue() {
        return cast(defaultValue);
    }

    @Override
    public Class<? extends ValueParser<?, ?>> valueParser() {
        return valueParser;
    }
}
