package com.edu.uni.augsburg.uniatron.domain;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.domain.dao.AppUsageDao;
import com.edu.uni.augsburg.uniatron.domain.dao.StepCountDao;
import com.edu.uni.augsburg.uniatron.domain.dao.TimeCreditDao;
import com.edu.uni.augsburg.uniatron.domain.model.AppUsageEntity;
import com.edu.uni.augsburg.uniatron.domain.util.TestUtils;
import com.edu.uni.augsburg.uniatron.model.AppUsage;
import com.edu.uni.augsburg.uniatron.model.StepCount;
import com.edu.uni.augsburg.uniatron.model.TimeCredit;
import com.edu.uni.augsburg.uniatron.model.TimeCredits;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import static com.edu.uni.augsburg.uniatron.domain.util.TestUtils.getLiveDataValue;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DataRepositoryTest {
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    private DataRepository mRepository;
    private AppUsageDao appUsageDao;
    private StepCountDao stepCountDao;
    private TimeCreditDao timeCreditDao;

    @Before
    public void setUp() {
        appUsageDao = mock(AppUsageDao.class);
        stepCountDao = mock(StepCountDao.class);
        timeCreditDao = mock(TimeCreditDao.class);

        final AppDatabase database = mock(AppDatabase.class);
        when(database.appUsageDao()).thenReturn(appUsageDao);
        when(database.stepCountDao()).thenReturn(stepCountDao);
        when(database.timeCreditDao()).thenReturn(timeCreditDao);

        mRepository = new DataRepository(database);
    }

    @Test
    public void addTimeCredit() {
        final TimeCredit timeCredit = mRepository.addTimeCredit(TimeCredits.CREDIT_1000);
        assertThat(timeCredit, is(notNullValue()));
    }

    @Test(expected = Exception.class)
    public void addTimeCreditFailed() {
        Mockito.doThrow(Exception.class).when(timeCreditDao).add(any());

        mRepository.addTimeCredit(TimeCredits.CREDIT_1000);
        assertFalse("Should has thrown an exception!", true);
    }

    @Test
    public void getTimeCreditsToday() throws InterruptedException {
        final int value = 10;
        final MutableLiveData<Integer> liveData = new MutableLiveData<>();
        liveData.setValue(value);
        when(timeCreditDao.loadTimeCredits(any(), any())).thenReturn(liveData);

        final LiveData<Integer> timeCreditsToday = mRepository.getTimeCreditsToday();

        final Integer liveDataValue = TestUtils.getLiveDataValue(timeCreditsToday);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue, is(value));
        verify(timeCreditDao, atLeastOnce()).loadTimeCredits(any(), any());
    }

    @Test
    public void getTimeCreditsByDate() throws InterruptedException {
        final int value = 10;
        final MutableLiveData<Integer> liveData = new MutableLiveData<>();
        liveData.setValue(value);
        when(timeCreditDao.loadTimeCredits(any(), any())).thenReturn(liveData);

        final LiveData<Integer> timeCreditsToday = mRepository.getTimeCreditsByDate(new Date());

        final Integer liveDataValue = TestUtils.getLiveDataValue(timeCreditsToday);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue, is(value));
        verify(timeCreditDao, atLeastOnce()).loadTimeCredits(any(), any());
    }

    @Test
    public void addStepCount() {
        final int value = 10;
        final StepCount stepCount = mRepository.addStepCount(value);
        assertThat(stepCount, is(notNullValue()));
        assertThat(stepCount.getStepCount(), is(value));
    }

    @Test(expected = Exception.class)
    public void addStepCountFailed() {
        Mockito.doThrow(Exception.class).when(stepCountDao).add(any());

        mRepository.addStepCount(10);
        assertFalse("Should has thrown an exception!", true);
    }

    @Test
    public void getStepCountsToday() throws InterruptedException {
        final int value = 10;
        final MutableLiveData<Integer> liveData = new MutableLiveData<>();
        liveData.setValue(value);
        when(stepCountDao.loadStepCounts(any(), any())).thenReturn(liveData);

        final LiveData<Integer> stepCountsToday = mRepository.getStepCountsToday();
        final Integer liveDataValue = TestUtils.getLiveDataValue(stepCountsToday);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue, is(value));
        verify(stepCountDao, atLeastOnce()).loadStepCounts(any(), any());
    }

    @Test
    public void getStepCountsByDate() throws InterruptedException {
        final int value = 10;
        final MutableLiveData<Integer> liveData = new MutableLiveData<>();
        liveData.setValue(value);
        when(stepCountDao.loadStepCounts(any(), any())).thenReturn(liveData);

        final LiveData<Integer> stepCountsToday = mRepository.getStepCountsByDate(new Date());

        final Integer liveDataValue = TestUtils.getLiveDataValue(stepCountsToday);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue, is(value));
        verify(stepCountDao, atLeastOnce()).loadStepCounts(any(), any());
    }

    @Test
    public void addAppUsage() {
        final int value = 10;
        final AppUsage appUsage = mRepository.addAppUsage("test", value);
        assertThat(appUsage, is(notNullValue()));
        assertThat(appUsage.getUsageTimeInSeconds(), is(value));
    }

    @Test(expected = Exception.class)
    public void addAppUsageFailed() {
        Mockito.doThrow(Exception.class).when(appUsageDao).add(any());

        mRepository.addAppUsage("test", 10);
        assertFalse("Should has thrown an exception!", true);
    }

    @Test
    public void getAppUsageTimeToday() throws Exception {
        final List<AppUsageEntity> list = new ArrayList<>();

        final AppUsageEntity entity = new AppUsageEntity();
        entity.setId(0);
        entity.setTimestamp(new Date());
        entity.setUsageTimeInSeconds(10);
        entity.setAppName("Test");
        list.add(entity);

        final AppUsageEntity entity1 = new AppUsageEntity();
        entity1.setId(1);
        entity1.setTimestamp(new Date());
        entity1.setUsageTimeInSeconds(7);
        entity1.setAppName("Test1");
        list.add(entity1);

        final MutableLiveData<List<AppUsageEntity>> liveData = new MutableLiveData<>();
        liveData.setValue(list);
        when(appUsageDao.loadAppUsageTime(any(), any())).thenReturn(liveData);

        final LiveData<Map<String, Integer>> data = mRepository.getAppUsageTimeToday();

        assertThat(getLiveDataValue(data), is(notNullValue()));
        assertThat(getLiveDataValue(data).size(), is(2));
        assertThat(getLiveDataValue(data).keySet(), hasItems("Test", "Test1"));
        assertThat(getLiveDataValue(data).values(), hasItems(10, 7));
        verify(appUsageDao, atLeastOnce()).loadAppUsageTime(any(), any());
    }

    @Test
    public void getAppUsageTimeByDate() throws Exception {
        final List<AppUsageEntity> list = new ArrayList<>();

        final AppUsageEntity entity = new AppUsageEntity();
        entity.setId(0);
        entity.setTimestamp(getDate(1, 1, 1990));
        entity.setUsageTimeInSeconds(10);
        entity.setAppName("Test");
        list.add(entity);

        final AppUsageEntity entity1 = new AppUsageEntity();
        entity1.setId(1);
        entity1.setTimestamp(getDate(1, 1, 2000));
        entity1.setUsageTimeInSeconds(7);
        entity1.setAppName("Test1");
        list.add(entity1);

        final MutableLiveData<List<AppUsageEntity>> liveData = new MutableLiveData<>();
        liveData.setValue(list);
        when(appUsageDao.loadAppUsageTime(any(), any())).thenReturn(liveData);

        final LiveData<Map<String, Integer>> data = mRepository
                .getAppUsageTimeByDate(getDate(1, 1, 2000));

        assertThat(getLiveDataValue(data), is(notNullValue()));
        assertThat(getLiveDataValue(data).size(), is(2));
        assertThat(getLiveDataValue(data).keySet(), hasItems("Test", "Test1"));
        assertThat(getLiveDataValue(data).values(), hasItems(10, 7));
        verify(appUsageDao, atLeastOnce()).loadAppUsageTime(any(), any());
    }

    @Test
    public void getAppUsagePercentToday() throws Exception {
        final List<AppUsageEntity> list = new ArrayList<>();

        final AppUsageEntity entity = new AppUsageEntity();
        entity.setId(0);
        entity.setTimestamp(new Date());
        entity.setUsageTimeInSeconds(90);
        entity.setAppName("Test");
        list.add(entity);

        final AppUsageEntity entity1 = new AppUsageEntity();
        entity1.setId(1);
        entity1.setTimestamp(new Date());
        entity1.setUsageTimeInSeconds(10);
        entity1.setAppName("Test1");
        list.add(entity1);

        final MutableLiveData<List<AppUsageEntity>> liveData = new MutableLiveData<>();
        liveData.setValue(list);
        when(appUsageDao.loadAppUsagePercent(any(), any())).thenReturn(liveData);

        final LiveData<Map<String, Double>> data = mRepository.getAppUsagePercentToday();

        assertThat(getLiveDataValue(data), is(notNullValue()));
        assertThat(getLiveDataValue(data).size(), is(2));
        assertThat(getLiveDataValue(data).keySet(), hasItems("Test", "Test1"));
        assertThat(getLiveDataValue(data).values(), hasItems(0.90, 0.10));
        verify(appUsageDao, atLeastOnce()).loadAppUsagePercent(any(), any());
    }

    @Test
    public void getAppUsagePercentByDate() throws Exception {
        final List<AppUsageEntity> list = new ArrayList<>();

        final AppUsageEntity entity = new AppUsageEntity();
        entity.setId(0);
        entity.setTimestamp(getDate(1, 1, 1990));
        entity.setUsageTimeInSeconds(90);
        entity.setAppName("Test");
        list.add(entity);

        final AppUsageEntity entity1 = new AppUsageEntity();
        entity1.setId(1);
        entity1.setTimestamp(getDate(1, 1, 2000));
        entity1.setUsageTimeInSeconds(10);
        entity1.setAppName("Test1");
        list.add(entity1);

        final MutableLiveData<List<AppUsageEntity>> liveData = new MutableLiveData<>();
        liveData.setValue(list);
        when(appUsageDao.loadAppUsagePercent(any(), any())).thenReturn(liveData);

        final LiveData<Map<String, Double>> data = mRepository
                .getAppUsagePercentByDate(getDate(1, 1, 2000));

        assertThat(getLiveDataValue(data), is(notNullValue()));
        assertThat(getLiveDataValue(data).size(), is(2));
        assertThat(getLiveDataValue(data).keySet(), hasItems("Test", "Test1"));
        assertThat(getLiveDataValue(data).values(), hasItems(0.9, 0.1));
        verify(appUsageDao, atLeastOnce()).loadAppUsagePercent(any(), any());
    }

    @NonNull
    private static Date getDate(int date, int month, int year) {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(year, month - 1, date);
        return calendar.getTime();
    }
}