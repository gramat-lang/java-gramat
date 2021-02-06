package org.gramat.exceptions.reporting;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ErrorDetailGroup implements ErrorDetail, Iterable<ErrorDetail> {

    private final ErrorDetail[] details;

    public ErrorDetailGroup(ErrorDetail[] details) {
        this.details = details;
    }

    @Override
    public Iterator<ErrorDetail> iterator() {
        return new Iterator<>() {
            int i;
            @Override
            public boolean hasNext() {
                return i < details.length;
            }

            @Override
            public ErrorDetail next() {
                if (i < details.length) {
                    return details[i];
                }
                else {
                    throw new NoSuchElementException();
                }
            }
        };
    }

    @Override
    public void printDetail(PrintStream out, int indentation) {
        for (int i = 0; i < details.length; i++) {
            out.print("  ".repeat(indentation));
            out.println(i + ":");

            details[i].printDetail(out, indentation + 1);
        }
    }
}
