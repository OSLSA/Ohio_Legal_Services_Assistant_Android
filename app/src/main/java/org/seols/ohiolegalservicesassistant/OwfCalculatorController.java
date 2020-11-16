package org.seols.ohiolegalservicesassistant;

        import android.os.CountDownTimer;
        import androidx.annotation.NonNull;
        import androidx.fragment.app.Fragment;
        import android.app.AlertDialog;
        import android.os.Bundle;
        import androidx.fragment.app.FragmentManager;
        import androidx.appcompat.app.AppCompatActivity;
        import android.util.Log;
        import android.view.Gravity;
        import android.view.ViewGroup;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.widget.AdapterView.*;
        import android.widget.*;

        import com.google.firebase.analytics.FirebaseAnalytics;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.util.ArrayList;


public class OwfCalculatorController extends Fragment implements IncomeDialogFragment.OnUpdateIncomeListener {

    private double mDeemedIncome, mDependentCare, mGrossEarnedIncomeFinal, mUnearnedIncome;
    private EditText etAGSize, etDeemedIncome, etDependentCare, etGrossEarnedIncome, etUnearnedIncome, requestingET;
    private int mAGSize, countableIncome, version;
    private ArrayList<String> versionKeys;
    private ArrayList<ArrayList> bigList;
    private Spinner versionSpinner;
    private DatabaseReference mOWFRef;
    private Button submitButton;

    private ArrayList<ArrayList> OWF_PAYMENT_STANDARD, INITIAL_ELIGIBILITY_STANDARD;

    private Bundle savedInstanceState;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle instanceState) {

        View rootView = inflater.inflate(R.layout.owf_layout, container, false);
        OWF_PAYMENT_STANDARD = new ArrayList<ArrayList>();
        INITIAL_ELIGIBILITY_STANDARD = new ArrayList<ArrayList>();
        savedInstanceState = instanceState;
        submitButton(rootView);
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        mOWFRef = mRootRef.child("OWF");
        populateVersionSpinner(rootView);
        resetButton(rootView);
        initializeVariables(rootView);
        resetAll();
        logSearch("OWF Opened");

        // see if anything in SavedInstanceState Bundle from
        // prior restarts

        return rootView;

    }

    private void getData() {

        final int size = versionKeys.size();
        for (String k: versionKeys ) {
            DatabaseReference mPaymentStd = mOWFRef.child("PaymentStd" + k);
            DatabaseReference mInitEligStd = mOWFRef.child("InitialEligStd" + k);
            mPaymentStd.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<Long> incomingList = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        incomingList.add((Long)child.getValue());
                    }
                    addToBigList(incomingList);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            mInitEligStd.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<Long> incomingList = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        incomingList.add((Long)child.getValue());
                    }
                    addToEligList(incomingList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void addToBigList(ArrayList<Long> l) {
        OWF_PAYMENT_STANDARD.add(l);
    }

    private void addToEligList(ArrayList<Long> l) {
        INITIAL_ELIGIBILITY_STANDARD.add(l);
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

        versionSpinner = (Spinner) v.findViewById(R.id.owf_version_spinner);

        DatabaseReference mVersionRef = mOWFRef.child("Versions");

        final CountDownTimer timer = new CountDownTimer(5000,5000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                //pb.setVisibility(View.INVISIBLE);
                Toast toast = Toast.makeText(getActivity(), "Sorry, we can't connect to the database. Try again later", Toast.LENGTH_LONG);
                toast.show();
            }
        }.start();
        mVersionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<String> keys = new ArrayList<String>();
                ArrayList<String> vers = new ArrayList<String>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    vers.add(0, (String)child.getValue());
                    keys.add(0, (String)child.getKey());
                }

                // Create array adapter  using string-array
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, vers);

                // set layout for when dropdown shown
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // apply adapter to spinner
                versionSpinner.setAdapter(adapter);

                // set default to monthly
                versionSpinner.setSelection(0);
                submitButton.setEnabled(true);
                timer.cancel();

                setVersionKeys(keys);

                getData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast toast = Toast.makeText(getActivity(), "Sorry, we can't connect to the database. Try again later", Toast.LENGTH_LONG);
                toast.show();
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

    private void setVersionKeys(ArrayList<String> v) {
        versionKeys = new ArrayList<String>();
        versionKeys = v;
        Log.d("keys", versionKeys.toString());
    }

    private void submitButton(View rootView) {
        submitButton = (Button) rootView.findViewById(R.id.submit);
        submitButton.setEnabled(false);
        submitButton.setOnClickListener(new View.OnClickListener() {

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
        if (grossIncomeTotal > (int)(long)INITIAL_ELIGIBILITY_STANDARD.get(version).get(mAGSize)) {
            // if the income is too high, show alert dialog with explanation
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Initial Eligibility Test Failed. The total gross income of $" + grossIncomeTotal + " exceeds the initial eligibility standard of $" + (int)(long)INITIAL_ELIGIBILITY_STANDARD.get(version).get(mAGSize) + " by $" + (grossIncomeTotal - (int)(long)INITIAL_ELIGIBILITY_STANDARD.get(version).get(mAGSize)))
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

        int payStd = (int)(long)OWF_PAYMENT_STANDARD.get(version).get(mAGSize);

        // make sure earnedincome isn't negative
        int adjustedEarnedIncome = Math.max((int)Math.floor((mGrossEarnedIncomeFinal - 250) / 2), 0);
        countableIncome = (int)Math.floor(adjustedEarnedIncome - mDependentCare + mUnearnedIncome + mDeemedIncome);

        // compare countable income against payment standard
        if (countableIncome >= payStd) {
            // if the countable income is too high, show alert dialog with explanation
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Countable Income Test Failed. The total countable income of $" + countableIncome + " exceeds the OWF payment standard of $" +
                    payStd + " by $" + (countableIncome - payStd))
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
        int allotment;
        if (countableIncome >= 0) {
            allotment = ((int)(long)OWF_PAYMENT_STANDARD.get(version).get(mAGSize) - countableIncome);
        } else {
            allotment = (int)(long)OWF_PAYMENT_STANDARD.get(version).get(mAGSize);
        }
        builder.setMessage("Eligible for OWF in the amount of $" + allotment + " per month")
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
        double monthlyIncome = Double.parseDouble(annualIncome);
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
