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

package grphaqladapter.adaptedschema.system_objects.directive;

import grphaqladapter.adaptedschema.mapping.mapped_elements.method.MappedFieldMethod;

import java.util.Collections;
import java.util.List;

public class FieldDirectives extends GraphqlDirectivesList {

    public static FieldDirectives empty(MappedFieldMethod field) {
        return new FieldDirectives(Collections.emptyList(), field);
    }

    private final MappedFieldMethod field;

    public FieldDirectives(List<GraphqlDirectiveDetails> directives, MappedFieldMethod field) {
        super(directives);
        this.field = field;
    }

    public MappedFieldMethod field() {
        return field;
    }
}
