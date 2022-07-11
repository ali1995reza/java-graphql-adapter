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

package grphaqladapter.adaptedschema.functions;

import grphaqladapter.adaptedschema.tools.object_builder.ObjectBuilder;
import grphaqladapter.codegenerator.ObjectConstructor;

public final class ValueParsingContext {

    private final ObjectConstructor objectConstructor;
    private final ObjectBuilder objectBuilder;

    public ValueParsingContext(ObjectConstructor objectConstructor, ObjectBuilder objectBuilder) {
        this.objectConstructor = objectConstructor;
        this.objectBuilder = objectBuilder;
    }

    public ObjectBuilder objectBuilder() {
        return objectBuilder;
    }

    public ObjectConstructor objectConstructor() {
        return objectConstructor;
    }
}
