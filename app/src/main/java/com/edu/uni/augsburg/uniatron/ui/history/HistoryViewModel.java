package com.edu.uni.augsburg.uniatron.ui.history;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;
import com.edu.uni.augsburg.uniatron.model.Summary;

import java.util.Date;
import java.util.List;

/**
 * @author Fabio Hellmann
 */
public class HistoryViewModel extends AndroidViewModel {
    private final MediatorLiveData<List<Summary>> mObservableDaySummary;
    private final DataRepository mRepository;

    public HistoryViewModel(@NonNull Application application) {
        super(application);

        mRepository = ((MainApplication) application).getRepository();

        mObservableDaySummary = new MediatorLiveData<>();
    }

    public LiveData<List<Summary>> getSummary(Date from, Date to) {
        mObservableDaySummary.addSource(
                mRepository.getSummary(from, to),
                mObservableDaySummary::setValue
        );
        return mObservableDaySummary;
    }
}
