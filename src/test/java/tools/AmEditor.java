package tools;

import org.gramat.automata.Automaton;
import org.gramat.io.AutomatonFormatter;
import org.gramat.io.MachineFormatter;
import org.gramat.machine.Machine;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AmEditor {

    public static String url(Automaton automaton) {
        return url(AutomatonFormatter.toString(automaton));
    }

    public static String url(Machine machine) {
        var builder = new StringBuilder();
        var formatter = new MachineFormatter();
        formatter.writeMachine(builder, machine);
        var amCode = builder.toString();
        return url(amCode);
    }

    public static String url(String amCode) {
        var data = amCode.getBytes(StandardCharsets.UTF_8);
        var base64 = Base64.getEncoder().encodeToString(data);
        var param = URLEncoder.encode(base64, StandardCharsets.UTF_8);
        return "https://sergiouph.github.io/am-editor/?dir=LR&input=" + param;
    }
}
