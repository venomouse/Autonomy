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
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    boolean inRecordingMode = false;
    boolean inTestingMode = false;
    TiltElevationConverter converter = new TiltElevationConverter();
    HeadMotionTracker headMotionTracker = new HeadMotionTracker();

    int notificationId = 001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button recordButton = (Button) findViewById(R.id.recordBtn);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inRecordingMode = true;
                recordButton.setEnabled(false);
            }
        });
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


                if (inRecordingMode || inTestingMode) {
                    updateHeadMotionTracker(data.quaternion);

                    if (headMotionTracker.motionDetected()) {
                        HeadMotion motion = headMotionTracker.getLastMotion();

                        TextView motionDetectedText = (TextView) findViewById(R.id.motionDetectedText);
                        motionDetectedText.setText("MotionDetected, min motions" + Integer.toString(motion.minMotions));
                        recordButton.setEnabled(true);
                        sendNotification("Autonomy Alert");
                        inRecordingMode = false;
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


    private void updateHeadMotionTracker(float[] q) {

        float[] te = converter.convertLH(q);
        headMotionTracker.update(te[0]);
        TextView tiltVal = (TextView) findViewById(R.id.tiltVal);
        tiltVal.setText(Float.toString(te[0]));

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Bind the Gem Service to the app
        GemManager.getDefault().bindService(this);
        headMotionTracker.reset();
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


