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

import grphaqladapter.adaptedschema.mapping.mapped_elements.annotation.MappedAnnotation;

import java.util.Map;

public class GraphqlDirectiveDetails {

    private final MappedAnnotation annotation;
    private final Map<String, Object> arguments;

    public GraphqlDirectiveDetails(MappedAnnotation annotation, Map<String, Object> arguments) {
        this.annotation = annotation;
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "GraphqlDirectiveDetails{" +
                "annotation=" + annotation +
                ", arguments=" + arguments +
                '}';
    }

    public MappedAnnotation annotation() {
        return annotation;
    }

    public <T> T getArgument(String name) {
        return (T) arguments.get(name);
    }
}
