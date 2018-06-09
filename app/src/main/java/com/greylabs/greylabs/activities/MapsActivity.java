package com.greylabs.greylabs.activities;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.greylabs.greylabs.DatabaseHandler;
import com.greylabs.greylabs.GPSTracker;
import com.greylabs.greylabs.R;
import com.greylabs.greylabs.model.LocationObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private GPSTracker gpsTracker;
    private EditText enterAddress;
    private Button submit;
    private FloatingActionButton currentLocation;
    DatabaseHandler db;
    Double latitude = 0.0;
    Double longitude = 0.0;
    String address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        enterAddress = (EditText)findViewById(R.id.address);
        submit = (Button)findViewById(R.id.submit);
        currentLocation = (FloatingActionButton)findViewById(R.id.current_location);
        db = MainActivity.db;
        mapFragment.getMapAsync(this);

        onClicks();
    }

    public void onClicks(){
        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gpsTracker = new GPSTracker(MapsActivity.this);

                if(gpsTracker.canGetLocation()){


                    latitude = gpsTracker.getLatitude();
                    longitude = gpsTracker.getLongitude();

                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());

                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String postalCode = addresses.get(0).getPostalCode();
                        String knownName = addresses.get(0).getFeatureName();

                        enterAddress.setText(address);

                        Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
                                + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

                        // Add a marker in Sydney and move the camera
                        LatLng sydney = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 13));

                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(latitude, longitude))      // Sets the center of the map to location user
                                .zoom(17)                   // Sets the zoom
                                .bearing(90)                // Sets the orientation of the camera to east
                                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                                .build();                   // Creates a CameraPosition from the builder
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }



                    // \n is for new line

                }else{
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gpsTracker.showSettingsAlert();
                }
            }
        });

        enterAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchLocation();
                    return true;
                }
                return false;
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationObject locationObject = new LocationObject(1, address, latitude, longitude);
                db.addLocation(locationObject);
                finish();
            }
        });


    }

    public void searchLocation(){

        enterAddress.clearFocus();
        InputMethodManager in = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(enterAddress.getWindowToken(), 0);

        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        try
        {
            List<Address> addresses = geoCoder.getFromLocationName(enterAddress.getText().toString(), 5);
            if (addresses.size() > 0)
            {
                latitude = (double) (addresses.get(0).getLatitude());
                longitude = (double) (addresses.get(0).getLongitude());
                address = enterAddress.getText().toString();

                Log.d("lat-long", "" + latitude + "......." + longitude);
                final LatLng enteredLocation = new LatLng(latitude, longitude);
        /*used marker for show the location */
                mMap.addMarker(new MarkerOptions().position(enteredLocation).title(enterAddress.getText().toString()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(enteredLocation));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 13));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(latitude, longitude))      // Sets the center of the map to location user
                        .zoom(17)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
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
        gpsTracker = new GPSTracker(MapsActivity.this);
//        double latitude = 0.0;
//        double longitude = 0.0;

        // check if GPS enabled
        if(gpsTracker.canGetLocation()){


            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();

            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();

                enterAddress.setText(address);

                Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
                        + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

                mMap = googleMap;
                // Add a marker in Sydney and move the camera
                LatLng sydney = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(sydney).title("Marker"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 13));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(latitude, longitude))      // Sets the center of the map to location user
                        .zoom(17)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));





            } catch (IOException e) {
                e.printStackTrace();
            }



            // \n is for new line

        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gpsTracker.showSettingsAlert();
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {

        latitude = latLng.latitude;
        longitude = latLng.longitude;
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            enterAddress.setText(address);

            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
                    + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();


            // Add a marker in Sydney and move the camera
            LatLng sydney = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latitude, longitude))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));





        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }
}
