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

import grphaqladapter.adaptedschema.utils.CollectionUtils;
import grphaqladapter.adaptedschema.utils.Utils;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GraphqlDirectivesList {

    private final static GraphqlDirectivesList EMPTY = new GraphqlDirectivesList(Collections.emptyList());
    private final List<GraphqlDirectiveDetails> directives;
    private Map<Class<? extends Annotation>, GraphqlDirectiveDetails> directivesByClass;

    public GraphqlDirectivesList(List<GraphqlDirectiveDetails> directives) {
        this.directives = Utils.getOrDefault(directives, Collections.emptyList());
        if (CollectionUtils.isEmpty(this.directives)) {
            directivesByClass = Collections.emptyMap();
        }
    }

    public List<GraphqlDirectiveDetails> directives() {
        return this.directives;
    }

    public Map<Class<? extends Annotation>, GraphqlDirectiveDetails> directivesByClass() {
        if (this.directivesByClass == null) {
            this.directivesByClass = directives.stream().collect(Collectors.toMap(directive -> directive.annotation().baseClass(), d -> d));
            this.directivesByClass = Collections.unmodifiableMap(this.directivesByClass);
        }

        return this.directivesByClass;
    }

    public static GraphqlDirectivesList empty() {
        return EMPTY;
    }
}
