package com.migapro.wearunitconverter.ui;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.migapro.wearunitconverter.R;
import com.migapro.wearunitconverter.model.numberpadkey.NumberPadKey;
import com.migapro.wearunitconverter.utility.NumberPadUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

public class NumberPadDialogFragment extends DialogFragment {

    @InjectView(R.id.gridview) GridView gridView;

    private NumberInputDialogListener listener;

    public interface NumberInputDialogListener {
        public void onKeyPress(NumberPadKey key);
        public void onDialogDismiss();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_number_pad, container, false);

        ButterKnife.inject(this, view);

        gridView.setAdapter(new NumberPadGridAdapter(getActivity()));

        return view;
    }

    @OnItemClick(R.id.gridview)
    public void onGridViewItemClick(View view) {
        NumberPadKey numberPadKey =
                NumberPadUtil.generateNumberPadKey(((Button) view).getText().toString());

        listener.onKeyPress(numberPadKey);
    }

    @Override
    public void onResume() {
        super.onStart();
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, calcDisplayHeightFromBottom());
    }

    private int calcDisplayHeightFromBottom() {
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        int height = (int) (displayMetrics.heightPixels / displayMetrics.density);
        return (int) (height / 1.75);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        listener.onDialogDismiss();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (NumberInputDialogListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
