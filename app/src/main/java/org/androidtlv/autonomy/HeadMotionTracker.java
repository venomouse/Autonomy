package org.androidtlv.autonomy;

import android.graphics.Color;
import android.widget.TextView;

import com.gemsense.gemsdk.utils.TiltElevationConverter;

import java.util.Date;
import java.util.LinkedList;

/**
 * Created by Maria on 3/11/2016.
 */
public class HeadMotionTracker {
    final float THRESHOLD_SPEED = 0.015f;
    int MIN_CONTINUOUS_MOTIONS = 5;
    int MAX_TILT_NUM = 900;

    int fps = 15;
    LinkedList<Float> tilts;
    LinkedList<Float> speeds;

    Date lastFrameTime = new Date();

    int continuousMotions = 0;
    float originalTilt = 0;

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
    }

    public void update(float tilt) {

/**        if ( ((new Date()).getTime() - lastFrameTime.getTime()) > 1000 / (double)fps ) {

            TextView tiltVal = (TextView) findViewById(R.id.tiltVal);
            tiltVal.setText(Float.toString(te[0]));
            originalTilt =tilt;

            TextView elevationVal = (TextView) findViewById(R.id.elevationVal);
            elevationVal.setText(Float.toString(te[1]));

            lastFrameTime = new Date();
            tilts.add(tilt);

            if (tilts.size() > fps * 2) {
                float currSpeed = Math.abs(tilts.get(tilts.size() - 2) - tilts.getLast());

                if (currSpeed > THRESHOLD_SPEED) {
                    if (continuousMotions == 0) {
                        originalTilt = (tilts.get(tilts.size() - 2) + tilts.get(tilts.size() - 3) +
                                tilts.get(tilts.size() - fps - 4))/3.0f;
                        TextView originalTiltView = (TextView) findViewById(R.id.origTilt);
                        originalTiltView.setText(Float.toString(originalTilt));
                    }
                    continuousMotions +=1;
                }
                else {
                    int prevContinuousMotions = continuousMotions;
                    if (!(tilt - originalTilt < -0.1)) {
                        continuousMotions = 0;
                    }

                    //the action has ended
                    if (prevContinuousMotions > 0 && continuousMotions == 0) {
                        float tiltDiff = tilt - originalTilt;
                        if (tiltDiff < 0) {
                            contMotion.setText("Finished motion, tilt:" + Float.toString(tiltDiff));
                            contMotion.setTextColor(Color.GREEN);
                        }
                        else {
                            contMotion.setText("Finished motion, tilt:" + Float.toString(tiltDiff));
                            contMotion.setTextColor(Color.RED);
                        }
                    }

                }

                TextView contMotions = (TextView) findViewById(R.id.contMotions);
                contMotions.setText(Integer.toString(continuousMotions));

                TextView speedVal = (TextView) findViewById(R.id.speedVal);
                speedVal.setText(Float.toString(currSpeed));


                if (continuousMotions > MIN_CONTINUOUS_MOTIONS) {
                    contMotion.setChecked(true);

                }
                else {
                    contMotion.setChecked(false);
                }

            }



            if (tilts.size() > MAX_TILT_NUM) {
                tilts.removeFirst();
            }



        } **/


    }
}
