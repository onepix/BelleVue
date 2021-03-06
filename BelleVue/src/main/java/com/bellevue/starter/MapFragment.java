package com.bellevue.starter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Paul on 8/11/15.
 */

public class MapFragment extends SupportMapFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener {

    // http://stackoverflow.com/questions/13713726/maps-api-v2-with-different-marker-actions
    // http://stackoverflow.com/questions/15333971/get-other-values-from-marker
    private HashMap<String, String> markers= new HashMap<String, String>();

    private GoogleApiClient mGoogleApiClient;

    public static Location mCurrentLocation;

    private final int[] MAP_TYPES = {
            GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_NONE
    };

    private int curMapTypeIndex = 0;
    /* implements GoogleMap.OnMyLocationChangeListener
            getMap().setOnMyLocationChangeListener(this);
            getMap().setOnMyLocationChangeListener(null);
        @Override
        public void onMyLocationChange(Location location) {
            getMap().addMarker(new MarkerOptions().position(new LatLng( location.getLatitude(), location.getLongitude())).icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            Log.d("OOUPS", "REFRESH EVERY SINGLE TIME");
        }
    */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        mGoogleApiClient = new GoogleApiClient.Builder( getActivity() )
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener( this )
                .addApi( LocationServices.API )
                .build();

        initListeners();
    }

    private void initListeners() {
        getMap().setOnMarkerClickListener(this);
        getMap().setOnMapLongClickListener(this);
        getMap().setOnInfoWindowClickListener(this);
        getMap().setOnMapClickListener(this);
    }

    private void removeListeners() {
        if( getMap() != null ) {
            getMap().setOnMarkerClickListener( null );
            getMap().setOnMapLongClickListener(null);
            getMap().setOnInfoWindowClickListener(null);
            getMap().setOnMapClickListener(null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeListeners();
    }

    private void initCamera( Location location ) {
        CameraPosition position = CameraPosition.builder()
                .target( new LatLng( location.getLatitude(), location.getLongitude() ) )
                .zoom( 16f )
                .bearing( 0.0f )
                .tilt( 0.0f )
                .build();

        getMap().animateCamera( CameraUpdateFactory.newCameraPosition(position), null );

        getMap().setMapType(MAP_TYPES[curMapTypeIndex]);
        getMap().setTrafficEnabled(false);
        getMap().setMyLocationEnabled(true);
        getMap().getUiSettings().setZoomControlsEnabled(false);
        getMap().setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {

                ParseGeoPoint userLocation = new ParseGeoPoint(MapFragment.mCurrentLocation.getLatitude(),
                        MapFragment.mCurrentLocation.getLongitude());
                ParseQuery<ParseObject> query = ParseQuery.getQuery("BelleVue");
                query.whereNear("location", userLocation);
                query.setLimit(10);
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> belleVueList, ParseException e) {
                        if (e == null) {
                            Log.d("score", "Retrieved " + belleVueList.size() + " scores");

                            LatLng latLng;
                            for (ParseObject belleVue : belleVueList) {
                                /* Log.d("Categorie & ID", "Retrieved " + belleVue.get("name") + " categorie : " +
                                        String.valueOf(belleVue.getInt("categorie")) + " ID : " + belleVue.getObjectId());*/
                                latLng = new LatLng(((ParseGeoPoint) belleVue.get("location")).getLatitude(),
                                        ((ParseGeoPoint) belleVue.get("location")).getLongitude());
                                MarkerOptions options = new MarkerOptions().position(latLng);
                                switch (belleVue.getInt("categorie")) {
                                    case 1:
                                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                                        break;
                                    case 2:
                                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                                        break;
                                    case 3:
                                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                        break;
                                    case 4:
                                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                                        break;
                                    default:
                                        options.icon(BitmapDescriptorFactory.defaultMarker());
                                        break;
                                }
                                // getMap().addMarker(options);
                                markers.put(getMap().addMarker(options).getId(), belleVue.getObjectId());
                            }
                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                        }
                    }
                });


                return false;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation( mGoogleApiClient );

        initCamera(mCurrentLocation);
    }

    @Override
    public void onConnectionSuspended(int i) {
        //handle play services disconnecting if location is being constantly used
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Create a default location if the Google API Client fails. Placing location at Googleplex
        mCurrentLocation = new Location( "" );
        mCurrentLocation.setLatitude(48.8567);
        mCurrentLocation.setLongitude(2.3508);
        initCamera(mCurrentLocation);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        // Toast.makeText( getActivity(), "Clicked on marker", Toast.LENGTH_SHORT ).show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // marker.showInfoWindow();
        // Toast.makeText( getActivity(), "Clicked on marker : " + markers.get(marker.getId()), Toast.LENGTH_SHORT ).show();
        Intent vueView = new Intent(getActivity().getApplicationContext(), VueViewTabs.class);
        vueView.putExtra("objectId", markers.get(marker.getId()));
        startActivity(vueView);
        return true;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        /*MarkerOptions options = new MarkerOptions().position( latLng );
        options.title( getAddressFromLatLng(latLng) );

        options.icon( BitmapDescriptorFactory.fromBitmap(
                BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)) );

        getMap().addMarker(options);*/
    }

    @Override
    public void onMapClick(LatLng latLng) {
        /*
        //**** just for now
        getMap().setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
              public boolean onMarkerClick(Marker m) {
                Intent vueView = new Intent(getActivity().getApplicationContext(), VueViewTabs.class);
                startActivity(vueView);
                // vueView.putExtra("user_name","WQFIDPyMIg");

                return true;
            }

         });
        //**** just for now
        */
    }

    private void cycleMapType() {
        if( curMapTypeIndex < MAP_TYPES.length - 1 ) {
            curMapTypeIndex++;
        } else {
            curMapTypeIndex = 0;
        }

        getMap().setMapType( MAP_TYPES[curMapTypeIndex] );
    }
}