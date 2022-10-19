package com.example.tallermapas;

import static java.lang.System.out;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Environment;
import android.util.Log;

import android.view.View;
import android.widget.Toast;




import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.tallermapas.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener, RoutingListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    float currentLux = 0;
    float maxLux;
    private double lat;
    private double lon;
    private double currentLatitude = 0;
    private double currentLongitude= 0;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;



    LocationListener listener;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private mapa maps = new mapa();
    private JSONArray jsonArray = new JSONArray();
    private SensorManager sensorManager;
    private Sensor lightSensorListener;
    SensorEventListener Slistener;
    private ArrayList<LatLng> coordList = new ArrayList<LatLng>();
    private List<Polyline> polylines=null;
    protected LatLng start=null;
    protected LatLng end=null;
    
    private Boolean hayarchivo = false; 

    //Minimo tiempo para updates en Milisegundos
    private static final long MIN_TIEMPO_ENTRE_UPDATES = 100; // 1 minuto
    //Minima distancia para updates en metros.
    private static final long MIN_CAMBIO_DISTANCIA_PARA_UPDATES = 30; // 1.5 metros

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(localizacionpermiso()){
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        lightSensorListener = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        mLocationRequest = createLocationRequest();
        //startLocationUpdates();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);}
        else{
            localizacionpermiso();
        }
    }

    private LocationRequest createLocationRequest(){
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(1000)
                .setFastestInterval(500)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }





    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(mLocationCallback);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(mLocationCallback);
    }



    /**
     * If connected get lat and long
     *
     */


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    private  boolean localizacionpermiso() {
        List<String> listPermissionsNeeded = new ArrayList<>();


        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {



        mMap = googleMap;

        Log.d("LUXes", "LUX: " + currentLux);
        Slistener = new SensorEventListener() {

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                int grayShade = (int) event.values[0];
                if (grayShade < 150){
                    binding.buscartxt.setTextColor(Color.WHITE);
                    binding.buscartxt.setHintTextColor(Color.WHITE);
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this, R.raw.darkmaptheme));
                }
                else{
                    binding.buscartxt.setTextColor(Color.BLACK);
                    binding.buscartxt.setHintTextColor(Color.BLACK);
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this, R.raw.lightmaptheme));
                }
            }
        };
        sensorManager.registerListener( Slistener, lightSensorListener, SensorManager.SENSOR_DELAY_FASTEST);


        float zoomLevel = 16.0f; //This goes up to 21
        ArrayList markerPoints= new ArrayList();
        Geocoder geocoder = new Geocoder(this);
        mMap.setOnMapLongClickListener(latLng -> {
            lat = latLng.latitude;
            lon = latLng.longitude;

            start = new LatLng(lat, lon);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
            try {
                mMap.addMarker(new MarkerOptions().position(latLng).title(geocoder.getFromLocation(lat, lon, 1).get(0).getAddressLine(0)));
            } catch (IOException e) {
                e.printStackTrace();
            }



        });

        mMap.setOnMarkerClickListener(marker -> {
            marker.showInfoWindow();
            return false;
        });



        binding.buscartxt.setOnClickListener(v -> {
            try {
                List<Address> addresses = geocoder.getFromLocationName(binding.buscartxt.getText().toString(), 1);
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

                    mMap.addMarker(new MarkerOptions().position(latLng).title(binding.buscartxt.getText().toString()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
                    Location dis1 = new Location("localizacion 1");
                    dis1.setLatitude(lat);  //latitud
                    dis1.setLongitude(lon); //longitud
                    Location dis2 = new Location("localizacion 2");
                    dis2.setLatitude(address.getLatitude());  //latitud
                    dis2.setLongitude(address.getLongitude()); //longitud
                    double distance = dis1.distanceTo(dis2);
                    double distanceKm = distance / 1000;

                    LatLng destination = new LatLng(dis2.getLatitude(), dis2.getLongitude());


                    end = destination;
                    if(lat != 0 && lon != 0){
                       start = new LatLng(lat, lon);
                        Findroutes(start,end);
                    }
                    else{
                        Findroutes(start,end);
                    }


                    Toast.makeText(this, "Distancia: " + distanceKm + " km", Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });



        if(checkPermissions()) {
            mMap.setMyLocationEnabled(true);
            mMap.isMyLocationEnabled();
            Location location2 = mMap.getMyLocation();
            if (location2 != null) {
                maps.setLatitud(location2.getLatitude());
                maps.setLongitud(location2.getLatitude());
                LatLng latLng = new LatLng(maps.getLatitud(), maps.getLongitud());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
            }

            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        if (locationResult == null) {
                            return;
                        }
                        //Showing the latitude, longitude and accuracy on the home screen.
                        for (Location location : locationResult.getLocations()) {

                            currentLatitude = location.getLatitude();
                            currentLongitude = location.getLongitude();
                            start = new LatLng(currentLatitude, currentLongitude);

                            if(currentLatitude != 0 && currentLongitude != 0) {
                                if (currentLatitude != maps.getLatitud() && currentLongitude != maps.getLongitud()) {
                                    Location loct = new Location("localizacion 1");
                                    loct.setLatitude(currentLatitude);  //latitud
                                    loct.setLongitude(currentLongitude); //longitud
                                    Location location2 = new Location("localizacion 2");
                                    location2.setLatitude(maps.getLatitud());  //latitud
                                    location2.setLongitude(maps.getLongitud()); //longitud
                                    double currentdistance = loct.distanceTo(location2);
                                    if (currentdistance > 30) {
                                        maps.setLatitud(currentLatitude);
                                        maps.setLongitud(currentLongitude);
                                        maps.setFecha(new Date(System.currentTimeMillis()));
                                        maps.setDistancia(currentdistance);
                                        writeJSONObject();
                                    }
                                }
                            }

                        }
                    }
                }
            };

            startLocationUpdates();

            
            binding.mostrarBTN.setOnClickListener(v -> {
                Log.d("siss", "onCreate: " + hayarchivo);
                if (hayarchivo == true) {
                    try {
                        String json = loadJSONFromAsset();
                        Log.d("siss",  json);

                        //JSONObject root = new JSONObject(json);

                        JSONArray array = new JSONArray(json);
                        Log.d("siss", " el tamde:  " + array.length());

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject1 = array.getJSONObject(i);
                            double latitud = jsonObject1.getDouble("latitud");
                            double longitud = jsonObject1.getDouble("longitud");
                            Geocoder geocoder1 = new Geocoder(this);
                            try {
                                mMap.addMarker(new MarkerOptions().position(new LatLng(latitud, longitud)).title(geocoder1.getFromLocation(latitud, longitud, 1).get(0).getAddressLine(0)));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            Log.d("thisnuts", String.valueOf(jsonObject1.getDouble("longitud")));
                            LatLng orgi = new LatLng(latitud, longitud);
                            JSONObject jsonObject2 = jsonArray.getJSONObject(i+1);
                            double latitud2 = jsonObject2.getDouble("latitud");
                            double longitud2 = jsonObject2.getDouble("longitud");
                            LatLng desti = new LatLng(latitud2, longitud2);
                            Routing routing = new Routing.Builder()
                                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                                    .withListener(this)
                                    .alternativeRoutes(true)
                                    .waypoints(orgi, desti)
                                    .key("AIzaSyCydYqy3SwNNILEiFKMZRyloqvEVUuTkFU")  //also define your api key here.
                                    .build();
                            routing.execute();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }else{
                    Toast.makeText(this, "No hay archivo", Toast.LENGTH_SHORT).show();
                }
            });

        }






    }


    public String loadJSONFromAsset() {
        String json = null;
        String nomarchivo = "locations.json";
        File tarjeta = Environment.getExternalStorageDirectory();
        File file = new File(getBaseContext().getExternalFilesDir(null), nomarchivo);
        Log.d("Filejsonpath", "loadJSONFromAsset: " + getBaseContext().getExternalFilesDir(null));
        try {
            FileInputStream fIn = new FileInputStream(file);
            InputStreamReader archivo=new InputStreamReader(fIn);
            BufferedReader br=new BufferedReader(archivo);
            String linea=br.readLine();

            String todo="";
            while (linea!=null)
            {
                todo=todo+linea+"\n";

                linea=br.readLine();
            }
            Log.d("Filejson", "loadJSONFromAsset: " + todo);
            br.close();
            archivo.close();
            json = todo;

        } catch (IOException e)
        {
            Toast.makeText(this, "No se pudo leer",
                    Toast.LENGTH_SHORT).show();
        }
        return json;
    }


    private void writeJSONObject(){

        jsonArray.put(maps.toJSON());
        Writer output = null;
        String filename= "locations.json";
        try {
            File file = new File(getBaseContext().getExternalFilesDir(null), filename);
            Log.d("Latitud", file.getAbsolutePath() );
            output = new BufferedWriter(new FileWriter(file));
            output.write(jsonArray.toString());
            output.close();
            Toast.makeText(this, "se salvo la localizacion", Toast.LENGTH_SHORT).show();
            hayarchivo = true;
        } catch (Exception e) {

        }
    }
    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions();
            return false;
        }
    }
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }
    @SuppressLint("MissingPermission")
    private void startLocationUpdates(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED){
            String locationProvider = LocationManager.NETWORK_PROVIDER;
            fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    public void Findroutes(LatLng Start, LatLng End)
    {
        if(Start==null || End==null) {
            Toast.makeText(MapsActivity.this,"Unable to get location", Toast.LENGTH_LONG).show();
        }
        else
        {

            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
                    .key("AIzaSyCydYqy3SwNNILEiFKMZRyloqvEVUuTkFU")  //also define your api key here.
                    .build();
            routing.execute();
        }
    }


    //Routing call back functions.
    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar= Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
        snackbar.show();
//        Findroutes(start,end);
    }

    @Override
    public void onRoutingStart() {
        Toast.makeText(MapsActivity.this,"Finding Route...",Toast.LENGTH_LONG).show();
    }

    //If Route finding success..
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        if(polylines!=null) {
            polylines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng=null;
        LatLng polylineEndLatLng=null;


        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i <route.size(); i++) {

            if(i==shortestRouteIndex)
            {
                polyOptions.color(getResources().getColor(R.color.purple_500));
                polyOptions.width(7);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                Polyline polyline = mMap.addPolyline(polyOptions);
                polylineStartLatLng=polyline.getPoints().get(0);
                int k=polyline.getPoints().size();
                polylineEndLatLng=polyline.getPoints().get(k-1);
                polylines.add(polyline);

            }
            else {

            }

        }


    }

    @Override
    public void onRoutingCancelled() {
        Findroutes(start,end);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Findroutes(start,end);

    }

}