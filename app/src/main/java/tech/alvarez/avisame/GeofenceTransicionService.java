package tech.alvarez.avisame;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

public class GeofenceTransicionService extends IntentService {

    public GeofenceTransicionService() {
        super("AvisameService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e("MIAPP", "onHandleIntent: " + intent.toString());

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            int errorCode = geofencingEvent.getErrorCode();
            if (errorCode == GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE) {
                Log.e("MIAPP", "Geofence no disponible");
            } else if (errorCode == GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES) {
                Log.e("MIAPP", "Hay muchos geofences");
            } else if (errorCode == GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS) {
                Log.e("MIAPP", "Hay muchos Pending intents");
            } else {
                Log.e("MIAPP", "Error desconocido de Geofence");
            }
            return;
        }

        int tipoTransicion = geofencingEvent.getGeofenceTransition();

        if (tipoTransicion == Geofence.GEOFENCE_TRANSITION_ENTER || tipoTransicion == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Lista de Geofences que sucedieron
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Obtenemos solo los ids de los Geofences
            ArrayList triggeringGeofencesIdsList = new ArrayList();
            for (Geofence geofence : triggeringGeofences) {
                triggeringGeofencesIdsList.add(geofence.getRequestId());
            }
            String idsGeofencesString = TextUtils.join(", ", triggeringGeofencesIdsList);

            // Armamos el mensaje que se mostrará
            String geofenceTransitionDetails = obtenerNombreTransicion(tipoTransicion) + " a " + idsGeofencesString;

            enviarNotificacion(geofenceTransitionDetails);

            Log.i("MIAPP", geofenceTransitionDetails);
        } else {
            Log.e("MIAPP", "Sucedió una transición pero de diferente tipo: " + tipoTransicion);
        }
    }

    private String obtenerNombreTransicion(int transitionType) {
        if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
            return "Entrando";
        } else if (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
            return "Saliendo";
        } else {
            return "Transición desconocida";
        }
    }


    private void enviarNotificacion(String notificationDetails) {
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_feliz)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.angel))
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText("Detectamos tu ubicación con Geofence API")
                .setContentIntent(notificationPendingIntent);

        builder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }
}
