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
package test_graphql_adapter.schema;

import graphql_adapter.adaptedschema.AdaptedGraphQLSchema;
import graphql_adapter.adaptedschema.scalar.ScalarEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test_graphql_adapter.schema.types.CustomObjectConstructor;
import test_graphql_adapter.schema.types.Splitor;

public class TestSchemaProvider {

    private final static Logger LOGGER = LoggerFactory.getLogger(TestSchemaProvider.class);

    private static AdaptedGraphQLSchema SCHEMA;

    public static synchronized AdaptedGraphQLSchema schema() {
        if (SCHEMA == null) {

            LOGGER.info("building test schema");

            SCHEMA = AdaptedGraphQLSchema
                    .newSchema()
                    .addAllBuiltInScalars()
                    .addScalar(ScalarEntry
                            .newScalarEntry()
                            .description("Splitor Description")
                            .name("Splitor")
                            .type(Splitor.class)
                            .coercing(new Splitor.CoercingImpl())
                            .build())
                    .addGraphqlAnnotatedClassesFormPackage("test_graphql_adapter.schema.types")
                    .addGraphqlAnnotatedClassesFormPackage("test_graphql_adapter.schema.directives")
                    .objectConstructor(new CustomObjectConstructor())
                    .build();

            LOGGER.info("Result schema is : \r\n\r\n" + SCHEMA.asSchemaDefinitionLanguage());
        }
        return SCHEMA;
    }
}
