package edu.miami.c08159659.phlogit;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class AddEntry extends ActionBarActivity implements LocationListener, SensorEventListener  {
	private static final int IMAGE_CAPTURE = 1;
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	private PhlogItDatabase db;
	private Uri photoUri;
	private LocationManager locationManager;
	private SensorManager sensorManager;
	private Location bestLocation;
	private float[] orientation = new float[3];
	
	//TODO better bitmap handling
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_entry);
		db = PhlogItDatabase.getInstance(this);
		locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
	}
	
	@Override
	protected void onResume() {
		List<String> locators;
        locators = locationManager.getProviders(true);
        if (locators.contains(LocationManager.GPS_PROVIDER)) {
        	locationManager.requestLocationUpdates(
        			LocationManager.GPS_PROVIDER,getResources().getInteger(
        			R.integer.time_between_location_updates_ms),0,this);
        } 
        if (locators.contains(LocationManager.NETWORK_PROVIDER)) {
        	locationManager.requestLocationUpdates(
        			LocationManager.NETWORK_PROVIDER,getResources().getInteger(
        			R.integer.time_between_location_updates_ms),0,this);
        }
        if (!locators.contains(LocationManager.GPS_PROVIDER) && !locators.contains(LocationManager.NETWORK_PROVIDER))
        	Toast.makeText(this,"No location available",Toast.LENGTH_LONG).show();
        
        if (!startSensor(Sensor.TYPE_ORIENTATION))
        	Toast.makeText(this,"No orientation available",Toast.LENGTH_LONG).show();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		locationManager.removeUpdates(this);
        sensorManager.unregisterListener(this);
		super.onPause();
	}
	
	private boolean startSensor(int sensorType) {
        
        if (sensorManager.getSensorList(sensorType).isEmpty()) {
            return(false);
        } else {
            sensorManager.registerListener(this,
            		sensorManager.getDefaultSensor(sensorType),SensorManager.SENSOR_DELAY_NORMAL);
            return(true);
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_entry, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		EditText title;
		EditText text;
		switch(item.getItemId()) {
		case R.id.action_save_entry:
			//TODO check for required entries
			title = (EditText)findViewById(R.id.entry_title);
			text = (EditText)findViewById(R.id.entry_text);
			db.addEntry(title.getText().toString(), 
					text.getText().toString(), 
					photoUri, 
					bestLocation.getLatitude() + ", " + bestLocation.getLongitude(), 
					orientation[0] + " " + orientation[1] + " " + orientation[2]);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void takePicture(View view) {
		Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (pictureIntent.resolveActivity(getPackageManager()) != null) {
			File photoFile = null;
	        try {
	            photoFile = createImageFile();
	        } catch (IOException ex) {
	        }
	        if (photoFile != null) {
	            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
	                    Uri.fromFile(photoFile));
	            startActivityForResult(pictureIntent, IMAGE_CAPTURE);
	        }
		} else {
			Toast.makeText(this, R.string.cannot_get_camera, Toast.LENGTH_LONG).show();
		}
	}
	
	private File createImageFile() throws IOException {
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "JPEG_" + timeStamp + "_";
	    File storageDir = Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_PICTURES);
	    File image = File.createTempFile(
	        imageFileName,
	        ".jpg",
	        storageDir
	    );
	    photoUri = Uri.fromFile(image);
	    return image;
	}
	
	private void setThumbnail(Uri photo) {
		ImageView thumb = (ImageView)findViewById(R.id.entry_thumb);
		ImageButton cameraButton = (ImageButton)findViewById(R.id.take_photo);
		thumb.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				takePicture(null);
				return true;
			}
		});
			thumb.setImageURI(photo);
			cameraButton.setVisibility(ImageButton.INVISIBLE);
	}
	// TODO Auto-generated method stub
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch(requestCode) {
			case IMAGE_CAPTURE:
				setThumbnail(photoUri);
				break;
			}
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		if (isBetterLocation(location, bestLocation)) {
			bestLocation = location;
			Log.i("locationAccuracy", String.valueOf(location.getAccuracy()));
			Log.i("locationLatLong", String.valueOf(location.getLatitude()) + " " +String.valueOf(location.getLongitude()));
			Log.i("locationProvider", location.getProvider());
		}
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	    	return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (arrayCopyChangeTest(event.values, orientation, 3, 5.0f))
			System.arraycopy(event.values, 0, orientation, 0, 3);
	}

	private boolean arrayCopyChangeTest(float[] from,float[] to,int length,
			float amountForChange) {
			        
			        int copyIndex;
			        boolean changed = false;
			        
			        for (copyIndex=0;copyIndex<length;copyIndex++) {
			            if (Math.abs(from[copyIndex] - to[copyIndex]) > amountForChange) {
			                to[copyIndex] = from[copyIndex];
			                changed = true;
			            }
			        }
			        return(changed);
			    }
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}
}
