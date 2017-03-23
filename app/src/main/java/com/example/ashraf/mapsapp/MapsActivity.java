package com.example.ashraf.mapsapp;

import android.location.Location;
import android.nfc.Tag;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.util.Pools;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        LocationListener,GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener{
    private static final String tag= MapsActivity.class.getSimpleName();
    private static final int  PLAY_SERVICES_RESOLUTION_REQUEST=1000;
    private Location location;
    private GoogleApiClient googleApiClient;
    private boolean M_REQUEST_LOCATION_UPDATE=false;
    private LocationRequest mLocationRequest;
    double latitude ,longitude;





    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        /*SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/
        if (checkPlayServices()){
            buildGoogleApiClient();
            CreateLocationRequest();
        }

    }

    private void CreateLocationRequest() {
        mLocationRequest=new LocationRequest();
        int Updateinterval=10000;
        mLocationRequest.setInterval(Updateinterval);
        int fastestinterval=5000;
        mLocationRequest.setFastestInterval(fastestinterval);
        mLocationRequest.setPriority(mLocationRequest.PRIORITY_HIGH_ACCURACY);
        int displacement=10;
        mLocationRequest.setSmallestDisplacement(displacement);
    }

    private boolean checkPlayServices() {
        int resultcode= GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultcode!=ConnectionResult.SUCCESS){
            if (GooglePlayServicesUtil.isUserRecoverableError(resultcode)){
                GooglePlayServicesUtil.getErrorDialog(resultcode,this,PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"This Device is not supported",Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;

        }
        return true;
    }

    protected synchronized  void buildGoogleApiClient() {
        googleApiClient=new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this)
        .addApi(LocationServices.API).build();
    }

    @Override
    protected void onStart() {

        super.onStart();
        if (googleApiClient!=null){
            googleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
        checkPlayServices();
        if (googleApiClient.isConnected()&& M_REQUEST_LOCATION_UPDATE)
        {
            startLocationUpdates();
        }

    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,mLocationRequest,this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected())
        {
            googleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();

    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(latitude,longitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker College"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location=location;
        Toast.makeText(getApplicationContext(),"location Changed",Toast.LENGTH_LONG).show();
        displaylocation();


    }


    @Override
    public void onConnected(Bundle bundle) {
        displaylocation();
        if(M_REQUEST_LOCATION_UPDATE)
        {
            startLocationUpdates();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(tag,"");

    }
    private void displaylocation()

    {
        location=LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location!= null)
        {
            latitude=location.getLatitude();
            latitude=location.getLongitude();
            String getlati=String.valueOf(latitude);
            String getlongi=String.valueOf(longitude);
            Log.v("lat",getlati);
            Log.v("long",getlongi);
            SupportMapFragment mapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

        }
        else
        {
            Toast.makeText(getApplicationContext(),"enable location settings",Toast.LENGTH_SHORT).show();
        }
    }



}
