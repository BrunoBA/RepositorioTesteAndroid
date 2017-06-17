package com.example.bruno.awarenesstest;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks {

    private GoogleMap mMap;
    private GoogleApiClient mClient;
//    private Toast t;
//    private Context context = getApplicationContext();
    private static String textHole = "Cuidado com o buraco!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(Awareness.API)
                .addApi(ActivityRecognition.API)
                .build();
        mClient.connect();

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

        LatLng casaDoCriador = new LatLng(-8.14049705, -34.90979254);
        LatLng unicap = new LatLng(-8.05557534,-34.88822222);

        mMap.setMyLocationEnabled(true);

        mMap.addMarker(new MarkerOptions().position(casaDoCriador).title(textHole));
        mMap.addMarker(new MarkerOptions().position(unicap).title(textHole));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(casaDoCriador));

    }


    public void onRequestPermissionsResult(){

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("teste", "Conected on API Awareness!");

//      ESSA PARTE DO CÓDIGO FOI INTERROMPIDA POR CONTA DAS PERMISSOES POSTAS MANUALMENTE NO APLICATIVO!!!!



//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            //TESTA SE O USUÁRIO TEM PERMISSÃO PARA ACESSAR A LOCALIZAÇÃO
//
//            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION){
//                t.makeText(context,"É preciso dar permissões para esse aplicativo funcionar perfeitamente",Toast.LENGTH_LONG);
//                t.show();
//            }else{
//
//                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,0});
//            }
//
//        }else{

            Awareness.SnapshotApi.getWeather(mClient)
                    .setResultCallback(new ResultCallback<WeatherResult>() {
                        @Override
                        public void onResult(@NonNull WeatherResult weatherResult) {

                            Log.i("teste", String.valueOf(weatherResult.getWeather().getTemperature(Weather.CELSIUS)));

                        }
                    });
//        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
