package com.migapro.wearunitconverter.ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.migapro.wearunitconverter.R;
import com.migapro.wearunitconverter.model.Conversion;
import com.migapro.wearunitconverter.model.numberpadkey.NumberPadKey;
import com.migapro.wearunitconverter.utility.Constants;
import com.migapro.wearunitconverter.utility.NumberPadUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class MainActivity extends Activity implements NumberPadDialogFragment.NumberInputDialogListener {

    @InjectView(R.id.unit_from) TextView unitFromLabel;
    @InjectView(R.id.num_from) TextView numFromLabel;
    @InjectView(R.id.unit_to) TextView unitToLabel;
    @InjectView(R.id.num_to) TextView numToLabel;

    private Conversion mConversion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        initConversion();

        // TODO Display this e.g. once in twice for 5 times
        Toast.makeText(this, getString(R.string.long_press_tip), Toast.LENGTH_SHORT).show();
    }

    private void initConversion() {
        mConversion = new Conversion(this);
        updateUnitLabels();
    }

    private void updateUnitLabels() {
        unitFromLabel.setText(mConversion.getUnitFromName());
        unitToLabel.setText(mConversion.getUnitToName());
    }

    @OnClick(R.id.unit_from)
    public void onUnitFromClick() {
        startWearListActivityForResult(mConversion.getUnitNamesList(), Constants.REQUEST_CODE_UNIT_FROM);
    }

    @OnClick(R.id.num_from)
    public void onNumFromClick() {
        FragmentManager fm = getFragmentManager();
        NumberPadDialogFragment numberInputDialog = new NumberPadDialogFragment();
        numberInputDialog.show(fm, "fragment_number_pad");
    }

    @OnClick(R.id.unit_to)
    public void onUnitToClick() {
        startWearListActivityForResult(mConversion.getUnitNamesList(), Constants.REQUEST_CODE_UNIT_TO);
    }

    @OnLongClick({R.id.unit_from, R.id.unit_to})
    public boolean onUnitLongClick() {
        startWearListActivityForResult(
                getResources().getStringArray(R.array.measurement_types),
                Constants.REQUEST_CODE_CHANGE_MEASUREMENT_TYOE);
        return true;
    }

    private void startWearListActivityForResult(String[] data, int requestCode) {
        Intent intent = new Intent(this, WearListActivity.class);
        intent.putExtra(Constants.KEY_LISTVIEW_DATA, data);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        int unitSelectedIndex = data.getIntExtra(Constants.KEY_ITEM_SELECTED_POSITION, 0);

        if (requestCode == Constants.REQUEST_CODE_CHANGE_MEASUREMENT_TYOE) {
            mConversion.setMeasurementType(unitSelectedIndex);
            mConversion.loadUnitsList(this);
            updateUnitLabels();
            return;
        }

        if (requestCode == Constants.REQUEST_CODE_UNIT_FROM) {
            mConversion.setUnitFrom(unitSelectedIndex);
            unitFromLabel.setText(mConversion.getUnitFromName());
        } else if (requestCode == Constants.REQUEST_CODE_UNIT_TO) {
            mConversion.setUnitTo(unitSelectedIndex);
            unitToLabel.setText(mConversion.getUnitToName());
        }

        updateConversion();
    }

    private void updateConversion() {
        mConversion.convertNumber();
        numToLabel.setText(mConversion.getNumTo());
    }

    @Override
    public void onKeyPress(NumberPadKey key) {
        String processedNumber = key.processKey(mConversion.getNumFrom());
        mConversion.setNumFrom(processedNumber);
        numFromLabel.setText(processedNumber);
    }

    @Override
    public void onDialogDismiss() {
        String numberFrom = mConversion.getNumFrom();

        if (NumberPadUtil.isLastCharPeriod(numberFrom)) {
            mConversion.setNumFrom(numberFrom.substring(0, numberFrom.length() - 1));
            numFromLabel.setText(mConversion.getNumFrom());
        }

        if (NumberPadUtil.isNegativeZero(numberFrom)) {
            mConversion.setNumFrom("0");
            numFromLabel.setText(mConversion.getNumFrom());
        }

        updateConversion();
    }
}