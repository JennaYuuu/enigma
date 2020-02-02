package enigma;

import java.util.ArrayList;
import java.util.List;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Jianing Yu
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;

        List<String> cycleArray = parseCycles(cycles);
        _cycles = new ArrayList<>(cycleArray.size());
        for (String cycleStr : cycleArray) {
            if (cycleStr.trim().isEmpty()) {
                continue;
            }
            String cycle = cycleStr.substring(1, cycleStr.length() - 1);
            addCycle(cycle);
        }
    }

    /**
     * Parse cycles inputted.
     *
     * @param cycles input of permutation cycles.
     * @return a list of parsed cycles.
     */
    private List<String> parseCycles(String cycles) {
        cycles = cycles.trim();
        List<String> result = new ArrayList<>();
        for (int i = 0, len = cycles.length(); i < len; ) {
            char ch = cycles.charAt(i);
            if (ch == '(') {
                int right = cycles.indexOf(')', i);
                result.add(cycles.substring(i, right + 1));
                i = right + 1;
            } else {
                i++;
            }
        }
        return result;
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        List<Integer> cycleList = new ArrayList<>(cycle.length());
        for (int i = 0; i < cycle.length(); i++) {
            char ch = cycle.charAt(i);
            int index = _alphabet.toInt(ch);
            cycleList.add(index);
        }
        _cycles.add(cycleList);
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        int r = wrap(p);

        for (List<Integer> cycle : _cycles) {
            int index = cycle.indexOf(r);
            if (index == -1) {
                continue;
            }

            int next = (index + 1) % cycle.size();
            return cycle.get(next);
        }

        return r;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        int r = wrap(c);

        for (List<Integer> cycle : _cycles) {
            int index = cycle.indexOf(r);
            if (index == -1) {
                continue;
            }

            int next = (index - 1 + cycle.size()) % cycle.size();
            return cycle.get(next);
        }

        return r;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int index = _alphabet.toInt(p);
        int permuted = permute(index);
        return _alphabet.toChar(permuted);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        int index = _alphabet.toInt(c);
        int permuted = invert(index);
        return _alphabet.toChar(permuted);
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        int cycleSum = 0;
        return cycleSum == _alphabet.size();
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /**
     * All cycles in this permutation.
     */
    private List<List<Integer>> _cycles;
}
