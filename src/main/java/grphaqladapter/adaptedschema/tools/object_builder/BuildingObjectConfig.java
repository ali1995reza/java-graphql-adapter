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

package grphaqladapter.adaptedschema.tools.object_builder;

import grphaqladapter.adaptedschema.utils.builder.IBuilder;

public class BuildingObjectConfig {

    public final static BuildingObjectConfig DISABLE_BOTH = BuildingObjectConfig.newConfig()
            .dontUseInputFieldDefaultValues()
            .dontUseExactProvidedListForScalarTypes()
            .build();

    public final static BuildingObjectConfig ENABLE_BOTH = BuildingObjectConfig.newConfig()
            .dontUseInputFieldDefaultValues()
            .dontUseExactProvidedListForScalarTypes()
            .build();

    public final static BuildingObjectConfig ONLY_USE_EXACT_LIST = BuildingObjectConfig.newConfig()
            .dontUseInputFieldDefaultValues()
            .useExactProvidedListForScalarTypes()
            .build();

    public final static BuildingObjectConfig ONLY_USE_DEFAULT_VALUES = BuildingObjectConfig.newConfig()
            .useInputFieldsDefaultValues()
            .dontUseExactProvidedListForScalarTypes()
            .build();


    public static BuildingObjectConfig.Builder newConfig() {
        return new Builder();
    }

    public static class Builder implements IBuilder<Builder, BuildingObjectConfig> {

        private boolean useInputFieldsDefaultValues = false;
        private boolean useExactProvidedListForScalarTypes = false;

        public Builder useExactProvidedListForScalarTypes(boolean useExactProvidedListForScalarTypes) {
            this.useExactProvidedListForScalarTypes = useExactProvidedListForScalarTypes;
            return this;
        }

        public Builder useInputFieldsDefaultValues(boolean useInputFieldsDefaultValues) {
            this.useInputFieldsDefaultValues = useInputFieldsDefaultValues;
            return this;
        }

        public Builder useExactProvidedListForScalarTypes() {
            return this.useExactProvidedListForScalarTypes(true);
        }

        public Builder useInputFieldsDefaultValues() {
            return this.useInputFieldsDefaultValues(true);
        }

        public Builder dontUseExactProvidedListForScalarTypes() {
            return this.useExactProvidedListForScalarTypes(false);
        }

        public Builder dontUseInputFieldDefaultValues() {
            return this.useInputFieldsDefaultValues(false);
        }

        @Override
        public BuildingObjectConfig build() {
            return new BuildingObjectConfig(useInputFieldsDefaultValues, useExactProvidedListForScalarTypes);
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
            return this;
        }
    }

    private final boolean useInputFieldsDefaultValues;
    private final boolean useExactProvidedListForScalarTypes;

    public BuildingObjectConfig(boolean useInputFieldsDefaultValues, boolean useExactProvidedListForScalarTypes) {
        this.useInputFieldsDefaultValues = useInputFieldsDefaultValues;
        this.useExactProvidedListForScalarTypes = useExactProvidedListForScalarTypes;
    }

    public boolean useInputFieldsDefaultValues() {
        return useInputFieldsDefaultValues;
    }

    public boolean useExactProvidedListForScalarTypes() {
        return useExactProvidedListForScalarTypes;
    }
}
