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

package tests.T1;

import grphaqladapter.adaptedschema.AdaptedGraphQLSchema;
import grphaqladapter.adaptedschema.scalar.impl.ScalarEntryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tests.T1.schema.CustomObjectConstructor;
import tests.T1.schema.Splitor;

public class TestSchemaProvider {

    private final static Logger LOGGER = LoggerFactory.getLogger(TestSchemaProvider.class);

    private static AdaptedGraphQLSchema SCHEMA;

    public static synchronized AdaptedGraphQLSchema schema() {
        if (SCHEMA == null) {

            LOGGER.info("building test schema");

            SCHEMA = AdaptedGraphQLSchema
                    .newBuilder()
                    .addAllBuiltInScalars()
                    .addScalar(ScalarEntryBuilder
                            .newBuilder()
                            .description("Splitor Description")
                            .name("Splitor")
                            .type(Splitor.class)
                            .coercing(new Splitor.CoercingImpl())
                            .build())
                    .addGraphqlAnnotatedClassesFormPackage("tests.T1.schema")
                    .addGraphqlAnnotatedClassesFormPackage("tests.T1.schema.directives")
                    .objectConstructor(new CustomObjectConstructor())
                    .build();

            LOGGER.info("Result schema is : \r\n\r\n" + SCHEMA.asSchemaDefinitionLanguage());

        }
        return SCHEMA;
    }

}
