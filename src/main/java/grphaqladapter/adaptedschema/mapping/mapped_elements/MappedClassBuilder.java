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

package grphaqladapter.adaptedschema.mapping.mapped_elements;

import grphaqladapter.adaptedschema.mapping.mapped_elements.classes.MappedClass;

public abstract class MappedClassBuilder<T extends MappedClassBuilder<T, E>, E extends MappedClass> extends MappedElementBuilder<T, E> {

    private Class baseClass;

    public MappedClassBuilder(MappedElementType elementType) {
        super(elementType);
    }

    @Override
    public T copy(E element) {
        return super.copy(element)
                .baseClass(element.baseClass());
    }

    @Override
    public T refresh() {
        this.baseClass = null;
        return super.refresh();
    }

    public T baseClass(Class baseClass) {
        this.baseClass = baseClass;
        return (T) this;
    }

    public Class baseClass() {
        return baseClass;
    }
}
