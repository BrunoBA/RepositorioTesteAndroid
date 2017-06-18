package com.example.bruno.awarenesstest;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, SensorEventListener {


    //    private static final String FENCE_RECEIVER_ACTION = "FENCE_RECEIVE";
    //    private static String textHole = "Cuidado com o buraco!";
    //    private Toast t;
    //    private Context context = getApplicationContext();
    //    private HeadphoneFenceBroadcastReceiver fenceReceiver;
    //    private PendingIntent mFencePendingIntent;
    private GoogleMap mMap;
    private GoogleApiClient mClient;
    private Sensor sensor;
    private SensorManager sensorManager;
    private static double SENSIBILIDADE_SENSOR_X = 1.2;
    private static double SENSIBILIDADE_SENSOR_Y = 1.5;
    private static double MIN_SENSOR_Z = 8;
    private static double MAX_SENSOR_Z = 12;
    private static String TAG = "Awareness";
    private static final int TIPO_SENSOR = Sensor.TYPE_ACCELEROMETER;
    private static int atividade = DetectedActivity.UNKNOWN;

    private ArrayList<LatLng> holes = new ArrayList<LatLng>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(TIPO_SENSOR);

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(Awareness.API)
                .addApi(ActivityRecognition.API)
                .build();
        mClient.connect();

        //Inicializar valores dos buracos via banco de dados
        holes.add(new LatLng(-8.191051, -34.921126));

        Log.i(TAG, "onCreate");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng casaDoCriador = new LatLng(-8.14049705, -34.90979254);
        LatLng unicap = new LatLng(-8.05557534, -34.88822222);

        //RUNTIME PERMISSIONS PARA LOCALIZAÇAO

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);

        //MARCANDO LOCAIS INDICADOS COM BURACOS
        initHole(holes);

        mMap.addMarker(new MarkerOptions().position(casaDoCriador).
                icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        mMap.addMarker(new MarkerOptions().position(unicap).
                icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(casaDoCriador));

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Conected on API Awareness!");

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    12345
            );
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float sensorX = -event.values[0];
        float sensorY = event.values[1];
        float sensorZ = event.values[2];


        if(checkTheConditions(sensorX,sensorY,sensorZ)){

            Log.i("pegou","marcou em casa");

            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        12345
                );
            }

            Awareness.SnapshotApi.getDetectedActivity(mClient)
                    .setResultCallback(new ResultCallback<DetectedActivityResult>() {
                        @Override
                        public void onResult(@NonNull DetectedActivityResult detectedActivityResult) {
                            if (!detectedActivityResult.getStatus().isSuccess()) {
                                Log.e(TAG, "Could not get the current activity.");
                            }else{
                                ActivityRecognitionResult ar = detectedActivityResult.getActivityRecognitionResult();
                                atividade = ar.getMostProbableActivity().getType();
                            }
                        }
                    });


            // UMA VEZ DETECTADO UM BURACO, ESTE DEVE SER SALVO
            Awareness.SnapshotApi.getLocation(mClient)
                    .setResultCallback(new ResultCallback<LocationResult>() {
                        @Override
                        public void onResult(@NonNull LocationResult locationResult) {

                            if (!locationResult.getStatus().isSuccess()) {
                                Log.e(TAG, "Could not get location.");
                                return;
                            }


                            else if(atividade == DetectedActivity.IN_VEHICLE){

                                CharSequence text = "Buraco!";
                                Toast t = Toast.makeText( getApplicationContext(), text, Toast.LENGTH_SHORT);
                                t.show();

                                Log.i(TAG,"Buraco detectado!");
                                Location location = locationResult.getLocation();
                                Log.i(TAG, String.valueOf(atividade));
                                Log.i(TAG, "Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude());
                            }
                            else{
                                Log.i(TAG, String.valueOf(atividade));
                                Log.i(TAG,"foi detectado um buraco, mas foi ignorado pelo contexto");
                            }


                        }
                    });
        }
    }

    //METODO FEITO PARA IDENTIFICAR SE HOUVE A DETECÇAO DE UM BURACO
    private boolean checkTheConditions(float x, float y, float z){

        if(x > SENSIBILIDADE_SENSOR_X && y > SENSIBILIDADE_SENSOR_Y &&
                (MAX_SENSOR_Z > z &&  z < MIN_SENSOR_Z) ||
                (-MAX_SENSOR_Z > z &&  z < -MIN_SENSOR_Z)){
            return true;
        }
        return false;

    }

    protected void initHole(ArrayList<LatLng> holes){

        for (int i = 0; i < holes.size(); i++ ){
            mMap.addMarker(new MarkerOptions().position(holes.get(i)).
                    icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
