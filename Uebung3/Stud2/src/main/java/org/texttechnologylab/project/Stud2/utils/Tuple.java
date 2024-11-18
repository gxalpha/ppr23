package org.texttechnologylab.project.Stud2.utils;

/**
 * Klasse für ein 2-Tupel
 *
 * @author Stud2
 */
public class Tuple<F, S> {
    private final F first;
    private final S second;

    /**
     * Konstruktor für ein 2-Tupel
     *
     * @param first das erste Element
     * @param second das zweite Element
     */
    public Tuple(F first, S second) {
        this.first = first;
        this.second = second;
    }

    /**
     * @return erstes Element des Tupels
     */
    public F getFirst() {
        return first;
    }

    /**
     * @return zweites Element des Tupels
     */
    public S getSecond() {
        return second;
    }
}
