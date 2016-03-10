package org.androidtlv.autonomy;

import com.gemsense.common.GemSensorsData;
import com.gemsense.gemsdk.*;
import com.gemsense.gemsdk.utils.AzimuthElevationConverter;

import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    int notificationId = 001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                float[] q = data.quaternion; // w x y z
                float[] a = data.acceleration;

                AzimuthElevationConverter converter = new AzimuthElevationConverter();
                float[] ta = converter.convertLH(q);

                TextView tiltVal = (TextView) findViewById(R.id.tiltVal);
                tiltVal.setText(Float.toString(ta[0]));

                TextView elevationVal = (TextView) findViewById(R.id.elevationVal);
                elevationVal.setText(Float.toString(ta[1]));

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


