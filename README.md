# CourseALS20171 - Avísame

Geofence API para Android.

## Paso 1

Crear lista de Geofences

```java
Geofence geofence = new Geofence.Builder()
        .setRequestId(lugar.getId())
        .setCircularRegion(lugar.getLatitud(), lugar.getLongitud(), 200) // radio en metros
        .setExpirationDuration(12 * 60 * 60 * 1000) // en horas
        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
        .build();

geofencesLista.add(geofence);
```

## Paso 2

Método acción del botón para adicionar Geofences

```java
LocationServices.GeofencingApi.addGeofences(googleApiClient, obtenerSolicitudGeofencing(), obtenterPendingIntent()).setResultCallback(this);
```

## Paso 3

Método acción del botón para eliminar Geofences

```java
LocationServices.GeofencingApi.removeGeofences(googleApiClient, obtenterPendingIntent()).setResultCallback(this);
```

## Paso 4

Realizar algo cuando sucedió una transición. En `GeofenceTransicionService`.

```java
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
```