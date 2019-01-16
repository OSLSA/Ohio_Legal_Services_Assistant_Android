package org.seols.ohiolegalservicesassistant;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by joshuagoodwin on 10/1/15.
 */
public class FederalPovertyLevelController extends Fragment implements IncomeDialogFragment.OnUpdateIncomeListener {

    private Bundle savedInstanceState;

    private DatabaseReference mRootRef, mPovertyLevelRef, mVersionRef, mYearRef;

    private EditText etAGSize, etGrossEarnedIncome;

    private Spinner versionSpinner;

    double annualIncome;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle instanceState) {

        savedInstanceState = instanceState;
        // get view
        View rootView = inflater.inflate(R.layout.fpl_layout, container, false);

        // get FB references
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mPovertyLevelRef = mRootRef.child("povertyLevel");
        mPovertyLevelRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mVersionRef = mPovertyLevelRef.child("fplVersion");

        // populate the version spinner
        populateFrequencySpinner(rootView);

        // set up the edittexts
        initializeViews(rootView);

        // set up the clear button
        clearButton(rootView);

        // set up the submit button
        submitButton(rootView);

        // log Firebase analytics that form was opened
        logSearch("FPL Opened");

        //return view
        return rootView;
    }


    private void populateFrequencySpinner(View v) {

        final View rv = v;
        mVersionRef.addListenerForSingleValueEvent(new ValueEventListener() {

            CountDownTimer timer = new CountDownTimer(5000,5000) {

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    //pb.setVisibility(View.INVISIBLE);
                    Toast toast = Toast.makeText(getActivity(), "Sorry, we can't connect to the database. Try again later", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
            }.start();

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> versions = new ArrayList<String>();
                versions = (ArrayList<String>) dataSnapshot.getValue();
                versionSpinner = (Spinner) rv.findViewById(R.id.version_spinner);

                // create array adapter
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, versions);

                // set layout for when dropdown shown
                 adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // apply adapter to spinner
                versionSpinner.setAdapter(adapter);

                // set spinner default to current year
                versionSpinner.setSelection(0);
                timer.cancel();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //pb.setVisibility(View.INVISIBLE);
                Toast toast = Toast.makeText(getActivity(), "Sorry, we can't connect to the database. Try again later", Toast.LENGTH_LONG);
                toast.show();
            }
        });

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
                getResults();
            }
        });
    }

    private void logSearch(String value) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, value);
        ((MainActivity)getActivity()).recordAnalytics(bundle);
    }

    private void getResults() {
        annualIncome = etGrossEarnedIncome.getText().toString().equals("") ? 0.0 :
                Double.parseDouble(etGrossEarnedIncome.getText().toString());
        String version = "fpl" + versionSpinner.getSelectedItem().toString();

        mYearRef = mPovertyLevelRef.child(version);
        mYearRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Long> fplInfo = (ArrayList<Long>) dataSnapshot.getValue();
                Long povertyStart = fplInfo.get(0);
                Long povertyIncrement = fplInfo.get(1);
                double fpl = ((getAGSize() - 1) * povertyIncrement) + povertyStart;
                double results = Math.floor(((annualIncome / fpl) * 100) * 100) / 100;
                showResults(results);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
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