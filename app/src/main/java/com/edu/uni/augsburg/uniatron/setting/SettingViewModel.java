package com.edu.uni.augsburg.uniatron.setting;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * The {@link SettingViewModel} provides the data for the {@link SettingFragment}.
 *
 * @author Fabio Hellmann
 */
public class SettingViewModel extends ViewModel {
    private final MutableLiveData<Set<String>> mObservableInstalledApps;

    /**
     * Ctr.
     */
    public SettingViewModel() {
        mObservableInstalledApps = new MutableLiveData<>();
        mObservableInstalledApps.setValue(null);
    }

    /**
     * Get the installed apps.
     *
     * @param context The context of the app.
     * @return The app-names.
     */
    @NonNull
    public LiveData<Set<String>> getInstalledApps(
            @NonNull final Context context) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        final PackageManager packageManager = context.getPackageManager();
        final List<ApplicationInfo> installedApplications = packageManager
                .getInstalledApplications(PackageManager.GET_META_DATA);

        final String uniatronApp = context.getApplicationInfo()
                .loadLabel(packageManager).toString();

        if (installedApplications != null) {
            final Set<String> result = Stream.of(installedApplications)
                    .map(ai -> packageManager.getApplicationLabel(ai).toString())
                    .filter(ai -> !ai.equals(uniatronApp))
                    .collect(Collectors.toSet());
            mObservableInstalledApps.setValue(result);
        }

        return Transformations.map(mObservableInstalledApps,
                data -> data != null ? data : Collections.emptySet());
    }
}
