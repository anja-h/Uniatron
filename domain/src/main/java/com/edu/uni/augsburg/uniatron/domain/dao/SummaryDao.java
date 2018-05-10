package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import com.edu.uni.augsburg.uniatron.domain.converter.DateConverter;
import com.edu.uni.augsburg.uniatron.domain.model.SummaryEntity;

import java.util.Date;
import java.util.List;

@Dao
@TypeConverters({DateConverter.class})
public interface SummaryDao {

    @Query("SELECT MAX(timestamp) as 'mTimestamp', " +
            "TOTAL(usage_time_in_seconds) as 'mAppUsageTime', " +
            "(SELECT TOTAL(step_count) FROM StepCountEntity WHERE date(timestamp) = date(aue.timestamp)) as 'mSteps', " +
            "(SELECT CASE WHEN AVG(value) IS NULL THEN 0.0 ELSE AVG(value) END FROM EmotionEntity WHERE date(timestamp) = date(aue.timestamp)) as 'mEmotionAvg' " +
            "FROM AppUsageEntity aue " +
            "WHERE timestamp BETWEEN :dateFrom AND :dateTo " +
            "GROUP BY date(timestamp) " +
            "ORDER BY timestamp DESC")
    LiveData<List<SummaryEntity>> getSummaries(Date dateFrom, Date dateTo);
}
