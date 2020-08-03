package com.map.geotag.ui.activities;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import timber.log.Timber;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.gson.JsonObject;
import com.map.geotag.R;
import com.map.geotag.database.dao.LocationDAO;
import com.map.geotag.database.dbhandler.GeoTagDBHandler;
import com.map.geotag.model.Location;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.opencsv.CSVWriter;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.google.gson.JsonObject;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import com.mapbox.pluginscalebar.ScaleBarOptions;
import com.mapbox.pluginscalebar.ScaleBarPlugin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static android.media.MediaRecorder.VideoSource.CAMERA;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static java.lang.Double.parseDouble;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback {
    private static final int REQUEST_CODE_AUTOCOMPLETE = 10;
    private static final int PERMISSION_REQ_CODE_CAMERA = 1;
    private boolean isEndNotified;
    private int regionSelected;
    private com.map.geotag.model.Location currLocation, prevLocation;
    private GeoTagDBHandler geoTagDBHandler;
    private SQLiteDatabase sqLiteDatabase;
    private Mapbox mMap;
    private Marker marker;
    private CarmenFeature home;
    private ScaleBarPlugin scaleBarPlugin;
    private ScaleBarOptions[] listOfScalebarStyleVariations;
    private CarmenFeature work;
    private String geojsonSourceLayerId = "geojsonSourceLayerId";
    private String symbolIconId = "symbolIconId";
    private String pictureImagePath = "";
    private ArrayList<Location> locations;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final String FILE_NAME = "databases";
    private static final String TAG = "OffManActivity";
    //private Marker marker;
    // JSON encoding/decoding
    public static final String JSON_CHARSET = "UTF-8";
    public static final String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";
    private MapboxMap mapboxMap;
    // UI elements
    private MapView mapView;
    private MapboxMap map;
    private ProgressBar progressBar;
    private Button downloadButton;
    private Button listButton;
    private OfflineManager offlineManager;
    private OfflineRegion offlineRegion;
    private PermissionsManager permissionsManager;
    private String[] listOfStyles = new String[] {Style.LIGHT, Style.DARK, Style.SATELLITE_STREETS, Style.OUTDOORS};
    private int index = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplicationContext() ,android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CALL_PHONE},
                    PERMISSION_REQ_CODE_CAMERA
            );
        }
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.map);

        mapView.onCreate(savedInstanceState);



        geoTagDBHandler = new GeoTagDBHandler(MainActivity.this);
        locations = new ArrayList<>();
        if (!isNetworkConnected(MainActivity.this)) {
            showNoInternetDialog(MainActivity.this);
        }
        if (getIntent() != null && getIntent().getSerializableExtra("location") != null) {
            prevLocation = (com.map.geotag.model.Location) getIntent().getSerializableExtra("location");
            locations.add(prevLocation);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(final MapboxMap mapboxMap) {
        MainActivity.this.mapboxMap = mapboxMap;
        map = mapboxMap;
        //  mMap = mapboxMap;

        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {

            @Override
            public void onStyleLoaded(@NonNull Style style) {
                initSearchFab();
                scaleBarPlugin = new ScaleBarPlugin(mapView, mapboxMap);
                initStyling();
                scaleBarPlugin.create(listOfScalebarStyleVariations[index]);
                findViewById(R.id.switch_scalebar_style_fab).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (index == listOfScalebarStyleVariations.length - 1) {
                            index = 0;
                        }

                        mapboxMap.setStyle(listOfStyles[index], new Style.OnStyleLoaded() {
                            @Override
                            public void onStyleLoaded(@NonNull Style style) {

                                scaleBarPlugin.create(listOfScalebarStyleVariations[index]);

                            }
                        });

                        index++;

                    }});
                addUserLocations();
                setUpSource(style);
                setupLayer(style);
                enableLocationComponent(style);

                // Assign progressBar for later use
                progressBar = findViewById(R.id.progress_bar);

                // Set up the offlineManager
                offlineManager = OfflineManager.getInstance(MainActivity.this);

                // Bottom navigation bar button clicks are handled here.
                // Download offline button
                downloadButton = findViewById(R.id.download_button);

                downloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        downloadRegionDialog();
                    }
                });
                listButton = findViewById(R.id.list_button);
                listButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        downloadedRegionList();
                    }


                });

                mapboxMap.addOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {

                    @Override
                    public boolean onMapLongClick(@NonNull LatLng point) {
                        Geocoder geocoder;
                        List<Address> addresses;
                        geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        try {
                            addresses = geocoder.getFromLocation(point.getLatitude(), point.getLongitude(), 1);
                            String address = "No Address Available";
                            if (addresses != null && addresses.size() > 0 && addresses.get(0) != null && addresses.get(0).getAddressLine(0) != null) {
                                address = addresses.get(0).getAddressLine(0);
                            }
                            currLocation = new Location();
                            currLocation.setAddress(address);
                            ;

                            currLocation.setLongi(point.getLongitude() + "");
                            currLocation.setLat(point.getLatitude() + "");
                            marker = mapboxMap.addMarker(new MarkerOptions().position(point).title(address));
                            //   googleMap.addMarker(new MarkerOptions().position(point).title(address).
                            //  icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            //googleMap.moveCamera(CameraUpdateFactory.newLatLng(point));


                            if (Build.VERSION.SDK_INT >= 23) {
                                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(MainActivity.this,
                                            new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            PERMISSION_REQ_CODE_CAMERA
                                    );
                                } else {

                                   takePhotoFromCamera();
                                }
                            } else {
                                //takePhotoFromCamera();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return false;


                    }




                    private void takePhotoFromCamera() {
                        try {

                            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                            String millisInString = dateFormat.format(new Date());
                            //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                            // String imageFileName = millisInString ;
                            String imageFileName = millisInString + ".jpg";
                            String rootPath = Environment.getExternalStorageDirectory()
                                    .getAbsolutePath() + "/GeoTag/";
                            File root = new File(rootPath);
                            boolean success; //= true;
                            if (!root.exists()) {
                                Toast.makeText(MainActivity.this, "New Folder named GeoTag created", Toast.LENGTH_SHORT).show();
                                root.mkdir();
                            }


                            File storageDir = new File(rootPath);
                            pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
                            File file = new File(pictureImagePath);
                            Uri outputFileUri = FileProvider.getUriForFile(MainActivity.this, getPackageName() + ".provider", file);
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                            startActivityForResult(cameraIntent, CAMERA);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                });


                if (prevLocation != null) {
                    LocationDAO locationDAO = new LocationDAO(MainActivity.this);
                    ArrayList<com.map.geotag.model.Location> locations = locationDAO.getLocations();
                    for (com.map.geotag.model.Location location : locations) {
                        double prevLat = parseDouble(location.getLat());
                        double prevLong = parseDouble(location.getLongi());
                        // double currlat = latLng.latitude;
                        // double currLongi = latLng.longitude;


                        LatLng latLng = new LatLng(prevLat, prevLong);
                        marker = mapboxMap.addMarker(new MarkerOptions().position(latLng).title(location.getAddress()));
                        CameraPosition position = new CameraPosition.Builder()
                                .target(latLng)
                                .zoom(10)
                                .tilt(20)
                                .build();

                        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));

                        //       icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        //   googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        //   CameraUpdate center = CameraUpdateFactory.newLatLngZoom(latLng, 17);
                        //   googleMap.moveCamera(center);
                        //   googleMap.addCircle(new CircleOptions()
                        //        .center(latLng)   //set center
                        //         .radius(150)   //set radius in meters
                        // .fillColor(0x402092fd)  //default
                        //         .strokeColor(Color.RED)
                        //      .strokeWidth(10));

                    }
                }

             
