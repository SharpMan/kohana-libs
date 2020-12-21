package koh.collections;

import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by Melancholia on 1/16/16.
 */
public class GenericIntStack {

    public GenericIntCell head;

    public GenericIntStack()
    {
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        GenericIntCell _loc2_ = head;
        while(_loc2_ != null)
        {
            sb.append(_loc2_.elt).append(',');
            _loc2_ = _loc2_.next;
        }
        return "{" + sb.toString() + "}";
    }

    public boolean remove(int param1)
    {
        GenericIntCell _loc2_ = null;
        GenericIntCell _loc3_ = head;
        while(_loc3_ != null)
        {
            if(_loc3_.elt == param1)
            {
                if(_loc2_ == null)
                {
                    head = _loc3_.next;
                    break;
                }
                _loc2_.next = _loc3_.next;
                break;
            }
            _loc2_ = _loc3_;
            _loc3_ = _loc3_.next;
        }
        return _loc3_ != null;
    }

    public Integer pop()
    {
        GenericIntCell _loc1_ = head;
        if(_loc1_ == null)
        {
            return null;
        }
        head = _loc1_.next;
        return _loc1_.elt;
    }

    public Iterator<Integer> iterator()
    {
        final MutableObject<GenericIntCell> l = new MutableObject<GenericIntCell>(head);
        return new Iterator<Integer>() {
            @Override
            public boolean hasNext() {
                return l.getValue() != null;
            }
            @Override
            public Integer next() {
                if(this.hasNext()) {
                    GenericIntCell _loc1_ = l.getValue();
                    l.setValue(_loc1_.next);
                    return _loc1_.elt;
                }
                throw new NoSuchElementException();
            }
        };
    }

    public boolean isEmpty()
    {
        return head == null;
    }

    public int first()
    {
        return head == null?null:head.elt;
    }

    public void add(int param1)
    {
        head = new GenericIntCell(param1,head);
    }


}
