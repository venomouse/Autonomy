package org.androidtlv.autonomy;

import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import com.gemsense.gemsdk.utils.TiltElevationConverter;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by Maria on 3/11/2016.
 */
public class HeadMotionTracker {
    final float THRESHOLD_SPEED = 0.01f;
    final int SPEED_TIME_DIFF = 2;
    int MIN_CONTINUOUS_MOTIONS = 7;
    int MAX_TILT_NUM = 900;
    final float TILT_DIFF_VAR = 0.07f;
    final int DEFAULT_MOTIONS_VAR = 3;

    int fps = 15;
    LinkedList<Float> tilts;
    LinkedList<Float> speeds;

    Date lastFrameTime = new Date();

    int continuousMotions = 0;
    float originalTilt = 0;

    boolean inMeaningfulMotion = false;
    boolean motionDetected = false;
    HeadMotion lastMotion;

    public HeadMotionTracker() {
        tilts = new LinkedList<Float>();
        speeds = new LinkedList<Float>();

    }

    public void reset() {
        tilts = new LinkedList<Float>();
        speeds = new LinkedList<Float>();
        continuousMotions = 0;
        originalTilt = 0;
        lastFrameTime = new Date();
        inMeaningfulMotion = false;
        motionDetected = false;
    }

    public boolean update(float tilt) {
        motionDetected = false;
        if (((new Date()).getTime() - lastFrameTime.getTime()) > 1000 / (double) fps) {

            lastFrameTime = new Date();
            tilts.add(tilt);

            if (tilts.size() > fps * 2) {
                float currSpeed = Math.abs(tilts.get(tilts.size() - SPEED_TIME_DIFF) - tilts.getLast());
                int prevContinuousMotions = continuousMotions;
                if (currSpeed > THRESHOLD_SPEED) {
                    continuousMotions += 1;
                } else {
                    continuousMotions = 0;

                }


                boolean prevInMeaningful = inMeaningfulMotion;
                inMeaningfulMotion = (continuousMotions > MIN_CONTINUOUS_MOTIONS);

                //start of the motion
                if (!prevInMeaningful && inMeaningfulMotion) {
                    int totalDiff = MIN_CONTINUOUS_MOTIONS + SPEED_TIME_DIFF;
                    originalTilt = (tilts.get(tilts.size() - totalDiff) + tilts.get(tilts.size() - totalDiff - 1) +
                            tilts.get(tilts.size() - totalDiff - 2)) / 3.0f;

                }

                //end of the motion
                if (prevInMeaningful && !inMeaningfulMotion) {
                    float tiltDiff = tilt - originalTilt;
                    if (tiltDiff > 0) {
                        Log.d("Gem", "Finished motion, tiltDiff:" + Float.toString(tiltDiff)
                                + " numMotions:" + Integer.toString(prevContinuousMotions));

                        motionDetected = true;
                        lastMotion = new HeadMotion(null, HeadMotion.UP, prevContinuousMotions - DEFAULT_MOTIONS_VAR,
                                prevContinuousMotions - DEFAULT_MOTIONS_VAR);
                    }
                    if (tiltDiff > 0) {


                    }
                }

            }


            if (tilts.size() > MAX_TILT_NUM) {
                tilts.removeFirst();
            }

            return false;

        }

        return true;
    }

    public boolean motionDetected() {
        return motionDetected;
    }


    public HeadMotion getLastMotion() {
        return lastMotion;
    }





}
