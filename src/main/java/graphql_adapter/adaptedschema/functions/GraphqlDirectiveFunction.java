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

package graphql_adapter.adaptedschema.functions;

import graphql.language.OperationDefinition;
import graphql.schema.*;
import graphql_adapter.adaptedschema.mapping.mapped_elements.annotation.MappedAnnotation;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedInputTypeClass;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedObjectTypeClass;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedScalarClass;
import graphql_adapter.adaptedschema.mapping.mapped_elements.enums.MappedEnum;
import graphql_adapter.adaptedschema.mapping.mapped_elements.enums.MappedEnumConstant;
import graphql_adapter.adaptedschema.mapping.mapped_elements.interfaces.MappedInterface;
import graphql_adapter.adaptedschema.mapping.mapped_elements.interfaces.MappedUnionInterface;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedAnnotationMethod;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedFieldMethod;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedInputFieldMethod;
import graphql_adapter.adaptedschema.mapping.mapped_elements.parameter.MappedParameter;
import graphql_adapter.adaptedschema.system_objects.directive.GraphqlDirectiveDetails;

public interface GraphqlDirectiveFunction<T> {

    default T handleFieldDirective(GraphqlDirectiveDetails directive, T value, Object source, MappedFieldMethod field, DataFetchingEnvironment env) {
        return value;
    }

    default T handleFragmentDirective(GraphqlDirectiveDetails directive, T value, Object source, MappedObjectTypeClass type, MappedFieldMethod field, DataFetchingEnvironment env) {
        return value;
    }

    default T handleOperationDirective(GraphqlDirectiveDetails directive, T value, Object source, OperationDefinition operation, MappedFieldMethod field, DataFetchingEnvironment env) {
        return value;
    }

    default GraphQLArgument onDirectiveArgument(GraphqlDirectiveDetails directive, GraphQLArgument argument, MappedAnnotation annotationClass, MappedAnnotationMethod parameter, SchemaDirectiveHandlingContext context) {
        return argument;
    }

    default GraphQLEnumType onEnum(GraphqlDirectiveDetails directive, GraphQLEnumType enumType, MappedEnum mappedEnum, SchemaDirectiveHandlingContext context) {
        return enumType;
    }

    default GraphQLEnumValueDefinition onEnumValue(GraphqlDirectiveDetails directive, GraphQLEnumValueDefinition enumValueDefinition, MappedEnum mappedEnum, MappedEnumConstant enumConstant, SchemaDirectiveHandlingContext context) {
        return enumValueDefinition;
    }

    default GraphQLFieldDefinition onField(GraphqlDirectiveDetails directive, GraphQLFieldDefinition fieldDefinition, MappedObjectTypeClass typeClass, MappedFieldMethod field, SchemaDirectiveHandlingContext context) {
        return fieldDefinition;
    }

    default GraphQLArgument onFieldArgument(GraphqlDirectiveDetails directive, GraphQLArgument argument, MappedObjectTypeClass typeClass, MappedFieldMethod field, MappedParameter parameter, SchemaDirectiveHandlingContext context) {
        return argument;
    }

    default GraphQLInputObjectField onInputObjectField(GraphqlDirectiveDetails directive, GraphQLInputObjectField inputObjectField, MappedInputTypeClass mappedInputTypeClass, MappedInputFieldMethod inputField, SchemaDirectiveHandlingContext context) {
        return inputObjectField;
    }

    default GraphQLInputObjectType onInputObjectType(GraphqlDirectiveDetails directive, GraphQLInputObjectType inputObjectType, MappedInputTypeClass mappedInputTypeClass, SchemaDirectiveHandlingContext context) {
        return inputObjectType;
    }

    default GraphQLInterfaceType onInterface(GraphqlDirectiveDetails directive, GraphQLInterfaceType interfaceType, MappedInterface mappedInterface, SchemaDirectiveHandlingContext context) {
        return interfaceType;
    }

    default GraphQLObjectType onObject(GraphqlDirectiveDetails directive, GraphQLObjectType objectType, MappedObjectTypeClass mappedObjectTypeClass, SchemaDirectiveHandlingContext context) {
        return objectType;
    }

    default GraphQLScalarType onScalar(GraphqlDirectiveDetails directive, GraphQLScalarType scalarType, MappedScalarClass mappedScalarClass, SchemaDirectiveHandlingContext context) {
        return scalarType;
    }

    default GraphQLUnionType onUnion(GraphqlDirectiveDetails directive, GraphQLUnionType unionType, MappedUnionInterface mappedUnionInterface, SchemaDirectiveHandlingContext context) {
        return unionType;
    }

    default void preHandleFieldDirective(GraphqlDirectiveDetails directive, Object source, MappedFieldMethod field, DataFetchingEnvironment env) {
    }

    default void preHandleFragmentDirective(GraphqlDirectiveDetails directive, Object source, MappedFieldMethod field, DataFetchingEnvironment env) {
    }

    default void preHandleOperationDirective(GraphqlDirectiveDetails directive, Object source, OperationDefinition operation, MappedFieldMethod field, DataFetchingEnvironment env) {
    }
}
