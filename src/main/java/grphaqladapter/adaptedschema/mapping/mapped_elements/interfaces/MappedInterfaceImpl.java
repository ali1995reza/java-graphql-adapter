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

package grphaqladapter.adaptedschema.mapping.mapped_elements.interfaces;

import grphaqladapter.adaptedschema.mapping.mapped_elements.MappedClassImpl;
import grphaqladapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import grphaqladapter.adaptedschema.mapping.mapped_elements.annotation.AppliedAnnotation;
import grphaqladapter.adaptedschema.mapping.mapped_elements.method.MappedFieldMethod;
import grphaqladapter.adaptedschema.utils.CollectionUtils;

import java.util.List;
import java.util.Map;

final class MappedInterfaceImpl extends MappedClassImpl implements MappedInterface {

    private final Map<String, MappedFieldMethod> fieldMethods;

    MappedInterfaceImpl(String name, String description, List<AppliedAnnotation> appliedAnnotations, Class baseClass, Map<String, MappedFieldMethod> fieldMethods) {
        super(name, MappedElementType.INTERFACE, description, appliedAnnotations, baseClass);
        this.fieldMethods = CollectionUtils.getOrEmptyMap(fieldMethods);
    }

    @Override
    public Map<String, MappedFieldMethod> fieldMethods() {
        return fieldMethods;
    }
}
