package enigma;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Jianing Yu
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;

        _activeRotors = new ArrayList<>(numRotors);
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _activeRotors.clear();
        for (int i = 0, len = rotors.length; i < len; i++) {
            String rotorName = rotors[i];
            Rotor rotor = getRotor(rotorName);
            if (_activeRotors.contains(rotor)) {
                throw error("Duplicate rotors selected: %s", rotorName);
            }
            if (i == 0 && !rotor.reflecting()) {
                throw error("The first rotor is not a reflector.");
            }
            _activeRotors.add(rotor);
        }
    }

    /**
     * Get rotor from all available rotors by name.
     * @param name the rotor name.
     * @return the Rotor.
     */
    private Rotor getRotor(String name) {
        for (Rotor rotor : _allRotors) {
            if (rotor.name().equals(name)) {
                return rotor;
            }
        }
        throw error("Unable to find rotor named %s.", name);
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != _numRotors - 1) {
            throw error("Wrong number of settings");
        }
        for (int i = 1; i < _numRotors; i++) {
            Rotor rotor = _activeRotors.get(i);
            rotor.set(setting.charAt(i - 1));
        }
    }

    /**
     * Get setting string of rotors. (Test-purpose)
     *
     * @return the setting.
     */
    String getSettings() {
        String settings = "";
        for (int i = 1; i < _activeRotors.size(); i++) {
            Rotor activeRotor = _activeRotors.get(i);
            int setting = activeRotor.setting();
            settings += _alphabet.toChar(setting);
        }
        return settings;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        /*
         1. Move pawls;
         2. Pass plugboard;
         3. Convert code;
         4. Reflection;
         5. Convert backward;
         6. Plugboard again;
         */

        advanceRotors();

        if (_plugboard != null) {
            c = _plugboard.permute(c);
        }

        for (int i = _activeRotors.size() - 1; i > 0; i--) {
            Rotor activeRotor = _activeRotors.get(i);
            c = activeRotor.convertForward(c);
        }

        c = _activeRotors.get(0).convertForward(c);

        for (int i = 1; i < _activeRotors.size(); i++) {
            Rotor activeRotor = _activeRotors.get(i);
            c = activeRotor.convertBackward(c);
        }

        if (_plugboard != null) {
            c = _plugboard.invert(c);
        }

        return c;
    }

    /**
     * Advance rotors as needed.
     */
    private void advanceRotors() {
        boolean[] advanced = new boolean[_activeRotors.size()];
        advanced[_activeRotors.size() - 1] = true;
        for (int i = _activeRotors.size() - 1; i > 0; i--) {
            Rotor activeRotor = _activeRotors.get(i);
            Rotor leftRotor = _activeRotors.get(i - 1);
            if (activeRotor.atNotch() && leftRotor.rotates()) {
                advanced[i] = true;
                 advanced[i - 1] = true;
            }
        }

        for (int i = 1; i < _activeRotors.size(); i++) {
            Rotor activeRotor = _activeRotors.get(i);
            if (activeRotor.rotates() && advanced[i]) {
                activeRotor.advance();
            }
        }
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String result = "";
        for (int i = 0; i < msg.length(); i++) {
            char ch = msg.charAt(i);
            if (Character.isWhitespace(ch)) {
                continue;
            }
            int code = _alphabet.toInt(ch);
            int encrypted = convert(code);
            result += _alphabet.toChar(encrypted);
        }
        return result;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /**
     * The number of rotors.
     */
    private final int _numRotors;

    /**
     * The number of pawls.
     */
    private final int _pawls;

    /**
     * All rotors available.
     */
    private final Collection<Rotor> _allRotors;

    /**
     * Rotors inserted into this machine.
     */
    private final List<Rotor> _activeRotors;

    /**
     * The plugboard of this machine.
     */
    private Permutation _plugboard;
}
