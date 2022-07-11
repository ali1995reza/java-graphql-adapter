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
package graphql_adapter.adaptedschema.system_objects.directive;

import graphql_adapter.adaptedschema.utils.CollectionUtils;
import graphql_adapter.adaptedschema.utils.NullifyUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GraphqlDirectivesList {

    private final List<GraphqlDirectiveDetails> directives;
    private Map<Class<?>, GraphqlDirectiveDetails> directivesByClass;
    private Map<String, GraphqlDirectiveDetails> directivesByName;

    public GraphqlDirectivesList(List<GraphqlDirectiveDetails> directives) {
        this.directives = NullifyUtils.getOrDefault(directives, Collections.emptyList());
        if (CollectionUtils.isEmpty(this.directives)) {
            this.directivesByClass = Collections.emptyMap();
        }
    }

    public List<GraphqlDirectiveDetails> directives() {
        return this.directives;
    }

    public Map<Class<?>, GraphqlDirectiveDetails> directivesByClass() {
        if (this.directivesByClass == null) {
            this.directivesByClass = directives.stream().collect(Collectors.toMap(directive -> directive.annotation().baseClass(), d -> d));
            this.directivesByClass = Collections.unmodifiableMap(this.directivesByClass);
        }

        return this.directivesByClass;
    }

    public Map<String, GraphqlDirectiveDetails> directivesByName() {
        if (this.directivesByName == null) {
            this.directivesByName = directives.stream().collect(Collectors.toMap(directive -> directive.annotation().name(), d -> d));
            this.directivesByName = Collections.unmodifiableMap(this.directivesByName);
        }

        return this.directivesByName;
    }
}
