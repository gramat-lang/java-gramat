package org.gramat.util;

import java.util.function.BiConsumer;

public interface WalkFunction<T> extends BiConsumer<T, WalkControl> {}
