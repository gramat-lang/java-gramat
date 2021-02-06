package org.gramat.makers;

import java.util.List;

public interface ListMaker extends Maker {

    Object make(List<Object> items);

}
