package com.example.gaber.graduation_demo_driver.fragments.uber;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gaber.graduation_demo_driver.R;
import com.example.gaber.graduation_demo_driver.activities.confirm_code;
import com.example.gaber.graduation_demo_driver.activities.mobile_authentication;
import com.example.gaber.graduation_demo_driver.custom.DirectionsJSONParser;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class Main_map extends Fragment implements LocationListener {
    View view;
    MapView mMapView;
    private GoogleMap googleMap;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    AlertDialog alert;
    private RequestQueue queue;
    private LocationManager lm;
    private static final long MIN_TIME = 500;
    private static final float MIN_DISTANCE = 5;
    private boolean driver_available=true;
    private Marker marker;
    private String provider;
    private FusedLocationProviderClient mFusedLocationClient;





    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        view = inflater.inflate(R.layout.main_map_fragment, container, false);

        LocationManager service = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        boolean enabledGPS = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean enabledWiFi = service
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!enabledGPS) {
            Toast.makeText(getActivity(), "GPS signal not found", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        else if(!enabledWiFi){
            Toast.makeText(getActivity(), "Network signal not found", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        lm = (LocationManager)getActivity().getSystemService(LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER

        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        Criteria criteria = new Criteria();
        provider = lm.getBestProvider(criteria, false);
        Location location = lm.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            onLocationChanged(location);
        } else {

            //do something
        }


        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                googleMap.setMyLocationEnabled(true);
                if (getActivity().getSharedPreferences("driver_data",MODE_PRIVATE)
                        .getString("driver_lat","").length()>0) {
                    double driver_lat = Double.parseDouble(getActivity().getSharedPreferences("driver_data", MODE_PRIVATE)
                            .getString("driver_lat", ""));

                    double driver_long = Double.parseDouble(getActivity().getSharedPreferences("driver_data", MODE_PRIVATE)
                            .getString("driver_long", ""));

                    marker=googleMap.addMarker(new MarkerOptions().position(new LatLng(driver_lat, driver_long)).title("My Location"));
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(driver_lat,driver_long)).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
                //get_direction_json(new LatLng(31.2400588999,32.2896713),new LatLng(30.0916471,31.3398668),googleMap);


            }


        });



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isLocationEnabled(getActivity())&&alert==null) {
            location_is_available();
        }
        mMapView.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

   private void location_is_available(){
       try {
           gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
       } catch(Exception ex) {}

       try {
           network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
       } catch(Exception ex) {}

       if(!gps_enabled && !network_enabled) {
           // notify user
           alert=new AlertDialog.Builder(getActivity())
                   .setMessage(R.string.gps_title)
                   .setTitle(R.string.gps_network_not_enabled)
                   .setPositiveButton(R.string.open_location_settings, new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                           getActivity().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                           alert=null;
                       }
                   })
                   .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialogInterface, int i) {
                           alert.dismiss();
                           alert=null;
                       }
                   })
                   .show();
       }
   }
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }
   private void get_direction_json(LatLng origin, LatLng destination, final GoogleMap mMap)
    {


        try {
               String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+origin.latitude+","+origin.longitude+
                       "&destination="+destination.latitude+","+destination.longitude+"&mode=driving&key=AIzaSyDxy0ndkDYovy6TEo71pnhMhfopHbX4fpg";
            if (queue == null) {
                queue = Volley.newRequestQueue(getActivity());
            }
            // Request a string response from the provided URL.
            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            parse_direction_json(response,mMap);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // error
                }
            });
            // Add the request to the RequestQueue.
            queue.add(stringRequest);

        } catch (Exception e) {

        }


    }
    private void parse_direction_json(JSONObject object, GoogleMap mMap)
    {
        List<List<HashMap<String, String>>> routes = null;
        DirectionsJSONParser parser = new DirectionsJSONParser();
        routes = parser.parse(object);
        draw_poly_line(routes,mMap);

    }
    private void draw_poly_line(List<List<HashMap<String, String>>> result, GoogleMap mMap)
    {
        ArrayList points = null;
        PolylineOptions lineOptions = null;
        MarkerOptions markerOptions = new MarkerOptions();

        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList();
            lineOptions = new PolylineOptions();

            List<HashMap<String, String>> path = result.get(i);

            for (int j = 0; j < path.size(); j++) {
                HashMap point = path.get(j);

                double lat = Double.parseDouble(String.valueOf(point.get("lat")));
                double lng = Double.parseDouble(String.valueOf(point.get("lng")));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            lineOptions.addAll(points);
            lineOptions.width(12);
            lineOptions.color(Color.RED);
            lineOptions.geodesic(true);

        }

// Drawing polyline in the Google Map for the i-th route
        mMap.addPolyline(lineOptions);
    }
    @Override
    public void onLocationChanged(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.w("kjhhhhklj",latLng.latitude+","+latLng.longitude);
        Toast.makeText(getActivity(),latLng.latitude+","+latLng.longitude,Toast.LENGTH_LONG).show();

        if (googleMap!=null) {
            getActivity().getSharedPreferences("driver_data", MODE_PRIVATE)
                    .edit().putString("driver_lat", String.valueOf(location.getLatitude()));

            getActivity().getSharedPreferences("driver_data", MODE_PRIVATE)
                    .edit().putString("driver_long", String.valueOf(location.getLongitude()));
            if (marker!=null) {
                animateMarker(marker, latLng, true);
            }

            //lm.removeUpdates(this);
            if (driver_available) {
                update_my_location(latLng.latitude, latLng.longitude);
            }
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getActivity(),"location is working",Toast.LENGTH_LONG).show();
        request_location();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getActivity(),"location isn't working",Toast.LENGTH_LONG).show();

    }
    private void update_my_location(final double Latt, final double longt){

        String driver_key=getActivity().getSharedPreferences("logged_in",MODE_PRIVATE).getString("driver_key", "");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("drivers_locations");
        GeoFire geoFire = new GeoFire(reference);
        geoFire.setLocation(driver_key, new GeoLocation(Latt,longt), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    System.err.println("There was an error saving the location to GeoFire: " + error);
                } else {
                    System.out.println("Location saved on server successfully!");
                }
            }


        });


    }
    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = googleMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }
    @SuppressLint("MissingPermission")
    public void request_location(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener( getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            getActivity().getSharedPreferences("driver_data",MODE_PRIVATE)
                                    .edit().putString("driver_lat", String.valueOf(location.getLatitude()))
                                    .putString("driver_long", String.valueOf(location.getLongitude()))
                                    .apply();
                        }
                    }
                });
    }

}
