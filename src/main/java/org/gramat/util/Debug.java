package org.gramat.util;

import org.gramat.automating.Automaton;
import org.gramat.automating.DeterministicMachine;
import org.gramat.automating.Machine;
import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionProgram;
import org.gramat.formatting.AutomatonFormatting;
import org.gramat.formatting.ExpressionFormatter;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Debug {

    private static final int CONSOLE_WIDTH = 80;

    private Debug() {}

    public static void print(Expression expr, String name) {
        var formatter = new ExpressionFormatter(System.out);

        formatter.setMaxWidth(CONSOLE_WIDTH);

        System.out.print("=".repeat(10));
        System.out.print(" ");
        System.out.print(name);
        System.out.print(" ");
        System.out.print("=".repeat(CONSOLE_WIDTH - 12 - name.length()));
        System.out.println();
        formatter.write(expr);
        System.out.println();
        System.out.println("=".repeat(CONSOLE_WIDTH));
    }

    public static void print(ExpressionProgram program) {
        print(program.main, "program main");
        for (var entry : program.dependencies.entrySet()) {
            print(entry.getValue(), entry.getKey());
        }
    }

    public static void print(DeterministicMachine machine, boolean copy) {
        var buffer = new StringBuilder();
        var af = new AutomatonFormatting(buffer);

        af.write(machine);

        System.out.println("=".repeat(80));
        System.out.println(buffer);

        if (copy) {
            Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new StringSelection(buffer.toString()), null);
            System.out.println("Copied to clipboard!");
        }

        System.out.println("=".repeat(80));
    }

    public static void print(Machine machine, boolean copy) {
        var buffer = new StringBuilder();
        var af = new AutomatonFormatting(buffer);

        af.write(machine);

        System.out.println("=".repeat(80));
        System.out.println(buffer);

        if (copy) {
            Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new StringSelection(buffer.toString()), null);
            System.out.println("Copied to clipboard!");
        }

        System.out.println("=".repeat(80));
    }
}
