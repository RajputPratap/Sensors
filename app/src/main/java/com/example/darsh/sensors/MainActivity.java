package com.example.darsh.sensors;

import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "SENSORS";
    SensorManager sensorManager;
    Sensor accelSensor;
    ImageView ivBall;
    Sensor proxySensor;
    FrameLayout flBack;
    SensorEventListener sensorEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivBall = (ImageView) findViewById(R.id.ivBall);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        final float px = 100 * (displaymetrics.densityDpi / 160f);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        flBack = (FrameLayout) findViewById(R.id.flBack);
//        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

//        Log.d(TAG, "onCreate: " + sensorList.size());

//        for (Sensor sensor : sensorList){
//            Log.d(TAG, "onCreate: " + sensor.getName());
//            Log.d(TAG, "onCreate: " + sensor.getVendor());
//            Log.d(TAG, "onCreate: " + sensor.getVersion());
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
//                Log.d(TAG, "onCreate: " + sensor.getStringType());
//            }
//            Log.d(TAG, "---------------------------------");
//        }

        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        proxySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        sensorEventListener = new SensorEventListener() {
            float x = 0.0f;
            float y = 0.0f, res = 0.0f, curres = 0.0f;
            float curxsp, curysp, t = 0.666f;
            float speedx = 0.0f, speedy = 0.0f;
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//                    Log.d(TAG, "onSensorChanged: X " + sensorEvent.values[0]);
//                    Log.d(TAG, "onSensorChanged: Y " + sensorEvent.values[1]);
//                    Log.d(TAG, "onSensorChanged: Z " + sensorEvent.values[2]);
//                    int red = (int)((sensorEvent.values[0] + 13) * 255)/26;
//                    int blue = (int)((sensorEvent.values[1] + 13) * 255)/26;
//                    int green = (int)((sensorEvent.values[2] + 13) * 255)/26;
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                        flBack.setBackgroundColor(Color.rgb(red , blue , green ));
//                    }

                    float accy = sensorEvent.values[1] / 10;
                    float accx = -sensorEvent.values[0] / 10;
                    curxsp = speedx;
                    curysp = speedy;
                    curres = res;

                    speedy += accy * t;
                    speedx += accx * t;

                    y += (curysp * t) + (0.5 * accy * Math.pow(t, 2));
                    x += (curxsp * t) + (0.5 * accx * Math.pow(t, 2));


                    Log.d(TAG, "onSensorChanged: accx" + accx);
                    Log.d(TAG, "onSensorChanged: accy" + accy);
                    res = ivBall.getRotation() + accx*5;
                    if (accy < 0) res = ivBall.getRotation() + accy * 5;
                    if ((y < 0 && x < 0)|| (y < 0 && x > flBack.getRight() - px)) res = curres;
                    if ((x < 0 && y > flBack.getBottom() - px)|| (y > flBack.getBottom() - px && x > flBack.getRight() - px)) res = curres;

                    if (y < 0) {
                        y = 0.0f;
                        speedy = 0.0f;
                    }

                    if (x < 0){
                        x = 0.0f;
                        speedx = 0.0f;
                    }

                    if (y > flBack.getBottom() - px) {
                        y = flBack.getBottom() - px;
                        speedy = 0.0f;
                    }

                    if (x > flBack.getRight() - px) {
                        x = flBack.getRight() - px;
                        speedx = 0.0f;
                    }

                    ivBall.setX(x);
                    ivBall.setY(y);
                    ivBall.setRotation(res);

                }
                if (sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY){
                    if (sensorEvent.values[0] == proxySensor.getMaximumRange()){
                        Toast.makeText(MainActivity.this, "Anirudh bola : Hi", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Anjali boli : BHAG BC ", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener, accelSensor, sensorManager.SENSOR_DELAY_GAME);
//        sensorManager.registerListener(sensorEventListener,proxySensor, 1000*1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }
}