//        mapboxMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
//            @Override
//            public void onMarkerDragStart(Marker marker) {
//                LatLng latLng = marker.getPosition();
//                LocationDAO locationDAO = new LocationDAO(MainActivity.this);
//                ArrayList<com.map.geotag.model.Location> locations = locationDAO.getLocations();
//                if (locations != null) {
//                    com.map.geotag.model.Location foundLocation = null;
//                    for (com.map.geotag.model.Location location : locations) {
//                        double prevLat = parseDouble(location.getLat());
//                        double prevLong = parseDouble(location.getLongi());
//                        double currlat = latLng.latitude;
//                        double currLongi = latLng.longitude;
//                        if (currLongi == prevLong && currlat == prevLat) {
//                            foundLocation = location;
//                        }
//                    }
//                    if (foundLocation != null) {
//                        locations.remove(foundLocation);
//                        locations.add(0, foundLocation);
//                        Intent intent = new Intent(MainActivity.this, ListActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        intent.putExtra("locations", locations);
//                        startActivity(intent);
//                    }
//                }
//
//            }
//
//            @Override
//            public void onMarkerDrag(Marker marker) {
//
//            }
//
//            @Override
//            public void onMarkerDragEnd(Marker marker) {
//
//            }
//
//
//        });


//        googleMap.getUiSettings().setCompassEnabled(true);
//        googleMap.getUiSettings().setMapToolbarEnabled(true);
//        googleMap.getUiSettings().setZoomControlsEnabled(true);
//        googleMap.getUiSettings().setTiltGesturesEnabled(true);

                //googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                //   googleMap.
                // googleMap.getUiSettings().setZoomGesturesEnabled(true);
