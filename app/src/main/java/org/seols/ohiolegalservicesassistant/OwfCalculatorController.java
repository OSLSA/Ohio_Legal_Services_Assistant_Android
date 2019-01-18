package org.seols.ohiolegalservicesassistant;

        import android.support.annotation.NonNull;
        import android.support.v4.app.Fragment;
        import android.app.AlertDialog;
        import android.os.Bundle;
        import android.support.v4.app.FragmentManager;
        import android.support.v7.app.AppCompatActivity;
        import android.view.Gravity;
        import android.widget.EditText;
        import android.widget.Spinner;
        import android.widget.TextView;
        import android.widget.ArrayAdapter;
        import android.widget.AdapterView;
        import android.widget.Toast;
        import android.view.ViewGroup;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.support.v4.widget.DrawerLayout;
        import android.widget.ListView;
        import android.widget.AdapterView.*;
        import android.widget.*;

        import com.google.firebase.analytics.FirebaseAnalytics;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.util.ArrayList;
        import java.util.List;


public class OwfCalculatorController extends Fragment implements IncomeDialogFragment.OnUpdateIncomeListener {

    private double mDeemedIncome, mDependentCare, mGrossEarnedIncomeFinal, mUnearnedIncome;
    private EditText etAGSize, etDeemedIncome, etDependentCare, etGrossEarnedIncome, etUnearnedIncome, requestingET;
    private int mAGSize, countableIncome, version;
    private Spinner versionSpinner;
    private DatabaseReference mRootRef, mOWFRef, mVersionRef;

    private static final int[][] OWF_PAYMENT_STANDARD = {
            {289,395,483,596,698,776,898,926,1058,1153,1246,1342}, // January 2018
            {283,387,474,584,684,761,851,943,1037,1130,1222,1316}, // July 2017
            {283,387,474,584,684,761,851,943,1037,1130,1222,1316}, // January 2017
            {282,386,473,582,682,759,848,940,1034,1127,1218,1312}, // July 2016
            {282,386,473,582,682,759,848,940,1034,1127,1218,1312}, // January 2016
            {282,386,473,582,682,759,848,940,1034,1127,1218,1312}, // January 2015
            {277,380,465,572,671,746,834,924,1017,1108,1198,1290} // July 2014
    };

    private static final int[][] INITIAL_ELIGIBILITY_STANDARD = {
            {503, 677, 851 ,1025, 1200 ,1374, 1548 ,1722, 1896, 2070 ,2245, 2419}, // January 2018
            {503, 677, 851 ,1025, 1200 ,1374, 1548 ,1722, 1896, 2070 ,2245, 2419}, // July 2017
            {495,668,840,1013,1185,1358, 1531, 1704, 1878, 2051, 2224, 2398}, // January 2017
            {495,668,840,1013,1185,1358, 1531, 1704, 1878, 2051, 2224, 2398}, // Julyy 2017
            { 491, 664, 838, 1011, 1184, 1358, 1531, 1704, 1878, 2051, 2224, 2398}, // January 2016
            {487,656,825,994,1163,1333,1502,1671,1840,2009,2178,2348}, // January 2015
            {487,656,825,994,1163,1333,1502,1671,1840,2009,2178,2348} // July 2014
    };

