package com.edu.uni.augsburg.uniatron;

import android.app.Application;

import com.edu.uni.augsburg.uniatron.domain.AppDatabase;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;
import com.edu.uni.augsburg.uniatron.model.TimeCreditItem;

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

        mDataRepository = new DataRepository(AppDatabase.buildInMemory(this));

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
        mDataRepository.addTimeCreditDayStart();
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
