package grphaqladapter.adaptedschemabuilder.utils;

import java.util.function.Supplier;

public class Reference<T> implements Supplier<T> {

    private T object;
    private boolean lock;

    public synchronized Reference<T> set(T object) {
        if(lock) {
            throw new IllegalStateException("can not set object when ref is lock");
        }
        this.object = object;
        return this;
    }

    public synchronized Reference<T> lock() {
        if(lock) {
            throw new IllegalStateException("ref is already locked");
        }
        lock = true;
        return this;
    }

    @Override
    public T get() {
        return object;
    }
}
