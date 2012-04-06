package it.silence.please;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Istruzioni extends Activity implements OnClickListener {
	
	Intent main_intent;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.istruzioni);
        
        main_intent = new Intent (this, SilencePleaseActivity.class);
        
        // Creazione button per tornare alla mappa.
		Button tornaistr = (Button) findViewById(R.id.gomapistr);
		tornaistr.setOnClickListener((OnClickListener)this);
	}
	
	public void onClick(View v) {
		if (v.getId() == R.id.gomapistr) {
		// Torna alla mappa (interfaccia principale).
		startActivity(main_intent);
		}
	}

}
