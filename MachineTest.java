package enigma;

import org.junit.Test;
import static enigma.TestUtils.*;
import static org.junit.Assert.*;

/**
 * The suite of all JUnit tests for the Permutation class.
 *
 * @author Jianing Yu
 */
public class MachineTest {
    /**
     * Test the example of instructions.
     */
    @Test
    public void testExample() {
        Machine machine = new Machine(UPPER, 5, 3, NAVALA_ROTORS);
        machine.insertRotors(new String[]{"B", "Beta", "III", "IV", "I"});
        machine.setRotors("AXLE");
        machine.setPlugboard(new Permutation("(YF) (ZH)", UPPER));

        String result = machine.convert("Y");
        assertEquals("Z", result);
        assertEquals("AXLF", machine.getSettings());
    }

    /**
     * Test the "Double Stepping".
     */
    @Test
    public void testStepping() {
        Machine machine = new Machine(UPPER, 5, 3, NAVALA_ROTORS);
        machine.insertRotors(new String[]{"B", "Beta", "III", "IV", "I"});
        machine.setRotors("AXLE");
        machine.setPlugboard(new Permutation("(YF) (ZH)", UPPER));

        for (int i = 0; i < 12; i++) {
            machine.convert("Y");
        }
        assertEquals("AXLQ", machine.getSettings());

        machine.convert("Y");
        assertEquals("AXMR", machine.getSettings());

        for (int i = 0; i < 597; i++) {
            machine.convert("Y");
        }
        assertEquals("AXIQ", machine.getSettings());

        machine.convert("Y");
        assertEquals("AXJR", machine.getSettings());

        machine.convert("Y");
        assertEquals("AYKS", machine.getSettings());
    }

    /**
     * Test the conversion provided by the instruction.
     */
    @Test
    public void testExampleConvert() {
        Machine machine = new Machine(UPPER, 5, 3, NAVALA_ROTORS);
        machine.insertRotors(new String[]{"B", "Beta", "III", "IV", "I"});
        machine.setRotors("AXLE");
        machine.setPlugboard(
                new Permutation("(HQ) (EX) (IP) (TR) (BY)", UPPER));

        String result = machine.convert("FROM HIS SHOULDER HIAWATHA");
        assertEquals("QVPQSOKOILPUBKJZPISFXDW", result);
    }
}
