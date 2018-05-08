package com.edu.uni.augsburg.uniatron.ui.home.dialogs;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.ui.home.HomeViewModel;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * The shop dialog is a chooser for a {@link com.edu.uni.augsburg.uniatron.model.Emotions}.
 *
 * @author Fabio Hellmann
 */
public class EmotionDialogFragment extends DialogFragment {
    /**
     * The name of this dialog.
     */
    public static final String NAME = EmotionDialogFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_emotion, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setAllowReturnTransitionOverlap(false);
        setAllowEnterTransitionOverlap(false);
        setCancelable(false);

        final HomeViewModel model = ViewModelProviders.of(this).get(HomeViewModel.class);
    }

    /**
     * The on click event of the ok button is registered here.
     */
    @OnClick(R.id.okButton)
    public void onOkButtonClicked() {
        dismiss();
    }
}
