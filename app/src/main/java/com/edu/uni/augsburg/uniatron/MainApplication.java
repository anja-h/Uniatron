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

        mDataRepository.addAppUsage("WhatsApp", 100);
        mDataRepository.addAppUsage("Facebook", 231);
        mDataRepository.addAppUsage("Timely", 18);
        mDataRepository.addAppUsage("Youtube", 298);
        mDataRepository.addAppUsage("Gmail", 838);
        mDataRepository.addAppUsage("Spotify", 92);
        mDataRepository.addStepCount(11);
        mDataRepository.addStepCount(1734);
        mDataRepository.addTimeCredit(TimeCreditItem.CREDIT_1000);
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
