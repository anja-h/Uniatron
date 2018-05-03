package com.edu.uni.augsburg.uniatron.home;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.edu.uni.augsburg.uniatron.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * The {@link HomeFragment} is the main entry point for the user.
 *
 * @author Fabio Hellmann
 */
public class HomeFragment extends Fragment {

    @BindView(R.id.appUsageChart)
    PieChart mAppUsagePieChart;
    @BindView(R.id.stepButton)
    Button mStepButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAppUsagePieChart.setUsePercentValues(true);
        mAppUsagePieChart.getDescription().setEnabled(false);
        mAppUsagePieChart.setDrawHoleEnabled(true);
        mAppUsagePieChart.setDrawCenterText(true);

        final PieDataSet pieDataSet = new PieDataSet(new ArrayList<>(), "");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        final PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());

        final HomeViewModel homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);

        homeViewModel.getAppUsageOfTop3Apps().observe(this, item -> {
            pieDataSet.clear();
            if (item != null) {
                float value = 0;
                for (Map.Entry<String, Double> entry : item.entrySet()) {
                    value += entry.getValue().floatValue();
                    pieDataSet.addEntry(
                            new PieEntry(entry.getValue().floatValue(), entry.getKey())
                    );
                }
                pieDataSet.addEntry(new PieEntry(1 - value, getString(R.string.app_others)));
            }
            pieDataSet.notifyDataSetChanged();
            pieData.notifyDataChanged();
            mAppUsagePieChart.animateY(1000, Easing.EasingOption.EaseInElastic);
            mAppUsagePieChart.notifyDataSetChanged();
            mAppUsagePieChart.invalidate();
        });

        homeViewModel.getStepCountsToday().observe(this, stepCount -> {
            if (stepCount == null) {
                mAppUsagePieChart.setCenterText(String.valueOf(0));
            } else {
                mAppUsagePieChart.setCenterText(String.valueOf(stepCount));
            }
        });

        homeViewModel.getRemainingStepCountToday().observe(this, stepCount -> {
            if (stepCount == null) {
                mStepButton.setText(String.valueOf(0));
            } else {
                mStepButton.setText(String.valueOf(stepCount));
            }
        });
    }

    /**
     * Is called when the step button is clicked.
     *
     * @param view The button.
     */
    @OnClick(R.id.stepButton)
    public void onStepButtonClick(View view) {
        mStepButton.setText("Test");
    }
}
