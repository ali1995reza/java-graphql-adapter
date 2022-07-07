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


import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLTypeReference;
import grphaqladapter.adaptedschema.functions.SchemaDirectiveHandlingContext;
import grphaqladapter.adaptedschema.mapping.mapped_elements.MappedElementType;
import grphaqladapter.adaptedschema.mapping.mapped_elements.classes.MappedClass;
import grphaqladapter.codegenerator.ObjectConstructor;


interface BuildingContext {

    void addToPossibleTypesOf(MappedClass mappedClass, MappedClass possible);

    SchemaDirectiveHandlingContext directiveHandlingContext();

    GraphQLTypeReference geOutputTypeFor(Class clazz);

    GraphQLDirective getDirectiveFor(Class clazz);

    GraphQLTypeReference getEnumFor(Class clazz);

    GraphQLTypeReference getInputObjectTypeFor(Class clazz);

    GraphQLTypeReference getPossibleInputTypeFor(Class clazz);

    GraphQLTypeReference getInterfaceFor(Class clazz);

    <T extends MappedClass> T getMappedClassFor(Class clazz, MappedElementType mappedElementType);

    GraphQLTypeReference getObjectTypeFor(Class clazz);

    GraphQLTypeReference getScalarTypeFor(Class clazz);

    GraphQLTypeReference getUnionTypeFor(Class clazz);

    default boolean isAnInterface(Class clazz) {
        return getInterfaceFor(clazz) != null;
    }

    default boolean isAnUnion(Class clazz) {
        return getUnionTypeFor(clazz) != null;
    }

    default boolean isScalar(Class clazz) {
        return getScalarTypeFor(clazz) != null;
    }

    default boolean isEnum(Class clazz) {
        return getEnumFor(clazz) != null;
    }

    default boolean isInputType(Class clazz) {
        return getInputObjectTypeFor(clazz) != null;
    }

    ObjectConstructor objectConstructor();

}
