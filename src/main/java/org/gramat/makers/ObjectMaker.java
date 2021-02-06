package org.gramat.makers;

import java.util.Map;

public interface ObjectMaker {
    Object make(Map<String, Object> attributes);
}
