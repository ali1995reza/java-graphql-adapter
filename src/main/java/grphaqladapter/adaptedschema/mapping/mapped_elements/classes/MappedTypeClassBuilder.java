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

package grphaqladapter.adaptedschema.mapping.mapped_elements.classes;

import grphaqladapter.adaptedschema.assertutil.Assert;
import grphaqladapter.adaptedschema.mapping.mapped_elements.MappedClassBuilder;
import grphaqladapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import grphaqladapter.adaptedschema.mapping.mapped_elements.method.MappedFieldMethod;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MappedTypeClassBuilder extends MappedClassBuilder<MappedTypeClassBuilder, MappedObjectTypeClass> {

    public static MappedTypeClassBuilder newBuilder(MappedElementType elementType) {
        return new MappedTypeClassBuilder(elementType);
    }

    private final Map<String, MappedFieldMethod> fieldMethods = new HashMap<>();

    MappedTypeClassBuilder(MappedElementType elementType) {
        super(elementType);
        Assert.isTrue(elementType.isTopLevelType() || elementType.isObjectType(),
                new IllegalStateException("MappedTypeClass valid element types is [QUERY, MUTATION, SUBSCRIPTION, OBJECT_TYPE]"));
    }

    @Override
    public MappedObjectTypeClass build() {
        return new MappedTypeClassImpl(
                name(),
                elementType(),
                description(),
                appliedAnnotations(),
                baseClass(),
                fieldMethods()
        );
    }

    @Override
    public MappedTypeClassBuilder copy(MappedObjectTypeClass element) {
        super.copy(element);
        element.fieldMethods().values().forEach(this::addFieldMethod);
        return this;
    }

    @Override
    public MappedTypeClassBuilder refresh() {
        this.clearFieldMethods();
        return super.refresh();
    }

    public MappedTypeClassBuilder addFieldMethod(MappedFieldMethod fieldMethod) {
        Assert.isFalse(fieldMethods.containsKey(fieldMethod.name()), new IllegalStateException("input field with name [" + fieldMethod.name() + "] already exists"));
        this.fieldMethods.put(fieldMethod.name(), fieldMethod);
        return this;
    }

    public MappedTypeClassBuilder clearFieldMethods() {
        this.fieldMethods.clear();
        return this;
    }

    public Map<String, MappedFieldMethod> fieldMethods() {
        return Collections.unmodifiableMap(new HashMap<>(fieldMethods));
    }

    public MappedTypeClassBuilder removeFieldMethod(MappedFieldMethod fieldMethod) {
        this.fieldMethods.remove(fieldMethod.name(), fieldMethod);
        return this;
    }

    public MappedTypeClassBuilder removeFieldMethod(String name) {
        this.fieldMethods.remove(name);
        return this;
    }
}
