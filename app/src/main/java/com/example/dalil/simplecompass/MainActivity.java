package com.example.dalil.simplecompass;

import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "MainActivity";

    ImageView imageView;
    private float currentDegree = 0f;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Location location = new Location("A");
    private Location target = new Location("B");
    private LocationManager locationManager;
    GeomagneticField geoField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView2);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        location.setLatitude(43.855025);
        location.setLongitude(18.420023);

        target.setLatitude(48.210033);
        target.setLongitude(16.363449);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Log.d(TAG, "onSensorChanged: called");
        // get the angle around the z-axis rotated



        float azimuth = (float)Math.round(Math.toDegrees(Math.round(event.values[2])));
        // Log.d(TAG, "onSensorChanged: azimuth: " + azimuth);

        Log.d(TAG, event.values[0] + " " + event.values[1] + " " + event.values[2]);

        geoField = new GeomagneticField(
                Double.valueOf(location.getLatitude()).floatValue(),
                Double.valueOf(location.getLongitude()).floatValue(),
                Double.valueOf(location.getAltitude()).floatValue(),
                System.currentTimeMillis()
        );

        azimuth += geoField.getDeclination();

        // Log.d(TAG, "onSensorChanged: declination: " + geoField.getDeclination());

        float bearing = location.bearingTo(target);
        //normalizeDegree(bearing);
        //Log.d(TAG, "onSensorChanged: bearing: " + bearing);

        float direction = azimuth - bearing;
      //  azimuth = (bearing - azimuth) * -1;
    //    azimuth = normalizeDegree(azimuth);


        //float direction = azimuth - bearing

            // create a rotation animation (reverse turn degree degrees)
            RotateAnimation ra = new RotateAnimation(
                    currentDegree,
                    direction,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            // how long the animation will take place
            ra.setDuration(210);

            // set the animation after the end of the reservation status
            ra.setFillAfter(true);

            // Start the animation
            imageView.startAnimation(ra);
            currentDegree = direction;

        //Log.d(TAG, "onSensorChanged: current degree: " + currentDegree);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: called");
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: called");
        super.onPause();
        mSensorManager.unregisterListener(this); // to stop the listener and save battery
    }

    private float normalizeDegree(float value) {
        if (value >= 0.0f && value <= 360.0f) {
            return value;
        } else {
            return value % 360;
        }
    }
}
