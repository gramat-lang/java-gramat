package org.gramat.machines;

import org.gramat.symbols.Symbol;
import org.gramat.tools.Validations;

public class RecursionSymbol implements Symbol {

    public final String name;

    public RecursionSymbol(String name) {
        this.name = Validations.notEmpty(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
