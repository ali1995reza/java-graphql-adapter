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

package graphql_adapter.adaptedschema.mapping.mapped_elements;

import graphql_adapter.adaptedschema.mapping.mapped_elements.annotation.AppliedAnnotation;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedClass;

import java.util.List;

public abstract class MappedClassImpl extends MappedElementImpl implements MappedClass {

    private final String name;
    private final Class<?> baseClass;
    private final String description;

    protected MappedClassImpl(String name, MappedElementType mappedType, String description, List<AppliedAnnotation> appliedAnnotations, Class<?> baseClass) {
        super(name, mappedType, description, appliedAnnotations);
        this.name = name;
        this.baseClass = baseClass;
        this.description = description;
    }

    @Override
    public Class<?> baseClass() {
        return baseClass;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public String name() {
        return name;
    }
}