//        googleMap.setMyLocationEnabled(true);
//        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

                //LatLng myPosition = new LatLng(11.1271, 78.6569);
                //googleMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
                //CameraUpdate center = CameraUpdateFactory.newLatLngZoom(myPosition, 15);
                //googleMap.moveCamera(center);
//        googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
//            @Override
//            public void onMyLocationChange(Location arg0) {
//                double latitude = arg0.getLatitude();
//                double longitude = arg0.getLongitude();

//                googleMap.addMarker(new MarkerOptions()
//                        .position(myPosition)
//                        .alpha(0.8f)
//                        .anchor(0.0f, 1.0f)
//                       // .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_pin))
//                        .title("Your position :\n ")
//                        .snippet(latitude + " and " + longitude));

//                googleMap.addCircle(new CircleOptions()
//                        .center(myPosition)   //set center
//                        .radius(150)   //set radius in meters
//                        .fillColor(0x402092fd)  //default
//                        .strokeColor(Color.LTGRAY)
//                        .strokeWidth(5));
                // circle = googleMap.addCircle(circleOptions);


//                CameraPosition cameraPosition = CameraPosition.builder()
//                        .target(myPosition)
//                        .zoom(15)
//                        .bearing(90)
//                        .build();

                // googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),2000, null);
//            }
//        });
//    }


//        @Override
//        public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
//        @NonNull int[] grantResults){
//            if (requestCode == PERMISSION_REQ_CODE_CAMERA) {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    takePhotoFromCamera();
//                } else {
//                    Toast.makeText(MainActivity.this, "Please grant necessary permission(s)", Toast.LENGTH_SHORT).show();
//                }
//            }


