package it.silence.please;

import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.location.LocationListener;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.widget.LinearLayout;
import android.widget.Toast;
import android.media.AudioManager;

public class SilencePleaseActivity extends MapActivity {
	
	Intent agg_intent;
	Intent list_intent;
	Intent istr_intent;
	Intent cred_intent;
	
	// Creazione variabili per la gestione degli alert.
	static final int NOTIFICA = 0;
	static final int PROFILO_1 = 1;
	static final int PROFILO_2 = 2;
	static final int PROFILO_3 = 3;
	static final int ATTIVO = 4;

	// Raggio di azione in metri entro il quale attivare un profilo salvato.
	static final double distance = 10;
	
	// Creazione variabili per la gestione del GPS.
	LocationManager locationManager;
	LocationProvider gpsProvider;
	LocationListener locationListener;
	
	// Creazione oggetti per il controllo sulla mappa.
	LinearLayout linearLayout;
	MapView mapView;
	HelloItemizedOverlay itemizedoverlay;
	OverlayItem overlayitem;
	Drawable drawable;
	List<Overlay> mapOverlays;
	GeoPoint point;
	MapController mapController;
	
	// Variabili per la gestione delle tabelle nel database.
	int latitudinedb;
	int longitudinedb;
	String luogofromdb;
	int latfromdb;
	int lonfromdb;
	int profromdb;
	SilenceDatabase db;
	Cursor c;
	
	// Creazione oggetti per il controllo sull'audio e sulla vibrazione.
	AudioManager audioManager;
	
	@Override
	protected boolean isRouteDisplayed() {
	    return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        agg_intent = new Intent (this, Aggiungi.class);
        list_intent = new Intent (this, Lista.class);
        istr_intent = new Intent (this, Istruzioni.class);
        cred_intent = new Intent (this, Credits.class);
        
        db = new SilenceDatabase(this);
        
        // Funzioni per il controllo zoom sulla mappa.
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        // Settaggio overlay mappa.
        mapOverlays = mapView.getOverlays();
        drawable = this.getResources().getDrawable(R.drawable.androidmarker);
        itemizedoverlay = new HelloItemizedOverlay(drawable, this);
        
        // Settaggio del servizio per la gestione dell'audio e della vibrazione.
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
         
        // Recupero del servizio di locazione.
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        
        // Controllo sul provider GPS.
        gpsProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);    
        
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
    	Toast toast = Toast.makeText(this, "Usa il tasto MENU", Toast.LENGTH_SHORT);
        toast.show();
        
        // Se il GPS non è attivo, viene notificato all'utente.
        if (gpsProvider == null) {
        	showDialog(ATTIVO);
        }
        
        // Settaggio del listener che riceve tutti gli update provenienti dal location manager.
        locationListener = new LocationListener () {
        	 
        	// Notifica un cambio di stato nel provider in argomento.
        	public	void onStatusChanged(String	provider, int status, Bundle extras) {}
   
        	// Notifica che il provider in argomento è stato abilitato.
        	public	void onProviderEnabled(String provider) {}
        	
        	// Notifica che il provider in argomento è stato disabilitato.
        	public	void onProviderDisabled(String provider) {
        		showDialog(NOTIFICA);
        	}
        	
        	// Notifica la lettura di una nuova posizione.
        	public void onLocationChanged(Location location) {
        		
        		// Ricava latitudine e longitudine della locazione attuale.
        		Double geoLat = location.getLatitude()*1E6;
        		Double geoLng = location.getLongitude()*1E6;
        		
        		// Crea un nuovo geopoint relativo alla locazione attuale.
        		point = new GeoPoint(geoLat.intValue(), geoLng.intValue());
        		mapController = mapView.getController();
				mapController.animateTo(point); // Muove la mappa verso la locazione attuale...
				mapController.setZoom(18); // ...e setta il livello di zoom della mappa (valori tra 1 e 21).
				
				// Setta l'itemized overlay per la locazione attuale.
				itemizedoverlay.clearOverlay();
				overlayitem = new OverlayItem(point,"","");
				itemizedoverlay.addOverlay(overlayitem);
		        mapOverlays.add(itemizedoverlay);
		        
				
				// Memorizza latitudine e longitudine in variabili locali.
        		latitudinedb = geoLat.intValue();
        		longitudinedb = geoLng.intValue();
        		
        		checklocation(latitudinedb, longitudinedb);
        		Log.i("CHECK", "Chiamata funzione checklocation");
        		Log.i("Latitudine attuale",String.valueOf(latitudinedb));
        		Log.i("Longitudine attuale",String.valueOf(longitudinedb));
        	}
        	
        };
        
