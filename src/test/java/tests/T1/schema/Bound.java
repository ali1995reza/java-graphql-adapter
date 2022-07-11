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

package tests.T1.schema;

import java.util.Objects;

public class Bound implements Comparable<Bound> {

    private final int number;
    private final boolean contains;

    public Bound(int number, boolean contains) {
        this.number = number;
        this.contains = contains;
    }

    @Override
    public int compareTo(Bound o) {
        if (number > o.number)
            return 1;
        if (number < o.number)
            return -1;

        if (number == o.number) {
            if (contains && !o.contains)
                return 1;
            if (!contains && o.contains)
                return -1;
        }

        return 0;
    }

    public String asLowerBound() {
        return new StringBuffer().append(contains ? '[' : '(').append(number)
                .toString();
    }

    public String asUpperBound() {
        return new StringBuffer().append(number)
                .append(contains ? ']' : ')').toString();
    }

    public boolean isContains() {
        return contains;
    }

    public int number() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bound bound = (Bound) o;
        return number == bound.number && contains == bound.contains;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, contains);
    }
}
