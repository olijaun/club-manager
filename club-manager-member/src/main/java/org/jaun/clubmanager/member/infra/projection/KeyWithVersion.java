package org.jaun.clubmanager.member.infra.projection;

import java.io.Serializable;

public class KeyWithVersion<T> implements Serializable {

    private final T key;
    private final long version;

    public KeyWithVersion(T key, long version) {
        this.key = key;
        this.version = version;
    }

    public T getKey() {
        return key;
    }

    public long getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        KeyWithVersion<?> that = (KeyWithVersion<?>) o;

        return key != null ? key.equals(that.key) : that.key == null;
    }

    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }
}
