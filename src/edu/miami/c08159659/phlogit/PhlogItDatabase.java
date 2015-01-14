package edu.miami.c08159659.phlogit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import edu.miami.c08159659.phlogit.PhlogEntryContract.PhlogEntry;

//TODO add contentprovider?

public class PhlogItDatabase {
	private static PhlogItDatabase databaseInstance;
	private PhlogItDatabaseHelper dbHelper;
	private SQLiteDatabase db;
	
	public static PhlogItDatabase getInstance(Context context) {
		if (databaseInstance == null) {
			databaseInstance = new PhlogItDatabase(context.getApplicationContext());
		}
		return databaseInstance;
	}
	
	private PhlogItDatabase(Context context) {
		dbHelper = new PhlogItDatabaseHelper(context);
		db = dbHelper.getWritableDatabase();
	}
	
	public Cursor getEntry(long id) {
        String[] fieldNames = {
        		PhlogEntry._ID,
        		PhlogEntry.COLUMN_NAME_TITLE,
        		PhlogEntry.COLUMN_NAME_TEXT,
        		PhlogEntry.COLUMN_NAME_PHOTO_URI,
        		PhlogEntry.COLUMN_NAME_CREATED_AT,
        		PhlogEntry.COLUMN_NAME_LOCATION,
        		PhlogEntry.COLUMN_NAME_ORIENTATION
        		};
        String[] whereArgs = {String.valueOf(id)};
        return(db.query(PhlogEntry.TABLE_NAME,fieldNames,PhlogEntry._ID + " = ?",whereArgs,null,null,
        		PhlogEntry._ID));
	}
	
	public Cursor getEntrys() {
        String[] fieldNames = {
        		PhlogEntry._ID,
        		PhlogEntry.COLUMN_NAME_TITLE,
        		PhlogEntry.COLUMN_NAME_PHOTO_URI
        		};
        return(db.query(PhlogEntry.TABLE_NAME,fieldNames,null,null,null,null,
        		PhlogEntry._ID));
	}
	
	public boolean addEntry(String title, String text, Uri photo, String latLong, String orientation) {
        ContentValues entryData = new ContentValues();
        entryData.put(PhlogEntry.COLUMN_NAME_TITLE, title);
        entryData.put(PhlogEntry.COLUMN_NAME_CREATED_AT, System.currentTimeMillis());
        entryData.put(PhlogEntry.COLUMN_NAME_TEXT, text);
        entryData.put(PhlogEntry.COLUMN_NAME_LOCATION, latLong);
        entryData.put(PhlogEntry.COLUMN_NAME_ORIENTATION, orientation);
        if (photo != null)
        	entryData.put(PhlogEntry.COLUMN_NAME_PHOTO_URI, photo.toString());
        return(db.insert(PhlogEntry.TABLE_NAME, null, entryData) == 1);
	}
	
	public boolean deleteEntry(long id) {
		String[] whereArgs = {String.valueOf(id)};
        return(db.delete(PhlogEntry.TABLE_NAME, PhlogEntry._ID + " = ?",whereArgs) == 1);
	}
	
	private class PhlogItDatabaseHelper extends SQLiteOpenHelper {
		private static final String DATABASE_NAME = "PhlogIt.db";
		private static final int DATABASE_VERSION = 1;
		
		private static final String TEXT_DATA_TYPE = " TEXT";
		private static final String COMMA_SEP = ", ";
		private static final String SQL_CREATE_ENTRY_TABLE =
				"CREATE TABLE " + PhlogEntry.TABLE_NAME + " (" + 
				PhlogEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
				PhlogEntry.COLUMN_NAME_TITLE + TEXT_DATA_TYPE + COMMA_SEP +
				PhlogEntry.COLUMN_NAME_CREATED_AT + " NUMERIC" + COMMA_SEP +
				PhlogEntry.COLUMN_NAME_TEXT + TEXT_DATA_TYPE + COMMA_SEP +
				PhlogEntry.COLUMN_NAME_PHOTO_URI + TEXT_DATA_TYPE + COMMA_SEP +
				PhlogEntry.COLUMN_NAME_LOCATION + TEXT_DATA_TYPE + COMMA_SEP +
				PhlogEntry.COLUMN_NAME_ORIENTATION + 
				")";
		private static final String SQL_DELETE_ENTRY_TABLE = 
				"DROP TABLE IF EXISTS " + PhlogEntry.TABLE_NAME;
		
		public PhlogItDatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(SQL_CREATE_ENTRY_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL(SQL_DELETE_ENTRY_TABLE);
			onCreate(db);
		}

		@Override
		public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			onUpgrade(db, oldVersion, newVersion);
		}
	}
}