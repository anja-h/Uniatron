package com.edu.uni.augsburg.uniatron.domain.model;

import com.edu.uni.augsburg.uniatron.model.Summary;

import java.util.Date;

public class SummaryEntity implements Summary {
    private Date mTimestamp;
    private long mAppUsageTime;
    private long mSteps;
    private double mEmotionAvg;

    public Date getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.mTimestamp = timestamp;
    }

    public long getAppUsageTime() {
        return mAppUsageTime;
    }

    public void setAppUsageTime(long appUsageTime) {
        this.mAppUsageTime = appUsageTime;
    }

    public long getSteps() {
        return mSteps;
    }

    public void setSteps(long steps) {
        this.mSteps = steps;
    }

    public double getEmotionAvg() {
        return mEmotionAvg;
    }

    public void setEmotionAvg(double emotionAvg) {
        this.mEmotionAvg = emotionAvg;
    }
}
