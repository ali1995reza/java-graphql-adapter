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

package grphaqladapter.adaptedschema.mapping.mapped_elements.enums;

import grphaqladapter.adaptedschema.mapping.mapped_elements.MappedElementImpl;
import grphaqladapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import grphaqladapter.adaptedschema.mapping.mapped_elements.annotation.AppliedAnnotation;

import java.lang.reflect.Field;
import java.util.List;

final class MappedEnumConstantImpl extends MappedElementImpl implements MappedEnumConstant {

    private final Enum constant;
    private final Field field;

    MappedEnumConstantImpl(String name, String description, List<AppliedAnnotation> appliedAnnotations, Enum constant, Field field) {
        super(name, MappedElementType.ENUM_VALUE, description, appliedAnnotations);
        this.constant = constant;
        this.field = field;
    }

    @Override
    public Enum constant() {
        return constant;
    }

    @Override
    public Field field() {
        return field;
    }
}
