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

package grphaqladapter.adaptedschema.utils.chain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChainBuilder<T> {


    public final static <T> ChainBuilder<T> newBuilder() {
        return new ChainBuilder<>();
    }
    private final List<T> chainList = new ArrayList<>();

    private ChainBuilder() {
    }

    public synchronized ChainBuilder addAfter(T t, T after) {
        int index = chainList.indexOf(after);
        if (index == -1)
            throw new IllegalStateException("element not found");

        if (index == chainList.size() - 1) {
            chainList.add(t);
        } else {
            chainList.add(index + 1, t);
        }

        return this;

    }

    public synchronized ChainBuilder addBefore(T t, T before) {
        int index = chainList.indexOf(before);
        if (index == -1)
            throw new IllegalStateException("element not found");

        if (index == 0) {
            chainList.add(0, t);
        } else {
            chainList.add(index - 1, t);
        }

        return this;

    }

    public synchronized ChainBuilder addToLast(T t) {
        chainList.add(t);
        return this;
    }

    public synchronized ChainBuilder addToTop(T t) {
        if (chainList.size() > 0) {
            chainList.set(0, t);
        } else {
            chainList.add(t);
        }

        return this;
    }

    public synchronized Chain<T> build() {
        List<T> copy = Collections.unmodifiableList(new ArrayList<>(chainList));

        return new ChainImpl<>(copy);
    }

}
