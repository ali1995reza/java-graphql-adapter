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
package graphql_adapter.adaptedschema.utils.chain;

import graphql_adapter.adaptedschema.utils.builder.IBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChainBuilder<T> implements IBuilder<ChainBuilder<T>, Chain<T>> {

    public static <T> ChainBuilder<T> newBuilder() {
        return new ChainBuilder<>();
    }

    private final List<T> chainList = new ArrayList<>();

    private ChainBuilder() {
    }

    @Override
    public Chain build() {
        List<T> copy = Collections.unmodifiableList(new ArrayList<>(chainList));
        return new ChainImpl<>(copy);
    }

    @Override
    public ChainBuilder<T> copy(Chain<T> chain) {
        this.refresh();
        chain.forEach(this::addToLast);
        return this;
    }

    @Override
    public ChainBuilder<T> refresh() {
        return this.clear();
    }

    public ChainBuilder<T> addAfter(T t, T after) {
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

    public ChainBuilder<T> addBefore(T t, T before) {
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

    public ChainBuilder<T> addToLast(T t) {
        chainList.add(t);
        return this;
    }

    public ChainBuilder<T> addToTop(T t) {
        if (chainList.size() > 0) {
            chainList.set(0, t);
        } else {
            chainList.add(t);
        }

        return this;
    }

    public ChainBuilder<T> clear() {
        this.chainList.clear();
        return this;
    }

    public T get(int index) {
        return chainList.get(index);
    }

    public ChainBuilder<T> remove(T t) {
        this.chainList.remove(t);
        return this;
    }
}
