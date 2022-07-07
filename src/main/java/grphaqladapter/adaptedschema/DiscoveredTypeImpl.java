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

package grphaqladapter.adaptedschema;


import graphql.schema.GraphQLType;
import grphaqladapter.adaptedschema.assertutil.Assert;
import grphaqladapter.adaptedschema.discovered.DiscoveredType;
import grphaqladapter.adaptedschema.mapping.mapped_elements.classes.MappedClass;


abstract class DiscoveredTypeImpl<T extends GraphQLType, E extends MappedClass> implements DiscoveredType<T, E> {

    private final E mappedElement;
    private final String name;
    private final T graphQLType;
    private final Boolean isImmutable = false;

    public DiscoveredTypeImpl(E mappedElement, String name, T graphQLType) {
        this.mappedElement = mappedElement;
        this.name = name;
        this.graphQLType = graphQLType;
    }

    @Override
    public T asGraphqlElement() {
        return graphQLType;
    }

    @Override
    public E asMappedElement() {
        return mappedElement;
    }

    @Override
    public String name() {
        return name;
    }

    void makeImmutable() {

    }

    final synchronized void setImmutable() {
        Assert.isFalse(isImmutable, new IllegalStateException("this type already immutable"));
        makeImmutable();
    }
}
