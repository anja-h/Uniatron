package com.edu.uni.augsburg.uniatron.stepcounter;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.edu.uni.augsburg.uniatron.domain.AppDatabase;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;

/**
 * The step count service collects steps and commits them to the database.
 *
 * @author Leon Wöhrl
 */
public class StepCountService extends Service implements SensorEventListener {

    private static NotificationManager notificationManager;

    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    // TODO start the service when the device boots. stop it when the device shuts down
    // -> listen to bootcompleted broadcast
    @Override
    public int onStartCommand(Intent intent,   int flags, int startId) {
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
            int detectedSteps = (int) event.values[0];

            // we currently commit every step to the database
            // the database will create the sum of daily steps
            // if this creates an energy consumption problem, we will do it in batches

            DataRepository data = new DataRepository(AppDatabase.buildInMemory(this));
            data.addStepCount(detectedSteps);
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
        // internal cleanup
        super.onDestroy();
    }

}
