package org.gramat.util;

import org.gramat.automating.DeterministicMachine;
import org.gramat.automating.Machine;
import org.gramat.eval.EvalNode;
import org.gramat.eval.EvalProgram;
import org.gramat.exceptions.EvalException;
import org.gramat.expressions.Expression;
import org.gramat.expressions.ExpressionProgram;
import org.gramat.formatting.AutomatonFormatting;
import org.gramat.formatting.EvalFormatter;
import org.gramat.formatting.ExpressionFormatter;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.PrintStream;

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

        checkCopy(buffer, copy);

        System.out.println("=".repeat(80));
    }

    public static void print(Machine machine, boolean copy) {
        var buffer = new StringBuilder();
        var af = new AutomatonFormatting(buffer);

        af.write(machine);

        System.out.println("=".repeat(80));
        System.out.println(buffer);

        checkCopy(buffer, copy);

        System.out.println("=".repeat(80));
    }

    public static void print(EvalNode node, boolean copy) {
        var buffer = new StringBuilder();
        var formatter = new EvalFormatter(buffer);

        formatter.write(node);

        System.out.println("=".repeat(80));
        System.out.println(buffer);

        checkCopy(buffer, copy);

        System.out.println("=".repeat(80));
    }

    private static void checkCopy(StringBuilder buffer, boolean copy) {
        if (copy) {
            Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new StringSelection(buffer.toString()), null);
            System.out.println("Copied to clipboard!");
        }
    }

    public static void print(PrintStream out, EvalException e, EvalProgram program) {
        e.printStackTrace(out);

        if (e.getNodeID() != null) {
            out.println("Node ID: " + e.getNodeID());
            var locations = program.sourceMap.getNodeLocations(e.getNodeID());

            if (locations != null) {
                for (var location : locations) {
                    out.println(" - " + location);
                }
            }
        }

        else if (e.getActionID() != null) {
            out.println("Action ID: " + e.getActionID());
            var locations = program.sourceMap.getActionLocations(e.getActionID());

            if (locations != null) {
                for (var location : locations) {
                    out.println(" - " + location);
                }
            }
        }

        if (e.getErrorDetail() != null) {
            out.println("=".repeat(80));
            out.println("== ERROR DETAILS: ");
            e.getErrorDetail().printDetail(out);
            out.println("=".repeat(80));
        }
    }
}
