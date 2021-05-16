package tools;

import org.gramat.machines.Automaton;
import org.gramat.machines.Machine;
import org.gramat.pipeline.MachineFormatter;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AmEditor {
    public static String url(Automaton automaton) {
        var formatter = new MachineFormatter();
        var amCode = formatter.writeAutomaton(automaton);
        return url(amCode);
    }

    public static String url(Machine machine) {
        var formatter = new MachineFormatter();
        var amCode = formatter.writeMachine(machine);
        return url(amCode);
    }

    public static String url(String amCode) {
        var data = amCode.getBytes(StandardCharsets.UTF_8);
        var base64 = Base64.getEncoder().encodeToString(data);
        var param = URLEncoder.encode(base64, StandardCharsets.UTF_8);
        return "https://sergiouph.github.io/am-editor/?dir=LR&input=" + param;
    }
}
