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

import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedClassImpl;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import graphql_adapter.adaptedschema.mapping.mapped_elements.annotation.AppliedAnnotation;
import graphql_adapter.adaptedschema.utils.CollectionUtils;

import java.util.List;
import java.util.Map;

final class MappedEnumImpl extends MappedClassImpl implements MappedEnum {

    private final Map<String, MappedEnumConstant> constantsByName;
    private final Map<Object, MappedEnumConstant> constantsByEnumValue;

    MappedEnumImpl(String name, String description, List<AppliedAnnotation> appliedAnnotations, Class<?> baseClass, Map<String, MappedEnumConstant> constantsByName) {
        super(name, MappedElementType.ENUM, description, appliedAnnotations, baseClass);
        this.constantsByName = CollectionUtils.getOrEmptyMap(constantsByName);
        this.constantsByEnumValue = CollectionUtils.getOrEmptyMap(
                CollectionUtils.separateToImmutableMap(this.constantsByName.values(), MappedEnumConstant.class, MappedEnumConstant::constant)
        );
    }

    @Override
    public Map<Object, MappedEnumConstant> constantsByEnumValue() {
        return constantsByEnumValue;
    }

    @Override
    public Map<String, MappedEnumConstant> constantsByName() {
        return constantsByName;
    }
}
