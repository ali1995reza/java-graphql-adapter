package grphaqladapter.adaptedschemabuilder.mapper.strategy.impl.chain;

import grphaqladapter.adaptedschemabuilder.mapper.strategy.Chain;
import grphaqladapter.adaptedschemabuilder.utils.Utils;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

final class ChainImpl<T> implements Chain<T> {

    private final List<T> chainList;

    ChainImpl(List<T> chainList) {
        this.chainList = Utils.nullifyOrGetDefault(chainList, Collections.EMPTY_LIST);
    }


    @Override
    public int size() {
        return chainList.size();
    }

    @Override
    public Iterator<T> iterator() {
        return chainList.iterator();
    }
}
