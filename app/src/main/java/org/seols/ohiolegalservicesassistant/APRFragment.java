package org.seols.ohiolegalservicesassistant;

/**
 * Created by joshuagoodwin on 10/2/15.
 */

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

public class APRFragment extends Fragment {

    private EditText etAmountBorrowed, etBaseRate, etCosts, etNumberOfPayments;
    private Button btnSubmit, btnClear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.apr_layout, container, false);
        initializeViews(rootView);
        initializeButtons();
        logSearch("APR Opened");
        return rootView;
    }

    public void showWarning(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getActivity().getString(R.string.apr_warning))
                .setPositiveButton("OK", null)
                .setTitle("Warning");
        // Create the AlertDialog object and return it
        builder.create().show();
    }

    // Link all views on layout to variables in this file
    private void initializeViews(View rootView) {

        etAmountBorrowed = (EditText) rootView.findViewById(R.id.amountBorrowed);
        etBaseRate = (EditText) rootView.findViewById(R.id.baseRate);
        etCosts = (EditText) rootView.findViewById(R.id.costs);
        etNumberOfPayments = (EditText) rootView.findViewById(R.id.numberOfPayments);
        btnSubmit = (Button) rootView.findViewById(R.id.submit);
        btnClear = (Button) rootView.findViewById(R.id.clear);
        TextView warning = (TextView) rootView.findViewById(R.id.tvWarning);
        warning.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showWarning(v);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title
        String title = getArguments().getString("title");
        ((AppCompatActivity)getActivity()).getSupportActionBar()
                .setTitle(title);
    }
    // Set onClick listeners for submit and clear buttons
    private void initializeButtons() {

        // onClickListener for submit button
        btnSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                calculateAPR();
            }
        });

        // onClickListener for clear button
        // sets all editexts back to defaults
        btnClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                etAmountBorrowed.setText("");
                etBaseRate.setText("");
                etCosts.setText("");
                etNumberOfPayments.setText("");
            }
        });
    }

    private void logSearch(String value) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, value);
        ((MainActivity)getActivity()).recordAnalytics(bundle);
    }

    // method to calculate APR and then display results
    private void calculateAPR() {

        logSearch("APR Calculated");
        // check for valid data in the input fields
        if (dataIsInvalid()) return;

        // costs are allowed to be 0 so set to 0 if not answered
        double costs = etCosts.getText().toString().equals("") ? 0.0 : Double.parseDouble(etCosts.getText().toString());

        APRCalculator calculator = new APRCalculator(
                Double.parseDouble(etAmountBorrowed.getText().toString()), // double amountBorrowed
                Double.parseDouble(etBaseRate.getText().toString()), // double baseRate,
                costs, // double costs
                Integer.parseInt(etNumberOfPayments.getText().toString()) // int numberOfPayments
        );

        // display results
        showResultsDialog(
                calculator.getAPR(), // testrate
                calculator.getMonthlyPayment(), // monthlyPayment
                calculator.getTotalPayments(), // totalPayments
                calculator.getTotalInterest() // totalInterest
        );

    }

    /**
     * Checks the data coming from the app to ensure it is valid and
     * won't throw an error
     * @return
     */
    private boolean dataIsInvalid() {

        // check for missing data to see if number of payments is missing or 0
        // if true, alert user and return true to indicate invalid data
        if (etNumberOfPayments.getText().toString().equals("") || etNumberOfPayments.getText().toString().equals("0")) {
            Toast.makeText(getActivity(), "The number of payments is missing or is 0", Toast.LENGTH_LONG).show();
            return true;
        }

        // check to see if base rate is missing or 0
        // if true, alert user and return true to indicate invalid data
        if (etBaseRate.getText().toString().equals("") || etBaseRate.getText().toString().equals("0")) {
            Toast.makeText(getActivity(), "The base rate cannot be blank", Toast.LENGTH_LONG).show();
            return true;
        }
        // check to see if amount borrowed is - or empty
        // if true, alert user and return true to indicate invalid data
        if (etAmountBorrowed.getText().toString().equals("") || etAmountBorrowed.getText().toString().equals("0")) {
            Toast.makeText(getActivity(), "Base rate cannot be blank or 0", Toast.LENGTH_LONG).show();
            return true;
        }

        // return false to show that all data is valid
        return false;

    }

    // TODO add comment

    // dialog to display results
    private void showResultsDialog(Double APR, Double monthlyPayment, Double totalPayments, Double totalInterest) {

        // TODO have this come from strings file
        String text = "The APR is " + APR + "% and the monthly payment is $" + monthlyPayment + ". Total paid is $" + totalPayments + ", of which, $" + totalInterest + " is interest.";
        // build alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(text)
                .setPositiveButton("OK", null)
                .setTitle("APR");
        // Create the AlertDialog object and return it
        builder.create().show();
        Log.d("APR", "showResultsDialog: ");
    }

}