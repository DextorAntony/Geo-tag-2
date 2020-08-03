package com.map.geotag.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.map.geotag.R;
import com.map.geotag.database.dbhandler.GeoTagDBHandler;
import com.map.geotag.model.Location;

import java.io.File;

public class Update extends AppCompatActivity {
private  TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        Intent mIntent = getIntent();
        int intValue = mIntent.getIntExtra("intVariableName", 0);
Toast.makeText(getApplicationContext(),String.valueOf(intValue),Toast.LENGTH_SHORT).show();

        GeoTagDBHandler geoTagDBHandler = new GeoTagDBHandler(this);
        final SQLiteDatabase db = geoTagDBHandler.getReadableDatabase();final SQLiteDatabase dbw = geoTagDBHandler.getWritableDatabase();

String pos = String.valueOf(intValue);

        Cursor cursor = db.rawQuery("select id from employee where id ="+ pos,null);
Cursor cursor1 = db.rawQuery("select address from employee where id = "+pos,null);
Cursor cursor2 = db.rawQuery("select latitude,longitude from employee where id = "+ pos,null);
        Cursor cursor3 = db.rawQuery("select file from employee where id = "+ pos,null);
    //  Cursor cursor4 = db.rawQuery("select AnimalN from employee where id = 1",null);
      //  Cursor cursor5 = db.rawQuery("PRAGMA journal_mode = WAL;", new String[Integer.parseInt(yourPreviousPzl)]);
    if (cursor!=null){
cursor1.moveToFirst();
        cursor.moveToFirst();
        cursor2.moveToFirst();
        cursor3.moveToFirst();
     // cursor4.moveToFirst();
    }
    StringBuilder stringBuilder = new StringBuilder();
    do{

        assert cursor != null;
        if(cursor.getCount() > 0 && cursor1.getCount()>0 && cursor2.getCount()>0 && cursor3.getCount()>0){
// get values from cursor here

            //  String anim = cursor4.getString(0);
        String img = cursor3.getString(0);
        String id1 = cursor.getString(0);
        String lat = cursor2.getString(0);
        String longi = cursor2.getString(1);
        stringBuilder.append(lat).append(longi);
       String address = cursor1.getString(0);
        EditText lati = findViewById(R.id.lat);
        String la = lat+","+longi;
        lati.setText(la);
        EditText id = findViewById(R.id.time1);
        id.setText(id1);
        EditText time = findViewById(R.id.address);
      time.setText(address);
        ImageView iv = (ImageView) findViewById(R.id.imageView);




        final File imgFile = new File(img);
        Glide.with(getApplicationContext()).load(imgFile.getAbsolutePath()).into(iv);}
    }
    while (cursor.moveToNext());





        Button button = findViewById(R.id.button3);

            EditText  add = findViewById(R.id.time1);

        final String up = add.getText().toString();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dbw.execSQL("ALTER TABLE employee ADD COLUMN animal TEXT");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                ContentValues cv = new ContentValues();
                cv.put(Location.KEY_ADDRESS, "up");
                dbw.update(Location.TABLE_NAME, cv, Location.KEY_ADDRESS + "= ?", new String[] {"1"});


            }
        });

    }
}