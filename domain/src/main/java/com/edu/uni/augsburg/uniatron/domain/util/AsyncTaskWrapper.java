package com.edu.uni.augsburg.uniatron.domain.util;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

/**
 * This is a helper class to create an {@link AsyncTask} with lambda expressions.
 *
 * @author Fabio Hellmann
 * @param <T> The type of the return value.
 */
public class AsyncTaskWrapper<T> extends AsyncTask<Void, Void, T> {
    private final DoInBackground<T> doInBackground;
    private final OnPostExecute<T> onPostExecute;

    /**
     * Ctr.
     *
     * @param doInBackground The statement which has to be executed in the background-thread.
     * @param onPostExecute The statment which has to be executed afterwards in the ui-thread.
     */
    public AsyncTaskWrapper(@NonNull final DoInBackground<T> doInBackground,
                            @NonNull final OnPostExecute<T> onPostExecute) {
        this.doInBackground = doInBackground;
        this.onPostExecute = onPostExecute;
    }

    @Override
    protected T doInBackground(Void... voids) {
        return doInBackground.doInBackground();
    }

    @Override
    protected void onPostExecute(T result) {
        onPostExecute.onPostExecute(result);
    }

    /**
     * A helper interface for lambda usage.
     *
     * @author Fabio Hellmann
     * @param <T> The type of the return value.
     */
    public interface DoInBackground<T> {
        /**
         * Will be executed in a background-thread.
         *
         * @return The result.
         */
        T doInBackground();
    }

    /**
     * A helper interface for lambda usage.
     *
     * @author Fabio Hellmann
     * @param <T> The type of the parameter.
     */
    public interface OnPostExecute<T> {
        /**
         * Will be executed in the ui-thread.
         *
         * @param result The result of the background statement.
         */
        void onPostExecute(T result);
    }
}
