package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.error;

/** Enigma simulator.
 *  @author Jianing Yu
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine machine = readConfig();

        String firstLine = _input.nextLine();
        if (!firstLine.trim().startsWith("*")) {
            throw error("First line of input is not a setting.");
        }
        setUp(machine, firstLine);

        while (_input.hasNextLine()) {
            String line = _input.nextLine();
            if (line.trim().startsWith("*")) {
                setUp(machine, line);
                continue;
            }

            String converted = machine.convert(line);
            printMessageLine(converted);
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            _alphabet = new Alphabet(_config.next());
            int numRotors = _config.nextInt();
            int pawls = _config.nextInt();
            List<Rotor> allRotors = new ArrayList<>();
            while (_config.hasNext()) {
                Rotor rotor = readRotor();
                allRotors.add(rotor);
            }

            return new Machine(_alphabet, numRotors, pawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file has wrong format.");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name = _config.next();

            String typeAndNotches = _config.next();
            char type = typeAndNotches.charAt(0);
            String notches = typeAndNotches.substring(1);

            String permBuilder = "";
            while (_config.hasNext("\\(.+\\)")) {
                String next = _config.next("\\(.+\\)");
                permBuilder += next;
            }
            String perms = permBuilder;

            if (type == 'M') {
                return new MovingRotor(name,
                        new Permutation(perms, _alphabet), notches);
            } else if (type == 'N') {
                return new FixedRotor(name, new Permutation(perms, _alphabet));
            } else if (type == 'R') {
                return new Reflector(name, new Permutation(perms, _alphabet));
            }
            throw error("Unknown rotor type %s", type);
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] setting = settings.split("\\s+");
        int numRotors = M.numRotors();

        M.insertRotors(Arrays.copyOfRange(setting, 1, numRotors + 1));
        M.setRotors(setting[numRotors + 1]);

        int index = settings.indexOf("(");
        if (index != -1) {
            String perm = settings.substring(index);
            Permutation plug = new Permutation(perm, _alphabet);
            M.setPlugboard(plug);
        }
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        for (int i = 0, len = msg.length(); i < len; i++) {
            char ch = msg.charAt(i);
            if (i != 0 && i % 5 == 0) {
                _output.print(" ");
            }
            _output.print(ch);
        }
        _output.println();
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
}
