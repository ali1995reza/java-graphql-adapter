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
package graphql_adapter.adaptedschema.assertion;

import java.util.Objects;

public class Assert {

    public static <T extends Throwable, E> void allNotNull(T exception, E... objects) throws T {
        if (objects == null) {
            return;
        }
        for (E o : objects) {
            if (o == null) {
                throw exception;
            }
        }
    }

    public static <E> void allNotNull(E... objects) {
        allNotNull(new NullPointerException("on of provided objects is null"), objects);
    }

    public static <T extends Throwable> void isAllFalse(T exception, boolean... conditions) throws T {
        Assert.isNotNull(exception, new NullPointerException("conditions is null"));
        for (boolean bool : conditions) {
            if (bool) {
                throw exception;
            }
        }
    }

    public static <T extends Throwable> void isAllTrue(T exception, boolean... conditions) throws T {
        Assert.isNotNull(exception, new NullPointerException("conditions is null"));
        for (boolean bool : conditions) {
            if (!bool) {
                throw exception;
            }
        }
    }

    public static <T extends Throwable> void isEquals(Object o1, Object o2, T exception) throws T {
        if (!Objects.equals(o1, o2))
            throw exception;
    }

    public static void isEquals(Object o1, Object o2) {
        isEquals(o1, o2, new IllegalStateException("objects not equal [object1:" + o1 + " , object2:" + o2 + "]"));
    }

    public static <T extends Throwable> void isExactlyOneFalse(T exception, boolean... conditions) throws T {
        Assert.isNotNull(exception, new NullPointerException("conditions is null"));
        boolean flag = false;
        for (boolean bool : conditions) {
            if (!bool && flag) {
                throw exception;
            } else if (!bool) {
                flag = true;
            }
        }
        if (!flag) {
            throw exception;
        }
    }

    public static <T extends Throwable> void isExactlyOneTrue(T exception, boolean... conditions) throws T {
        Assert.isNotNull(exception, new NullPointerException("conditions is null"));
        boolean flag = false;
        for (boolean bool : conditions) {
            if (bool && flag) {
                throw exception;
            } else if (bool) {
                flag = true;
            }
        }
        if (!flag) {
            throw exception;
        }
    }

    public static <T extends Throwable> void isFalse(boolean condition, T exception) throws T {
        if (condition) {
            throw exception;
        }
    }

    public static <T extends Throwable> void isNotEquals(Object o1, Object o2, T exception) throws T {
        if (Objects.equals(o1, o2))
            throw exception;
    }

    public static void isNotEquals(Object o1, Object o2) {
        isNotEquals(o1, o2, new IllegalStateException("objects are equal [object1:" + o1 + " , object2:" + o2 + "]"));
    }

    public static <T extends Throwable> void isNotNegative(int i, T exception) throws T {
        if (i < 0)
            throw exception;
    }

    public static void isNotNegative(int i) {
        isNotNegative(i, new IllegalStateException("number can not be negative"));
    }

    public static <T extends Throwable> void isNotNull(Object o, T exception) throws T {
        if (o == null)
            throw exception;
    }

    public static void isNotNull(Object o) {
        isNotNull(o, new NullPointerException("object is null"));
    }

    public static <T extends Throwable> void isNotPositive(int i, T exception) throws T {
        if (i > 0)
            throw exception;
    }

    public static void isNotPositive(int i) {

        isNotPositive(i, new IllegalStateException("number cant be positive"));
    }

    public static <T extends Throwable> void isNull(Object o, T exception) throws T {
        if (o != null)
            throw exception;
    }

    public static void isNull(Object o) {
        isNull(o, new IllegalStateException("object must be null"));
    }

    public static <T extends Throwable> void isOneOrMoreFalse(T exception, boolean... conditions) throws T {
        Assert.isNotNull(exception, new NullPointerException("conditions is null"));
        for (boolean bool : conditions) {
            if (!bool) return;
        }
        throw exception;
    }

    public static <T extends Throwable> void isOneOrMoreTrue(T exception, boolean... conditions) throws T {
        Assert.isNotNull(exception, new NullPointerException("conditions is null"));
        for (boolean bool : conditions) {
            if (bool) return;
        }
        throw exception;
    }

    public static <T extends Throwable> void isPositive(int i, T exception) throws T {
        isTrue(i > 0, exception);
    }

    public static <T extends Throwable> void isTrue(boolean condition, T exception) throws T {
        if (!condition) {
            throw exception;
        }
    }
}
