package edu.miami.c08159659.phlogit;

import android.provider.BaseColumns;

public final class PhlogEntryContract {
	private PhlogEntryContract() {};
	
	 public static abstract class PhlogEntry implements BaseColumns {
	        public static final String TABLE_NAME = "entry";
	        public static final String COLUMN_NAME_TITLE = "title";
	        public static final String COLUMN_NAME_CREATED_AT = "created_at";
	        public static final String COLUMN_NAME_TEXT = "text";
	        public static final String COLUMN_NAME_PHOTO_URI = "photo_uri";
	        public static final String COLUMN_NAME_LOCATION = "location";
	        public static final String COLUMN_NAME_ORIENTATION = "orientation";
	        /*TODO
	         * 	Add:
	         *	Geodecoded location
	         *	Multiple photos
	         *	Pictures selected from the gallery
	         *	Video recording(s)
	         *	Voice recording(s)
	         *	Links to relevant media (songs, gallery photos and videos)
	         *	Links to relevant contacts
	         *	An ambient light reading
	         *	Information about movement during the creation of the entry 
			*/
	    }
}
