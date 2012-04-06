package it.silence.please;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class Aggiungi extends Activity implements OnClickListener {
	
	Intent agg_intent;
	Intent main_intent;

	// Creazione variabili per la gestione degli elementi presenti nel database.
	SilenceDatabase db;
	int lati;
	int longi;
	int profilodb;
	String luogodb;

	// Creazione oggetti per il controllo sull'audio e sulla vibrazione.
	AudioManager audioManager;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aggiungi);
     
        main_intent = new Intent (this, SilencePleaseActivity.class);
        
        // Ricava latitudine e longitudine recuperate dal GPS.
        agg_intent = getIntent();
        lati = agg_intent.getIntExtra("lat",-1);
        longi = agg_intent.getIntExtra("long", -1);
        
        Log.i("Latitudine", String.valueOf(lati));
        Log.i("Longitudine", String.valueOf(longi));
        
    	// Settaggio del servizio per la gestione dell'audio e della vibrazione.
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        
        // Creazione istanza del database.
        db = new SilenceDatabase(this);
        db.open();
        Log.i("Database", "Chiamata open");
        db.close();
        Log.i("Database", "Chiamata close");
        
        // Creazione campo text per settare il nome del luogo da associare ad un profilo d'uso.
		EditText nl = (EditText) findViewById(R.id.nomeluogo);
		nl.setText("Nome Luogo...");
		nl.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
		});
		
		// Creazione button/radio button per settare un profilo d'uso.
		RadioButton ringvibr1 = (RadioButton) findViewById(R.id.ringvib);
		ringvibr1.setOnClickListener((OnClickListener)this);
		RadioButton mutevibr1 = (RadioButton) findViewById(R.id.mutevib);
		mutevibr1.setOnClickListener((OnClickListener)this);
		RadioButton muteall1 = (RadioButton) findViewById(R.id.mute);
		muteall1.setOnClickListener((OnClickListener)this); 
		Button savepro = (Button) findViewById(R.id.save);
		savepro.setOnClickListener((OnClickListener)this);  
		
		// Creazione button per tornare alla mappa.
		Button torna = (Button) findViewById(R.id.gomap);
		torna.setOnClickListener((OnClickListener)this);
	}
	
	public void onClick(View v) { 
		
		if (v.getId() == R.id.ringvib) {
			if (((RadioButton) v).isChecked()) {
				profilodb = R.id.ringvib;
			}
		}
		else if (v.getId() == R.id.mutevib) {
			if (((RadioButton) v).isChecked()) {
				profilodb = R.id.mutevib;
			}
		}
		else if (v.getId() == R.id.mute) {
			if (((RadioButton) v).isChecked()) {
				profilodb = R.id.mute;
			}
		}
		if (v.getId() == R.id.save) {
			
			// Memorizza il nome del luogo da associare al profilo.
			EditText nluogo = (EditText) findViewById(R.id.nomeluogo);
			Editable nlg = nluogo.getText();
			String nlgapp = nlg.toString();
			luogodb = nlgapp;
			
			if(profilodb == R.id.ringvib) { // Memorizza il Profilo Standard.
				audioManager.setVibrateSetting(1, 1); // Attiva vibrazione.
				audioManager.setRingerMode(2); // Suoneria normale -> RINGER_MODE_NORMAL.
				
				// Creazione nel database della tabella relativa alla locazione da aggiungere.
				db.open();
				db.createLocation(luogodb, lati, longi, profilodb);
				db.close();
			
				Log.i("Luogo", luogodb);
				Log.i("Profilo", "profilo 1");
				
				startActivity(main_intent);
			}
			else if(profilodb == R.id.mutevib) { // Memorizza il Profilo Riunione.
				audioManager.setVibrateSetting(1, 1); // Attiva vibrazione.
				audioManager.setRingerMode(1); // Suoneria muta -> RINGER_MODE_VIBRATE.
				
				// Creazione nel database della tabella relativa alla locazione da aggiungere.
				db.open();
				db.createLocation(luogodb, lati, longi, profilodb);
				db.close();

				Log.i("Luogo", luogodb);
				Log.i("Profilo", "profilo 2");
				
				startActivity(main_intent);
			}
			else if(profilodb == R.id.mute) { // Memorizza il Profilo Silenzioso.
				audioManager.setRingerMode(0); // Disattiva vibrazione - Suoneria muta -> RINGER_MODE_SILENT.
				
				// Creazione nel database della tabella relativa alla locazione da aggiungere.
				db.open();
				db.createLocation(luogodb, lati, longi, profilodb);
				db.close();

				Log.i("Luogo", luogodb);
				Log.i("Profilo", "profilo 3");
				
				startActivity(main_intent);
			}
		}
		else if (v.getId() == R.id.gomap) {
			
			// Torna alla mappa (interfaccia principale).
			startActivity(main_intent);
		}
	}
}
	