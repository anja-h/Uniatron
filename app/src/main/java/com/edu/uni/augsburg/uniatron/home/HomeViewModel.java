package com.edu.uni.augsburg.uniatron.home;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The {@link HomeViewModel} provides the data for the {@link HomeFragment}.
 *
 * @author Fabio Hellmann
 */
public class HomeViewModel extends AndroidViewModel {
    private static final int MAX_COUNT = 3;
    private final MediatorLiveData<Map<String, Double>> mObservableAppUsages;
    private final MediatorLiveData<Integer> mObservableRemainingStepCount;
    private final MediatorLiveData<Integer> mObservableRemainingAppUsageTime;

    /**
     * Ctr.
     *
     * @param application The application.
     */
    public HomeViewModel(@NonNull Application application) {
        super(application);

        final DataRepository repository = ((MainApplication) application).getRepository();

        mObservableAppUsages = new MediatorLiveData<>();
        mObservableAppUsages.setValue(null);

        final LiveData<Map<String, Double>> appUsagePercentToday =
                repository.getAppUsagePercentToday();
        mObservableAppUsages.addSource(appUsagePercentToday, mObservableAppUsages::setValue);

        mObservableRemainingStepCount = new MediatorLiveData<>();
        mObservableRemainingStepCount.setValue(null);

        final LiveData<Integer> remainingStepCountsToday = repository.getRemainingStepCountsToday();
        mObservableRemainingStepCount.addSource(remainingStepCountsToday,
                mObservableRemainingStepCount::setValue);

        mObservableRemainingAppUsageTime = new MediatorLiveData<>();
        mObservableRemainingAppUsageTime.setValue(null);

        final LiveData<Integer> remainingUsageTimeToday = repository.getRemainingUsageTimeToday();
        mObservableRemainingAppUsageTime.addSource(remainingUsageTimeToday,
                mObservableRemainingAppUsageTime::setValue);
    }

    /**
     * Get the app usage of the top 3 apps.
     *
     * @return The app usage.
     */
    @NonNull
    public LiveData<Map<String, Double>> getAppUsageOfTop3Apps() {
        return Transformations.map(mObservableAppUsages, data -> extractValues(data, MAX_COUNT));
    }

    /**
     * Get the remaining step count for today.
     *
     * @return The remaining step count.
     */
    @NonNull
    public LiveData<Integer> getRemainingStepCountToday() {
        return Transformations.map(mObservableRemainingStepCount,
                data -> data != null && data > 0? data : 0);
    }

    /**
     * Get the remaining app usage time for today.
     *
     * @return The remaining app usage time.
     */
    @NonNull
    public LiveData<Integer> getRemainingAppUsageTime() {
        return Transformations.map(mObservableRemainingAppUsageTime,
                data -> data != null && data > 0 ? data : 0);
    }

    @NonNull
    private Map<String, Double> extractValues(final Map<String, Double> data, final int maxCount) {
        if (data == null) {
            return Collections.emptyMap();
        }
        // 1. Convert Map to List of Map
        List<Map.Entry<String, Double>> list = new LinkedList<>(data.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, (o1, o2) -> (o1.getValue()).compareTo(o2.getValue()) * -1);

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, Double> sortedMap = new LinkedHashMap<>();
        for (int index = 0; index < list.size() && index < maxCount; index++) {
            final Map.Entry<String, Double> entry = list.get(index);
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
}
