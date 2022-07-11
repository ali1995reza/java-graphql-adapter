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

package grphaqladapter.adaptedschema.mapping.strategy.descriptions;

public abstract class NullableContainerDescriptionBuilder<T extends NullableContainerDescriptionBuilder<T, E>, E extends GraphqlNullableContainerElementDescription> extends GraphqlElementDescriptionBuilder<T, E> {

    private boolean nullable = false;

    @Override
    public T copy(E e) {
        return super.copy(e)
                .nullable(e.nullable());
    }

    @Override
    public T refresh() {
        this.nullable = false;
        return super.refresh();
    }

    public boolean isNullable() {
        return nullable;
    }

    public T nullable(boolean nullable) {
        this.nullable = nullable;
        return (T) this;
    }
}
