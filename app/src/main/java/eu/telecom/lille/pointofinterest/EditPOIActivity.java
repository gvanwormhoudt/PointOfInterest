package eu.telecom.lille.pointofinterest;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class EditPOIActivity extends ActionBarActivity implements OnClickListener {

	private static final String DATA_POI_KEY = "data_poi";

	private static final String tag = ActionBarActivity.class.getName();
	private static final int REQUEST_IMAGE_CAPTURE = 1;

	EditText labelEditText;
	EditText descriptionEditText;
	TextView positionTextView;

	Button addPOIBtn;
	Button setImageBtn;
	RatingBar scoreBar;

	ImageView imagePOIView;
	String imagePOIPath;

	Location location;
	LocationManager locationManager;
	LocationListener locationListener;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_poi);
		
		initLocationManager();
		if (!isGPSEnabled()) {
			Toast.makeText(this, "Please enable GPS", Toast.LENGTH_LONG).show();
			finish();
		} 
		
		labelEditText = (EditText) findViewById(R.id.labelEditText);
		descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
		positionTextView = (TextView) findViewById(R.id.positionTextView);
		scoreBar = (RatingBar) findViewById(R.id.poiScoreBar);
		
		addPOIBtn = (Button) findViewById(R.id.addPOIBtn);
		addPOIBtn.setOnClickListener(this);

		setImageBtn = (Button) findViewById(R.id.imageSetBtn);
		setImageBtn.setOnClickListener(this);

		imagePOIView = (ImageView) findViewById(R.id.imagePOIView);
	}
	
	/* Request updates at startup */
	  @Override
	  protected void onResume() {
	    super.onResume();
	    registerGPSListener();
	  }

	  /* Remove the locationlistener updates when Activity is paused */
	  @Override
	  protected void onPause() {
	    super.onPause();
	    unregisterGPSListener();
	  }


	
	protected void registerGPSListener() {
		locationListener = new MyLocationListener();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 0.0F, locationListener);
		// locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 400, 0.0F, locationListener);
	}
	
	protected void unregisterGPSListener() {
		locationManager.removeUpdates(locationListener);
	}
	
	
	
	protected void initLocationManager() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	}
	
	protected boolean isGPSEnabled() {
		 // boolean result =  locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		 boolean result =  locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		 return result;
	}
	

	@Override
	public void onClick(View view) {
		if (view == addPOIBtn) {
			addPOIClick();
		}
		else {
			imageSetClick();
		}

	}

	protected void imageSetClick() {
		Intent imagePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (imagePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			File imagePOIFile = null;
			try {
				imagePOIFile = createImageFile();
			} catch (IOException ex) {

			}
			// Continue only if the File was successfully created
			if (imagePOIFile != null) {
				Uri imagePOIUri = FileProvider.getUriForFile(this,
						"com.example.android.fileprovider",
						imagePOIFile);
				imagePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imagePOIUri);
				startActivityForResult(imagePictureIntent, REQUEST_IMAGE_CAPTURE);
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE) { //  && resultCode == RESULT_OK) {
			// Get the dimensions of the View
			int targetW = imagePOIView.getWidth();
			int targetH = imagePOIView.getHeight();

			// Get the dimensions of the bitmap
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inJustDecodeBounds = true;
			int photoW = bmOptions.outWidth;
			int photoH = bmOptions.outHeight;

			// Determine how much to scale down the image
			int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

			// Decode the image file into a Bitmap sized to fill the View
			bmOptions.inJustDecodeBounds = false;
			bmOptions.inSampleSize = scaleFactor;

			Bitmap imagePOIBitmap = BitmapFactory.decodeFile(imagePOIPath, bmOptions);
			imagePOIView.setImageBitmap(imagePOIBitmap);
		}
	}



	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		// File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		File storageDir = new File(getCacheDir(), "images");
		Log.i("tag", storageDir.getAbsolutePath());
		// File image = File.createTempFile(imageFileName, ".jpg", storageDir);
		File image = new File(storageDir, imageFileName + ".jpg");


		// Save a file: path for use with ACTION_VIEW intents
		imagePOIPath = image.getAbsolutePath();
		return image;
	}

	protected void addPOIClick() {
		Intent intent = new Intent();
		String label = labelEditText.getText().toString();
		if (label.isEmpty()) {
			Toast.makeText(this, "Label must not be empty", Toast.LENGTH_SHORT).show();
			return;
		}
		String description = descriptionEditText.getText().toString();
		if (description.isEmpty()) {
			Toast.makeText(this, "Description must not be empty", Toast.LENGTH_SHORT).show();
			return;
		}
		if (location == null) {
			Toast.makeText(this, "Current Position not found", Toast.LENGTH_SHORT).show();
			return;
		}

		PointOfInterest poi = new PointOfInterest(label, description, location.getLongitude(), location.getLatitude(), scoreBar.getProgress(), new Date(), imagePOIPath);
		intent.putExtra(DATA_POI_KEY, poi);
		setResult(RESULT_OK, intent);
		finish();
	}

	protected void updatePositionStr() {
		String positionStr = "Unknow position";
		if (location != null) {
 			positionStr = "Lat:" + Double.toString(location.getLatitude()) + "," + 
 						"Long:" + Double.toString(location.getLongitude());
		}
		positionTextView.setText(positionStr);
	}
	
	class MyLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location loc) {
			location = loc;
			Log.d(tag, "Latitude:" + Double.toString(loc.getLatitude()));
	        Log.d(tag, "Longitude:" + Double.toString(loc.getLongitude()));
	        updatePositionStr();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}

		@Override
		  public void onProviderEnabled(String provider) {
		    Toast.makeText(EditPOIActivity.this, "Enabled new provider " + provider,
		        Toast.LENGTH_SHORT).show();

		  }

		  @Override
		  public void onProviderDisabled(String provider) {
		    Toast.makeText(EditPOIActivity.this, "Disabled provider " + provider,
		        Toast.LENGTH_SHORT).show();
		  }
	}
}
