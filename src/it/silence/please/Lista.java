package it.silence.please;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class Lista extends ListActivity implements OnClickListener {
	
	Intent mod_intent;
	Intent main_intent;
	
	// Creazione oggetti per la gestione degli elementi del database.
	SilenceDatabase db;
	Cursor c;
	long rigatabella;
	
	@Override
	public void onCreate (Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        setContentView(R.layout.lista);
        
        mod_intent = new Intent (this, Modifica.class);
        main_intent = new Intent (this, SilencePleaseActivity.class);
        
        // Visualizza tutte le locazioni salvate nel database.
        db = new SilenceDatabase(this);
        db.open();
        c = db.fetchAllLocation();
        startManagingCursor(c);
        String[] from = new String[] {SilenceDatabase.LUO};
		int[] to = new int[] {R.id.elelista};
		SimpleCursorAdapter sc = new SimpleCursorAdapter(this, R.layout.elementilista, c, from, to);
		setListAdapter(sc);
		db.close();
		
		// Creazione button per tornare alla mappa.
		Button tornalist = (Button) findViewById(R.id.gomaplist);
		tornalist.setOnClickListener((OnClickListener)this);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		rigatabella = id; // Memorizza l'id della riga del database clickata (luogo).
		Log.i("Riga Database", String.valueOf(rigatabella));
		mod_intent.putExtra("riga", rigatabella);
		startActivity(mod_intent);
	}
	
	public void onClick(View v) {
			if (v.getId() == R.id.gomaplist) {
			// Torna alla mappa (interfaccia principale).
			startActivity(main_intent);
		}
	}
}
