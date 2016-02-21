package org.seols.ohiolegalservicesassistant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

/**
 * Created by joshuagoodwin on 2/21/16.
 */
public class GarnishmentFragment extends Fragment {

    EditText etNetIncome, etHours;
    LinearLayout hoursLayout;
    Spinner frequency;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle onSavedInstantState) {
        View rootView = inflater.inflate(R.layout.garnishment_layout, container, false);
        getViews(rootView);
        setSpinner();
        return rootView;
    }

    private void getViews(View rootView) {
        etNetIncome = (EditText) rootView.findViewById(R.id.net_income);
        etHours = (EditText) rootView.findViewById(R.id.hours);
        hoursLayout = (LinearLayout) rootView.findViewById(R.id.hours_layout);
        frequency = (Spinner) rootView.findViewById(R.id.frequency_spinner);

        Button submit = (Button) rootView.findViewById(R.id.submit);
        Button clear = (Button) rootView.findViewById(R.id.clear);
        submit.setOnClickListener(buttonListener);
        clear.setOnClickListener(buttonListener);
    }

    private void setSpinner() {
        // create array adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.frequency, android.R.layout.simple_spinner_dropdown_item);

        // set layout for when dropdown shown
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // apply adapter to spinner
        frequency.setAdapter(adapter);

        // show and hide hours layout accordingly
        frequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (frequency.getSelectedItemPosition() == 0) {
                    hoursLayout.setVisibility(View.VISIBLE);
                } else {
                    hoursLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void resetAll() {
        etNetIncome.setText("");
        etHours.setText("");
    }

    private void calculateGarnishability() {

    }

    private View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.submit:
                    calculateGarnishability();
                    break;
                case R.id.clear:
                    resetAll();
                    break;
                default:
                    break;
            }
        }
    };
}
