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

package graphql_adapter.adaptedschema.mapping.mapped_elements.interfaces;

import graphql_adapter.adaptedschema.assertion.Assert;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedClassBuilder;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedFieldMethod;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MappedInterfaceBuilder extends MappedClassBuilder<MappedInterfaceBuilder, MappedInterface> {

    public static MappedInterfaceBuilder newBuilder() {
        return new MappedInterfaceBuilder();
    }

    private final Map<String, MappedFieldMethod> fieldMethods = new HashMap<>();

    MappedInterfaceBuilder() {
        super(MappedElementType.INTERFACE);
    }


    @Override
    public MappedInterfaceBuilder baseClass(Class<?> baseClass) {
        Assert.isTrue(baseClass.isInterface() && !baseClass.isAnnotation(),
                new IllegalStateException("just non-annotation interface class can be base class of an interface"));
        return super.baseClass(baseClass);
    }

    @Override
    public MappedInterface build() {
        return new MappedInterfaceImpl(
                name(),
                description(),
                appliedAnnotations(),
                baseClass(),
                fieldMethods()
        );
    }

    @Override
    public MappedInterfaceBuilder copy(MappedInterface element) {
        super.copy(element);
        element.fieldMethods().values().forEach(this::addFieldMethod);
        return this;
    }

    @Override
    public MappedInterfaceBuilder refresh() {
        this.clearFieldMethods();
        return super.refresh();
    }

    public MappedInterfaceBuilder addFieldMethod(MappedFieldMethod fieldMethod) {
        Assert.isFalse(fieldMethods.containsKey(fieldMethod.name()), new IllegalStateException("input field with name [" + fieldMethod.name() + "] already exists"));
        this.fieldMethods.put(fieldMethod.name(), fieldMethod);
        return this;
    }

    public MappedInterfaceBuilder clearFieldMethods() {
        this.fieldMethods.clear();
        return this;
    }

    public Map<String, MappedFieldMethod> fieldMethods() {
        return Collections.unmodifiableMap(new HashMap<>(fieldMethods));
    }

    public MappedInterfaceBuilder removeFieldMethod(MappedFieldMethod fieldMethod) {
        this.fieldMethods.remove(fieldMethod.name(), fieldMethod);
        return this;
    }

    public MappedInterfaceBuilder removeFieldMethod(String name) {
        this.fieldMethods.remove(name);
        return this;
    }
}
