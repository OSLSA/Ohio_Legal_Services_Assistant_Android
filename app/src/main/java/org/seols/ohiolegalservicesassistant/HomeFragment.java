package org.seols.ohiolegalservicesassistant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by joshuagoodwin on 10/2/15.
 */
public class HomeFragment extends Fragment {

    private Bundle savedInstanceState;

    private Button viewRules;

    private EditText income, agSize;

    private Spinner rulesSpinner;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle instanceState) {

        savedInstanceState = instanceState;
        View rootView = inflater.inflate(R.layout.home_layout, container, false);
        getViews(rootView);
        setRulesSpinner();
        return rootView;
    }

    private void getViews(View rootView) {
        income = (EditText) rootView.findViewById(R.id.annual_income);
        agSize = (EditText) rootView.findViewById(R.id.ag_size);
        Button calculateFPL = (Button) rootView.findViewById(R.id.calculateFPL);
        calculateFPL.setOnClickListener(calculateFPLListener);
        viewRules = (Button) rootView.findViewById(R.id.view_rules);
        rulesSpinner = (Spinner) rootView.findViewById(R.id.rules_spinner);
    }

    private void setRulesSpinner() {

        // create array adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.Rules));

        // set layout for when dropdown shown
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // apply adapter to spinner
        rulesSpinner.setAdapter(adapter);

        // set spinner default to current year
        rulesSpinner.setSelection(0);

    }

    private View.OnClickListener calculateFPLListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // check to see if AGSize is filled in
            if (AGSizeMissing()) return;

            // calculate percentage of poverty
            double annualIncome = income.getText().toString().equals("") ? 0.0 :
                    Double.parseDouble(income.getText().toString());

            FederalPovertyLevel calc = new FederalPovertyLevel();

            calc.setSize(Integer.parseInt(agSize.getText().toString()));
            calc.setAnnualIncome(annualIncome);
            calc.setYear("2015");

            showFPLResults(calc.getResults());
        }
    };

    private void showFPLResults(Double results) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("This household is at " + results + "% of the Federal Poverty Level")
                .setPositiveButton("OK", null)
                .setTitle("Percentage of Poverty");
        builder.create().show();

    }

    private boolean AGSizeMissing() {

        // see if AGSize is greater than 0.
        // if it is, return false
        // if it is less than 1, show toast and return true

        String test = agSize.getText().toString();

        if (test.equals("") || test.equals("0")) {
            String text = "The assistance group size must be 1 or larger";
            errorToast(text);
            return true;
        } else {
            return false;
        }

    }

    private void errorToast(String text) {

        Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_LONG);
        toast.show();

    }

}
