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

package grphaqladapter.adaptedschema.utils;

import java.util.function.Supplier;

public final class Utils {

    public static <T> T getOrDefault(T t, T d) {
        if (t == null)
            return d;

        return t;
    }

    public static String getOrDefault(String s, String d) {
        if (s == null || s.isEmpty())
            return d;

        return s;
    }

    public static <T> T getOrCompute(T o, Supplier<T> supplier) {
        if(o != null) {
            return o;
        }
        return supplier.get();
    }
}
