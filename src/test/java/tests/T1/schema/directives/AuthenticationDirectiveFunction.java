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

package tests.T1.schema.directives;

import graphql.language.OperationDefinition;
import graphql.schema.DataFetchingEnvironment;
import grphaqladapter.adaptedschema.functions.GraphqlDirectiveFunction;
import grphaqladapter.adaptedschema.mapping.mapped_elements.method.MappedFieldMethod;
import grphaqladapter.adaptedschema.system_objects.directive.GraphqlDirectiveDetails;

public class AuthenticationDirectiveFunction implements GraphqlDirectiveFunction {

    @Override
    public void preHandleFieldDirective(GraphqlDirectiveDetails directive, Object source, MappedFieldMethod field, DataFetchingEnvironment env) {
        env.getGraphQlContext().put("auth", directive.getArgument("token"));
        GraphqlDirectiveFunction.super.preHandleFieldDirective(directive, source, field, env);
    }

    @Override
    public void preHandleOperationDirective(GraphqlDirectiveDetails directive, Object source, OperationDefinition operation, MappedFieldMethod field, DataFetchingEnvironment env) {
        env.getGraphQlContext().put("auth", directive.getArgument("token"));
        GraphqlDirectiveFunction.super.preHandleOperationDirective(directive, source, operation, field, env);
    }
}
