package com.edu.uni.augsburg.uniatron.stepcounter;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

//import com.edu.uni.augsburg.uniatron.MainApplication;

/**
 * The step count service collects steps and commits them to the database.
 *
 * @author Leon Wöhrl
 */
public class StepCountService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;


    private static int commitSize = 10;
    private int currentSteps;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        currentSteps = 0;
    }

    @Override
    public int onStartCommand(Intent intent,   int flags, int startId) {
        // grab step detector and register the listener
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);

        Toast.makeText(this, "StepCountService has been started", Toast.LENGTH_SHORT).show();
        // this causes the OS to restart the service if it has been force stopped
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // detects every single step
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            int detectedSteps = (int) event.values[0];
            currentSteps += detectedSteps;

            if (currentSteps >= commitSize) {
                // subtract steps here and always commit exactly <commitSize> steps to prevent async issues
                // async could happen when the sensor delivers new data before the async task is completed
                currentSteps -= commitSize;
                commitSteps(commitSize);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // ok
    }

    @Override
    public void onDestroy() {
        // commit the steps that are due
        int tempSteps = currentSteps;
        // set to zero before commit in case of async issue
        currentSteps = 0;
        commitSteps(tempSteps);

        super.onDestroy();
    }

    /**
     * The function to commit exactly <commitSize> to the DataRepository
     */
    private void commitSteps(int numberOfSteps) {
        //execute this on a separate thread as it may some time
        // java.lang.IllegalStateException: Cannot access database on the main thread since it may potentially lock the UI for a long period of time.
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //MainApplication.getRepository().addStepCount(numberOfSteps);
            }
        });
    }

}