        /* Si ricevono letture dal GPS:
         * non più di una notifica ogni 5 secondi;
         * lo spostamento equivale ad almeno un metro.
         */
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 1, locationListener);
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.bottone1:
			agg_intent.putExtra("lat", latitudinedb);
			agg_intent.putExtra("long", longitudinedb);
			startActivity(agg_intent);
			return true;
		case R.id.bottone2:
			startActivity(list_intent);
			return true;
		case R.id.bottone3:
			startActivity(istr_intent);
			return true;
		case R.id.bottone4:
			startActivity(cred_intent);
			return true;
		default:
	        return super.onOptionsItemSelected(item);
		}
	}

	// Calcola la distanza in metri tra due punti espressi in coordinate.
	private double gps2m(double lat_a, double lng_a, double lat_b, double lng_b) {
		
		/* 
		 * Per il calcolo della distanza è stata utilizzata la formula:
		 * Great-circle distance, approssimazione formula di Vincenty
		 */
		
		double distance = 0;
		double earthRadius = 6372.8; // Raggio terrestre in km (raggio quadratico medio).
		double erm = earthRadius * 1000; // Raggio terrestre in metri.
		
		// Conversione da decimale a radianti.
		double lat1 = lat_a*Math.PI/180;
		double lat2 = lat_b*Math.PI/180;
		double lon1 = lng_a*Math.PI/180;
		double lon2 = lng_b*Math.PI/180;
		
		double dist_long = lon2 - lon1;
        double p1 = Math.pow(Math.cos(lat2)*Math.sin(dist_long), 2);
        double p2 = Math.pow(Math.cos(lat1)*Math.sin(lat2)-Math.sin(lat1)*Math.cos(lat2)*Math.cos(dist_long), 2);
        double a = p1 + p2;
        double b = Math.sin(lat1)*Math.sin(lat2)+Math.cos(lat1)*Math.cos(lat2)*Math.cos(dist_long);    
        double angle = Math.atan2(Math.sqrt(a), b);        
        distance = angle * erm;    
        return distance;
	}
		
	public void checklocation(int lat, int lon) {
		
		db.open();
        c = db.fetchAllLocation();
        startManagingCursor(c);
        
        Log.i("CHECK", "Funzione checklocation");
		Log.i("Latitudine attuale (int)",String.valueOf(lat));
		Log.i("Longitudine attuale(int)",String.valueOf(lon));
        
		double appdist;
		
        double fromlat = ((double)lat / 1E6);
        double fromlon = ((double)lon / 1E6);
        
        Log.i("Latitudine attuale (double)",String.valueOf(fromlat));
		Log.i("Longitudine attuale(double)",String.valueOf(fromlon));
		
		while(c.moveToNext()) { // Finchè ci sono righe  nel database...
			
			Log.i("Latitudine database (int)", String.valueOf(c.getInt(2)));
			Log.i("Longitudine database (int)", String.valueOf(c.getInt(3)));
			
			double tolat = ((double)c.getInt(2) / 1E6);
	        double tolon = ((double)c.getInt(3) / 1E6);
			
	        Log.i("Latitudine database (double)", String.valueOf(tolat));
			Log.i("Longitudine database (double)", String.valueOf(tolon));
			
			appdist = gps2m(fromlat, fromlon, tolat, tolon);
			
			Log.i("Distanza attuale", String.valueOf(appdist));
		
			//...se nel posto in cui mi trovo rientro nel raggio di azione di un luogo salvato...
			if(appdist <= distance) {
				luogofromdb = c.getString(1); // ...memorizza il luogo della riga...
				Log.i("Luogo memorizzato", luogofromdb);
				profromdb = c.getInt(4); // ...memorizza il profilo della riga.
				Log.i("Profilo memorizzato",String.valueOf(profromdb));
				setprofile(luogofromdb, profromdb);
			}
		}
		
		db.close();
		c.close();
	}
	
	public void setprofile(String l, int p) {
		if(p == R.id.ringvib) { // Attiva il Profilo Standard.
			audioManager.setVibrateSetting(1, 1); // Attiva vibrazione.
			audioManager.setRingerMode(2); // Suoneria normale -> RINGER_MODE_NORMAL.
			showDialog(PROFILO_1);
		}
		else if(p == R.id.mutevib) { // Attiva il Profilo Riunione.
			audioManager.setVibrateSetting(1, 1); // Attiva vibrazione.
			audioManager.setRingerMode(1); // Suoneria muta -> RINGER_MODE_VIBRATE.
			showDialog(PROFILO_2);
		}
		else if(p == R.id.mute) { // Attiva il Profilo Silenzioso.
			audioManager.setRingerMode(0); // Disattiva vibrazione - Suoneria muta -> RINGER_MODE_SILENT.
			showDialog(PROFILO_3);
		}
		
	}
	
	protected Dialog onCreateDialog(int id) {
        Dialog dialog;
        switch(id) {
        case PROFILO_1:
        	AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        	builder1.setMessage("Silence Please ha attivato il Profilo Standard per il luogo " + luogofromdb + ".");
        	builder1.setCancelable(false);
        	builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int id) {
        	              dialog.dismiss(); // Chiude la finestra di dialogo al click di "OK"
        	              }
        		});
        	dialog = builder1.create();
            break;
        case PROFILO_2:
        	AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        	builder2.setMessage("Silence Please ha attivato il Profilo Riunione per il luogo " + luogofromdb + ".");
        	builder2.setCancelable(false);
        	builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int id) {
        	              dialog.dismiss(); // Chiude la finestra di dialogo al click di "OK"
        	              }
        		});
        	dialog = builder2.create();
            break;
        case PROFILO_3:
        	AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
        	builder3.setMessage("Silence Please ha attivato il Profilo Silenzioso per il luogo " + luogofromdb + ".");
        	builder3.setCancelable(false);
        	builder3.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int id) {
        	              dialog.dismiss(); // Chiude la finestra di dialogo al click di "OK"
        	              }
        		});
        	dialog = builder3.create();
            break;
        case NOTIFICA:
        	AlertDialog.Builder builder4 = new AlertDialog.Builder(this);
        	builder4.setMessage("Il GPS risulta disabilitato. Controllare che il dispositivo di locazione GPS sia attivo.");
        	builder4.setCancelable(false);
        	builder4.setPositiveButton("Chiudi", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int id) {
        	              dialog.dismiss(); // Chiude la finestra di dialogo al click di "Chiudi"
        	              }
        		});
        	dialog = builder4.create();
            break;       
        case ATTIVO:
        	AlertDialog.Builder builder5 = new AlertDialog.Builder(this);
        	builder5.setMessage("GPS non disponibile. Controllare che il dispositivo di locazione GPS sia attivo.");
        	builder5.setCancelable(false);
        	builder5.setPositiveButton("Chiudi", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int id) {
        	              dialog.dismiss(); // Chiude la finestra di dialogo al click di "Chiudi"
        	              }
        		});
        	dialog = builder5.create();
            break;   
        default:
            dialog = null;
        }
        return dialog;
    }
}
