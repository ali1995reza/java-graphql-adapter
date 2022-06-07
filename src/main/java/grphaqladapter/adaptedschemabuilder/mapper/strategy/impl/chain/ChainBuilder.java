package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.chain;

import grphaqladapter.adaptedschemabuilder.mapper.strategy.Chain;
import grphaqladapter.adaptedschemabuilder.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChainBuilder<T> {


    private final List<T> chainList = new ArrayList<>();


    private ChainBuilder() {
    }

    public final static <T> ChainBuilder<T> newBuilder() {
        return new ChainBuilder<>();
    }

    public synchronized ChainBuilder addToTop(T t) {
        if (chainList.size() > 0) {
            chainList.set(0, t);
        } else {
            chainList.add(t);
        }

        return this;
    }

    public synchronized ChainBuilder addToLast(T t) {
        chainList.add(t);
        return this;
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

    public synchronized Chain<T> build() {
        List<T> copy = Collections.unmodifiableList(Utils.copy(chainList));

        return new ChainImpl<>(copy);
    }

}