//        private void takePhotoFromCamera () {
//
//        }}
            }


            private void initSearchFab() {
                findViewById(R.id.fab_location_search).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new PlaceAutocomplete.IntentBuilder()
                                .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : getString(R.string.access_token))
                                .placeOptions(PlaceOptions.builder()
                                        .backgroundColor(Color.parseColor("#EEEEEE"))
                                        .limit(10)
                                        .addInjectedFeature(home)
                                        .addInjectedFeature(work)
                                        .build(PlaceOptions.MODE_CARDS))
                                .build(MainActivity.this);
                        startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
                    }
                });
            }
            private void addUserLocations() {
                home = CarmenFeature.builder().text("Mapbox SF Office")
                        .geometry(Point.fromLngLat(-122.3964485, 37.7912561))
                        .placeName("50 Beale St, San Francisco, CA")
                        .id("mapbox-sf")
                        .properties(new JsonObject())
                        .build();

                work = CarmenFeature.builder().text("Mapbox DC Office")
                        .placeName("740 15th Street NW, Washington DC")
                        .geometry(Point.fromLngLat(-77.0338348, 38.899750))
                        .id("mapbox-dc")
                        .properties(new JsonObject())
                        .build();
            }
            private void setUpSource(@NonNull Style loadedMapStyle) {
                loadedMapStyle.addSource(new GeoJsonSource(geojsonSourceLayerId));
            }

            private void setupLayer(@NonNull Style loadedMapStyle) {
                loadedMapStyle.addLayer(new SymbolLayer("SYMBOL_LAYER_ID", geojsonSourceLayerId).withProperties(
                        iconImage(symbolIconId),
                        iconOffset(new Float[] {0f, -8f})
                ));
            }



        });}

    private void initStyling() {
        listOfScalebarStyleVariations = new ScaleBarOptions[] {

// Using the plugin's default styling to start
                new ScaleBarOptions(this),

// Random styling option #2
                new ScaleBarOptions(this)
                        .setTextColor(R.color.red)
                        .setTextSize(40f)
                        .setBarHeight(15f)
                        .setBorderWidth(5f)
                        .setMetricUnit(true)
                        .setRefreshInterval(15)
                        .setMarginTop(30f)
                        .setMarginLeft(16f)
                        .setTextBarMargin(15f),

// Random styling option #3
                new ScaleBarOptions(this)
                        .setTextColor(R.color.mapbox_blue)
                        .setTextSize(60f)
                        .setBarHeight(15f)
                        .setBorderWidth(5f)
                        .setMetricUnit(true)
                        .setRefreshInterval(15)
                        .setMarginTop(30f)
                        .setMarginLeft(30f)
                        .setTextBarMargin(25f),

// Random styling option #4
                new ScaleBarOptions(this)
                        .setTextColor(R.color.white)
                        .setTextSize(30f)
                        .setBarHeight(15f)
                        .setBorderWidth(5f)
                        .setMetricUnit(false)
                        .setRefreshInterval(15)
                        .setMarginTop(30f)
                        .setMarginLeft(30f)
                        .setTextBarMargin(25f),
        };
    }

    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getApplicationContext())) {

// Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

// Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(getApplicationContext(), loadedMapStyle).build());

// Enable to make component visible
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationComponent.setLocationComponentEnabled(true);


// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
            //locationComponent.zoomWhileTracking(10);
            mapboxMap.getUiSettings().setZoomGesturesEnabled(true);



// Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager((PermissionsListener) getApplicationContext());
            permissionsManager.requestLocationPermissions(MainActivity.this);
        }
    }


    public void Activity(int requestcode, int resultCode ) {
        if (requestcode == CAMERA && resultCode == RESULT_OK) {


            File imgFile = new File(pictureImagePath);


            if (imgFile.exists() && currLocation != null) {
                currLocation.setFile(pictureImagePath);
                LocationDAO locationDAO = new LocationDAO(MainActivity.this);
                locationDAO.insert(currLocation);
                if (locations == null) {
                    locations = new ArrayList<>();
                }
                locations.add(currLocation);
            } else {
                Toast.makeText(MainActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED);
                finish();
            }



        }





    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data){
            //super.onActivityResult(requestCode, resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if(resultCode == Activity.RESULT_OK){

                    // Retrieve selected location's CarmenFeature
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

            // Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above.
            // Then retrieve and update the source designated for showing a selected location's symbol layer icon

            if (map != null) {
                Style style = map.getStyle();
                if (style != null) {
                    GeoJsonSource source = style.getSourceAs(geojsonSourceLayerId);
                    if (source != null) {
                        source.setGeoJson(FeatureCollection.fromFeatures(
                                new Feature[]{Feature.fromJson(selectedCarmenFeature.toJson())}));
                    }

                    // Move map camera to the selected location
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(((Point) Objects.requireNonNull(selectedCarmenFeature.geometry())).latitude(),
                                            ((Point) selectedCarmenFeature.geometry()).longitude()))
                                    .zoom(14)
                                    .build()), 4000);
                }
            }
        }}
         else   if (requestCode == CAMERA) {
             if(resultCode == RESULT_OK ){

                     File imgFile = new File(pictureImagePath);


                if (imgFile.exists() && currLocation != null) {
                    currLocation.setFile(pictureImagePath);
                    LocationDAO locationDAO = new LocationDAO(MainActivity.this);
                    locationDAO.insert(currLocation);
                    if (locations == null) {
                        locations = new ArrayList<>();
                    }
                    locations.add(currLocation);
                } else {
                    Toast.makeText(MainActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_CANCELED);
                    finish();
                }

            }}

        }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // int id = item.getItemId();
            case R.id.home: {
                onBackPressed();
                return false;
            } case R.id.taggedplaces:
                {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return false;
            }
            case R.id.weat:{
                Intent intent = new Intent(MainActivity.this, weather.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            case R.id.Forestweb:{
                Intent intent = new Intent(MainActivity.this, forestwebsite.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }


            case R.id.maptypeHYBRID:
                if(map != null){

                    map.setStyle(Style.SATELLITE_STREETS);
                    return true;
                }
            case R.id.maptypeNONE:
                if(map != null){
                    map.setStyle(Style.MAPBOX_STREETS);
                    return true;
                }
            case R.id.maptypeNORMAL:
                if(map != null){
                    map.setStyle(Style.LIGHT);

return  true;

                }
            case R.id.maptypeSATELLITE:
                if(map != null){
                    map.setStyle(Style.SATELLITE);
                    return true;
                }
            case R.id.maptypeTERRAIN:
                if(map != null){
                    map.setStyle(Style.TRAFFIC_DAY);

                    return true;
                }
            case R.id.police:
               {
                   Intent callIntent = new Intent(Intent.ACTION_CALL);
                   callIntent.setData(Uri.parse("tel:100"));
                   startActivity(callIntent);

                    return true;
                }
            case R.id.Fire:
                {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:101"));
                    startActivity(callIntent);

                    return true;
                }

            case R.id.Ambulance:
               {
                   Intent callIntent = new Intent(Intent.ACTION_CALL);
                   callIntent.setData(Uri.parse("tel:03192 232102"));
                   startActivity(callIntent);

                    return true;
                }
            case R.id.Shipping:
                {

                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:03192 245555"));
                    startActivity(callIntent);
                    return true;
                }
            case R.id.Disater:
               {
                   Intent callIntent = new Intent(Intent.ACTION_CALL);
                   callIntent.setData(Uri.parse("tel:1070/03192 238881"));
                   startActivity(callIntent);

                    return true;
                }







        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alltags, menu);
        return true;
    }

    public static boolean isNetworkConnected(MainActivity context) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert conMgr != null;
        return conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected();
    }

    public static void showNoInternetDialog(MainActivity context) {
        AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(context);
        alertdialogbuilder.setTitle("No Internet !!!");
        alertdialogbuilder.setMessage("Check your internet connection ,try again.");
        alertdialogbuilder.setPositiveButton("OK", null);
        alertdialogbuilder.show();

    }


    public void export(MenuItem item) {

        geoTagDBHandler = new GeoTagDBHandler(getApplicationContext());
        File exportDir = new File(Environment.getExternalStorageDirectory(), "GeoTag");
        if (!exportDir.exists())
        {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, "Geotag.csv");
        try
        {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = geoTagDBHandler.getReadableDatabase();
            Cursor curCSV = db.rawQuery("SELECT * FROM employee",null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while(curCSV.moveToNext())
            {
                //Which column you want to exprort
                String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2),curCSV.getString(3), curCSV.getString(4)};
                csvWrite.writeNext(arrStr);
                Toast.makeText(getApplicationContext(),"DATA EXPORTED",Toast.LENGTH_SHORT).show();
            }
            csvWrite.close();
            curCSV.close();
        }
        catch(Exception sqlEx)
        {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }







