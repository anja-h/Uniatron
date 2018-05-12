package com.edu.uni.augsburg.uniatron.ui.history;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.model.Emotions;
import com.edu.uni.augsburg.uniatron.model.Summary;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

import static com.edu.uni.augsburg.uniatron.domain.util.DateUtil.extractMaxTimeOfDate;
import static com.edu.uni.augsburg.uniatron.domain.util.DateUtil.extractMinTimeOfDate;

/**
 * This displays a history of all the previous data.
 *
 * @author Fabio Hellmann
 */
public class HistoryFragment extends Fragment {

    private static final int DAYS_TO_LOAD = 14;
    private static final int ANIMATION_DURATION = 500;

    @BindView(R.id.recyclerViewHistory)
    RecyclerView mRecyclerViewHistory;

    private Date mDateTo;
    private Date mDateFrom;

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
        mRecyclerViewHistory.addItemDecoration(
                new DividerItemDecoration(getContext(), layout.getOrientation())
        );
        mRecyclerViewHistory.setLayoutManager(layout);
        mRecyclerViewHistory.setItemAnimator(new SlideInLeftAnimator());
        mRecyclerViewHistory.getItemAnimator().setAddDuration(ANIMATION_DURATION);
        mRecyclerViewHistory.getItemAnimator().setChangeDuration(ANIMATION_DURATION);
        mRecyclerViewHistory.getItemAnimator().setMoveDuration(ANIMATION_DURATION);
        mRecyclerViewHistory.getItemAnimator().setRemoveDuration(ANIMATION_DURATION);

        final ItemAdapter itemAdapter = new ItemAdapter();
        mRecyclerViewHistory.setAdapter(itemAdapter);

        mDateTo = extractMaxTimeOfDate(new Date());
        mDateFrom = extractMinTimeOfDate(getPreviousDate(mDateTo, DAYS_TO_LOAD));
        model.registerDateRange(mDateFrom, mDateTo);

        model.getSummary().observe(this, itemAdapter::addItems);

        itemAdapter.setOnLoadMoreListener(() -> {
            // define next interval to load
            mDateTo = extractMaxTimeOfDate(mDateFrom);
            mDateFrom = extractMinTimeOfDate(getPreviousDate(mDateTo, DAYS_TO_LOAD));

            model.registerDateRange(mDateFrom, mDateTo);
        });
    }

    private Date getPreviousDate(Date date, int days) {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -days);
        return calendar.getTime();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textViewDate)
        TextView mTextViewDate;
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

        private static final int VIEW_TYPE_ITEM = 0;
        private static final int VIEW_TYPE_LOADING = 1;

        private final Map<Date, Summary> mSummaryMap = new HashMap<>();

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
                        mRecyclerViewHistory.post(() -> notifyItemInserted(getItemCount()));
                    }
                }
            });
        }

        void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
            this.mOnLoadMoreListener = onLoadMoreListener;
        }

        void addItems(@NonNull Collection<Summary> newItems) {
            if (isLoading) {
                notifyItemRemoved(getItemCount());
                isLoading = false;
            }

            Stream.of(newItems).forEach(item -> mSummaryMap.put(item.getTimestamp(), item));
            notifyDataSetChanged();
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
                Summary summary = getItemByIndex(position);

                final String timestampFormatted = String.format(
                        Locale.getDefault(),
                        "%te. %tb %ty",
                        summary.getTimestamp(),
                        summary.getTimestamp(),
                        summary.getTimestamp()
                );
                final String stepsFormatted = String.valueOf(summary.getSteps());
                final String timeFormatted = String.format(
                        Locale.getDefault(),
                        "%d:%02d",
                        summary.getAppUsageTime() / 60,
                        summary.getAppUsageTime() % 60
                );

                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                itemViewHolder.mTextViewDate.setText(timestampFormatted);
                itemViewHolder.mTextViewSteps.setText(stepsFormatted);
                itemViewHolder.mTextViewUsageTime.setText(timeFormatted);

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
            return mSummaryMap.size() + (isLoading ? 1 : 0);
        }

        @Override
        public int getItemViewType(int position) {
            return getItemCount() - 1 == position && isLoading ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        private Summary getItemByIndex(int position) {
            return Stream.of(mSummaryMap.entrySet())
                    .sorted((i1, i2) -> i2.getKey().compareTo(i1.getKey()))
                    .collect(Collectors.toList())
                    .get(position)
                    .getValue();
        }
    }

    private interface OnLoadMoreListener {
        void onLoadMore();
    }
}
