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
package graphql_adapter.adaptedschema.mapping.mapped_elements.enums;

import graphql_adapter.adaptedschema.assertion.Assert;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedClassBuilder;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElementType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MappedEnumBuilder extends MappedClassBuilder<MappedEnumBuilder, MappedEnum> {

    public static MappedEnumBuilder newBuilder() {
        return new MappedEnumBuilder();
    }

    private final Map<String, MappedEnumConstant> enumConstants = new HashMap<>();

    MappedEnumBuilder() {
        super(MappedElementType.ENUM);
    }

    @Override
    public MappedEnumBuilder baseClass(Class<?> baseClass) {
        Assert.isTrue(baseClass.isEnum(), new IllegalStateException("just enum-class can be base class of an enum"));
        return super.baseClass(baseClass);
    }

    @Override
    public MappedEnum build() {
        return new MappedEnumImpl(
                name(),
                description(),
                appliedAnnotations(),
                baseClass(),
                enumConstants()
        );
    }

    @Override
    public MappedEnumBuilder copy(MappedEnum element) {
        super.copy(element);
        element.constantsByName().values().forEach(this::addEnumConstant);
        return this;
    }

    @Override
    public MappedEnumBuilder refresh() {
        this.clearConstants();
        return super.refresh();
    }

    public MappedEnumBuilder addEnumConstant(MappedEnumConstant constant) {
        Assert.isFalse(enumConstants.containsKey(constant.name()), new IllegalStateException("an enum constant with name [" + name() + "]already exists"));
        this.enumConstants.put(constant.name(), constant);
        return this;
    }

    public MappedEnumBuilder clearConstants() {
        this.enumConstants.clear();
        return this;
    }

    public Map<String, MappedEnumConstant> enumConstants() {
        return Collections.unmodifiableMap(new HashMap<>(enumConstants));
    }

    public MappedEnumBuilder removeConstant(String name) {
        Assert.isTrue(enumConstants.containsKey(name), new IllegalStateException("enum constant with name [" + name + "] does not exists"));
        this.enumConstants.remove(name);
        return this;
    }
}
