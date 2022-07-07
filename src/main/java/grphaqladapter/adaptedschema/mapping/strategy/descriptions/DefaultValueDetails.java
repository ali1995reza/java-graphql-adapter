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
import grphaqladapter.adaptedschema.functions.impl.AutomaticDefaultValuerParser;

public final class DefaultValueDetails {

    public static DefaultValueDetails newValue(Object value, Class<? extends ValueParser> valueParser) {
        return new DefaultValueDetails(value, valueParser);
    }

    public static DefaultValueDetails newValue(Object value) {
        return newValue(value, AutomaticDefaultValuerParser.class);
    }

    private final Object value;
    private final Class<? extends ValueParser> valueParser;

    public DefaultValueDetails(Object value, Class<? extends ValueParser> valueParser) {
        this.value = value;
        this.valueParser = valueParser;
    }

    public Object value() {
        return value;
    }

    public Class<? extends ValueParser> valueParser() {
        return valueParser;
    }
}
