package com.edu.uni.augsburg.uniatron;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.os.Handler;
import android.os.Looper;

import com.edu.uni.augsburg.uniatron.domain.AppDatabase;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * The application context for this app.
 *
 * @author Fabio Hellmann
 */
public class MainApplication extends Application {
    private DataRepository mDataRepository;

    @Override
    public void onCreate() {
        super.onCreate();

        // TODO For the final app use the real database
        /*
        final AppDatabase database = Room.databaseBuilder(
                this,
                AppDatabase.class,
                "uniatron")
                .build();
         */

        final AppDatabase database = Room.inMemoryDatabaseBuilder(this, AppDatabase.class)
                .allowMainThreadQueries()
                .build();

        mDataRepository = new DataRepository(database);

        mDataRepository.addAppUsage("WhatsApp", 143);
        mDataRepository.addAppUsage("Facebook", 231);
        mDataRepository.addAppUsage("Youtube", 785);
        mDataRepository.addAppUsage("Gmail", 456);
        mDataRepository.addAppUsage("Spotify", 245);
        mDataRepository.addAppUsage("Timely", 19);
        mDataRepository.addStepCount(11);
        mDataRepository.addStepCount(1734);
        mDataRepository.addStepCount(3847);
        mDataRepository.addStepCount(34);
        mDataRepository.addStepCount(784);
    }

    /**
     * Get the data repository.
     *
     * @return The data repository.
     */
    public DataRepository getRepository() {
        return mDataRepository;
    }
}
