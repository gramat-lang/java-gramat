package org.gramat.exceptions.reporting;

import java.io.PrintStream;

public class ErrorDetailEntry implements ErrorDetail {

    private final String name;
    private final ErrorDetail value;

    public ErrorDetailEntry(String name, ErrorDetail value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public ErrorDetail getValue() {
        return value;
    }

    @Override
    public void printDetail(PrintStream out, int indentation) {
        out.print("  ".repeat(indentation));
        out.println(name + ":");
        value.printDetail(out, indentation + 1);
    }
}
