package enigma;

import java.util.HashSet;
import java.util.Set;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Jianing Yu
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initially in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);

        this._notches = new HashSet<>();
        for (int i = 0; i < notches.length(); i++) {
            char ch = notches.charAt(i);
            int idx = perm.alphabet().toInt(ch);
            this._notches.add(idx);
        }
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    boolean atNotch() {
        return _notches.contains(setting());
    }

    @Override
    void advance() {
        int nextPos = this.permutation().wrap(setting() + 1);
        set(nextPos);
    }

    /**
     * Notches of this rotor.
     */
    private Set<Integer> _notches;
}
