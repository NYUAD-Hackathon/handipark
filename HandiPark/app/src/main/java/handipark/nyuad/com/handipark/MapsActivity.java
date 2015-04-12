package handipark.nyuad.com.handipark;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class MapsActivity extends FragmentActivity implements LocationListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private MarkerOptions currentLocMarker;
    private GPSTracker gps;
    private double latitude;
    private double longitude;
    private ButtonRectangle addSpotBtn;
    private MarkerOptions pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        addSpotBtn = (ButtonRectangle) findViewById(R.id.add_button);
        addSpotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //addSpot();
                Intent i = new Intent(getApplicationContext(), NavigatorActivity.class);
                startActivity(i);
            }
        });
    }

    private void addSpot() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        // create class object
        gps = new GPSTracker(MapsActivity.this);

        // check if GPS enabled
        if(gps.canGetLocation()){

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            LocationAddress locationAddress = new LocationAddress();
            locationAddress.getAddressFromLocation(latitude, longitude,
                    getApplicationContext(), new GeocoderHandler());
                   /* TextView text =(TextView) findViewById(R.id.lat);
                    text.setText(""+latitude);
                    TextView text2 =(TextView) findViewById(R.id.lon);
                    text2.setText(""+longitude);*/

            // \n is for new line
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

        addNearParks();

        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerClickListener(this);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        currentLocMarker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("My Spot");
       // mMap.addMarker(currentLocMarker);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(latitude, longitude)).zoom(12).build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    private void addNearParks() {
        Log.d("ADD PINS", "inside ADD PINS");
        Log.i("Add", "add");
        final String url = "http://45.33.86.33/get_pins.php";
        final HttpClient client = new DefaultHttpClient();
        new AsyncTask<Void, Void, Void >() {
            protected Void doInBackground(Void... params) {
                try {
                    HttpResponse response = client.execute(new HttpGet(url));
                    String responseStr = EntityUtils.toString(response.getEntity());
                    Gson gSon = new Gson();
                    Type type = new TypeToken<List<ResponseModel>>() {}.getType();
                    final List<ResponseModel> rList = new GsonBuilder().create().fromJson(responseStr, type);

                    // Get a handler that can be used to post to the main thread
                    Handler mainHandler = new Handler(getApplicationContext().getMainLooper());
                    Runnable myRunnable = new Runnable(){
                            public void run () {

                                for (int i=0; i < rList.size(); i++) {
                                    String strLng = rList.get(i).getLng();
                                    String strLat = rList.get(i).getLat();

                                    double lng = Double.parseDouble(strLng);
                                    double lat = Double.parseDouble(strLat);

                                    Log.i("Lat Long", "" + lng + "" + lat);


                                    pin = new MarkerOptions().position(new LatLng(lat, lng)).title(rList.get(i).getId());
                                    pin.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                    Marker marker = mMap.addMarker(pin);

                                }
                    }
                    }; // This is your code
                    mainHandler.post(myRunnable);


                    System.out.println(responseStr);
                    JSONArray jsonArray = new JSONArray(responseStr);
                    System.out.println("json");
                    Log.i("MAP" , jsonArray.toString());

                    System.out.println(jsonArray);

                    System.out.println(jsonArray.get(1));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.i("onMarkerClick", "0clicked");
        String id = marker.getTitle();
        Intent nav = new Intent(getApplicationContext(), NavigatorActivity.class);
        nav.putExtra("id", id);
        startActivity(nav);

        return false;
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            //TextView tvAddress =(TextView) findViewById(R.id.tvAddress);
            //tvAddress.setText(locationAddress);
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(),"Address: " + locationAddress, Toast.LENGTH_LONG).show();

        }
    }
}