//        String DatabaseName = "GeoTag.db";
//        File sd = Environment.getExternalStorageDirectory();
//        File data = Environment.getDataDirectory();
//        FileChannel source=null;
//        FileChannel destination=null;
//        @SuppressLint("SdCardPath") String currentDBPath = "/data/com.map.geotag/databases/"+DatabaseName ;
//        String backupDBPath = "/Download/geotag.db";
//        File currentDB = new File(data, currentDBPath);
//        File backupDB = new File(sd, backupDBPath);
//        try {
//            source = new FileInputStream(currentDB).getChannel();
//            destination = new FileOutputStream(backupDB).getChannel();
//            destination.transferFrom(source, 0, source.size());
//            source.close();
//            destination.close();
//            Toast.makeText(this, "Your Database is Exported !!", Toast.LENGTH_LONG).show();
//        } catch(IOException e) {
//            Toast.makeText(this, "Your Database is not Exported !!", Toast.LENGTH_LONG).show();
//            e.printStackTrace();
//        }



    }
    private String getRegionName(OfflineRegion offlineRegion) {
        // Get the region name from the offline region metadata
        String regionName;

        try {
            byte[] metadata = offlineRegion.getMetadata();
            String json = new String(metadata, JSON_CHARSET);
            JSONObject jsonObject = new JSONObject(json);
            regionName = jsonObject.getString(JSON_FIELD_REGION_NAME);
        } catch (Exception exception) {
            //Timber.e("Failed to decode metadata: %s", exception.getMessage());
            regionName = getString(R.string.region_name) + offlineRegion.getID();
        }
        return regionName;
    }

    private void setPercentage(final int percentage) {
        progressBar.setIndeterminate(false);
        progressBar.setProgress(percentage);
    }
    private void endProgress(final String message) {
        // Don't notify more than once
        if (isEndNotified) {
            return;
        }

        // Enable buttons
        downloadButton.setEnabled(true);
        listButton.setEnabled(true);

        // Stop and hide the progress bar
        isEndNotified = true;
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.GONE);

        // Show a toast
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }
    private void downloadedRegionList() {
        regionSelected = 0;

        // Query the DB asynchronously
        offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
            @Override
            public void onList(final OfflineRegion[] offlineRegions) {
                // Check result. If no regions have been
                // downloaded yet, notify user and return
                if (offlineRegions == null || offlineRegions.length == 0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_no_regions_yet), Toast.LENGTH_SHORT).show();
                    return;
                }

                // Add all of the region names to a list
                ArrayList<String> offlineRegionsNames = new ArrayList<>();
                for (OfflineRegion offlineRegion : offlineRegions) {
                    offlineRegionsNames.add(getRegionName(offlineRegion));
                }
                final CharSequence[] items = offlineRegionsNames.toArray(new CharSequence[offlineRegionsNames.size()]);

                // Build a dialog containing the list of regions
                android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.navigate_title))
                        .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Track which region the user selects
                                regionSelected = which;
                            }
                        })
                        .setPositiveButton(getString(R.string.navigate_positive_button), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                Toast.makeText(MainActivity.this, items[regionSelected], Toast.LENGTH_LONG).show();

                                // Get the region bounds and zoom
                                LatLngBounds bounds = ((OfflineTilePyramidRegionDefinition)
                                        offlineRegions[regionSelected].getDefinition()).getBounds();
                                double regionZoom = ((OfflineTilePyramidRegionDefinition)
                                        offlineRegions[regionSelected].getDefinition()).getMinZoom();

                                // Create new camera position
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(bounds.getCenter())
                                        .zoom(regionZoom)
                                        .build();

                                // Move camera to new position
                                map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                            }
                        })
                        .setNeutralButton(getString(R.string.navigate_neutral_button_title), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // Make progressBar indeterminate and
                                // set it to visible to signal that
                                // the deletion process has begun
                                progressBar.setIndeterminate(true);
                                progressBar.setVisibility(View.VISIBLE);

                                // Begin the deletion process
                                offlineRegions[regionSelected].delete(new OfflineRegion.OfflineRegionDeleteCallback() {
                                    @Override
                                    public void onDelete() {
                                        // Once the region is deleted, remove the
                                        // progressBar and display a toast
                                        progressBar.setVisibility(View.INVISIBLE);
                                        progressBar.setIndeterminate(false);
                                        Toast.makeText(getApplicationContext(), getString(R.string.toast_region_deleted),
                                                Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onError(String error) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        progressBar.setIndeterminate(false);
                                        Timber.e("Error: %s", error);
                                    }
                                });
                            }
                        })
                        .setNegativeButton(getString(R.string.navigate_negative_button_title), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // When the user cancels, don't do anything.
                                // The dialog will automatically close
                            }
                        }).create();
                dialog.show();

            }

            @Override
            public void onError(String error) {
                // Timber.e( "Error: %s", error);
            }
        });
    }
