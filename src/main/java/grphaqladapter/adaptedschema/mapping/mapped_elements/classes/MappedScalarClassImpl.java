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
import grphaqladapter.adaptedschema.mapping.mapped_elements.MappedClassImpl;
import grphaqladapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import grphaqladapter.adaptedschema.mapping.mapped_elements.annotation.AppliedAnnotation;

import java.util.List;

public class MappedScalarClassImpl extends MappedClassImpl implements MappedScalarClass {

    private final Coercing<?, ?> coercing;

    MappedScalarClassImpl(String name, String description, List<AppliedAnnotation> appliedAnnotations, Class baseClass, Coercing<?, ?> coercing) {
        super(name, MappedElementType.SCALAR, description, appliedAnnotations, baseClass);
        this.coercing = coercing;
    }

    @Override
    public Coercing<?, ?> coercing() {
        return this.coercing;
    }
}
