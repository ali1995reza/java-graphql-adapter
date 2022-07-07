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

import graphql.Scalars;
import grphaqladapter.adaptedschema.discovered.DiscoveredScalarType;
import grphaqladapter.adaptedschema.mapping.mapped_elements.classes.MappedScalarClassBuilder;

final class StringDiscoveredType extends DiscoveredScalarTypeImpl {

    private final static StringDiscoveredType INSTANCE = new StringDiscoveredType();

    private StringDiscoveredType() {
        super(MappedScalarClassBuilder
                        .newBuilder()
                        .baseClass(String.class)
                        .name(Scalars.GraphQLString.getName())
                        .description(Scalars.GraphQLString.getDescription())
                        .coercing(Scalars.GraphQLString.getCoercing())
                        .build(),
                Scalars.GraphQLString.getName(),
                Scalars.GraphQLString);
    }

    @Override
    public String toString() {
        return StringDiscoveredType.class.getSimpleName();
    }

    public static DiscoveredScalarType getInstance() {
        return INSTANCE;
    }
}
