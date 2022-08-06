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
package test_graphql_adapter;

import graphql_adapter.adaptedschema.mapping.mapper.utils.DimensionsNullabilityUtils;
import graphql_adapter.annotations.GraphqlNonNull;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestNullabilityUtil {

    @Test
    public void testArrayMethod() throws Exception {
        List<Boolean> nullability = DimensionsNullabilityUtils.getNullabilityOfDimensions(getMethod("testMethod1"));
        assertEquals(nullability, Arrays.asList(false, true, true, false));
        nullability = DimensionsNullabilityUtils.getNullabilityOfDimensions(getMethod("testMethod1"), false);
        assertEquals(nullability, Arrays.asList(false, true, true, true));
    }

    @Test
    public void testArrayParameter() throws Exception {
        List<Boolean> nullability = DimensionsNullabilityUtils.getNullabilityOfDimensions(getParameter(1));
        assertEquals(nullability, Arrays.asList(false, true, true, true, true, true, true, false, false));
    }

    @Test
    public void testListParameter() throws Exception {
        List<Boolean> nullability = DimensionsNullabilityUtils.getNullabilityOfDimensions(getParameter(0));
        assertEquals(nullability, Arrays.asList(false, true, false, true, true, false));
    }

    public void testMethod(@GraphqlNonNull List<List<@GraphqlNonNull List<List<List<@GraphqlNonNull String>>>>> a, @GraphqlNonNull int @GraphqlNonNull [][][][][][][] @GraphqlNonNull [] b, String c, @GraphqlNonNull Integer d) {

    }

    public int @GraphqlNonNull [][][] testMethod1() {
        return null;
    }

    public List<@GraphqlNonNull List<List<@GraphqlNonNull String>>> testMethod2() {
        return null;
    }

    public @GraphqlNonNull String testMethod3() {
        return null;
    }

    public Integer testMethod4() {
        return null;
    }

    @Test
    public void testNonNullableSingleMethod() throws Exception {
        List<Boolean> nullability = DimensionsNullabilityUtils.getNullabilityOfDimensions(getMethod("testMethod3"));
        assertEquals(nullability, Arrays.asList(false));
    }

    @Test
    public void testNonNullableSingleParameter() throws Exception {
        List<Boolean> nullability = DimensionsNullabilityUtils.getNullabilityOfDimensions(getParameter(3));
        assertEquals(nullability, Arrays.asList(false));
    }

    @Test
    public void testNullableSingleMethod() throws Exception {
        List<Boolean> nullability = DimensionsNullabilityUtils.getNullabilityOfDimensions(getMethod("testMethod4"));
        assertEquals(nullability, Arrays.asList(true));
    }

    @Test
    public void testNullableSingleParameter() throws Exception {
        List<Boolean> nullability = DimensionsNullabilityUtils.getNullabilityOfDimensions(getParameter(2));
        assertEquals(nullability, Arrays.asList(true));
    }

    private Method getMethod(String name) throws NoSuchMethodException {
        return TestNullabilityUtil.class.getMethod(name);
    }

    private Parameter getParameter(int index) throws NoSuchMethodException {
        return TestNullabilityUtil.class.getMethod("testMethod", List.class, int[][][][][][][][].class, String.class, Integer.class)
                .getParameters()[index];
    }
}
