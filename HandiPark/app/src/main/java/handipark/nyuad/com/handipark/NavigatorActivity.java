package handipark.nyuad.com.handipark;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFlat;


public class NavigatorActivity extends Activity implements View.OnClickListener{

    Button reportButton;
    Button deleteButton;
    // ImageButton reportImage;
    public static ImageView mImageView;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    TextView myLocation;
    private String id;
    private Button navigateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigator);

        reportButton = (Button) findViewById(R.id.report);
        deleteButton = (Button) findViewById(R.id.delete);
        mImageView =(ImageView) findViewById(R.id.hidden);
        myLocation =(TextView) findViewById(R.id.textView);
        navigateButton = (Button) findViewById(R.id.nav_btn);
        reportButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);


        Intent markerId = getIntent();
        id =markerId.getStringExtra("id");
        Log.i("MakerId " , id);

    }

        @Override
        public void onClick(View v) {
            // Toast.makeText(getApplicationContext(), "button1 was clicked", Toast.LENGTH_SHORT).show();
            int id = v.getId();
            if (id == R.id.report) {
                dispatchTakePictureIntent();
            }

            if (id == R.id.delete) {
                Toast.makeText(getApplicationContext(), "delete Clicked", Toast.LENGTH_SHORT).show();
            /*Intent intent;
            intent = new Intent(MainActivity.this, report.class);
            startActivity(intent);*/
            }
            if (id == R.id.nav_btn) {
                Toast.makeText(getApplicationContext(), "button1 was clicked", Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                //        Uri.parse("http://maps.google.com/maps?daddr=" + longitude + "," + latitude));
                //startActivity(intent);
            }
        }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
            ReportActivity.bmp = ((BitmapDrawable)mImageView.getDrawable()).getBitmap();


            Intent intent;
            intent = new Intent(NavigatorActivity.this, ReportActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
