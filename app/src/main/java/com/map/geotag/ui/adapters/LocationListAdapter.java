package com.map.geotag.ui.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.pdf.PdfDocument;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.map.geotag.R;
import com.map.geotag.database.dbhandler.GeoTagDBHandler;
import com.map.geotag.model.Location;
import com.map.geotag.ui.activities.MainActivity;
import com.map.geotag.ui.activities.Update;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.map.geotag.model.Location.TABLE_NAME;
import static java.lang.Double.parseDouble;

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.MyViewHolder> {
    private ArrayList<Location> locations;
    private Activity activity;
    private GeoTagDBHandler geoTagDBHandler;
    private SQLiteDatabase db;
    public LocationListAdapter(ArrayList<Location> locations, Activity activity) {
        this.locations = locations;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_list_location, parent, false);
        return new MyViewHolder(itemview);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Location location = locations.get(position);
        if (location == null) {
            return;
        }

        LinearLayout llLocation = holder.llParent;
        final ImageView capImage = llLocation.findViewById(R.id.ivPhoto);
        TextView tvAddress = llLocation.findViewById(R.id.tvAddress);
        TextView tvLatLong = llLocation.findViewById(R.id.tvLatLong);
        TextView dateiv = llLocation.findViewById(R.id.date);
       // String date = new SimpleDateFormat("dd:MM:yyyy  hh:mm", Locale.getDefault()).format(new Date());
        String add = location.getAddress();
        String lat = location.getLat();
        String longi = location.getLongi();
        String date = location.getFile();
    String str = date.substring(date.lastIndexOf("/")+1);
        String result = str.substring(0, str.lastIndexOf("."));
//        String year = str.substring(0,4);
//
//        String month = str.substring(4, 6);
//
//        String  day = str.substring(6, 8);
//
//        String hour = str.substring(9, 11);
//
   //    String minute = str.substring(12, 13);
//        String format = year + "/"+month+"/"+day+" "+hour+"h:"+minute+"m";
        if (add == null || add.equals("")) {
            add = "No Address Found";
        }
        if (lat == null || lat.equals("")) {
            lat = "0.0";
        }
        if (longi == null || longi.equals("")) {
            longi = "0.0";
        }

        tvAddress.setText(add);
        dateiv.setText(result);
        lat = String.format("%.5f", parseDouble(lat));
        longi = String.format("%.5f", parseDouble(longi));
        tvLatLong.setText(String.format(" \n Latitude : %s, Longitude : %s", lat, longi));

        try {
            if (location.getFile() != null && !location.getFile().equals("")) {
                final File imgFile = new File(location.getFile());
                if (imgFile.exists()) {
                    Glide.with(activity).load(imgFile.getAbsolutePath()).into(capImage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        llLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                geoTagDBHandler = new GeoTagDBHandler(activity.getApplicationContext());
//                SQLiteDatabase db = geoTagDBHandler.getWritableDatabase();
//                String COlUMN_NAME ="id";
//                String value = "0";
//                db.execSQL("Delete from employee where id = 2",null);

                Intent intent = new Intent(activity, MainActivity.class);
                intent.putExtra("location", location);
                activity.startActivity(intent);


            }
        });
    }

    public int getItemCount() {
        return locations == null ? 0 : locations.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llParent;

        MyViewHolder(View itemView) {
            super(itemView);
            llParent = itemView.findViewById(R.id.llParent);
        }
    }
}