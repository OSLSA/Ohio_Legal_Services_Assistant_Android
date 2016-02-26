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

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by joshuagoodwin on 10/2/15.
 */
public class HomeFragment extends Fragment {

    private Bundle savedInstanceState;

    private Button viewRules;

    private EditText income, agSize, rule_number;

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
        rule_number = (EditText) rootView.findViewById(R.id.rule_number);
        Button calculateFPL = (Button) rootView.findViewById(R.id.calculateFPL);
        calculateFPL.setOnClickListener(calculateFPLListener);
        Button ruleSelected = (Button) rootView.findViewById(R.id.view_rules);
        ruleSelected.setOnClickListener(ruleSelectedListener);
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

    }

    private View.OnClickListener calculateFPLListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // check to see if AGSize is filled in
            if (AGSizeMissing()) return;

            // calculate percentage of poverty
            double annualIncome = income.getText().toString().equals("") ? 0.0 :
                    Double.parseDouble(income.getText().toString());

            FederalPovertyLevel calc = new FederalPovertyLevel(
                    Integer.parseInt(agSize.getText().toString()),
                    "2015",
                    annualIncome,
                    getContext()
            );

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

    private View.OnClickListener ruleSelectedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // figure out which book to go to
            int ruleBook = rulesSpinner.getSelectedItemPosition();
            String[] books = getResources().getStringArray(R.array.rules_tags);
            String bookName = books[ruleBook];

            // get that books TOC
            String[] toc = getResources().getStringArray(getResources().getIdentifier(bookName + "_toc", "array", "org.seols.ohiolegalservicesassistant"));

            // see if rule typed in exists
            // get rule
            String rule = rule_number.getText().toString();
            int rulePosition = -1;
            // check all rules
            for (int i = 0; i < toc.length; i++) {
                if (toc[i].startsWith(rule)) {
                    rulePosition = i;
                    break;
                }
            }
            // display error if rule doesn't exist
            if (rulePosition < 0) {
                String text = "Sorry, that rule does not exist";
                errorToast(text);
                return;
            }

            // create bundle to push
            Bundle args = new Bundle();
            args.putString("ruleNumber", rule);
            args.putString("bookName", bookName);
            args.putInt("rulePosition", rulePosition);

            // change framents
            Fragment newFragment = new RulesDetailFragment();
            ((MainActivity)getActivity()).setFragment(newFragment, rule, "Rule " + rule, args);

        }
    };

}
