package com.map.geotag.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.map.geotag.R;
import com.map.geotag.database.dbhandler.GeoTagDBHandler;
import com.map.geotag.ui.adapters.LocationListAdapter;
import com.map.geotag.database.dao.LocationDAO;
import com.map.geotag.model.Location;

import java.util.ArrayList;
import java.util.Objects;

import static com.map.geotag.model.Location.KEY_ID;
import static com.map.geotag.model.Location.TABLE_NAME;

public class ListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private TextView tvnoLocation;
    private Context mContext;
    private ArrayList<Location> locations;
    private LocationDAO locationDAO;
    private SQLiteDatabase mDatabase;
    private LocationListAdapter madapter;
    private GeoTagDBHandler geoTagDBHandler;
    private SQLiteDatabase db;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locationlist);
        GeoTagDBHandler dbHandler = new GeoTagDBHandler(this);
        mDatabase = dbHandler.getWritableDatabase();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null && getIntent().getExtras() != null) {
            locations = (ArrayList<Location>) getIntent().getSerializableExtra("locations");
        }

        recyclerView = findViewById(R.id.rvViewlocations);
        swipeRefreshLayout = findViewById(R.id.srlViewtour);
        swipeRefreshLayout.setOnRefreshListener(this);
        tvnoLocation = findViewById(R.id.tvnoLocation);
        locationDAO = new LocationDAO(getApplicationContext());
        setUpView();


    }

    public void setUpView() {
        if (locations == null) {
            locations = new ArrayList<>();
            locationDAO = new LocationDAO(getApplicationContext());
            locations = locationDAO.getLocations();



        }
        setUpRecyclerView();
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }

    public void setUpRecyclerView() {
        if (locations == null || locations.size() == 0) {
            tvnoLocation.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvnoLocation.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
            LocationListAdapter locationListAdapter = new LocationListAdapter(locations, this);
            new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);
            new ItemTouchHelper(update).attachToRecyclerView(recyclerView);
            recyclerView.setAdapter(locationListAdapter);
        }
    }
    LocationListAdapter locationListAdapter = new LocationListAdapter(locations, this);

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setUpView();
                swipeRefreshLayout.setRefreshing(false);
                locationListAdapter.notifyDataSetChanged();
            }
        }, 500);
    }
    ItemTouchHelper.SimpleCallback itemTouch = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT ) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

//            GeoTagDBHandler dbHelper = new GeoTagDBHandler(mContext);
//            dbHelper.deletePersonRecord(KEY_ID, mContext);
//
//            locations.remove(0);
//            recyclerView.removeViewAt(0);
            GeoTagDBHandler geoTagDBHandler = new GeoTagDBHandler(getApplicationContext());
            SQLiteDatabase db = geoTagDBHandler.getReadableDatabase();

            locations.remove(viewHolder.getAdapterPosition());
            int pos = viewHolder.getAdapterPosition();

           // locations.remove(pos);
            db.delete("employee","id=?",new String[]{String.valueOf(pos)});
            Toast.makeText(getApplicationContext(), "Item "+ pos +" is deleted.", Toast.LENGTH_LONG).show();
            setUpView();
            locationListAdapter.notifyDataSetChanged();
        }
    };
    ItemTouchHelper.SimpleCallback update = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            setUpView();
            locationListAdapter.notifyDataSetChanged();
            int pos = viewHolder.getAdapterPosition();
            Bundle ePzl= new Bundle();
            ePzl.putString("key", String.valueOf(pos));

            Intent i = new Intent(ListActivity.this,Update.class);
            i.putExtras(ePzl);
            startActivity(i);
        }
    };
}
