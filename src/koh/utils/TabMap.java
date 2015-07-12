package koh.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alleos13
 */
public class TabMap<TClass> {

    TClass[] tab;
    private int start_index;

    public TabMap(Class<TClass> classe, int size, int start_index) {
        this.start_index = start_index;
        this.tab = (TClass[]) Array.newInstance(classe, size);
    }

    public TabMap(Class<TClass> classe, int size) {
        this(classe, size, 0);
    }

    public int size() {
        return tab.length;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean contains(int key) {
        key = key - start_index;
        if (key < 0) {
            key = 0;
        }
        if (key > size()) {
            return false;
        }
        try {
            return tab[key] != null;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public TClass[] toTab() {
        return tab;
    }

    public List<TClass> toList() {
        List<TClass> list = new ArrayList<TClass>();
        for (TClass t : tab) {
            if (t != null) {
                list.add(t);
            }
        }
        return list;
    }

    public void set(TClass[] tab) {
        this.tab = tab;
    }

    public boolean add(int key, TClass value) {
        key = key - start_index;
        if (key < 0) {
            key = 0;
        }
        if (key > size()) {
            return false;
        }
        try {
            tab[key] = value;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean remove(int key) {
        key = key - start_index;
        if (key < 0) {
            key = 0;
        }
        if (key > size()) {
            return false;
        }
        try {
            tab[key] = null;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void clear() {
        this.tab = null;
        try {
            this.finalize();
        } catch (Throwable ex) {
        }
    }

    public TClass get(int key) {
        key = key - start_index;
        if (key < 0) {
            key = 0;
        }
        if (key > size()) {
            return null;
        }
        try {
            return tab[key];
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public int indexOf(TClass c_to) {
        for (int i = 0; i < size(); i++) {
            if (get(i) == c_to) {
                return i;
            }
        }
        return -1;
    }
}
