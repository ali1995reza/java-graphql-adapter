package tests;

import graphql.ExecutionResult;

import java.util.Map;

public class ExecutionResultParser {

    private final ExecutionResult result;

    public ExecutionResultParser(ExecutionResult result) {
        this.result = result;
    }

    public static ExecutionResultParser of(ExecutionResult result) {
        return new ExecutionResultParser(result);
    }

    public boolean hasError() {
        return result.getErrors() != null && !result.getErrors().isEmpty();
    }

    public <T> T getData(String key) {
        if (!result.isDataPresent()) {
            throw new IllegalStateException("data not present");
        }
        Map<String, Object> objectMap = result.getData();
        String[] routes = key.split("\\.");
        for (int i = 0; i < routes.length - 1; i++) {
            objectMap = (Map<String, Object>) objectMap.get(routes[i]);
        }
        return (T) objectMap.get(routes[routes.length - 1]);
    }

    @Override
    public String toString() {
        return result.toString();
    }
}
