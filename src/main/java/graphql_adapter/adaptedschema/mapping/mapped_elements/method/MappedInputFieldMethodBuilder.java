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

import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import graphql_adapter.adaptedschema.mapping.mapped_elements.ValidatableMappedMethodBuilder;

import java.lang.reflect.Method;

public class MappedInputFieldMethodBuilder extends ValidatableMappedMethodBuilder<MappedInputFieldMethodBuilder, MappedInputFieldMethod> {

    public static MappedInputFieldMethodBuilder newBuilder() {
        return new MappedInputFieldMethodBuilder();
    }

    private Object defaultValue;
    private Method setter;

    public MappedInputFieldMethodBuilder() {
        super(MappedElementType.INPUT_FIELD);
    }

    @Override
    public MappedInputFieldMethod build() {
        return new MappedInputFieldMethodImpl(
                name(),
                description(),
                appliedAnnotations(),
                method(),
                type(),
                validators(),
                defaultValue(),
                setter()
        );
    }

    @Override
    public MappedInputFieldMethodBuilder copy(MappedInputFieldMethod element) {
        return super.copy(element)
                .setter(element.setter())
                .defaultValue(element.defaultValue());
    }

    @Override
    public MappedInputFieldMethodBuilder refresh() {
        this.setter = null;
        this.defaultValue = null;
        return super.refresh();
    }

    public MappedInputFieldMethodBuilder defaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public Object defaultValue() {
        return defaultValue;
    }

    public Method setter() {
        return setter;
    }

    public MappedInputFieldMethodBuilder setter(Method setter) {
        this.setter = setter;
        return this;
    }
}
