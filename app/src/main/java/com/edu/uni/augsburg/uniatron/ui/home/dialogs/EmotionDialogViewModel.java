package com.edu.uni.augsburg.uniatron.ui.home.dialogs;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;
import com.edu.uni.augsburg.uniatron.model.Emotions;

public class EmotionDialogViewModel extends AndroidViewModel {
    private final DataRepository mRepository;

    /**
     * Ctr.
     *
     * @param application The application.
     */
    public EmotionDialogViewModel(@NonNull Application application) {
        super(application);

        mRepository = ((MainApplication) application).getRepository();
    }

    public void setEmotion(@NonNull final Emotions emotion) {
    }
}
