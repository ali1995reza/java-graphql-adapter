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

import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedMethod;

import java.lang.reflect.Method;

public abstract class MappedMethodBuilder<T extends MappedMethodBuilder<T, E>, E extends MappedMethod> extends MappedElementBuilder<T, E> {

    private Method method;
    private TypeInformation<?> type;

    public MappedMethodBuilder(MappedElementType elementType) {
        super(elementType);
    }

    @Override
    public T copy(E element) {
        return super.copy(element)
                .method(element.method())
                .type(element.type());
    }

    @Override
    public T refresh() {
        this.method = null;
        this.type = null;
        return super.refresh();
    }

    public T method(Method method) {
        this.method = method;
        return castThis();
    }

    public Method method() {
        return method;
    }

    public T type(TypeInformation<?> type) {
        this.type = type;
        return castThis();
    }

    public TypeInformation<?> type() {
        return type;
    }
}
