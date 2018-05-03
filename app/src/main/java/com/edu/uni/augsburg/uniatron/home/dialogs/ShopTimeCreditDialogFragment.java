package com.edu.uni.augsburg.uniatron.home.dialogs;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.home.HomeViewModel;
import com.edu.uni.augsburg.uniatron.model.TimeCreditItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * The shop dialog is a chooser for a {@link com.edu.uni.augsburg.uniatron.model.TimeCredit}.
 *
 * @author Fabio Hellmann
 */
public class ShopTimeCreditDialogFragment extends DialogFragment {
    /**
     * The name of this dialog.
     */
    public static final String NAME = ShopTimeCreditDialogFragment.class.getSimpleName();

    @BindView(R.id.textViewError)
    TextView mTextViewError;
    @BindView(R.id.timeCreditRecyclerView)
    RecyclerView mRecyclerView;

    private TimeCreditListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_shop_time_credit, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutFrozen(true);
        final LinearLayoutManager layout = new LinearLayoutManager(getContext());
        layout.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layout);
        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), layout.getOrientation())
        );
        mAdapter = new TimeCreditListAdapter();
        mRecyclerView.setAdapter(mAdapter);

        final HomeViewModel model = ViewModelProviders.of(this).get(HomeViewModel.class);
        model.getRemainingStepCountToday().observe(this, stepCount -> {
            if (TimeCreditItem.CREDIT_1000.isUsable(stepCount)) {
                mAdapter.setStepCount(stepCount);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.setVisibility(View.VISIBLE);
                mTextViewError.setVisibility(View.GONE);
            } else {
                mRecyclerView.setVisibility(View.GONE);
                mTextViewError.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * The on click event of the cancel button is registered here.
     */
    @OnClick(R.id.cancelButton)
    public void onCancelButtonClicked() {
        dismiss();
    }

    /**
     * The on click event of the buy button is registered here.
     */
    @OnClick(R.id.tradeButton)
    public void onBuyButtonClicked() {
        if (getContext() != null) {
            final MainApplication context = (MainApplication) getContext().getApplicationContext();
            context.getRepository().addTimeCredit(mAdapter.getSelection());
            dismiss();
        }
    }

    final class TimeCreditListAdapter extends
            RecyclerView.Adapter<TimeCreditListAdapter.ViewHolder> {
        private final List<ViewHolder> viewHolders = new ArrayList<>();
        private int mStepCount;
        private int mSelectionIndex = -1;

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.dialog_shop_time_credit_item, parent, false);
            final ViewHolder viewHolder = new ViewHolder(view);
            viewHolders.add(viewHolder);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final TimeCreditItem timeCreditItem = Stream.of(TimeCreditItem.values())
                    .sortBy(TimeCreditItem::getStepCount)
                    .collect(Collectors.toList())
                    .get(position);

            holder.mTextViewTradeOffer.setText(getString(
                    R.string.dialog_time_credit_item,
                    timeCreditItem.getStepCount(),
                    timeCreditItem.getTimeInMinutes())
            );
        }

        @Override
        public int getItemCount() {
            return (int) Stream.of(TimeCreditItem.values())
                    .filter(credit -> credit.isUsable(mStepCount))
                    .count();
        }

        void setStepCount(int stepCount) {
            this.mStepCount = stepCount;
        }

        TimeCreditItem getSelection() {
            return Stream.of(TimeCreditItem.values())
                    .sortBy(TimeCreditItem::getStepCount)
                    .collect(Collectors.toList())
                    .get(mSelectionIndex);
        }

        /**
         * The view holder for the time credit item.
         *
         * @author Fabio Hellmann
         */
        public final class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.textViewTradeOffer)
            TextView mTextViewTradeOffer;
            private int mDefaultBackgroundColor;

            ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                mDefaultBackgroundColor = mTextViewTradeOffer.getDrawingCacheBackgroundColor();
            }

            @OnClick(R.id.textViewTradeOffer)
            public void onClick() {
                if(mSelectionIndex == getAdapterPosition()) {
                    mTextViewTradeOffer.setBackgroundColor(mDefaultBackgroundColor);
                    mSelectionIndex = -1;
                } else {
                    // reset all items to default color
                    Stream.of(viewHolders).forEach(view -> view.mTextViewTradeOffer
                            .setBackgroundColor(mDefaultBackgroundColor));

                    // highlight the new selected item
                    final int color = getResources().getColor(R.color.secondaryLightColor);
                    mTextViewTradeOffer.setBackgroundColor(color);
                    mSelectionIndex = getAdapterPosition();
                }
            }
        }
    }
}
