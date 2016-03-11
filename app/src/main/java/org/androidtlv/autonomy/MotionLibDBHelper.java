package org.androidtlv.autonomy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Maria on 5/4/2015.
 */


public class MotionLibDBHelper extends SQLiteOpenHelper {

    public MotionLibDBHelper(Context context) {
        super(context, "motion_db", null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        String createTableCmd = "create table MotionLib(_id integer primary key autoincrement, " +
                " trigger text, min_tilt float, max_tilt float, min_motions integer, max_motions integer);";
        db.execSQL(createTableCmd);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