    private Bundle savedInstanceState;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle instanceState) {

        View rootView = inflater.inflate(R.layout.owf_layout, container, false);
        savedInstanceState = instanceState;
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mOWFRef = mRootRef.child("OWF");
        populateVersionSpinner(rootView);
        resetButton(rootView);
        submitButton(rootView);
        initializeVariables(rootView);
        resetAll();
        logSearch("OWF Opened");

        // see if anything in SavedInstanceState Bundle from
        // prior restarts

        return rootView;

    }

    public OwfCalculatorController(){
        // emptypublicconstructor
    }

    public void onActivityCreated(){

        // see if anything in SavedInstanceState Bundle from
        // prior restarts
        if (savedInstanceState != null) getInstanceState(savedInstanceState);



    }

    private void initializeVariables(View rootView){
        etAGSize = (EditText)rootView.findViewById(R.id.AGSize);

        etDeemedIncome = (EditText)rootView.findViewById(R.id.deemedIncome);
        addListeners(etDeemedIncome, getString(R.string.tvDeemedIncome));

        etDependentCare = (EditText)rootView.findViewById(R.id.dependentCare);
        addListeners(etDependentCare, getString(R.string.tvDependentCare));

        etGrossEarnedIncome = (EditText)rootView.findViewById(R.id.grossEarnedIncome);
        addListeners(etGrossEarnedIncome, getString(R.string.tvGrossEarnedIncome));

        etUnearnedIncome = (EditText)rootView.findViewById(R.id.unearnedIncome);
        addListeners(etUnearnedIncome, getString(R.string.tvUnearnedIncome));
    }

    private void addListeners(EditText et, String title) {
        final String text = title;
        final EditText editText = et;
        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestingET = editText;
                showIncomeDialog(text);
            }
        });
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    requestingET = editText;
                    showIncomeDialog(text);
                }
            }
        });
    }

    private void resetAll(){
        etAGSize.setText("");
        etDeemedIncome.setText("");
        etDependentCare.setText("");
        etGrossEarnedIncome.setText("");
        etUnearnedIncome.setText("");
        versionSpinner.setSelection(0);
    }

    private void getInstanceState(Bundle savedInstanceState) {

        etAGSize.setText(savedInstanceState.getString("etAGSize"));
        etDeemedIncome.setText(savedInstanceState.getString("etDeemedIncome"));
        etDependentCare.setText(savedInstanceState.getString("etDependentCare"));
        etGrossEarnedIncome.setText(savedInstanceState.getString("etGrossEarnedIncome"));
        etUnearnedIncome.setText(savedInstanceState.getString("etUnearnedIncome"));

    }

    private void populateVersionSpinner(View v) {

        final View rv = v;
        versionSpinner = (Spinner) v.findViewById(R.id.owf_version_spinner);

        mVersionRef = mOWFRef.child("Versions");
        mVersionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<String> keys = new ArrayList<String>();
                ArrayList<String> vers = new ArrayList<String>();
                //vers = (ArrayList<String>) dataSnapshot.getValue();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    CountyNames cn = child.getValue(CountyNames.class);
                    vers.add(cn.name);
                }

                // Create array adapter  using string-array
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, vers);

                // set layout for when dropdown shown
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // apply adapter to spinner
                versionSpinner.setAdapter(adapter);

                // set default to monthly
                versionSpinner.setSelection(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // set listener to change visibility of hours per week as needed
        versionSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                version = position;

            }
            @Override
            public void onNothingSelected(AdapterView<?>arg0){}

        });

    }

    private void submitButton(View rootView) {
        Button button = (Button) rootView.findViewById(R.id.submit);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // check to see if any data is missing
                boolean dataMissing = checkForMissingData();

                // if data is missing, leave method
                if (dataMissing) return;

                // set variables from edit text fields
                setVariables();

                // determine initial eligibility
                boolean initialEligibilityMet = checkInitialEligibility();

                // if the initial eligibility test fails, exit method
                if (!initialEligibilityMet) return;

                // see if countable income exceeds payment standard
                boolean countableIncomeStandardMet = checkCountableIncome();

                // if countable income test fails, exit method
                if (!countableIncomeStandardMet) return;

                // if both tests passed, show dialog of results
                logSearch("OWF Calculated");
                displayResults();
            }
        });
    }

    private void resetButton(View rootView) {
        Button button = (Button) rootView.findViewById(R.id.clear);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                resetAll();
            }
        });
    }

    private boolean checkForMissingData() {

        etAGSize = (EditText)getView().findViewById(R.id.AGSize);
        etDeemedIncome = (EditText)getView().findViewById(R.id.deemedIncome);
        etDependentCare = (EditText)getView().findViewById(R.id.dependentCare);
        etGrossEarnedIncome = (EditText)getView().findViewById(R.id.grossEarnedIncome);
        etUnearnedIncome = (EditText)getView().findViewById(R.id.unearnedIncome);

        // check AGSize to make sure it is > 0

        String test = etAGSize.getText().toString();
        if (test.equals("") || test.equals("0")) {
            Toast toast = Toast.makeText(getActivity(), "Assistance group size must be 1 or larger", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL| Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            return true;
        } else {
            // have to do - 1 b/c arrays start counting from 0, not 1
            mAGSize = Integer.parseInt(test) - 1;
        }

        return false;
    }

    private void setVariables() {

        // get values from edit texts (AG Size set in error check)
        mGrossEarnedIncomeFinal = etGrossEarnedIncome.getText().toString().equals("") ?
                0 : Double.parseDouble(etGrossEarnedIncome.getText().toString());

        mDeemedIncome = etDeemedIncome.getText().toString().equals("") ?
                0 : Double.parseDouble(etDeemedIncome.getText().toString());

        mUnearnedIncome = etUnearnedIncome.getText().toString().equals("") ?
                0 : Double.parseDouble(etUnearnedIncome.getText().toString());

        mDependentCare = etDependentCare.getText().toString().equals("") ?
                0 : Double.parseDouble(etDependentCare.getText().toString());

        // round everything down (drop cents)
        // OAC 5101:1-23-20(E)(1)
        mGrossEarnedIncomeFinal = Math.floor(mGrossEarnedIncomeFinal);
        mDeemedIncome = Math.floor(mDeemedIncome);
        mDependentCare = Math.floor(mDependentCare);
        mUnearnedIncome = Math.floor(mUnearnedIncome);

    }

    private boolean checkInitialEligibility() {

        // this is the initial eligibility test
        // OAC 5101:1-23-20(H)(1)

        // calculate gross income for purposes of initial eligibility
        int grossIncomeTotal = (int)Math.floor(mGrossEarnedIncomeFinal - mDependentCare + mDeemedIncome + mUnearnedIncome);

        // see if initial eligibility test in OAC 5101:1-23-20(H)(1) is met
        if (grossIncomeTotal > INITIAL_ELIGIBILITY_STANDARD[version][mAGSize]) {
            // if the income is too high, show alert dialog with explanation
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Initial Eligibility Test Failed. The total gross income of $" + grossIncomeTotal + " exceeds the initial eligibility standard of $" + INITIAL_ELIGIBILITY_STANDARD[version][mAGSize] + " by $" + (grossIncomeTotal - INITIAL_ELIGIBILITY_STANDARD[version][mAGSize]))
                    .setPositiveButton("OK", null)
                    .setTitle("Ineligible");
            // Create the AlertDialog object and return it
            builder.create().show();
            // return false to let function know eligibility failed
            return false;
        } else {
            // if the initial eligibility test passes
            return true;
        }

    }

    /**
     * see if countable income exceeds OWF payment standard OAC 5101:1-23-20(H)(2)
     * @return return true if standard is met and false if test fails
     */
    private boolean checkCountableIncome() {

        // calculate countable income from OAC 5101:1-23-20(H)(2)(a)-(b)

        // make sure earnedincome isn't negative
        int adjustedEarnedIncome = Math.max((int)Math.floor((mGrossEarnedIncomeFinal - 250) / 2), 0);
        countableIncome = (int)Math.floor(adjustedEarnedIncome - mDependentCare + mUnearnedIncome + mDeemedIncome);

        // compare countable income against payment standard
        if (countableIncome >= OWF_PAYMENT_STANDARD[version][mAGSize]) {
            // if the countable income is too high, show alert dialog with explanation
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Countable Income Test Failed. The total countable income of $" + countableIncome + " exceeds the OWF payment standard of $" +
                    OWF_PAYMENT_STANDARD[version][mAGSize] + " by $" + (countableIncome - OWF_PAYMENT_STANDARD[version][mAGSize]))
                    .setPositiveButton("OK", null)
                    .setTitle("Ineligible");
            // Create the AlertDialog object and return it
            builder.create().show();
            // return false to let function know eligibility failed
            return false;
        } else {
            // countable income test passed
            return true;
        }

    }

    private void displayResults() {

        // display dialog showing how much OWF person can get
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Eligible for OWF in the amount of $" + (OWF_PAYMENT_STANDARD[version][mAGSize] - countableIncome) + " per month")
                .setPositiveButton("OK", null)
                .setTitle("Eligible");
        // Create the AlertDialog object and return it
        builder.create().show();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putString("etAGSize", etAGSize.getText().toString());
        outState.putString("etDeemedIncome", etDeemedIncome.getText().toString());
        outState.putString("etDependentCare", etDependentCare.getText().toString());
        outState.putString("etGrossEarnedIncome", etGrossEarnedIncome.getText().toString());
        outState.putString("etUnearnedIncome", etUnearnedIncome.getText().toString());

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

    public void onIncomeSubmit(String annualIncome) {
        Double monthlyIncome = Double.parseDouble(annualIncome);
        monthlyIncome = (double)Math.round((monthlyIncome / 12.0) * 1000 / 1000);
        requestingET.setText("" + monthlyIncome);
    }

    private void logSearch(String value) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, value);
        ((MainActivity)getActivity()).recordAnalytics(bundle);
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
