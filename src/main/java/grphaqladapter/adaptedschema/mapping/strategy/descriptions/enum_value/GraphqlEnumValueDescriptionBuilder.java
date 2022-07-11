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

package grphaqladapter.adaptedschema.mapping.strategy.descriptions.enum_value;

import grphaqladapter.adaptedschema.mapping.strategy.descriptions.GraphqlElementDescriptionBuilder;

public class GraphqlEnumValueDescriptionBuilder extends GraphqlElementDescriptionBuilder<GraphqlEnumValueDescriptionBuilder, GraphqlEnumValueDescription> {

    public static GraphqlEnumValueDescriptionBuilder newBuilder() {
        return new GraphqlEnumValueDescriptionBuilder();
    }

    @Override
    public GraphqlEnumValueDescription build() {
        return new GraphqlEnumValueDescriptionImpl(name(), description());
    }
}
