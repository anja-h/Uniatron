package uniatron.augsburg.uni.edu.com.domain.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import java.util.Date;
import java.util.List;

import uniatron.augsburg.uni.edu.com.domain.model.AppUsage;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface AppUsageDao {
    /**
     * Persist an app usage.
     *
     * @param appUsage The app usage to persist.
     */
    @Insert(onConflict = REPLACE)
    void add(@NonNull AppUsage appUsage);

    /**
     * Query all app usages over time since the date.
     *
     * @param date The date to query since.
     * @return the app usages.
     */
    @Query("SELECT * FROM AppUsage WHERE AppUsage.timestamp >= :date")
    LiveData<List<AppUsage>> loadSince(@NonNull Date date);
}
