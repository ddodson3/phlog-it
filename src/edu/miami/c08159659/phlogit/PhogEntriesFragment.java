package edu.miami.c08159659.phlogit;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import edu.miami.c08159659.phlogit.PhlogEntryContract.PhlogEntry;

public class PhogEntriesFragment extends ListFragment {
	private static final int THUMBNAIL_SIZE = 256;
	
	
	
	SimpleCursorAdapter adapter;
	PhlogItDatabase db;
	Cursor cursor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//TODO create progress bar?
		
		String[] from = {PhlogEntry.COLUMN_NAME_TITLE, PhlogEntry.COLUMN_NAME_PHOTO_URI};
		int[] to = {R.id.list_title, R.id.list_thumbnail};
		db = PhlogItDatabase.getInstance(getActivity());
		cursor = db.getEntrys();
		//TODO fix cursor loading, make it update when changed		
		adapter = new SimpleCursorAdapter(this.getActivity(), R.layout.entry_list_row, cursor, from, to, 0);
		adapter.setViewBinder(viewBinder);
		setListAdapter(adapter);
		
	}
	
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent nextIntent = new Intent(getActivity(), ViewEntry.class);
		nextIntent.putExtra(ViewEntry.ID, id);
		startActivity(nextIntent);
	}
	
	SimpleCursorAdapter.ViewBinder viewBinder = new SimpleCursorAdapter.ViewBinder() {

		
		@Override
		public boolean setViewValue(View view, Cursor cursor, int colIndex) {
			ImageView photoView;
			if (cursor.getColumnIndex(PhlogEntry.COLUMN_NAME_PHOTO_URI) == colIndex && cursor.getString(colIndex) != null) {
				Bitmap photo = getPreview(URI.create(cursor.getString(colIndex)));
				photoView = (ImageView)view;
				photoView.setImageBitmap(photo);
				return true;
			}
				return false;
		}
		
	};
	
	private Bitmap getPreview(URI uri) {
		
	    File image = new File(uri);

	    BitmapFactory.Options bounds = new BitmapFactory.Options();
	    bounds.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(image.getPath(), bounds);
	    if ((bounds.outWidth == -1) || (bounds.outHeight == -1))
	        return null;

	    int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight
	            : bounds.outWidth;

	    BitmapFactory.Options opts = new BitmapFactory.Options();
	    opts.inSampleSize = originalSize / THUMBNAIL_SIZE;
	    
	    return BitmapFactory.decodeFile(image.getPath(), opts);     
	}
	
	public void onResume() {
		super.onResume();
		cursor.requery();
	}
	
	@Override
	public void onDestroy() {
		if (cursor != null)
			cursor.close();
		super.onDestroy();
	}
}
