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
import grphaqladapter.adaptedschema.assertutil.Assert;
import grphaqladapter.adaptedschema.functions.GraphqlDirectiveFunction;
import grphaqladapter.adaptedschema.mapping.mapped_elements.AppliedAnnotation;
import grphaqladapter.adaptedschema.mapping.mapped_elements.MappedClassBuilder;
import grphaqladapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import grphaqladapter.adaptedschema.mapping.mapped_elements.method.MappedAnnotationMethod;

import java.util.*;

public class MappedAnnotationBuilder extends MappedClassBuilder<MappedAnnotationBuilder, MappedAnnotation> {

    public static MappedAnnotationBuilder newBuilder() {
        return new MappedAnnotationBuilder();
    }

    private final Map<String, MappedAnnotationMethod> mappedMethods = new HashMap<>();
    private final Set<Introspection.DirectiveLocation> locations = new HashSet<>();
    private Class<? extends GraphqlDirectiveFunction> functionality;

    MappedAnnotationBuilder() {
        super(MappedElementType.DIRECTIVE);
    }

    @Override
    public MappedAnnotationBuilder addAppliedAnnotation(AppliedAnnotation annotation) {
        throw new IllegalStateException("an annotation can not apply AppliedAnnotation");
    }

    public MappedAnnotation build() {
        return new MappedAnnotationImpl(
                name(),
                description(),
                baseClass(),
                mappedMethods(),
                locations(),
                functionality());
    }

    @Override
    public MappedAnnotationBuilder clearAppliedAnnotations() {
        throw new IllegalStateException("an annotation can not apply AppliedAnnotation");
    }

    @Override
    public MappedAnnotationBuilder refresh() {
        this.clearMethods();
        this.clearLocations();
        this.functionality = null;
        return super.refresh();
    }

    @Override
    public MappedAnnotationBuilder removeAppliedAnnotation(AppliedAnnotation annotation) {
        throw new IllegalStateException("an annotation can not apply AppliedAnnotation");
    }

    @Override
    public MappedAnnotationBuilder removeAppliedAnnotation(Class clazz) {
        throw new IllegalStateException("an annotation can not apply AppliedAnnotation");
    }

    public MappedAnnotationBuilder addLocation(Introspection.DirectiveLocation location) {
        Assert.isFalse(locations.contains(location), new IllegalStateException("location [" + location + "] already exists"));
        locations.add(location);
        return this;
    }

    public MappedAnnotationBuilder addLocations(Introspection.DirectiveLocation... locations) {
        for (Introspection.DirectiveLocation location : locations) {
            addLocation(location);
        }
        return this;
    }

    public MappedAnnotationBuilder addMethod(MappedAnnotationMethod method) {
        Assert.isOneOrMoreFalse(new IllegalStateException("already a method with field typeName [" + method.name() +
                        "] exist - [exist:" + mappedMethods.get(method.name()) + "]"),
                mappedMethods.containsKey(method.name()));

        mappedMethods.put(method.name(), method);
        return this;
    }

    public MappedAnnotationBuilder clearLocations() {
        this.locations.clear();
        return this;
    }

    public MappedAnnotationBuilder clearMethods() {
        this.mappedMethods.clear();
        return this;
    }

    public MappedAnnotationBuilder deleteLocation(Introspection.DirectiveLocation location) {
        Assert.isTrue(locations.contains(location), new IllegalStateException("location [" + location + "] not present"));
        this.locations.remove(location);
        return this;
    }

    public MappedAnnotationBuilder functionality(Class<? extends GraphqlDirectiveFunction> functionality) {
        this.functionality = functionality;
        return this;
    }

    public Class<? extends GraphqlDirectiveFunction> functionality() {
        return functionality;
    }

    public Set<Introspection.DirectiveLocation> locations() {
        return Collections.unmodifiableSet(new HashSet<>(locations));
    }

    public Map<String, MappedAnnotationMethod> mappedMethods() {
        return Collections.unmodifiableMap(new HashMap<>(mappedMethods));
    }
}
