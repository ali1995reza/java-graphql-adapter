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

import grphaqladapter.adaptedschema.functions.ValueParser;

public abstract class DefaultValueContainerElementDescriptionBuilder<T extends DefaultValueContainerElementDescriptionBuilder<T, E>, E extends GraphqlDefaultValueContainerElementDescription> extends NullableContainerDescriptionBuilder<T, E> {

    private DefaultValueDetails defaultValue;

    @Override
    public T copy(E e) {
        return super.copy(e)
                .defaultValue(e.defaultValue());
    }

    @Override
    public T refresh() {
        this.defaultValue = null;
        return super.refresh();
    }

    public T defaultValue(DefaultValueDetails defaultValue) {
        this.defaultValue = defaultValue;
        return (T) this;
    }

    public T defaultValue(String value) {
        return defaultValue(DefaultValueDetails.newValue(value));
    }

    public T defaultValue(String value, Class<? extends ValueParser> valueParser) {
        return defaultValue(DefaultValueDetails.newValue(value, valueParser));
    }

    public DefaultValueDetails defaultValue() {
        return defaultValue;
    }
}
