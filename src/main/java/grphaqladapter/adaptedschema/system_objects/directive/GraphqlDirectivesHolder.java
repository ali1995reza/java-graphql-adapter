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

package grphaqladapter.adaptedschema.system_objects.directive;

public class GraphqlDirectivesHolder {

    private final OperationDirectives operationDirectives;
    private final FragmentDirectives fragmentDirectives;
    private final FieldDirectives fieldDirectives;

    public GraphqlDirectivesHolder(OperationDirectives operationDirectives, FragmentDirectives fragmentDirectives, FieldDirectives fieldDirectives) {
        this.operationDirectives = operationDirectives;
        this.fragmentDirectives = fragmentDirectives;
        this.fieldDirectives = fieldDirectives;
    }

    public FieldDirectives fieldDirectives() {
        return fieldDirectives;
    }

    public boolean isFieldDirectivesPresent() {
        return fieldDirectives != null;
    }

    public FragmentDirectives fragmentDirectives() {
        return fragmentDirectives;
    }

    public boolean isFragmentDirectivesPresent() {
        return fragmentDirectives != null;
    }

    public OperationDirectives operationDirectives() {
        return operationDirectives;
    }

    public boolean isOperationDirectivesPresent() {
        return operationDirectives != null;
    }
}
