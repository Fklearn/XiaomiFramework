package com.google.protobuf;

import com.google.protobuf.Internal;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

abstract class AbstractProtobufList<E> extends AbstractList<E> implements Internal.ProtobufList<E> {
    protected static final int DEFAULT_CAPACITY = 10;
    private boolean isMutable = true;

    AbstractProtobufList() {
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof List)) {
            return false;
        }
        if (!(o instanceof RandomAccess)) {
            return super.equals(o);
        }
        List<?> other = (List) o;
        int size = size();
        if (size != other.size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!get(i).equals(other.get(i))) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int size = size();
        int hashCode = 1;
        for (int i = 0; i < size; i++) {
            hashCode = (hashCode * 31) + get(i).hashCode();
        }
        return hashCode;
    }

    public boolean add(E e) {
        ensureIsMutable();
        return super.add(e);
    }

    public void add(int index, E element) {
        ensureIsMutable();
        super.add(index, element);
    }

    public boolean addAll(Collection<? extends E> c) {
        ensureIsMutable();
        return super.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        ensureIsMutable();
        return super.addAll(index, c);
    }

    public void clear() {
        ensureIsMutable();
        super.clear();
    }

    public boolean isModifiable() {
        return this.isMutable;
    }

    public final void makeImmutable() {
        this.isMutable = false;
    }

    public E remove(int index) {
        ensureIsMutable();
        return super.remove(index);
    }

    public boolean remove(Object o) {
        ensureIsMutable();
        return super.remove(o);
    }

    public boolean removeAll(Collection<?> c) {
        ensureIsMutable();
        return super.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        ensureIsMutable();
        return super.retainAll(c);
    }

    public E set(int index, E element) {
        ensureIsMutable();
        return super.set(index, element);
    }

    /* access modifiers changed from: protected */
    public void ensureIsMutable() {
        if (!this.isMutable) {
            throw new UnsupportedOperationException();
        }
    }
}
