package com.example.gaber.graduation_demo_driver.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gaber.graduation_demo_driver.R;
import com.example.gaber.graduation_demo_driver.custom.DataPassListener;
import com.example.gaber.graduation_demo_driver.custom.DirectionsJSONParser;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class uber_ride extends AppCompatActivity implements LocationListener {
    private RequestQueue queue;
    MapView mMapView;
    private GoogleMap googleMap;
    private static final float DEFAULT_ZOOM = 15f;
    Button cancel_ride,accept_ride;
    DataPassListener mCallback;
    private double pick_up_latitude,pick_up_longitude,drop_off_latitude,drop_off_longitude;
    private String phone,name,from_Address,going_to_Address,from_token;
    private long time;
    private LocationManager lm;
    private static final long MIN_TIME = 0;
    private static final float MIN_DISTANCE = 0;




    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uber_ride);
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER

        pick_up_latitude=getIntent().getDoubleExtra(" pick_up_latitude",0);
        pick_up_longitude=getIntent().getDoubleExtra("pick_up_longitude", 0);
        drop_off_latitude=getIntent().getDoubleExtra("drop_off_latitude", 0);
        drop_off_longitude=getIntent().getDoubleExtra("drop_off_longitude", 0);
        name=getIntent().getStringExtra("name");
        time=getIntent().getLongExtra("time", 0);
        phone=getIntent().getStringExtra("phone");
        from_token=getIntent().getStringExtra("from_token");
        from_Address=getIntent().getStringExtra("from_Address");
        going_to_Address=getIntent().getStringExtra("going_to_Address");
        mMapView = (MapView) findViewById(R.id.mapView);
        accept_ride = (Button) findViewById(R.id.accept_ride);
        cancel_ride = (Button) findViewById(R.id.cancel_ride);
        mMapView.onCreate(savedInstanceState);


        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(this);
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
                if (getSharedPreferences("driver_data",MODE_PRIVATE)
                        .getString("driver_lat","").length()>0) {

                    googleMap.clear();

                    double driver_lat = Double.parseDouble(getSharedPreferences("driver_data", MODE_PRIVATE)
                            .getString("driver_lat", ""));

                    double driver_long = Double.parseDouble(getSharedPreferences("driver_data", MODE_PRIVATE)
                            .getString("driver_long", ""));
                    googleMap.addMarker(new MarkerOptions().position(new LatLng(driver_lat, driver_long)).title("Marker Title").snippet("Marker Description"));

                    MarkerOptions pick_up = new MarkerOptions();

                    // Setting the position for the marker
                    pick_up.position(new LatLng(pick_up_latitude,pick_up_longitude));

                    // Setting the title for the marker.
                    // This will be displayed on taping the marker
                    pick_up.title(from_Address);

                    MarkerOptions drop_off = new MarkerOptions();

                    // Setting the position for the marker
                    drop_off.position(new LatLng(drop_off_latitude,drop_off_longitude));

                    // Setting the title for the marker.
                    // This will be displayed on taping the marker
                    drop_off.title(going_to_Address);

                    // Clears the previously touched position

                    // Animating to the touched position

                    // Placing a marker on the touched position
                    googleMap.addMarker(pick_up);
                    googleMap.addMarker(drop_off);


                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(driver_lat,driver_long)).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    get_direction_json(new LatLng(driver_lat,driver_long),new LatLng(pick_up_latitude,pick_up_longitude),Color.BLUE,googleMap);
                    get_direction_json(new LatLng(pick_up_latitude,pick_up_longitude),new LatLng(drop_off_latitude,drop_off_longitude),Color.RED,googleMap);
                }
            }
        });

        cancel_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        accept_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_message(from_token,getSharedPreferences("logged_in",MODE_PRIVATE).getString("name","")
                        +" is comming to pick up you");

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
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
    private void get_direction_json(LatLng origin, LatLng destination, final int color, final GoogleMap mMap)
    {


        try {
            String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+origin.latitude+","+origin.longitude+
                    "&destination="+destination.latitude+","+destination.longitude+"&mode=driving&key=AIzaSyDxy0ndkDYovy6TEo71pnhMhfopHbX4fpg";
            if (queue == null) {
                queue = Volley.newRequestQueue(this);
            }
            // Request a string response from the provided URL.
            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            parse_direction_json(response,color,mMap);
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
    private void parse_direction_json(JSONObject object,int color, GoogleMap mMap)
    {
        List<List<HashMap<String, String>>> routes = null;
        DirectionsJSONParser parser = new DirectionsJSONParser();
        routes = parser.parse(object);
        draw_poly_line(routes,color,mMap);

    }
    private void draw_poly_line(List<List<HashMap<String, String>>> result,int color, GoogleMap mMap)
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
            lineOptions.color(color);
            lineOptions.geodesic(true);

        }

// Drawing polyline in the Google Map for the i-th route
        mMap.addPolyline(lineOptions);
    }


    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());


        if (googleMap!=null) {
            getSharedPreferences("driver_data", MODE_PRIVATE)
                    .edit().putString("driver_lat", String.valueOf(location.getLatitude()));

            getSharedPreferences("driver_data", MODE_PRIVATE)
                    .edit().putString("driver_long", String.valueOf(location.getLongitude()));
            googleMap.addMarker(new MarkerOptions().position(latLng).title("Marker Title").snippet("Marker Description"));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
            googleMap.animateCamera(cameraUpdate);
            lm.removeUpdates(this);

            update_my_location(latLng.latitude, latLng.longitude);

        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    private void update_my_location(final double Latt, final double longt){

        String driver_key=getSharedPreferences("logged_in",MODE_PRIVATE).getString("driver_key", "");
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
    private void send_message(String to_user_token,String message)
    {


        try {
            JSONObject main = new JSONObject();
            JSONObject data = new JSONObject();

            data.put("text", message);
            data.put("type","uber_pickup" );
            data.put("key",getSharedPreferences("logged_in",MODE_PRIVATE).getString("key",""));
            data.put("time",String .valueOf((int)System.currentTimeMillis()));
            main.put("data", data);
            main.put("to", to_user_token);
            Log.w("l,jkndsajhkadsji",main.toString());
            String url = "https://fcm.googleapis.com/fcm/send";
            if (queue == null) {
                queue = Volley.newRequestQueue(this);
            }
            // Request a string response from the provided URL.
            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, main,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // error
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", "key=AAAAKajycwU:APA91bGqfmnx9VmTrLU-idWtQydPA58ZxlrluTEmcmwieQpzH5HIvdiOmaAu9x2yqvxE9FAstBUmMHRn0-e61FuSc6lzSzHSariBXJhcB3Tmo6v3K07EYxDhMwlCDlXmLc4rtvQ8m0_g");

                    return params;
                }
            };
            // Add the request to the RequestQueue.
            queue.add(stringRequest);

        } catch (Exception e) {

        }



    }

}
