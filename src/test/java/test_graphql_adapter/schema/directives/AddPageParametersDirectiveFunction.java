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
package test_graphql_adapter.schema.directives;

import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLTypeReference;
import graphql_adapter.adaptedschema.functions.GraphqlDirectiveFunction;
import graphql_adapter.adaptedschema.functions.SchemaDirectiveHandlingContext;
import graphql_adapter.adaptedschema.mapping.mapped_elements.classes.MappedObjectTypeClass;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.MappedFieldMethod;
import graphql_adapter.adaptedschema.system_objects.directive.GraphqlDirectiveDetails;
import test_graphql_adapter.schema.types.PageParameters;

public class AddPageParametersDirectiveFunction implements GraphqlDirectiveFunction<Object> {

    @Override
    public GraphQLFieldDefinition onField(GraphqlDirectiveDetails directive, GraphQLFieldDefinition fieldDefinition, MappedObjectTypeClass typeClass, MappedFieldMethod field, SchemaDirectiveHandlingContext context) {
        return GraphQLFieldDefinition.newFieldDefinition(fieldDefinition)
                .argument(GraphQLArgument
                        .newArgument()
                        .name("pageParameters")
                        .description("page parameters")
                        .type(GraphQLTypeReference.typeRef(PageParameters.class.getSimpleName()))
                        .build())
                .build();
    }
}
