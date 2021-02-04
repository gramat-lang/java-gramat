package org.gramat.logging;

public interface Logger {

    void debug(String format, Object... args);
    void info(String format, Object... args);
    void warn(String format, Object... args);
    void error(String message, Throwable error);

}
