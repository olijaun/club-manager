package org.jaun.clubmanager.domain.model.commons;

import java.util.Objects;

import com.google.common.base.MoreObjects;

public abstract class Entity<T extends Id> {

    public abstract T getId();

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).addValue(getId().getValue()).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Entity<?> entity = (Entity<?>) o;
        return Objects.equals(getId(), entity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}