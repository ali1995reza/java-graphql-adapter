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

package grphaqladapter.adaptedschema.mapping.mapped_elements.classes;

import grphaqladapter.adaptedschema.mapping.mapped_elements.MappedClassImpl;
import grphaqladapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import grphaqladapter.adaptedschema.mapping.mapped_elements.annotation.AppliedAnnotation;
import grphaqladapter.adaptedschema.mapping.mapped_elements.method.MappedInputFieldMethod;
import grphaqladapter.adaptedschema.utils.CollectionUtils;

import java.util.List;
import java.util.Map;

public class MappedInputTypeClassImpl extends MappedClassImpl implements MappedInputTypeClass {

    private final Map<String, MappedInputFieldMethod> inputFieldMethods;

    protected MappedInputTypeClassImpl(String name, String description, List<AppliedAnnotation> appliedAnnotations, Class baseClass, Map<String, MappedInputFieldMethod> inputFieldMethods) {
        super(name, MappedElementType.INPUT_TYPE, description, appliedAnnotations, baseClass);
        this.inputFieldMethods = CollectionUtils.getOrEmptyMap(inputFieldMethods);
    }

    @Override
    public Map<String, MappedInputFieldMethod> inputFiledMethods() {
        return inputFieldMethods;
    }
}
