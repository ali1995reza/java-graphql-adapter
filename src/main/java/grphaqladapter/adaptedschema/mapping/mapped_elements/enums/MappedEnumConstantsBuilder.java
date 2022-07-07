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

import grphaqladapter.adaptedschema.mapping.mapped_elements.MappedElementBuilder;
import grphaqladapter.adaptedschema.mapping.mapped_elements.MappedElementType;

public class MappedEnumConstantsBuilder extends MappedElementBuilder<MappedEnumConstantsBuilder, MappedEnumConstant> {

    public static MappedEnumConstantsBuilder newBuilder() {
        return new MappedEnumConstantsBuilder();
    }
    private Enum constant;

    public MappedEnumConstantsBuilder() {
        super(MappedElementType.ENUM_VALUE);
    }

    @Override
    public MappedEnumConstant build() {
        return new MappedEnumConstantImpl(
                name(),
                description(),
                appliedAnnotations(),
                constant()
        );
    }

    @Override
    public MappedEnumConstantsBuilder copy(MappedEnumConstant element) {
        return super.copy(element)
                .constant(element.constant());
    }

    @Override
    public MappedEnumConstantsBuilder refresh() {
        this.constant = null;
        return super.refresh();
    }

    public MappedEnumConstantsBuilder constant(Enum constant) {
        this.constant = constant;
        return this;
    }

    public Enum constant() {
        return constant;
    }
}
