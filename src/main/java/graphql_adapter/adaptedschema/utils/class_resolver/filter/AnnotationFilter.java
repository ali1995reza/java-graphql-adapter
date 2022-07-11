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
package graphql_adapter.adaptedschema.utils.class_resolver.filter;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class AnnotationFilter implements ClassFilter {

    @SafeVarargs
    public static ClassFilter annotatedWith(Class<? extends Annotation>... annotations) {
        return new AnnotationFilter(annotations);
    }

    private final Set<Class<? extends Annotation>> annotations;

    public AnnotationFilter(Collection<Class<? extends Annotation>> annotations) {
        this.annotations = new HashSet<>(annotations);
    }

    public AnnotationFilter(Class<? extends Annotation>... annotations) {
        this(Arrays.asList(annotations));
    }

    @Override
    public boolean accept(Class<?> cls) {
        for (Annotation annotation : cls.getAnnotations()) {
            if (annotations.contains(annotation.annotationType())) {
                return true;
            }
        }
        return false;
    }
}
