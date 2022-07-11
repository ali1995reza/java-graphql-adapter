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

package tests;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class Randomer {

    private final static Random RANDOM = new SecureRandom();

    public static <T> T random(List<T> list) {
        return list.get(RANDOM.nextInt(list.size()));
    }

    public static <T> T random(T... list) {
        return list[RANDOM.nextInt(list.length)];
    }

    public static <T> T random(Collection<T> collection)
    {
        int len = collection.size();
        int where = RANDOM.nextInt(len);
        for(T t: collection) {
            if(--where < 0) {
                return t;
            }
        }

        throw new IllegalStateException("collection bad behavior");
    }

    public static boolean randomBoolean() {
        return RANDOM.nextBoolean();
    }

}
