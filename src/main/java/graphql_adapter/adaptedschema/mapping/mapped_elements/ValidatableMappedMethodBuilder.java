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
package graphql_adapter.adaptedschema.mapping.mapped_elements;

import graphql_adapter.adaptedschema.assertion.Assert;
import graphql_adapter.adaptedschema.mapping.mapped_elements.method.ValidatableMappedMethod;
import graphql_adapter.adaptedschema.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class ValidatableMappedMethodBuilder<T extends ValidatableMappedMethodBuilder<T, E>, E extends ValidatableMappedMethod> extends MappedMethodBuilder<T, E> {

    private final List<GraphqlValidator> validators = new ArrayList<>();

    public ValidatableMappedMethodBuilder(MappedElementType elementType) {
        super(elementType);
    }

    @Override
    public T copy(E element) {
        super.copy(element);
        element.validators().forEach(this::addValidator);
        return castThis();
    }

    @Override
    public T refresh() {
        return super.refresh()
                .clearValidators();
    }

    public T addValidator(GraphqlValidator validator) {
        Assert.isNotNull(validator, new NullPointerException("validator is null"));
        this.validators.add(validator);
        return castThis();
    }

    public T addValidators(GraphqlValidator... validators) {
        if(CollectionUtils.isEmpty(validators)) {
            return castThis();
        }
        for (GraphqlValidator validator : validators) {
            this.addValidator(validator);
        }
        return castThis();
    }

    public T addValidators(Collection<GraphqlValidator> validators) {
        if(CollectionUtils.isEmpty(validators)) {
            return castThis();
        }
        for (GraphqlValidator validator : validators) {
            this.addValidator(validator);
        }
        return castThis();
    }

    public T clearValidators() {
        this.validators.clear();
        return castThis();
    }

    public T removeValidator(GraphqlValidator validator) {
        Assert.isNotNull(validator, new NullPointerException("validator is null"));
        this.validators.remove(validator);
        return castThis();
    }

    public List<GraphqlValidator> validators() {
        return Collections.unmodifiableList(new ArrayList<>(validators));
    }
}
