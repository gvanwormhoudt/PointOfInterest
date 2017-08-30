package eu.telecom.lille.pointofinterest;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends ListActivity  {


    static final String POI_FILENAME = "pois";

	private static final int EDIT_POI_RESULT = 1;

	private static final String DATA_POI_KEY = "data_poi";

    protected List<PointOfInterest> pointOfInterest = new LinkedList<PointOfInterest>();
	protected ArrayAdapter<PointOfInterest> pointOfInterestAdapter;
    protected ListView pointOfInterestListView;


	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadPOI();

        // fill adapter

        pointOfInterestAdapter = new ArrayAdapter<PointOfInterest>(this, android.R.layout.simple_list_item_single_choice, pointOfInterest);
        pointOfInterestListView = (ListView) findViewById(android.R.id.list);
        pointOfInterestListView.setAdapter(pointOfInterestAdapter);

        // pointOfInterestListView.setOnItemClickListener(this);
        // pointOfInterestAdapter.add(new PointOfInterest("Contis plage", "", 0, 0, 5, new Date(), null));
        // pointOfInterestAdapter.add(new PointOfInterest("Cliff of moher", "", 0, 0, 5, new Date(), null));
        // pointOfInterestAdapter.add(new PointOfInterest("Museum of modern art, NY", "", 0, 0, 4, new Date(), null));

    }

    @Override
    protected void onStop() {
        super.onStop();

        savePOI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	super.onPrepareOptionsMenu(menu);
    	
    	boolean visible = this.pointOfInterestListView.getCheckedItemPosition() != AdapterView.INVALID_POSITION;
    	menu.findItem(R.id.removePOI).setVisible(visible);
    	menu.findItem(R.id.openGMap).setVisible(visible);
    	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        int itemPosition; 
        PointOfInterest poi = null;
        switch(id) {
        case R.id.addPOI:
        	Intent intent = new Intent(MainActivity.this,
					EditPOIActivity.class);
			startActivityForResult(intent, EDIT_POI_RESULT);
        	return true; 
        	
        case R.id.removePOI:
        	itemPosition = this.pointOfInterestListView.getCheckedItemPosition();
        	poi = this.pointOfInterestAdapter.getItem(itemPosition);
        	this.pointOfInterestAdapter.remove(poi);
        	return true;
        	
        case R.id.openGMap:
        	itemPosition = this.pointOfInterestListView.getCheckedItemPosition();
        	poi = this.pointOfInterestAdapter.getItem(itemPosition);
        	Intent geoIntent = new Intent(android.content.Intent.ACTION_VIEW,
        			Uri.parse("geo:" + poi.getLattitude() + "," + poi.getLongitude()));
        	startActivity(geoIntent);
         
        	return true;
        	
        default:
        	return super.onOptionsItemSelected(item);
        }
    }

	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
	        case EDIT_POI_RESULT:
	            if (resultCode == Activity.RESULT_OK) {
	            	PointOfInterest newPOI = (PointOfInterest) data.getSerializableExtra(DATA_POI_KEY);
	            	pointOfInterestAdapter.add(newPOI);
	            }  
        }
    }


    final static String filenamePOI = "poi-objects";

    protected void savePOI() {
        FileOutputStream fileOutputStream;
        ObjectOutputStream objectOutputStream;

        try {
            fileOutputStream = openFileOutput(filenamePOI, Context.MODE_PRIVATE);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(pointOfInterest);
            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    projected void loadPOI() {
        FileInputStream fileInputStream;
        ObjectInputStream objectInputStream;

        try {
            fileInputStream = openFileInput(filenamePOI,Context.MODE_PRIVATE);
            objectInputStream = new ObjectInputStream(fileInputStream);
            List<PointOfInterest> pois = objectInputStream.readObject();
            objectInputStream.close();
            pointOfInterest.addAll(pois);
        } catch (Exception e) {

        }
    }


    /* @Override
    protected void onDestroy() {
        super.onDestroy();
        FileOutputStream fos = context.openFileOutput(POI_FILENAME, Context.MODE_PRIVATE);
        ObjectOutputStream os = new ObjectOutputStream(fos);
        List<PointOfInterest> pois = new LinkedList<PointOfInterest>();
        int count = pointOfInterestAdapter.getCount();
        for (int i = 0; i < coun; i++) {
            pois.add(pointOfInterestAdapter.getItem(i);
        }
        os.writeObject(pois);
        os.close();
    } */

    /*
    import android.content.Context;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ArrayAdapter;
    import android.widget.ImageView;
    import android.widget.TextView;

    public class MySimpleArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final String[] values;

        public MySimpleArrayAdapter(Context context, String[] values) {
            super(context, -1, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.label);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
            textView.setText(values[position]);
            // change the icon for Windows and iPhone
            String s = values[position];
            if (s.startsWith("iPhone")) {
                imageView.setImageResource(R.drawable.no);
            } else {
                imageView.setImageResource(R.drawable.ok);
            }

            return rowView;
        }
    } */
}
