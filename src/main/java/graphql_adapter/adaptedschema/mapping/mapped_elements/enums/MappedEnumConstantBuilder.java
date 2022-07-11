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

import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElementBuilder;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElementType;

import java.lang.reflect.Field;

public class MappedEnumConstantBuilder extends MappedElementBuilder<MappedEnumConstantBuilder, MappedEnumConstant> {

    public static MappedEnumConstantBuilder newBuilder() {
        return new MappedEnumConstantBuilder();
    }

    private Enum<?> constant;
    private Field field;

    public MappedEnumConstantBuilder() {
        super(MappedElementType.ENUM_VALUE);
    }

    @Override
    public MappedEnumConstant build() {
        return new MappedEnumConstantImpl(
                name(),
                description(),
                appliedAnnotations(),
                constant(),
                field());
    }

    @Override
    public MappedEnumConstantBuilder copy(MappedEnumConstant element) {
        return super.copy(element)
                .constant(element.constant())
                .field(element.field());
    }

    @Override
    public MappedEnumConstantBuilder refresh() {
        this.constant = null;
        this.field = null;
        return super.refresh();
    }

    public MappedEnumConstantBuilder constant(Enum<?> constant) {
        this.constant = constant;
        return this;
    }

    public Enum<?> constant() {
        return constant;
    }

    public MappedEnumConstantBuilder field(Field field) {
        this.field = field;
        return this;
    }

    public Field field() {
        return field;
    }
}
