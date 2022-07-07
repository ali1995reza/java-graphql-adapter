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

import graphql.schema.Coercing;
import grphaqladapter.adaptedschema.mapping.mapped_elements.MappedClassBuilder;
import grphaqladapter.adaptedschema.mapping.mapped_elements.MappedElementType;

public class MappedScalarClassBuilder extends MappedClassBuilder<MappedScalarClassBuilder, MappedScalarClass> {

    public static MappedScalarClassBuilder newBuilder() {
        return new MappedScalarClassBuilder();
    }

    private Coercing<?, ?> coercing;

    MappedScalarClassBuilder() {
        super(MappedElementType.SCALAR);
    }

    @Override
    public MappedScalarClass build() {
        return new MappedScalarClassImpl(
                name(),
                description(),
                appliedAnnotations(),
                baseClass(),
                coercing());
    }

    @Override
    public MappedScalarClassBuilder copy(MappedScalarClass element) {
        return super.copy(element)
                .coercing(element.coercing());
    }

    public MappedScalarClassBuilder coercing(Coercing<?, ?> coercing) {
        this.coercing = coercing;
        return this;
    }

    public Coercing<?, ?> coercing() {
        return coercing;
    }
}
