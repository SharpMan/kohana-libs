package koh.utils;

/**
 *
 * @author Alleos13
 */
public class Couple<L, R> {

    public L first;
    public R second;

    public Couple(L s, R i) {
        first = s;
        second = i;
    }

    public void Clear() {
        try {
            this.first = null;
            this.second = null;
            this.finalize();
        } catch (Throwable tr) {

        }
    }

}
