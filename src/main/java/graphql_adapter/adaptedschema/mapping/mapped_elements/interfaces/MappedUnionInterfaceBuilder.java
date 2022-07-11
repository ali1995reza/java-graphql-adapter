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
package graphql_adapter.adaptedschema.mapping.mapped_elements.interfaces;

import graphql_adapter.adaptedschema.assertion.Assert;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedClassBuilder;
import graphql_adapter.adaptedschema.mapping.mapped_elements.MappedElementType;

public class MappedUnionInterfaceBuilder extends MappedClassBuilder<MappedUnionInterfaceBuilder, MappedUnionInterface> {

    public static MappedUnionInterfaceBuilder newBuilder() {
        return new MappedUnionInterfaceBuilder();
    }

    public MappedUnionInterfaceBuilder() {
        super(MappedElementType.UNION);
    }

    @Override
    public MappedUnionInterfaceBuilder baseClass(Class<?> baseClass) {
        Assert.isTrue(baseClass.isInterface() && !baseClass.isAnnotation(),
                new IllegalStateException("just non-annotation interface class can be base class of an union-interface"));
        return super.baseClass(baseClass);
    }

    @Override
    public MappedUnionInterface build() {
        return new MappedUnionInterfaceImpl(
                name(),
                description(),
                appliedAnnotations(),
                baseClass()
        );
    }
}
