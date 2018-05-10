package com.edu.uni.augsburg.uniatron;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.edu.uni.augsburg.uniatron.domain.AppDatabase;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;
import com.edu.uni.augsburg.uniatron.domain.DatabaseUtil;

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
        DatabaseUtil.createRandomData(database);

        mDataRepository = new DataRepository(database);
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
