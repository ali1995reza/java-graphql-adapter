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

package grphaqladapter.adaptedschema.mapping.mapped_elements.annotation;

import graphql.introspection.Introspection;
import grphaqladapter.adaptedschema.functions.GraphqlDirectiveFunction;
import grphaqladapter.adaptedschema.mapping.mapped_elements.classes.MappedClass;
import grphaqladapter.adaptedschema.mapping.mapped_elements.method.MappedAnnotationMethod;

import java.util.Map;
import java.util.Set;

public interface MappedAnnotation extends MappedClass {

    static MappedAnnotationBuilder newAnnotation() {
        return MappedAnnotationBuilder.newBuilder();
    }

    Class<? extends GraphqlDirectiveFunction> functionality();

    Set<Introspection.DirectiveLocation> locations();

    Map<String, MappedAnnotationMethod> mappedMethods();
}
