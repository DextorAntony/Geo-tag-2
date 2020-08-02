package com.map.geotag.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
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

        Bundle p = getIntent().getExtras();
        String yourPreviousPzl =p.getString("key");
        Toast.makeText(this, yourPreviousPzl, Toast.LENGTH_LONG).show();

        GeoTagDBHandler geoTagDBHandler = new GeoTagDBHandler(this);
        final SQLiteDatabase db = geoTagDBHandler.getReadableDatabase();final SQLiteDatabase dbw = geoTagDBHandler.getWritableDatabase();


Cursor cursor = db.rawQuery("select id from employee where id = 3",null);
Cursor cursor1 = db.rawQuery("select address from employee where id = 3",null);
Cursor cursor2 = db.rawQuery("select latitude,longitude from employee where id = 3",null);
        Cursor cursor3 = db.rawQuery("select file from employee where id = 3",null);
    //  Cursor cursor4 = db.rawQuery("select AnimalN from employee where id = 1",null);
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
   //  String anim = cursor4.getString(0);
        String img = cursor3.getString(0);
        String id1 = cursor.getString(0);
        String lat = cursor2.getString(0);
        String longi = cursor2.getString(1);
        stringBuilder.append(lat).append(longi);
       String address = cursor1.getString(0);
        EditText lati = findViewById(R.id.lat);
        lati.setText(lat);
        EditText id = findViewById(R.id.time1);
        id.setText(id1);
        EditText time = findViewById(R.id.time1);
      //time.setText(anim);
        ImageView iv = (ImageView) findViewById(R.id.imageView);




        final File imgFile = new File(img);
        Glide.with(getApplicationContext()).load(imgFile.getAbsolutePath()).into(iv);
    }
    while (cursor.moveToNext());





        Button button = findViewById(R.id.button3);

        @SuppressLint("CutPasteId") EditText add = findViewById(R.id.time1);

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
cv.put(Location.KEY_ID,up);
dbw.update(Location.TABLE_NAME,cv,"id=?",new String[]{"1"});

            }
        });

    }
}