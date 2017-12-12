package org.xeustechnologies.jcl;

import java.util.*;
import java.io.Serializable;

import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentSkipListSet;

public class SetList<E>
        extends AbstractList<E>
        implements List<E>, RandomAccess, Cloneable, Serializable {
    protected final Set<E> underlying;
    public SetList() {
        // underlying = new ConcurrentSkipListSet<E>();
        underlying = new ConcurrentSkipListSet();
    }
    public SetList(Set<E> other) {
        underlying = other;
    }
    public E get(int index) {
        Iterator<E> it = iterator();
        for (int i = 0; i < index; i++) {
            if (it.hasNext()) {
                it.next();
            } else {
                throw new IndexOutOfBoundsException();
            }
        }
        return it.next();
    }
    public int size() {
        return underlying.size();
    }
    public E set(int index, E elem) {
        if (add(elem)) {
            return elem;
        } else {
            return null;
        }
    }
    public boolean add(E elem) {
        return underlying.add(elem);
    }
    public void add(int index, E elem) {
        add(elem);
    }
    public E remove(int index) {
        E elem = get(index);
        if (underlying.remove(elem)) {
            return elem;
        } else {
            return null;
        }
    }
    public Iterator<E> iterator() {
        return underlying.iterator();
    }
}
