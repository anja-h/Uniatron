package com.edu.uni.augsburg.uniatron.model;

import java.util.Date;

public interface Summary {
    Date getTimestamp();

    long getAppUsageTime();

    long getSteps();

    double getEmotionAvg();
}
