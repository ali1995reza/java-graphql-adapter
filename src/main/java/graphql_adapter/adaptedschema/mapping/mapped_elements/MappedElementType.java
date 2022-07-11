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

package graphql_adapter.adaptedschema.mapping.mapped_elements;

public enum MappedElementType {
    ENUM,
    ENUM_VALUE,
    INTERFACE,
    OBJECT_TYPE,
    UNION,
    INPUT_TYPE,
    SCALAR,
    QUERY(true),
    MUTATION(true),
    SUBSCRIPTION(true),
    DIRECTIVE,
    FIELD,
    INPUT_FIELD,
    ARGUMENT;

    private final boolean isTopLevel;

    MappedElementType(boolean isTopLevel) {
        this.isTopLevel = isTopLevel;
    }

    MappedElementType() {
        this(false);
    }


    public boolean is(MappedElementType other) {
        return this == other;
    }

    public boolean isArgument() {
        return is(ARGUMENT);
    }

    public boolean isDirective() {
        return is(DIRECTIVE);
    }

    public boolean isEnum() {
        return is(ENUM);
    }

    public boolean isEnumValue() {
        return is(ENUM_VALUE);
    }

    public boolean isField() {
        return is(FIELD);
    }

    public boolean isInputField() {
        return is(INPUT_FIELD);
    }

    public boolean isInputType() {
        return is(INPUT_TYPE);
    }

    public boolean isInterface() {
        return is(INTERFACE);
    }

    public boolean isMutation() {
        return is(MUTATION);
    }

    public boolean isNot(MappedElementType other) {
        return this != other;
    }

    public boolean isObjectType() {
        return is(OBJECT_TYPE);
    }

    public boolean isOneOf(MappedElementType... types) {
        for (MappedElementType type : types) {
            if (this == type) {
                return true;
            }
        }
        return false;
    }

    public boolean isQuery() {
        return is(QUERY);
    }

    public boolean isScalar() {
        return is(SCALAR);
    }

    public boolean isSubscription() {
        return is(SUBSCRIPTION);
    }

    public boolean isTopLevelType() {
        return isTopLevel;
    }

    public boolean isUnion() {
        return is(UNION);
    }
}
