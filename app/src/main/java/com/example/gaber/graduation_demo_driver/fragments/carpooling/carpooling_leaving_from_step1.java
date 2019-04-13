package com.example.gaber.graduation_demo_driver.fragments.carpooling;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.gaber.graduation_demo_driver.R;
import com.example.gaber.graduation_demo_driver.activities.Mainview;
import com.example.gaber.graduation_demo_driver.custom.DataPassListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class carpooling_leaving_from_step1 extends Fragment  {
    View view;
    DataPassListener mCallback;


    private TextView back,number_of_notifications,notifications;
    MapView mMapView;
    private GoogleMap googleMap;
    private AutocompleteSupportFragment autocompleteFragment;
    private static final float DEFAULT_ZOOM = 15f;
    private Button Confirm_Leaving_from;
    private double Leave_from_latt,Leave_from_longt;
    private String Leave_from_state,Leave_from_city,Leave_from_address;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (DataPassListener) context;
        Bundle args = getArguments();
        if (args!=null) {
            Leave_from_latt = args.getDouble("Leave_from_latt");
            Leave_from_longt = args.getDouble("Leave_from_longt");
            Leave_from_city = args.getString("Leave_from_city");
            Leave_from_address = args.getString("Leave_from_address");
            Leave_from_state = args.getString("Leave_from_state");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.confirm_leaving_from, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Places.initialize(getActivity(), "AIzaSyDxy0ndkDYovy6TEo71pnhMhfopHbX4fpg");
        PlacesClient placesClient = Places.createClient(getActivity());
        mMapView = (MapView)view.findViewById(R.id.mapView);
        back=(TextView)view.findViewById(R.id.back);
        Confirm_Leaving_from=(Button)view.findViewById(R.id.Confirm_Leaving_from);
        mMapView.onCreate(savedInstanceState);



        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG));
        autocompleteFragment.setCountry("EG");


        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                moveCamera(place.getLatLng(),DEFAULT_ZOOM,"Current Place");
                Leave_from_latt=place.getLatLng().latitude;
                Leave_from_longt=place.getLatLng().longitude;
                Leave_from_address=place.getAddress();
                Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
                try {
                    List<Address> addresses = gcd.getFromLocation(Leave_from_latt, Leave_from_longt, 1);
                    Leave_from_city=addresses.get(0).getLocality();
                    Leave_from_state=addresses.get(0).getAdminArea();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
            }
        });
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                googleMap.setMyLocationEnabled(true);
                if (getActivity().getSharedPreferences("driver_data",MODE_PRIVATE)
                        .getString("driver_lat","").length()>0&&(Leave_from_latt==0&&Leave_from_longt==0)) {
                    double driver_lat = Double.parseDouble(getActivity().getSharedPreferences("driver_data", MODE_PRIVATE)
                            .getString("driver_lat", ""));

                    double driver_long = Double.parseDouble(getActivity().getSharedPreferences("driver_data", MODE_PRIVATE)
                            .getString("driver_long", ""));
                    googleMap.addMarker(new MarkerOptions().position(new LatLng(driver_lat, driver_long)).title("Marker Title").snippet("Marker Description"));


                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(driver_lat,driver_long)).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }else {
                    googleMap.addMarker(new MarkerOptions().position(new LatLng(Leave_from_latt, Leave_from_longt)).title("Marker Title").snippet("Marker Description"));


                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Leave_from_latt,Leave_from_longt)).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        });

        Confirm_Leaving_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putDouble("Leave_from_latt",Leave_from_latt);
                args.putDouble("Leave_from_longt",Leave_from_longt);
                args.putString("Leave_from_city",Leave_from_city);
                args.putString("Leave_from_state",Leave_from_state);
                args.putString("Leave_from_address",Leave_from_address);
                mCallback.passData(new carpooling_going_to_step2(),args);
            }
        });




        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main=new Intent(getActivity(),Mainview.class);
                startActivity(main);
                getActivity().finish();
            }
        });

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    Intent main=new Intent(getActivity(),Mainview.class);
                    startActivity(main);
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });


        return view;
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if(!title.equals("My Location")){
            googleMap.clear();
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            googleMap.addMarker(options);
        }

        hideSoftKeyboard();
    }



    private void hideSoftKeyboard(){
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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


}