private void downloadRegionDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);

final EditText regionNameEdit = new EditText(MainActivity.this);
        regionNameEdit.setHint(getString(R.string.set_region_name_hint));

        // Build the dialog box
        builder.setTitle(getString(R.string.dialog_title))
        .setView(regionNameEdit)
        .setMessage(getString(R.string.dialog_message))
        .setPositiveButton(getString(R.string.dialog_positive_button), new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface dialog, int which) {
        String regionName = regionNameEdit.getText().toString();
        // Require a region name to begin the download.
        // If the user-provided string is empty, display
        // a toast message and do not begin download.
        if (regionName.length() == 0) {
        Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.dialog_toast), Toast.LENGTH_SHORT).show();
        } else {
        // Begin download process
        MainActivity.this.downloadRegion(regionName);
        }
        }
        })
        .setNegativeButton(getString(R.string.dialog_negative_button), new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
        }
        });

        // Display the dialog
        builder.show();
        }

private void downloadRegion(final String regionName) {
        startProgress();

        // Create offline definition using the current
        // style and boundaries of visible map area

        String styleUrl = Objects.requireNonNull(map.getStyle()).getUrl();
        LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
        double minZoom = map.getCameraPosition().zoom;
        double maxZoom = map.getMaxZoomLevel();
        float pixelRatio = this.getResources().getDisplayMetrics().density;
        OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
        styleUrl, bounds, minZoom, maxZoom, pixelRatio);

        // Build a JSONObject using the user-defined offline region title,
        // convert it into string, and use it to create a metadata variable.
        // The metadata variable will later be passed to createOfflineRegion()
        byte[] metadata;
        try {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_FIELD_REGION_NAME, regionName);
        String json = jsonObject.toString();
        metadata = json.getBytes(JSON_CHARSET);
        } catch (Exception exception) {
        Timber.e("Failed to encode metadata: %s", exception.getMessage());
        metadata = null;
        }

        // Create the offline region and launch the download
        offlineManager.createOfflineRegion(definition, metadata, new OfflineManager.CreateOfflineRegionCallback() {
@Override
public void onCreate(OfflineRegion offlineRegion) {
        Timber.d("Offline region created: %s", regionName);
        MainActivity.this.offlineRegion = offlineRegion;
        launchDownload();
        }

@Override
public void onError(String error) {
        Timber.e("Error: %s", error);
        }
        });
        }

