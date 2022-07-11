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

package grphaqladapter.adaptedschema.mapping.strategy.descriptors.annotations;

import java.util.Collections;
import java.util.Map;

final class DirectiveArgumentsValueImpl implements DirectiveArgumentsValue {


    public static DirectiveArgumentsValue empty(Class annotationClass) {
        return new DirectiveArgumentsValueImpl(annotationClass);
    }
    private final Map<String, Object> values;
    private final Class annotationClass;

    DirectiveArgumentsValueImpl(Map<String, Object> values, Class annotationClass) {
        this.values = values;
        this.annotationClass = annotationClass;
    }

    DirectiveArgumentsValueImpl(Class annotationClass) {
        this(Collections.emptyMap(), annotationClass);
    }

    @Override
    public Class annotationClass() {
        return annotationClass;
    }

    @Override
    public <T> T getArgumentValue(String argument) {
        return (T) values.get(argument);
    }

    @Override
    public String toString() {
        return "DirectiveArgumentsValueImpl{" +
                "values=" + values +
                ", annotationClass=" + annotationClass +
                '}';
    }
}
