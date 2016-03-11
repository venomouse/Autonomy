package org.androidtlv.autonomy;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChooseActivity extends AppCompatActivity {

    private Button btnHungry;
    private FloatingActionButton myFab;
    private Button btnToilat;
    private Button btnCold;
    private Button btnTierd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        setViews();
        setClicks();
    }

    private void setViews(){
        myFab = (FloatingActionButton)  findViewById(R.id.fab);
        btnTierd = (Button) findViewById(R.id.btnTired);
        btnHungry = (Button) findViewById(R.id.btnHungry);
        btnToilat = (Button) findViewById(R.id.btnToilat);
        btnCold = (Button) findViewById(R.id.btnCold);

    }

    private void setClicks(){
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
        btnToilat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseActivity.this, ToilatActivity.class);
                startActivity(intent);
            }
        });
    }
}