private void startProgress() {
        downloadButton.setEnabled(false);
        listButton.setEnabled(false);

        // Start and show the progress bar
        isEndNotified = false;
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
        }

private void launchDownload() {
        offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
@SuppressLint("SetTextI18n")
@Override
public void onStatusChanged(OfflineRegionStatus status) {
        // Compute a percentage
        double percentage = status.getRequiredResourceCount() >= 0
        ? (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) :
        0.0;

        if (status.isComplete()) {
        // Download complete
        endProgress(getString(R.string.end_progress_success));
        return;
        } else if (status.isRequiredResourceCountPrecise()) {
        // Switch to determinate state
        setPercentage((int) Math.round(percentage));
        }

        // Log what is being currently downloaded
        Timber.d("%s/%s resources; %s bytes downloaded.",
        String.valueOf(status.getCompletedResourceCount()),
        String.valueOf(status.getRequiredResourceCount()),
        String.valueOf(status.getCompletedResourceSize()));
        TextView textView = findViewById(R.id.text);
        textView.setText(""+percentage);
        }

@Override
public void onError(OfflineRegionError error) {
        Timber.e("onError reason: %s", error.getReason());
        Timber.e("onError message: %s", error.getMessage());
        }

@Override
public void mapboxTileCountLimitExceeded(long limit) {
        Timber.e("Mapbox tile count limit exceeded: %s", limit);
        }
        });

        // Change the region state
        offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);

        }
    }



