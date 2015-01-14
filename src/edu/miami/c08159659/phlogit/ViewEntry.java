package edu.miami.c08159659.phlogit;

import java.text.SimpleDateFormat;
import java.util.Date;

import edu.miami.c08159659.phlogit.PhlogEntryContract.PhlogEntry;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewEntry extends ActionBarActivity {
	public static final String ID = "edu.miami.c08159659.phlogit.Id";
	private PhlogItDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_entry);
		db = PhlogItDatabase.getInstance(this);
		Cursor c = db.getEntry(getIntent().getLongExtra(ID, -1));
		Log.i("results", String.valueOf(c.getCount()));
		if (c.moveToFirst()) {
			TextView title = (TextView)findViewById(R.id.entry_title);
			int titleCol = c.getColumnIndex(PhlogEntry.COLUMN_NAME_TITLE);
			TextView text = (TextView)findViewById(R.id.entry_text);
			int textCol = c.getColumnIndex(PhlogEntry.COLUMN_NAME_TEXT);
			TextView createdAt = (TextView)findViewById(R.id.entry_created_at);
			int createdAtCol = c.getColumnIndex(PhlogEntry.COLUMN_NAME_CREATED_AT);
			int uriCol = c.getColumnIndex(PhlogEntry.COLUMN_NAME_PHOTO_URI);
			TextView location = (TextView)findViewById(R.id.entry_location);
			int locationCol = c.getColumnIndex(PhlogEntry.COLUMN_NAME_LOCATION);
			TextView orientation = (TextView)findViewById(R.id.entry_orientation);
			int orientationCol = c.getColumnIndex(PhlogEntry.COLUMN_NAME_ORIENTATION);
		
			title.setText(c.getString(titleCol));
			text.setText(c.getString(textCol));
			setThumbnail(Uri.parse(c.getString(uriCol)));
			Date date = new Date(Long.valueOf(c.getString(createdAtCol)));
			SimpleDateFormat dateFormat= new SimpleDateFormat("MM-dd-yyyy HH:mm");
			createdAt.setText(dateFormat.format(date));
			location.setText(c.getString(locationCol));
			orientation.setText(c.getString(orientationCol));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_entry, menu);
		return true;
	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_delete_entry:
			db.deleteEntry(getIntent().getLongExtra(ID, -1));
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		ImageView thumb = (ImageView)findViewById(R.id.entry_thumb);
		thumb.setImageURI(null);
		super.onDestroy();
	}
	
	private void setThumbnail(Uri photo) {
		ImageView thumb = (ImageView)findViewById(R.id.entry_thumb);
			thumb.setImageURI(photo);
	}
	
	
}
