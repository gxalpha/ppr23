package org.texttechnologylab.project.Stud2.impl.helper;

/**
 * Klasse für ein 3-Tupel
 *
 * @author Stud2
 */
public class Tripple<F, S, T> {
    private final F first;
    private final S second;
    private final T third;

    /**
     * Konstruktor für ein 3-Tupel
     *
     * @param first das erste Element
     * @param second das zweite Element
     * @param third das dritte Element
     */
    public Tripple(F first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    /**
     * @return erstes Element des Tripels
     */
    public F getFirst() {
        return first;
    }

    /**
     * @return zweites Element des Tripels
     */
    public S getSecond() {
        return second;
    }

    /**
     * @return drittes Element des Tripels
     */
    public T getThird() {
        return third;
    }
}
