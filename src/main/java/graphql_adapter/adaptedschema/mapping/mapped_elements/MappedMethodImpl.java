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
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedMethod;

import java.lang.reflect.Method;
import java.util.List;

public class MappedMethodImpl extends MappedElementImpl implements MappedMethod {

    private final Method method;
    private final TypeInformation<?> type;

    public MappedMethodImpl(String name, MappedElementType mappedType, String description, List<AppliedAnnotation> appliedAnnotations, Method method, TypeInformation<?> type) {
        super(name, mappedType, description, appliedAnnotations);
        this.method = method;
        this.type = type;
    }

    @Override
    public Method method() {
        return method;
    }

    @Override
    public TypeInformation<?> type() {
        return type;
    }
}
