package com.edu.uni.augsburg.uniatron.ui.history;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.model.Emotions;
import com.edu.uni.augsburg.uniatron.model.Summary;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This displays a history of all the previous data.
 *
 * @author Fabio Hellmann
 */
public class HistoryFragment extends Fragment {

    private static final int DAYS_TO_LOAD = 7;

    @BindView(R.id.recyclerViewHistory)
    RecyclerView mRecyclerViewHistory;

    private Date mDateFrom;
    private Date mDateTo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final HistoryViewModel model = ViewModelProviders.of(this).get(HistoryViewModel.class);

        final LinearLayoutManager layout = new LinearLayoutManager(getContext());
        layout.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewHistory.setLayoutManager(layout);

        final ItemAdapter itemAdapter = new ItemAdapter();
        mRecyclerViewHistory.setAdapter(itemAdapter);

        mDateFrom = new Date();
        mDateTo = getPreviousDate(mDateFrom, DAYS_TO_LOAD);

        model.getSummary(mDateFrom, mDateTo).observe(this, data -> {
            itemAdapter.clear();
            itemAdapter.addItems(data);
        });

        itemAdapter.setOnLoadMoreListener(() -> {
            itemAdapter.showLoading();

            // define next interval to load
            mDateFrom = mDateTo;
            mDateTo = getPreviousDate(mDateFrom, DAYS_TO_LOAD);

            model.getSummary(mDateFrom, mDateTo).observe(this, itemAdapter::addItems);
        });
    }

    private Date getPreviousDate(Date date, int days) {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -days);
        return calendar.getTime();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textViewSteps)
        TextView mTextViewSteps;
        @BindView(R.id.textViewUsageTime)
        TextView mTextViewUsageTime;
        @BindView(R.id.imageViewEmoticon)
        ImageView mImageViewEmoticon;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.progressBar)
        ProgressBar mProgressBar;

        LoadingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    final class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;

        private final List<Summary> mSummaries = new ArrayList<>();

        private OnLoadMoreListener mOnLoadMoreListener;

        private boolean isLoading;
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;

        ItemAdapter() {
            final LinearLayoutManager linearLayoutManager =
                    (LinearLayoutManager) mRecyclerViewHistory.getLayoutManager();

            mRecyclerViewHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                    }
                }
            });
        }

        void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
            this.mOnLoadMoreListener = mOnLoadMoreListener;
        }

        void showLoading() {
            mSummaries.add(null);
            notifyItemInserted(mSummaries.size() - 1);
        }

        void addItems(@NonNull Collection<Summary> newItems) {
            if (!mSummaries.isEmpty()) {
                mSummaries.remove(mSummaries.size() - 1);
                notifyItemRemoved(mSummaries.size());
            }

            mSummaries.addAll(newItems);
            notifyDataSetChanged();
            isLoading = false;
        }

        void clear() {
            final int size = mSummaries.size() - 1;
            mSummaries.clear();
            notifyItemRangeRemoved(0, size);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_LOADING) {
                View view = LayoutInflater.from(getContext())
                        .inflate(R.layout.fragment_history_loading, parent, false);
                return new LoadingViewHolder(view);
            }
            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.fragment_history_item, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ItemViewHolder) {
                Summary summary = mSummaries.get(position);
                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                itemViewHolder.mTextViewSteps.setText(String.valueOf(summary.getSteps()));
                itemViewHolder.mTextViewUsageTime.setText(String.format(
                        Locale.getDefault(),
                        "%d:%02d",
                        summary.getAppUsageTime() / 60,
                        summary.getAppUsageTime() % 60
                ));

                final double emotionAvg = summary.getEmotionAvg();
                final int emotionIndex = (int) Math.round(emotionAvg);
                final Emotions emotion = Emotions.values()[emotionIndex];

                final Drawable drawable;
                switch (emotion) {
                    case ANGRY:
                        drawable = getResources()
                                .getDrawable(R.drawable.ic_emoticon_angry_selected);
                        break;
                    case SADNESS:
                        drawable = getResources()
                                .getDrawable(R.drawable.ic_emoticon_sad_selected);
                        break;
                    case HAPPINESS:
                        drawable = getResources()
                                .getDrawable(R.drawable.ic_emoticon_happy_selected);
                        break;
                    case FANTASTIC:
                        drawable = getResources()
                                .getDrawable(R.drawable.ic_emoticon_fantastic_selected);
                        break;
                    case NEUTRAL:
                    default:
                        drawable = getResources()
                                .getDrawable(R.drawable.ic_emoticon_neutral_selected);
                        break;
                }
                itemViewHolder.mImageViewEmoticon.setImageDrawable(drawable);
            } else if (holder instanceof LoadingViewHolder) {
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.mProgressBar.setIndeterminate(true);
            }
        }

        @Override
        public int getItemCount() {
            return mSummaries.size();
        }

        @Override
        public int getItemViewType(int position) {
            return mSummaries.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }
    }

    private interface OnLoadMoreListener {
        void onLoadMore();
    }
}
