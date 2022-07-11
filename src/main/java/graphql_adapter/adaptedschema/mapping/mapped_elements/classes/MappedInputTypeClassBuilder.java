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
package graphql_adapter.adaptedschema.mapping.mapped_elements.classes;

import graphql_adapter.adaptedschema.assertion.Assert;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedClassBuilder;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedInputFieldMethod;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MappedInputTypeClassBuilder extends MappedClassBuilder<MappedInputTypeClassBuilder, MappedInputTypeClass> {

    public static MappedInputTypeClassBuilder newBuilder() {
        return new MappedInputTypeClassBuilder();
    }

    private final Map<String, MappedInputFieldMethod> inputFieldMethods = new HashMap<>();

    MappedInputTypeClassBuilder() {
        super(MappedElementType.INPUT_TYPE);
    }

    @Override
    public MappedInputTypeClass build() {
        return new MappedInputTypeClassImpl(
                name(),
                description(),
                appliedAnnotations(),
                baseClass(),
                inputFieldMethods()
        );
    }

    @Override
    public MappedInputTypeClassBuilder copy(MappedInputTypeClass element) {
        super.copy(element);
        element.inputFiledMethods().values().forEach(this::addInputFieldMethod);
        return this;
    }

    @Override
    public MappedInputTypeClassBuilder refresh() {
        this.clearInputFieldMethods();
        return super.refresh();
    }

    public MappedInputTypeClassBuilder addInputFieldMethod(MappedInputFieldMethod inputFieldMethod) {
        Assert.isFalse(inputFieldMethods.containsKey(inputFieldMethod.name()), new IllegalStateException("input field with name [" + inputFieldMethod.name() + "] already exists"));
        inputFieldMethods.put(inputFieldMethod.name(), inputFieldMethod);
        return this;
    }

    public MappedInputTypeClassBuilder clearInputFieldMethods() {
        this.inputFieldMethods.clear();
        return this;
    }

    public Map<String, MappedInputFieldMethod> inputFieldMethods() {
        return Collections.unmodifiableMap(new HashMap<>(inputFieldMethods));
    }

    public MappedInputTypeClassBuilder removeInputFieldMethod(MappedInputFieldMethod inputFieldMethod) {
        inputFieldMethods.remove(inputFieldMethod.name(), inputFieldMethod);
        return this;
    }

    public MappedInputTypeClassBuilder removeInputFieldMethod(String name) {
        inputFieldMethods.remove(name);
        return this;
    }
}
