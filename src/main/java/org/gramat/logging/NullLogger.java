package org.gramat.logging;

public class NullLogger implements Logger {
    @Override
    public void debug(String format, Object... args) {
        // shh...
    }

    @Override
    public void info(String format, Object... args) {
        // shh...
    }

    @Override
    public void warn(String format, Object... args) {
        // shh...
    }

    @Override
    public void error(String message, Throwable error) {
        // shh...
    }
}
