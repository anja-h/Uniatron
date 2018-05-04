package com.edu.uni.augsburg.uniatron.stepcounter;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.edu.uni.augsburg.uniatron.domain.AppDatabase;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;

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
        Toast.makeText(this, "StepCountService has been created", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent,   int flags, int startId) {
        Toast.makeText(this, "Listening to StepDetectorSensor", Toast.LENGTH_LONG).show();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);

        // this causes the OS to restart the service if it has been force stopped
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // inefficient way
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            Toast.makeText(this, "sensor input received", Toast.LENGTH_LONG).show();
            int detectedSteps = (int) event.values[0];
            currentSteps += detectedSteps;

            if (currentSteps > commitSize) {
                DataRepository data = new DataRepository(AppDatabase.buildInMemory(this));
                data.addStepCount(currentSteps);
                Toast.makeText(this, currentSteps + " steps have been commited", Toast.LENGTH_LONG).show();
                currentSteps = 0;
            }
        }

        // possibly implement an energy efficient way later on
        //if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {}
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // ok
    }

    @Override
    public void onDestroy() {
        // commit the steps that are due
        DataRepository data = new DataRepository(AppDatabase.buildInMemory(this));
        data.addStepCount(currentSteps);
        currentSteps = 0;
        super.onDestroy();
    }

}
