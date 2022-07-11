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

import graphql_adapter.adaptedschema.utils.NullifyUtils;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

final class ChainImpl<T> implements Chain<T> {

    private final List<T> chainList;

    ChainImpl(List<T> chainList) {
        this.chainList = NullifyUtils.getOrDefault(chainList, Collections.emptyList());
    }

    @Override
    public Iterator<T> iterator() {
        return chainList.iterator();
    }

    @Override
    public int size() {
        return chainList.size();
    }
}
