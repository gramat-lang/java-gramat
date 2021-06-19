package org.gramat.automata.builder;

import java.util.List;

public class DataContext {
    public Object get() {
        throw new UnsupportedOperationException();
    }

    public void pushContainer() {
    }

    public DataContainer popContainer() {
    }

    public DataContainer peekContainer() {
    }

    public Object createList(List<Object> list, String typeHint) {

    }

    public void setBeginPosition(int position) {

    }

    public int getBeginPosition() {
    }

    public String getSubstring(int begin, int end) {

    }

    public Object createValue(String text, String typeHint) {

    }
}
