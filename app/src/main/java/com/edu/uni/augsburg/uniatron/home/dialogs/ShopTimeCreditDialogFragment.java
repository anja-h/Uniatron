package com.edu.uni.augsburg.uniatron.home.dialogs;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
        final TimeCreditListAdapter adapter = new TimeCreditListAdapter();
        mRecyclerView.setAdapter(adapter);

        final HomeViewModel model = ViewModelProviders.of(this).get(HomeViewModel.class);
        model.getRemainingStepCountToday().observe(this, stepCount -> {
            if (stepCount != null) {
                adapter.setStepCount(stepCount);
                adapter.notifyDataSetChanged();
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

    final class TimeCreditListAdapter extends
            RecyclerView.Adapter<TimeCreditListAdapter.ViewHolder> {

        private int mStepCount;

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.dialog_shop_time_credit_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final TimeCreditItem timeCreditItem = Stream.of(TimeCreditItem.values())
                    .sortBy(TimeCreditItem::getStepCount)
                    .collect(Collectors.toList())
                    .get(position);

            holder.mTextViewSteps.setText(String.valueOf(timeCreditItem.getStepCount()));
            holder.mTextViewTime.setText(getResources().getQuantityString(
                    R.plurals.minute,
                    timeCreditItem.getTimeInMinutes(),
                    timeCreditItem.getTimeInMinutes()));
            holder.mTimeCreditItem = timeCreditItem;
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

        /**
         * The view holder for the time credit item.
         *
         * @author Fabio Hellmann
         */
        public final class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.textViewSteps)
            TextView mTextViewSteps;
            @BindView(R.id.textViewTime)
            TextView mTextViewTime;
            private TimeCreditItem mTimeCreditItem;

            ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            @OnClick({R.id.textViewSteps, R.id.textViewTime})
            public void onClick() {
                if (getContext() != null) {
                    final MainApplication context = (MainApplication) getContext().getApplicationContext();
                    context.getRepository().addTimeCredit(mTimeCreditItem);
                    dismiss();
                }
            }
        }
    }
}
