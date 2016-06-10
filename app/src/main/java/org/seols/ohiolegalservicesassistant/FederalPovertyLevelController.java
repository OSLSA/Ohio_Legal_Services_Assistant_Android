package org.seols.ohiolegalservicesassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by joshuagoodwin on 10/1/15.
 */
public class FederalPovertyLevelController extends Fragment implements IncomeDialogFragment.OnUpdateIncomeListener {

    private Bundle savedInstanceState;

    private EditText etAGSize, etGrossEarnedIncome;

    private Spinner versionSpinner;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle instanceState) {

        savedInstanceState = instanceState;
        // get view
        View rootView = inflater.inflate(R.layout.fpl_layout, container, false);

        // populate the version spinner
        populateFrequencySpinner(rootView);

        // set up the edittexts
        initializeViews(rootView);

        // set up the clear button
        clearButton(rootView);

        // set up the submit button
        submitButton(rootView);

        //return view
        return rootView;
    }


    private void populateFrequencySpinner(View v) {
        versionSpinner = (Spinner) v.findViewById(R.id.version_spinner);

        // create array adapter
       ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.fpl_version));

        // set layout for when dropdown shown
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // apply adapter to spinner
        versionSpinner.setAdapter(adapter);

        // set spinner default to current year
        versionSpinner.setSelection(0);
    }

    private void initializeViews(View rootView) {
        etAGSize = (EditText) rootView.findViewById(R.id.etAGSize);
        etGrossEarnedIncome = (EditText) rootView.findViewById(R.id.etGrossEarnedIncome);
        etGrossEarnedIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showIncomeDialog("Gross Earned Income");
            }
        });
        etGrossEarnedIncome.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showIncomeDialog("Gross Earned Income");
                }
            }
        });
    }

    // TODO is this needed?
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Make sure fragment codes match up
        if (requestCode == 0) {
            String etGrossEarnedIncome = data.getStringExtra(
                    "result");
        }
    }

    private void clearButton(View rootView) {

        Button button = (Button) rootView.findViewById(R.id.clear);

        // set button's onClickListener
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetAll();
            }
        });
    }

    private void resetAll() {

        // reset the EditTexts
        etAGSize.setText("");
        etGrossEarnedIncome.setText("");

    }

    private void submitButton(View rootView) {
        Button button = (Button) rootView.findViewById(R.id.submit);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                calculateFPL();
            }
        });
    }

    private void calculateFPL() {

        // check to see if AGSize is filled in
        if (AGSizeMissing()) return;

        // calculate percentage of poverty
        double annualIncome = etGrossEarnedIncome.getText().toString().equals("") ? 0.0 :
                Double.parseDouble(etGrossEarnedIncome.getText().toString());

        FederalPovertyLevel calculator = new FederalPovertyLevel(
                getAGSize(), // int size
                versionSpinner.getSelectedItem().toString(), // String year
                annualIncome, // double annualIncome
                getContext()  // context
        );

        showResults(calculator.getResults());
    }

    private boolean AGSizeMissing() {

        // see if AGSize is greater than 0.
        // if it is, return false
        // if it is less than 1, show toast and return true

        String test = etAGSize.getText().toString();

        if (test.equals("") || test.equals("0")) {
            Toast toast = Toast.makeText(getActivity(), getString(R.string.error_ag_size), Toast.LENGTH_LONG);
            toast.show();
            return true;
        } else {
            return false;
        }

    }

    private int getAGSize() {

        return Integer.parseInt(etAGSize.getText().toString());
    }

    private void showResults(Double results) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // This should be coming from the string xml file with variables in it
        builder.setMessage("This household is at " + results + "% of the Federal Poverty Level")
                .setPositiveButton("OK", null)
                .setTitle("Percentage of Poverty");
        builder.create().show();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putString("AGSize", etAGSize.getText().toString());
        outState.putString("etGrossEarnedIncome", etGrossEarnedIncome.getText().toString());
    }

    private void showIncomeDialog(String title) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Bundle args = new Bundle();
        args.putString("title", title);
        IncomeDialogFragment dialog = new IncomeDialogFragment();
        dialog.setTargetFragment(this, 0);
        dialog.setArguments(args);
        dialog.show(fm, "IncomeDialog");
    }

    @Override
    public void onIncomeSubmit(String annualIncome) {
        etGrossEarnedIncome.setText(annualIncome);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title
        String title = getArguments().getString("title");
        ((AppCompatActivity)getActivity()).getSupportActionBar()
                .setTitle(title);
    }
}