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
package test_graphql_adapter.utils;

import graphql.ExecutionResult;

import java.util.Map;

import static graphql_adapter.adaptedschema.utils.ClassUtils.cast;

public class ExecutionResultParser {

    public static ExecutionResultParser of(ExecutionResult result) {
        return new ExecutionResultParser(result);
    }

    private final ExecutionResult result;

    public ExecutionResultParser(ExecutionResult result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return result.toString();
    }

    public <T> T getData(String key) {
        if (!result.isDataPresent()) {
            throw new IllegalStateException("data not present");
        }
        Map<String, Object> objectMap = result.getData();
        String[] routes = key.split("\\.");
        for (int i = 0; i < routes.length - 1; i++) {
            objectMap = cast(objectMap.get(routes[i]));
        }
        return cast(objectMap.get(routes[routes.length - 1]));
    }

    public ExecutionResult getResult() {
        return result;
    }

    public boolean hasError() {
        return result.getErrors() != null && !result.getErrors().isEmpty();
    }
}
