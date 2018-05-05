package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import com.edu.uni.augsburg.uniatron.domain.converter.DateConverter;
import com.edu.uni.augsburg.uniatron.domain.converter.EmotionConverter;
import com.edu.uni.augsburg.uniatron.domain.model.EmotionEntity;

import java.util.Date;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * The dao contains all the calls depending to
 * {@link com.edu.uni.augsburg.uniatron.domain.model.EmotionEntity}.
 *
 * @author Fabio Hellmann
 */
@Dao
@TypeConverters({DateConverter.class, EmotionConverter.class})
public interface EmotionDao {
    /**
     * Persist an emotion.
     *
     * @param emotion The emotion to persist.
     */
    @Insert(onConflict = REPLACE)
    void insert(EmotionEntity emotion);

    /**
     * Load the average emotion for a specified time range.
     *
     * @param dateFrom The date to start searching.
     * @param dateTo The date to end searching.
     * @return The average emotion.
     */
    @Query("SELECT SUM(value) FROM EmotionEntity WHERE timestamp BETWEEN :dateFrom AND :dateTo")
    LiveData<Integer> getAverageEmotion(Date dateFrom, Date dateTo);
}
