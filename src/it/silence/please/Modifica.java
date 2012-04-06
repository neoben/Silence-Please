package it.silence.please;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class Modifica extends Activity implements OnClickListener {
	
	Intent mod_intent;
	Intent main_intent;
	
	// Creazione delle variabili per la gestione degli elementi presenti nel database.
	SilenceDatabase db;
	Cursor c;
	String nomeluogo;
	int latitudine;
	int longitudine;
	int profilo;
	int profilodb;
	String nomeluogodb;
	long rigatabella;
	int rig;
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modifica);
        
        main_intent = new Intent (this, SilencePleaseActivity.class);
        
        // Ricava la riga del database interessata.
        mod_intent = getIntent();
        rigatabella = mod_intent.getLongExtra("riga", -1);
        Log.i("Riga Database", String.valueOf(rigatabella));
        
        // Selezione la riga della database interessata.
        db = new SilenceDatabase(this);
        db.open();
        c = db.fetchLocation(rigatabella);
        startManagingCursor(c);
        db.close();
        
        // Memorizza i vari valori della riga del database interessata.
        nomeluogo = c.getString(1);
        latitudine = c.getInt(2);
        longitudine = c.getInt(3);
        profilo = c.getInt(4);
        
        rig = (int)rigatabella;
        
        // Creazione campo text per modificare il nome del luogo da associare ad un profilo d'uso.
        EditText nl = (EditText) findViewById(R.id.nomeluogo);
		nl.setText(nomeluogo);
		nl.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
		});
		
		// Creazione button/radio button per modificare un profilo d'uso salvato.
		RadioButton ringvibr1 = (RadioButton) findViewById(R.id.ringvib);
		ringvibr1.setOnClickListener((OnClickListener)this);
		RadioButton mutevibr1 = (RadioButton) findViewById(R.id.mutevib);
		mutevibr1.setOnClickListener((OnClickListener)this);
		RadioButton muteall1 = (RadioButton) findViewById(R.id.mute);
		muteall1.setOnClickListener((OnClickListener)this); 
		Button savemod = (Button) findViewById(R.id.savemod);
		savemod.setOnClickListener((OnClickListener)this);
		Button delete = (Button) findViewById(R.id.delete);
		delete.setOnClickListener((OnClickListener)this);
		
		// Creazione button per tornare alla mappa.
		Button torna = (Button) findViewById(R.id.go);
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
		if (v.getId() == R.id.savemod) {
			
			// Memorizza il nome del luogo (modificato o meno) da associare al profilo.
			EditText nluogo = (EditText) findViewById(R.id.nomeluogo);
			Editable nlg = nluogo.getText();
			String nlgapp = nlg.toString();
			nomeluogodb = nlgapp;
			
			if(profilodb == R.id.ringvib) { // Memorizza il Profilo Standard.
				
				// Modifica le informazioni nel database relative al luogo interessato.
				db.open();
				db.updateLocation(rig, nomeluogodb, latitudine, longitudine, profilo);
				db.close();

				Log.i("Luogo", nomeluogodb);
				Log.i("Profilo", "profilo 1");
			
				startActivity(main_intent);
			}
			else if(profilodb == R.id.mutevib) { // Memorizza il Profilo Riunione.
				
				// Modifica le informazioni nel database relative al luogo interessato.
				db.open();
				db.updateLocation(rig, nomeluogodb, latitudine, longitudine, profilo);
				db.close();

				Log.i("Luogo", nomeluogodb);
				Log.i("Profilo", "profilo 2");
			
				startActivity(main_intent);
			}
			else if(profilodb == R.id.mute) { // Memorizza il Profilo Silenzioso.
				
				// Modifica le informazioni nel database relative al luogo interessato.
				db.open();
				db.updateLocation(rig, nomeluogodb, latitudine, longitudine, profilo);
				db.close();

				Log.i("Luogo", nomeluogodb);
				Log.i("Profilo", "profilo 3");
			
				startActivity(main_intent);
			}
		
		}
		else if (v.getId() == R.id.delete) {
			
			// Cancella tutte le informazioni relative al luogo interessato.
			db.open();
			db.deleteLocations(rig);
			db.close();
			
			startActivity(main_intent);
		}
		else if (v.getId() == R.id.go) {
			
			// Torna alla mappa (interfaccia principale).
			startActivity(main_intent);
		}
	}
}
