package org.androidtlv.autonomy;

/**
 * Created by Maria on 3/11/2016.
 */
public class HeadMotion {

    final static int DOWN = 0;
    final static int UP = 1;

    String message;
    int direction;

    int minMotions;
    int maxMotions;

    public HeadMotion(String message, int direction, int minMotions, int maxMotions) {
        this.message = message;
        this.direction = direction;

        this.minMotions = minMotions;
        this.maxMotions = maxMotions;

    }

}
