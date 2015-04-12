package handipark.nyuad.com.handipark;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;


public class SplashActivity extends Activity {

    private final int SPLASH_DISPLAY_LENGTH = 3000;

    ImageView splashImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // to remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);


        //set content view AFTER ABOVE sequence (to avoid crash)
        // setContentView(R.layout.activity_splash);
        splashImage=(ImageView) findViewById(R.id.imageView2);
        splashImage.setScaleType(ImageView.ScaleType.FIT_XY);




        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashActivity.this, MapsActivity.class);
                finish();
                startActivity(i);
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

}
