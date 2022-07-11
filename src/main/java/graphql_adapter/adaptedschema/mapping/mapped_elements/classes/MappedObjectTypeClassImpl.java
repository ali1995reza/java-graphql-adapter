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
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedClassImpl;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import graphql_adapter.adaptedschema.mapping.mapped_elements.annotation.AppliedAnnotation;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedFieldMethod;
import graphql_adapter.adaptedschema.utils.CollectionUtils;

import java.util.List;
import java.util.Map;

final class MappedObjectTypeClassImpl extends MappedClassImpl implements MappedObjectTypeClass {

    private final Map<String, MappedFieldMethod> fieldMethods;

    MappedObjectTypeClassImpl(String name, MappedElementType elementType, String description, List<AppliedAnnotation> appliedAnnotations, Class<?> baseClass, Map<String, MappedFieldMethod> fieldMethods) {
        super(name, elementType, description, appliedAnnotations, baseClass);
        Assert.isTrue(elementType.isTopLevelType() || elementType.isObjectType(),
                new IllegalStateException("MappedTypeClass valid element types is [QUERY, MUTATION, SUBSCRIPTION, OBJECT_TYPE]"));
        this.fieldMethods = CollectionUtils.getOrEmptyMap(fieldMethods);
    }

    @Override
    public Map<String, MappedFieldMethod> fieldMethods() {
        return fieldMethods;
    }
}
