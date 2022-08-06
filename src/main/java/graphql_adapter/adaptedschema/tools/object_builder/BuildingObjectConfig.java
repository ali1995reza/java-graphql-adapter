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
package graphql_adapter.adaptedschema.tools.object_builder;

import graphql_adapter.adaptedschema.utils.builder.IBuilder;

public class BuildingObjectConfig {

    public static BuildingObjectConfig.Builder newConfig() {
        return new Builder();
    }

    private final boolean useInputFieldsDefaultValues;
    private final boolean useExactProvidedListForScalarTypes;
    private final boolean validateInputFields;

    public BuildingObjectConfig(boolean useInputFieldsDefaultValues, boolean useExactProvidedListForScalarTypes, boolean validateInputFields) {
        this.useInputFieldsDefaultValues = useInputFieldsDefaultValues;
        this.useExactProvidedListForScalarTypes = useExactProvidedListForScalarTypes;
        this.validateInputFields = validateInputFields;
    }

    public boolean useExactProvidedListForScalarTypes() {
        return useExactProvidedListForScalarTypes;
    }

    public boolean useInputFieldsDefaultValues() {
        return useInputFieldsDefaultValues;
    }

    public boolean validateInputFields() {
        return validateInputFields;
    }

    public static class Builder implements IBuilder<Builder, BuildingObjectConfig> {

        private boolean useInputFieldsDefaultValues = false;
        private boolean useExactProvidedListForScalarTypes = false;
        private boolean validateInputFields = false;

        @Override
        public BuildingObjectConfig build() {
            return new BuildingObjectConfig(useInputFieldsDefaultValues, useExactProvidedListForScalarTypes, validateInputFields);
        }

        @Override
        public Builder copy(BuildingObjectConfig config) {
            return refresh()
                    .useExactProvidedListForScalarTypes(config.useExactProvidedListForScalarTypes())
                    .useInputFieldsDefaultValues(config.useInputFieldsDefaultValues());
        }

        @Override
        public Builder refresh() {
            this.useInputFieldsDefaultValues = false;
            this.useExactProvidedListForScalarTypes = false;
            this.validateInputFields = false;
            return this;
        }

        public Builder dontUseExactProvidedListForScalarTypes() {
            return this.useExactProvidedListForScalarTypes(false);
        }

        public Builder dontUseInputFieldDefaultValues() {
            return this.useInputFieldsDefaultValues(false);
        }

        public Builder useExactProvidedListForScalarTypes(boolean useExactProvidedListForScalarTypes) {
            this.useExactProvidedListForScalarTypes = useExactProvidedListForScalarTypes;
            return this;
        }

        public Builder useExactProvidedListForScalarTypes() {
            return this.useExactProvidedListForScalarTypes(true);
        }

        public Builder useInputFieldsDefaultValues(boolean useInputFieldsDefaultValues) {
            this.useInputFieldsDefaultValues = useInputFieldsDefaultValues;
            return this;
        }

        public Builder useInputFieldsDefaultValues() {
            return this.useInputFieldsDefaultValues(true);
        }

        public Builder dontValidateInputFields() {
            return validateInputFields(false);
        }

        public Builder validateInputFields() {
            return validateInputFields(true);
        }

        public Builder validateInputFields(boolean validateInputFields) {
            this.validateInputFields = validateInputFields;
            return this;
        }

    }
}
