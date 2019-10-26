package com.cwhq.odigos;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.cwhq.odigos.Models.User;
import com.cwhq.odigos.Models.UserLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.WriteResult;

public class GuideActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, OnMapReadyCallback, LocationListener, CompoundButton.OnCheckedChangeListener {

    public static String TAG = "odigosdebug";
    private static final int MY_LOCATION_REQUEST_CODE = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private int defaultZoom = 15;


    private GoogleMap mMap;
    private Snackbar mSnackbar;
    private FusedLocationProviderClient mFusedLocationClient;

    private LocationManager locationManager;

    private TextView statusText;
    private Switch status;

    private FirebaseFirestore fireDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guide);

        fireDB = FirebaseFirestore.getInstance();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        ImageView menubar = findViewById(R.id.menubutton);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        menubar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
            }
        });

        statusText = (TextView) findViewById(R.id.statustext);
        status = (Switch) findViewById(R.id.status);
        mSnackbar = Snackbar.make(findViewById(R.id.guidelayout), "Press again to exit", Snackbar.LENGTH_SHORT);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_);
        mapFragment.getMapAsync(this);


        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);


        status.setOnCheckedChangeListener(this);

    }


    /* added navigation item selected */

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav1) {
            Intent intent = new Intent(getApplicationContext(), GuideActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav2) {


        } else if (id == R.id.nav3) {

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mSnackbar.isShown()) {
            finish();
        } else {
            mSnackbar.show();
        }
    }



    @Override
    public void onDestroy(){
        super.onDestroy();
        if(status.isChecked()) {
            status.setChecked(false);
            statusText.setText("OFFLINE");
            statusText.setTextColor(ContextCompat.getColor(this, R.color.offlinecolor));
            updateOnlineStatus(false);
        }
    }


    /* check location changes */

    public void onLocationChanged(Location location) {
        // Called when a new location is found by the network location provider.
        Log.d(TAG,"Location changed, " + location.getAccuracy() + " , " + location.getLatitude()+ "," + location.getLongitude());
        UserLocation uLocation = new UserLocation(location.getLatitude(),location.getLongitude());
        MainActivity.CurrentUserLcoation = uLocation;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                // Permission was denied. Display an error message.
            }
        }
    }


    /* location enable function */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.

            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_REQUEST_CODE);
            getMyLocation();

        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
            getMyLocation();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,this);
        }
    }


    /* google map add function */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();

        } else {
            // Show rationale and request permission.
            enableMyLocation();

        }

    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public boolean onMyLocationButtonClick() {

        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }




    public void getMyLocation(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                UserLocation uLocation = new UserLocation(location.getLatitude(),location.getLongitude());
                                MainActivity.CurrentUserLcoation = uLocation;
                                LatLng me = new LatLng(location.getLatitude(), location.getLongitude());
                                addMyMarker(me,"You are here");
                                moveCamera(me,defaultZoom);
                            }
                        }
                    });

        } else {
            // Show rationale and request permission.
            enableMyLocation();

        }

    }








    public void moveCamera(LatLng latLng, int zoomSize){
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLng, zoomSize);
        mMap.animateCamera(location);
    }

    public Marker addMyMarker(LatLng latLng, String title){
        return mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.maker)));

    }



    // Method for display toast message
    public void showMsg(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        Log.d(TAG,isChecked?"Online":"Offline");
        if(isChecked) {
            statusText.setText("ONLINE");
            statusText.setTextColor(ContextCompat.getColor(this, R.color.onlinecolor));
        } else {
            statusText.setText("OFFLINE");
            statusText.setTextColor(ContextCompat.getColor(this, R.color.offlinecolor));
        }
        updateOnlineStatus(isChecked);
    }



    public void updateOnlineStatus(Boolean isOnline){
         if(isOnline && MainActivity.CurrentUserLcoation!=null){

             DocumentReference onlineRef = fireDB.collection("online_guides").document(MainActivity.UID);
             onlineRef.set(MainActivity.CurrentUserLcoation).addOnCompleteListener(new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull Task<Void> task) {
                     if(task.isSuccessful()) {
                         showMsg("You are online now");

                     }else{
                         //Error occurred while adding data
                         showMsg("Going online failed ! Please try again later");
                         Log.d(TAG,"Err "+task.getException().getMessage());

                     }
                 }
             });

         }else if(isOnline && MainActivity.CurrentUserLcoation==null){
             Log.d(TAG,"User Location not available");
             showMsg("Your current location is unavailable. Can not go online");
         }else if(!isOnline){
             // asynchronously delete a document
             fireDB.collection("online_guides").document(MainActivity.UID).delete();
         }
    }




}
