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
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedMethodBuilder;

public class MappedAnnotationMethodBuilder extends MappedMethodBuilder<MappedAnnotationMethodBuilder, MappedAnnotationMethod> {

    public static MappedAnnotationMethodBuilder newBuilder() {
        return new MappedAnnotationMethodBuilder();
    }

    private Class<? extends ValueParser<?, ?>> valueParser;
    private Object defaultValue;

    public MappedAnnotationMethodBuilder() {
        super(MappedElementType.ARGUMENT);
    }

    @Override
    public MappedAnnotationMethod build() {
        return new MappedAnnotationMethodImpl(
                name(),
                description(),
                appliedAnnotations(),
                method(),
                type(),
                defaultValue(),
                valueParser()
        );
    }

    @Override
    public MappedAnnotationMethodBuilder copy(MappedAnnotationMethod element) {
        return super.copy(element)
                .valueParser(element.valueParser())
                .defaultValue(element.defaultValue());
    }

    @Override
    public MappedAnnotationMethodBuilder refresh() {
        this.valueParser = null;
        this.defaultValue = null;
        return super.refresh();
    }

    public MappedAnnotationMethodBuilder defaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public Object defaultValue() {
        return defaultValue;
    }

    public MappedAnnotationMethodBuilder valueParser(Class<? extends ValueParser<?, ?>> valueParser) {
        this.valueParser = valueParser;
        return this;
    }

    public Class<? extends ValueParser<?, ?>> valueParser() {
        return valueParser;
    }
}
