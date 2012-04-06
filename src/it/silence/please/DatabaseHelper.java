package it.silence.please;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper  {

	// Stringhe elementi della tabella.
	public static final String KEY_ROWID = "_id";
	public static final String LAT = "latitudine";
	public static final String LON = "longitudine";
	public static final String PRO = "profilo";
	public static final String LUO = "luogo";

	
	private static final String DATABASE_NAME = "location_database";	
	private static final String DATABASE_TABLE = "location";
	private static final int DATABASE_VERSION = 1;
	
	private static final String TAG = "LocationTable";
	
	// Stringa per la creazione del database.
	private static final String DATABASE_CREATE = 
		"create table " + DATABASE_TABLE + " (" 
		+ KEY_ROWID + " integer primary key autoincrement, "
	    + LUO + " text, " 
	    + LAT + " integer, " 
	    + LON + " integer, " 
	    + PRO + " integer);";
	
	DatabaseHelper(Context context) {
	      super(context, DATABASE_NAME, null, DATABASE_VERSION);
	      Log.i(TAG, "Costruttore DBhelper");
	}
	
	@Override
    public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "Creo il DataBase: " + DATABASE_CREATE);
		db.execSQL(DATABASE_CREATE);
    }
	
	@Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Aggiorno il Database dalla versione " + oldVersion + " alla "
          + newVersion + ", verranno distrutti tutti i vecchi dati");
		db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
		onCreate(db);
    }
}

