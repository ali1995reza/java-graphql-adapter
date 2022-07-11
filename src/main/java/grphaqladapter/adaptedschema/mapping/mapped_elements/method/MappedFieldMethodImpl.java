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

package grphaqladapter.adaptedschema.mapping.mapped_elements.method;

import grphaqladapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import grphaqladapter.adaptedschema.mapping.mapped_elements.MappedMethodImpl;
import grphaqladapter.adaptedschema.mapping.mapped_elements.TypeInformation;
import grphaqladapter.adaptedschema.mapping.mapped_elements.annotation.AppliedAnnotation;
import grphaqladapter.adaptedschema.mapping.mapped_elements.parameter.MappedParameter;
import grphaqladapter.adaptedschema.utils.CollectionUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

final class MappedFieldMethodImpl extends MappedMethodImpl implements MappedFieldMethod {

    private final List<MappedParameter> parameters;
    private final Map<String, MappedParameter> parametersByName;

    MappedFieldMethodImpl(String name, String description, List<AppliedAnnotation> appliedAnnotations, Method method, TypeInformation type, List<MappedParameter> parameters) {
        super(name, MappedElementType.FIELD, description, appliedAnnotations, method, type);
        this.parameters = CollectionUtils.getOrEmptyList(parameters);
        this.parametersByName = CollectionUtils.getOrEmptyMap(
                CollectionUtils.separateToImmutableMap(parameters, MappedParameter.class, MappedParameter::name, true)
        );
    }


    @Override
    public List<MappedParameter> parameters() {
        return parameters;
    }

    @Override
    public Map<String, MappedParameter> parametersByName() {
        return parametersByName;
    }
}
