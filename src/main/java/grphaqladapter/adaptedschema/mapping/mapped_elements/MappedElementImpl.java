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

import grphaqladapter.adaptedschema.utils.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MappedElementImpl implements MappedElement {

    private final String name;
    private final MappedElementType mappedType;
    private final String description;
    private final List<AppliedAnnotation> appliedAnnotations;
    private final Map<Class, AppliedAnnotation> appliedAnnotationsByClass;
    private final Map<String, AppliedAnnotation> appliedAnnotationsByName;

    public MappedElementImpl(String name, MappedElementType mappedType, String description, List<AppliedAnnotation> appliedAnnotations) {
        this.name = name;
        this.mappedType = mappedType;
        this.description = description;
        if (appliedAnnotations.isEmpty()) {
            this.appliedAnnotations = Collections.emptyList();
            this.appliedAnnotationsByClass = Collections.emptyMap();
            this.appliedAnnotationsByName = Collections.emptyMap();
        } else {
            this.appliedAnnotations = appliedAnnotations;
            this.appliedAnnotationsByClass = CollectionUtils.separateToImmutableMap(this.appliedAnnotations, AppliedAnnotation.class, AppliedAnnotation::annotationClass);
            this.appliedAnnotationsByName = CollectionUtils.separateToImmutableMap(this.appliedAnnotations, AppliedAnnotation.class, AppliedAnnotation::name);
        }
    }

    @Override
    public List<AppliedAnnotation> appliedAnnotations() {
        return appliedAnnotations;
    }

    @Override
    public Map<Class, AppliedAnnotation> appliedAnnotationsByClass() {
        return appliedAnnotationsByClass;
    }

    @Override
    public Map<String, AppliedAnnotation> appliedAnnotationsByName() {
        return appliedAnnotationsByName;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public MappedElementType mappedType() {
        return mappedType;
    }

    @Override
    public String name() {
        return name;
    }
}
