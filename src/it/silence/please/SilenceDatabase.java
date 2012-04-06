package it.silence.please;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SilenceDatabase {
	
	// Stringhe elementi della tabella.
	public static final String KEY_ROWID = "_id";
	public static final String LAT = "latitudine";
	public static final String LON = "longitudine";
	public static final String PRO = "profilo";
	public static final String LUO = "luogo";

	// Stringhe nomi del database.	
	private static final String DATABASE_TABLE = "location";
	
	// Oggetti per la gestione del database.
	private static final String TAG = "LocationTable";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mCtx;
	
	public SilenceDatabase (Context ctx) {
	    this.mCtx = ctx;
	  }
	
	// Apre la connessione al database.
	public SilenceDatabase open() throws SQLException {
	    mDbHelper = new DatabaseHelper(mCtx);
	    mDb = mDbHelper.getWritableDatabase();
	    Log.i(TAG, "Apro la connessione al Database....");
	    return this;
	  }
	
	// Chiude la connessione al database.
	public void close() {
	    mDbHelper.close();
	  }
	
	// Crea una tabella del database.
	public long createLocation(String luog, int lati, int longi, int prof) {
	    Log.i(TAG, "Inserting record...");
	    ContentValues initialValues = new ContentValues();
	    initialValues.put(LUO, luog);
	    initialValues.put(LAT, lati);
	    initialValues.put(LON, longi);
	    initialValues.put(PRO, prof);

	    return mDb.insert(DATABASE_TABLE, null, initialValues);
	  }
	
	// Cancella una tabella del database.
	public boolean deleteLocations(long rowId) {
		 
	    return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	  }
	
	// Trova tutte le tabelle del database.
	public Cursor fetchAllLocation() {
		 
	    return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, LUO, LAT, LON, PRO},null, null, null, null, null);
	  }
	
	// Trova una particolare tabella del databse.
	public Cursor fetchLocation(long locId) throws SQLException {
		 
	    Cursor mCursor =
	 
	      mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, LUO, LAT, LON, PRO}, KEY_ROWID + "=" + locId, null,
	          null, null, null, null);
	    if (mCursor != null) {
	      mCursor.moveToFirst();
	    }
	    return mCursor;
	}
	
	// Aggiorna una particolare tabella del database.
	public boolean updateLocation(int locId, String luog, int lati, int longi, int prof) {
	    ContentValues args = new ContentValues();
	    args.put(LUO, luog);
	    args.put(LAT, lati);
	    args.put(LON, longi);
	    args.put(PRO, prof);
	 
	    return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + locId, null) > 0;
	  }
}
