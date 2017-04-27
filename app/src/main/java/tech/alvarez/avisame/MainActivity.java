package tech.alvarez.avisame;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private GoogleMap mMap;
    private ArrayList<Lugar> lugares;

    private GoogleApiClient googleApiClient;

    protected ArrayList<Geofence> geofencesLista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        lugares = new ArrayList<Lugar>();
        lugares.add(new Lugar("UMSA", -16.504772, -68.129992));
        lugares.add(new Lugar("Multicine", -16.510916, -68.122146));
        lugares.add(new Lugar("COE", -16.537775, -68.084310));


        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .build();

        crearListaGeofences();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleApiClient.connect();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Cede permiso de localización manualmente.", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 777);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 777) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // El usuario dio permiso
            } else {
                Toast.makeText(this, "No diste permiso, no podemos hacer nada.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        for (Lugar lugar : lugares) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(lugar.getLatitud(), lugar.getLongitud()))
                    .strokeColor(ContextCompat.getColor(this, R.color.colorAccent))
                    .strokeWidth(3)
                    .fillColor(ContextCompat.getColor(this, R.color.colorAccentTransparente))
                    .radius(200);

            mMap.addCircle(circleOptions);
        }

        Lugar primerLugar = lugares.get(0);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(primerLugar.getLatitud(), primerLugar.getLongitud()), 14));

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public void crearListaGeofences() {
        geofencesLista = new ArrayList<Geofence>();
        for (Lugar lugar : lugares) {

            // TODO: Paso 1

        }
    }


    // Acciones de los botones

    public void adicionar(View view) {
        if (!googleApiClient.isConnected()) {
            Toast.makeText(this, "GoogleApiClient no esta conectado, intenta de nuevo", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // TODO: Paso 2
    }

    public void eliminar(View view) {
        // TODO: Paso 3
    }


    // Métodos para armar

    private GeofencingRequest obtenerSolicitudGeofencing() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofencesLista);
        return builder.build();
    }

    private PendingIntent obtenterPendingIntent() {
        Intent intent = new Intent(this, GeofenceTransicionService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    // Donde llegan la respuesta al adicionar o eliminar Geofences

    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            Toast.makeText(this, "Geofences adicionados o eliminados de forma exitosa.", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("MIAPP", "Error onResult");
            int errorCode = status.getStatusCode();
            if (errorCode == GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE) {
                Log.e("MIAPP", "Geofence no disponible");
            } else if (errorCode == GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES) {
                Log.e("MIAPP", "Hay muchos geofences");
            } else if (errorCode == GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS) {
                Log.e("MIAPP", "Hay muchos Pending intents");
            } else {
                Log.e("MIAPP", "Error desconocido de Geofence: " + status.getStatusMessage());
            }
        }
    }
}
