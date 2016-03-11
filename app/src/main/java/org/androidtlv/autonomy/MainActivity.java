package org.androidtlv.autonomy;

import com.gemsense.common.GemSensorsData;
import com.gemsense.gemsdk.*;
import com.gemsense.gemsdk.utils.AzimuthElevationConverter;
import com.gemsense.gemsdk.utils.TiltElevationConverter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    int fps = 15;
    LinkedList<Float> tilts;
    LinkedList<Float> speeds;
    int MAX_TILT_NUM = 900;
    Date lastFrameTime = new Date();
    final int SPEED_TIME_DIFF = 2;
    final float THRESHOLD_SPEED = 0.015f;
    int MIN_CONTINUOUS_MOTIONS = 5;
    int continuousMotions = 0;
    float originalTilt = 0;
    int[] motionLengths = {0,5,12, 20};

    boolean inMeaningfulMotion = false;

    int notificationId = 001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tilts = new LinkedList<Float>();
        speeds = new LinkedList<Float>();

        String[] whitelist = GemSDKUtilityApp.getWhiteList((Context)this);
        GemListener gemListener = new GemListener() {
            @Override
            public void onStateChanged(int state) {
                //States handling
                switch (state) {
                    case Gem.STATE_CONNECTED:
                        Log.d("GemDemo", "Connected to a gem");
                        Toast.makeText(MainActivity.this, "Connected to the gem", Toast.LENGTH_SHORT).show();
                        break;
                    case Gem.STATE_DISCONNECTED:
                        Log.d("GemDemo", "Gem was disconnected");
                        break;
                }
            }

            @Override
            public void onSensorsChanged(GemSensorsData data) {
                RadioButton contMotion = (RadioButton) findViewById(R.id.contButton);
                if ( ((new Date()).getTime() - lastFrameTime.getTime()) > 1000 / (double)fps ) {

                    float[] q = data.quaternion; // w x y z
                    float[] a = data.acceleration;

                    TiltElevationConverter converter = new TiltElevationConverter();
                    float[] te = converter.convertLH(q);
                    float tilt = te[0];
                    float elevation = te[1];


                    TextView tiltVal = (TextView) findViewById(R.id.tiltVal);
                    tiltVal.setText(Float.toString(te[0]));


                    TextView elevationVal = (TextView) findViewById(R.id.elevationVal);
                    elevationVal.setText(Float.toString(te[1]));

                    lastFrameTime = new Date();
                    tilts.add(tilt);

                    if (tilts.size() > fps * 2) {
                        float currSpeed = Math.abs(tilts.get(tilts.size() - SPEED_TIME_DIFF) - tilts.getLast());
                        int prevContinuousMotions = continuousMotions;
                        if (currSpeed > THRESHOLD_SPEED) {
                            continuousMotions +=1;
                        }
                        else {
                            continuousMotions = 0;

                        }

                        TextView contMotions = (TextView) findViewById(R.id.contMotions);
                        contMotions.setText(Integer.toString(continuousMotions));

                        TextView speedVal = (TextView) findViewById(R.id.speedVal);
                        speedVal.setText(Float.toString(currSpeed));

                        boolean prevInMeaningful = inMeaningfulMotion;
                        inMeaningfulMotion = (continuousMotions > MIN_CONTINUOUS_MOTIONS);

                        //start of the motion
                        if (!prevInMeaningful && inMeaningfulMotion) {
                            int totalDiff = MIN_CONTINUOUS_MOTIONS + SPEED_TIME_DIFF;
                            originalTilt = (tilts.get(tilts.size() - totalDiff) + tilts.get(tilts.size() - totalDiff -1) +
                                    tilts.get(tilts.size() - totalDiff - 2))/3.0f;
                            TextView originalTiltView = (TextView) findViewById(R.id.origTilt);
                            originalTiltView.setText(Float.toString(originalTilt));
                        }

                        //end of the motion
                        if (prevInMeaningful && !inMeaningfulMotion) {
                            float tiltDiff = tilt - originalTilt;
                            if (tiltDiff > 0) {
                                Log.d("Gem", "Finished motion, tiltDiff:" + Float.toString(tiltDiff)
                                        + " numMotions:" + Integer.toString(prevContinuousMotions));
                            }
                            if (tiltDiff < 0) {
                                contMotion.setText("Finished motion, tilt:" + Float.toString(tiltDiff));
                                contMotion.setTextColor(Color.GREEN);
                            }
                            else {
                                contMotion.setText("Finished motion, tilt:" + Float.toString(tiltDiff));
                                contMotion.setTextColor(Color.RED);
                            }

                            int motionGrade = Math.abs(Arrays.binarySearch(motionLengths, continuousMotions) + 1);

                        }
                    }



                    if (tilts.size() > MAX_TILT_NUM) {
                        tilts.removeFirst();
                    }



                }
            }

            @Override
            public void onErrorOccurred(int errCode) {
                //Gem not found
                if(errCode == Gem.ERR_CONNECTING_TIMEOUT) {
                    Toast.makeText(MainActivity.this, "Gem device not found", Toast.LENGTH_SHORT).show();
                }
            }
        };

        if(whitelist.length > 0) {
            //Get Gem by MAC-address
            Gem firstGem = GemManager.getDefault().getGem(whitelist[0], gemListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Bind the Gem Service to the app
        GemManager.getDefault().bindService(this);
        tilts = new LinkedList<Float>();
        speeds = new LinkedList<Float>();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Unbind Gem Service from the application
        GemManager.getDefault().unbindService(this);
    }

    private void sendNotification(String stringAlert){
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Emergency from Itamar Room")
                        .setContentText(stringAlert);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, notificationBuilder.build());

    }
}


