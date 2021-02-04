package org.gramat.logging;

import java.io.PrintStream;

public class PrintStreamLogger implements Logger {

    private final PrintStream out;

    public PrintStreamLogger(PrintStream out) {
        this.out = out;
    }

    @Override
    public void debug(String format, Object... args) {
        log("DEBUG", format, args);
    }

    @Override
    public void info(String format, Object... args) {
        log("INFO", format, args);
    }

    @Override
    public void warn(String format, Object... args) {
        log("WARNING", format, args);
    }

    @Override
    public void error(String message, Throwable e) {
        log("ERROR", message, new Object[0]);
        e.printStackTrace(out);
    }

    private void log(String level, String format, Object[] args) {
        out.println(level + ": " + String.format(format, args));
        try { Thread.sleep(1); } catch (Exception e) {} // TODO remove this
    }
}
