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
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedFieldMethod;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MappedObjectTypeClassBuilder extends MappedClassBuilder<MappedObjectTypeClassBuilder, MappedObjectTypeClass> {

    public static MappedObjectTypeClassBuilder newBuilder(MappedElementType elementType) {
        return new MappedObjectTypeClassBuilder(elementType);
    }

    private final Map<String, MappedFieldMethod> fieldMethods = new HashMap<>();

    MappedObjectTypeClassBuilder(MappedElementType elementType) {
        super(elementType);
        Assert.isTrue(elementType.isTopLevelType() || elementType.isObjectType(),
                new IllegalStateException("MappedTypeClass valid element types is [QUERY, MUTATION, SUBSCRIPTION, OBJECT_TYPE]"));
    }

    @Override
    public MappedObjectTypeClass build() {
        return new MappedObjectTypeClassImpl(
                name(),
                elementType(),
                description(),
                appliedAnnotations(),
                baseClass(),
                fieldMethods()
        );
    }

    @Override
    public MappedObjectTypeClassBuilder copy(MappedObjectTypeClass element) {
        super.copy(element);
        element.fieldMethods().values().forEach(this::addFieldMethod);
        return this;
    }

    @Override
    public MappedObjectTypeClassBuilder refresh() {
        this.clearFieldMethods();
        return super.refresh();
    }

    public MappedObjectTypeClassBuilder addFieldMethod(MappedFieldMethod fieldMethod) {
        Assert.isFalse(fieldMethods.containsKey(fieldMethod.name()), new IllegalStateException("input field with name [" + fieldMethod.name() + "] already exists"));
        this.fieldMethods.put(fieldMethod.name(), fieldMethod);
        return this;
    }

    public MappedObjectTypeClassBuilder clearFieldMethods() {
        this.fieldMethods.clear();
        return this;
    }

    public Map<String, MappedFieldMethod> fieldMethods() {
        return Collections.unmodifiableMap(new HashMap<>(fieldMethods));
    }

    public MappedObjectTypeClassBuilder removeFieldMethod(MappedFieldMethod fieldMethod) {
        this.fieldMethods.remove(fieldMethod.name(), fieldMethod);
        return this;
    }

    public MappedObjectTypeClassBuilder removeFieldMethod(String name) {
        this.fieldMethods.remove(name);
        return this;
    }
}
