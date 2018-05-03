package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import com.edu.uni.augsburg.uniatron.domain.converter.DateConverter;
import com.edu.uni.augsburg.uniatron.domain.model.StepCountEntity;

import java.util.Date;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * The dao contains all the calls depending to step count.
 *
 * @author Fabio Hellmann
 */
@Dao
@TypeConverters({DateConverter.class})
public interface StepCountDao {
    /**
     * Persist a step count.
     *
     * @param stepCount The step count to persist.
     */
    @Insert(onConflict = REPLACE)
    void add(StepCountEntity stepCount);

    /**
     * Load the step count for a specified date range.
     *
     * @param dateFrom The date to start searching.
     * @param dateTo The date to end searching.
     * @return The step count.
     */
    @Query("SELECT SUM(step_count) FROM StepCountEntity "
            + "WHERE timestamp BETWEEN :dateFrom AND :dateTo")
    LiveData<Integer> loadStepCounts(Date dateFrom, Date dateTo);

    /**
     * Load the remaining step count for a specified date range.
     *
     * @param dateFrom The date to start searching.
     * @param dateTo The date to end searching.
     * @return The step count.
     */
    @Query("SELECT SUM(step_count) - (SELECT SUM(time_in_minutes) * 60 FROM TimeCreditEntity "
            + "WHERE timestamp BETWEEN :dateFrom AND :dateTo) FROM StepCountEntity "
            + "WHERE timestamp BETWEEN :dateFrom AND :dateTo")
    LiveData<Integer> loadRemainingStepCount(Date dateFrom, Date dateTo);
}
