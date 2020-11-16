package org.seols.ohiolegalservicesassistant;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Joshua Goodwin on 2/26/16.
 * <p/>
 * License information
 */
public class FoodStampController extends Fragment implements IncomeDialogFragment.OnUpdateIncomeListener{

    private boolean isAged, isDisabled;

    private Button submit;

    private ArrayList<String> versionKeys;

    private ArrayList<ArrayList<String>> allotment, grossincome, gross165, gross200, netstandard, standarddeduction;

    private ArrayList<String> homeless, standardutility, telephone, singleutility, excessmedical, minnimumallot, shelterlimit, excessincome, dependentcare, limitedUtility;

    private CheckBox cbElectricGasOil, cbGarbageTrash, cbHeatingCooling, cbHomeless, cbPhone, cbWaterSewer, cbAGSSI, cbAGAged;

    private double earnedIncome, unearnedIncome;

    private EditText etAGsize, etChildSupport, etDependentCare, etEarnedIncome, etMedicalExpenses, etPropertyInsurance, etPropertyTaxes, etRent, etUnearnedIncome, requestingET;

    private Spinner versionSpinner;

    private DatabaseReference mRootRef, mFSRef, mVersionRef;

    private int AGSize, childSupport, dependentCare, medicalExpenses, propertyInsurance, propertyTaxes, rent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.food_stamp_layout, container, false);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mFSRef = mRootRef.child("FoodStamps");
        initializeVariables();
        initializeViews(rootView);
        initializeHomelessCheck();
        initializeClearButton(rootView);
        initializeSubmitButton(rootView);
        logSearch("Food Stamps Opened");
        // if (savedInstanceState != null) restoreState(savedInstanceState);
        return rootView;
    }

    private void initializeVariables() {
        allotment = new ArrayList<ArrayList<String>>();
        grossincome = new ArrayList<ArrayList<String>>();
        gross165 = new ArrayList<ArrayList<String>>();
        gross200 = new ArrayList<ArrayList<String>>();
        netstandard = new ArrayList<ArrayList<String>>();
        standarddeduction = new ArrayList<ArrayList<String>>();
        dependentcare = new ArrayList<String>();
        homeless = new ArrayList<String>();
        standardutility = new ArrayList<String>();
        telephone = new ArrayList<String>();
        limitedUtility = new ArrayList<>();
        singleutility = new ArrayList<String>();
        excessmedical = new ArrayList<String>();
        minnimumallot = new ArrayList<String>();
        shelterlimit = new ArrayList<String>();
        excessincome = new ArrayList<String>();
    }

    private void initializeViews(View rootView){

        // initialize check boxes
        cbElectricGasOil = (CheckBox) rootView.findViewById(R.id.electricGasOil);
        cbGarbageTrash = (CheckBox) rootView.findViewById(R.id.garbageTrash);
        cbHeatingCooling = (CheckBox) rootView.findViewById(R.id.heatingCooling);
        cbHomeless = (CheckBox) rootView.findViewById(R.id.clientHomeless);
        cbPhone = (CheckBox) rootView.findViewById(R.id.phone);
        cbWaterSewer = (CheckBox) rootView.findViewById(R.id.waterSewer);

        // initialize spinners
        cbAGSSI = (CheckBox) rootView.findViewById(R.id.agSSISwitch);
        cbAGAged = (CheckBox) rootView.findViewById(R.id.agAgedSwitch);
        versionSpinner = (Spinner) rootView.findViewById(R.id.version_spinner);
        setSpinner();

        // initialize EditTexts
        etAGsize = (EditText) rootView.findViewById(R.id.etAGSize);
        etChildSupport = (EditText) rootView.findViewById(R.id.etChildSupport);
        etDependentCare = (EditText) rootView.findViewById(R.id.etDependentCare);
        etEarnedIncome = (EditText) rootView.findViewById(R.id.etEarnedIncome);
        addListeners(etEarnedIncome, getResources().getString(R.string.tvEarnedIncome));
        etMedicalExpenses = (EditText) rootView.findViewById(R.id.etMedicalExpenses);
        etPropertyInsurance = (EditText) rootView.findViewById(R.id.etPropertyInsurance);
        etPropertyTaxes = (EditText) rootView.findViewById(R.id.etPropertyTaxes);
        etRent = (EditText) rootView.findViewById(R.id.etRent);
        etUnearnedIncome = (EditText) rootView.findViewById(R.id.etUnearnedIncome);
        addListeners(etUnearnedIncome, getResources().getString(R.string.tvUnearnedIncome));

    }

    private void setSpinner() {

        mVersionRef = mFSRef.child("Versions");
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
                timer.cancel();
                submit.setEnabled(true);

                setVersionKeys(keys);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void setVersionKeys(ArrayList<String> v) {
        versionKeys = new ArrayList<String>();
        versionKeys = v;
        getData();
        Log.d("keys", versionKeys.toString());
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

    private void getData() {

        String[] dataToGet = {"Allotment", "GrossIncome", "GrossIncome165", "GrossIncome200", "NetStandard", "StandardDeduction"};
        final String[] smallData = {"DependentCare", "ExcessIncomeDeduction", "ShelterLimit", "ExcessMedical", "LimitedUtility", "MinnimumAllotment", "SingleUtility", "StandardHomeless", "StandardUtility", "Telephone"};

        for (final String name : dataToGet) {

            for (String k : versionKeys) {
                DatabaseReference mAllotment = mFSRef.child(name + k);
                mAllotment.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<String> incomingList = new ArrayList<>();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String s = String.valueOf(child.getValue());
                            incomingList.add(s);
                        }
                        addToBigList(incomingList, name);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        }
        for (final String name: smallData) {


            for (String k : versionKeys) {
                DatabaseReference ref = mFSRef.child(name + k);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        smallDataAdd(name, String.valueOf(dataSnapshot.getValue()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        }

    }

    private void smallDataAdd(String name, String info) {
        switch (name) {
            case "DependentCare":
                dependentcare.add(info);
                break;
            case "ShelterLimit":
                shelterlimit.add(info);
                break;
            case "ExcessIncomeDeduction":
                excessincome.add(info);
                break;
            case "ExcessMedical":
                excessmedical.add(info);
                break;
            case "LimitedUtility":
                limitedUtility.add(info);
                break;
            case "MinnimumAllotment":
                minnimumallot.add(info);
                break;
            case "SingleUtility":
                singleutility.add(info);
                break;
            case "StandardUtility":
                standardutility.add(info);
                break;
            case "Telephone":
                telephone.add(info);
                break;
            case "StandardHomeless":
                homeless.add(info);
                break;
            default:
                break;
        }
    }

    private void addToBigList(ArrayList<String> l, String name) {

        switch (name) {
            case "Allotment":
                allotment.add(l);
                break;
            case "GrossIncome":
                grossincome.add(l);
                break;
            case "GrossIncome165":
                gross165.add(l);
                break;
            case "GrossIncome200":
                gross200.add(l);
                break;
            case "NetStandard":
                netstandard.add(l);
                break;
            case "StandardDeduction":
                standarddeduction.add(l);
                break;
            default:
                break;
        }
    }

    private void initializeClearButton(View rootView){

        Button button = (Button) rootView.findViewById(R.id.clear);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                resetAll();
            }

        });
    }

    private void initializeSubmitButton(View rootView) {

        submit = (Button) rootView.findViewById(R.id.submit);
        submit.setEnabled(false);
        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                submitPressed();
            }

        });
    }

    private void logSearch(String value) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, value);
        ((MainActivity)getActivity()).recordAnalytics(bundle);
    }

    private void resetAll(){

        cbAGAged.setChecked(false);
        cbAGSSI.setChecked(false);

        etAGsize.setText("");
        etChildSupport.setText("");
        etDependentCare.setText("");
        etEarnedIncome.setText("");
        etUnearnedIncome.setText("");
        etMedicalExpenses.setText("");
        etPropertyInsurance.setText("");
        etPropertyTaxes.setText("");
        etRent.setText("");

        cbGarbageTrash.setEnabled(true);
        cbHeatingCooling.setEnabled(true);
        cbHomeless.setEnabled(true);
        cbWaterSewer.setEnabled(true);
        cbPhone.setEnabled(true);
        cbElectricGasOil.setEnabled(true);

    }

    private void submitPressed() {

        // validate AG size
        if (!isValidAGSize()) {
            Toast toast = Toast.makeText(getActivity(), "AG Size must be 1 or larger", Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        // log Firebase analytics
        logSearch("Food Stamps Calculated");

        // fill all other variables
        getVariables();

        // create the food stamp calculator
        FoodStampCalculator calculator = new FoodStampCalculator(createVariableBundle(), getContext());
        resultsDialog(calculator.getResults());

    }

    private void resultsDialog(Bundle results){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(results.getString("results"))
                .setPositiveButton("OK", null)
                .setTitle(results.getString("title"));
        // Create the AlertDialog object and return it
        builder.create().show();
    }

    private boolean isValidAGSize() {

        String test = etAGsize.getText().toString();
        AGSize = test.equals("") ? 0 : Integer.parseInt(test);
        return AGSize > 0;

    }

    private void getVariables() {

        // get main variables, convert to doubles, then round down and cast as ints
        childSupport = etChildSupport.getText().toString().equals("") ? 0 : (int)Math.floor(Double.parseDouble(etChildSupport.getText().toString()));
        dependentCare = etDependentCare.getText().toString().equals("") ? 0 : (int)Math.floor(Double.parseDouble(etDependentCare.getText().toString()));
        medicalExpenses = etMedicalExpenses.getText().toString().equals("") ? 0 : (int)Math.floor(Double.parseDouble(etMedicalExpenses.getText().toString()));
        propertyInsurance = etPropertyInsurance.getText().toString().equals("") ? 0 : (int)Math.floor(Double.parseDouble(etPropertyInsurance.getText().toString()));
        propertyTaxes = etPropertyTaxes.getText().toString().equals("") ? 0 : (int)Math.floor(Double.parseDouble(etPropertyTaxes.getText().toString()));
        rent = etRent.getText().toString().equals("") ? 0 : (int)Math.floor(Double.parseDouble(etRent.getText().toString()));
        isAged = cbAGAged.isChecked();
        isDisabled = (cbAGSSI.isChecked() || isAged);


        // get incomes as doubles or processing later
        unearnedIncome = etUnearnedIncome.getText().toString().equals("") ? 0 : Double.parseDouble(etUnearnedIncome.getText().toString());
        earnedIncome = etEarnedIncome.getText().toString().equals("") ? 0 : Double.parseDouble(etEarnedIncome.getText().toString());

    }

    /**
     * Method creates a bundle properly formatted to construct the FoodStampCalculator
     **/
    private Bundle createVariableBundle() {

        int pos = versionSpinner.getSelectedItemPosition();
        Bundle bundle = new Bundle();
        bundle.putInt("AGSize", AGSize);
        bundle.putDouble("earnedIncome", earnedIncome);
        bundle.putDouble("unearnedIncome", unearnedIncome);
        bundle.putBoolean("isAged", isAged);
        bundle.putBoolean("isDisabled", isDisabled);
        bundle.putInt("medicalExpenses", medicalExpenses);
        bundle.putInt("dependentCare", dependentCare);
        bundle.putInt("childSupport", childSupport);
        bundle.putBoolean("isHomeless", cbHomeless.isChecked());
        bundle.putBoolean("AGSSI", cbAGSSI.isChecked());
        bundle.putInt("utilityAllowance", calculateUtilityAllowance());
        bundle.putInt("rent", rent);
        bundle.putInt("propertyInsurance", propertyInsurance);
        bundle.putInt("propertyTaxes", propertyTaxes);
        //bundle.putString("version", getVersion());
        bundle.putStringArray("allotment", allotment.get(pos).toArray(new String[0]));
        bundle.putStringArray("standardDeduction", standarddeduction.get(pos).toArray(new String[0]));
        bundle.putStringArray("netStandard", netstandard.get(pos).toArray(new String[0]));
        bundle.putStringArray("grossIncomeLimit", grossincome.get(pos).toArray(new String[0]));
        bundle.putStringArray("gross165", gross165.get(pos).toArray(new String[0]));
        bundle.putStringArray("gross200", gross200.get(pos).toArray(new String[0]));
        bundle.putString("standardHomeless", homeless.get(pos));
        bundle.putString("excessIncome", excessincome.get(pos));
        bundle.putString("excessMedical", excessmedical.get(pos));
        bundle.putString("dependent", dependentcare.get(pos));
        bundle.putString("minnimumAllotment", minnimumallot.get(pos));
        bundle.putString("standardUtility", standardutility.get(pos));
        bundle.putString("limitedUtility", limitedUtility.get(pos));
        bundle.putString("singleUtility", singleutility.get(pos));
        bundle.putString("standardTelephone", telephone.get(pos));
        bundle.putString("shelterDeduction", shelterlimit.get(pos));
        return bundle;

    }

    private void initializeHomelessCheck() {
        cbHomeless.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbHomeless.isChecked()) {
                    // set utilities to disabled and unchecked
                    cbGarbageTrash.setEnabled(false);
                    cbGarbageTrash.setChecked(false);
                    cbHeatingCooling.setEnabled(false);
                    cbHeatingCooling.setChecked(false);
                    cbWaterSewer.setEnabled(false);
                    cbWaterSewer.setChecked(false);
                    cbPhone.setEnabled(false);
                    cbPhone.setChecked(false);
                    cbElectricGasOil.setEnabled(false);
                    cbElectricGasOil.setChecked(false);
                    // set rent, taxes, and insurance to 0
                    etPropertyInsurance.setText("0");
                    etPropertyInsurance.setEnabled(false);
                    etPropertyTaxes.setText("0");
                    etPropertyTaxes.setEnabled(false);
                    etRent.setText("0");
                    etRent.setEnabled(false);
                    // Toast explaining changes made
                    // TODO this toast message should be in the strings file
                    Toast toast = Toast.makeText(getActivity(), "Rent, taxes, and property insurance set to 0 as they aren't allowed for homeless applicants", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    cbGarbageTrash.setEnabled(true);
                    cbHeatingCooling.setEnabled(true);
                    cbWaterSewer.setEnabled(true);
                    cbPhone.setEnabled(true);
                    cbElectricGasOil.setEnabled(true);
                    etPropertyInsurance.setEnabled(true);
                    etPropertyTaxes.setEnabled(true);
                    etRent.setEnabled(true);
                }
            }
        });
    }
    private int calculateUtilityAllowance() {
        /* int homeless = cbHomeless.isChecked() ? 1 : 0; */
        int phone = cbPhone.isChecked() ? 2 : 0;
        int heating = cbHeatingCooling.isChecked() ? 4 : 0;
        int electric = cbElectricGasOil.isChecked() ? 8 : 0;
        int water = cbWaterSewer.isChecked() ? 8 : 0;
        int garbage = cbGarbageTrash.isChecked() ? 8 : 0;
        return phone + heating + electric + water + garbage;

    }

    private void restoreState(Bundle savedInstanceState) {

        cbElectricGasOil.setChecked(savedInstanceState.getBoolean("cbElectricGasOil"));
        cbGarbageTrash.setChecked(savedInstanceState.getBoolean("cbGarbageTrash"));
        cbHeatingCooling.setChecked(savedInstanceState.getBoolean("cbHeatingCooling"));
        cbHomeless.setChecked(savedInstanceState.getBoolean("cbHomeless"));
        cbPhone.setChecked(savedInstanceState.getBoolean("cbPhone"));
        cbWaterSewer.setChecked(savedInstanceState.getBoolean("cbWaterSewer"));

        etAGsize.setText(savedInstanceState.getString("etAGsize"));
        etChildSupport.setText(savedInstanceState.getString("etChildSupport"));
        etDependentCare.setText(savedInstanceState.getString("etDependentCare"));
        etEarnedIncome.setText(savedInstanceState.getString("etEarnedHoursPerWeek"));
        etMedicalExpenses.setText(savedInstanceState.getString("etMedicalExpenses"));
        etPropertyInsurance.setText(savedInstanceState.getString("etPropertyInsurance"));
        etPropertyTaxes.setText(savedInstanceState.getString("etPropertyTaxes"));
        etRent.setText(savedInstanceState.getString("etRent"));

        cbAGAged.setChecked(savedInstanceState.getBoolean("AGaged", false));
        cbAGSSI.setChecked(savedInstanceState.getBoolean("AGSSI", false));
    }

    @Override
    public void onSaveInstanceState(Bundle OutState) {

        OutState.putBoolean("cbElectricGasOil", cbElectricGasOil.isChecked());
        OutState.putBoolean("cbGarbageTrash", cbGarbageTrash.isChecked());
        OutState.putBoolean("cbHeatingCooling", cbHeatingCooling.isChecked());
        OutState.putBoolean("cbHomeless", cbHomeless.isChecked());
        OutState.putBoolean("cbPhone", cbPhone.isChecked());
        OutState.putBoolean("cbWaterSewer", cbWaterSewer.isChecked());

        OutState.putString("etAGsize", etAGsize.getText().toString());
        OutState.putString("etChildSupport", etChildSupport.getText().toString());
        OutState.putString("etDependentCare", etDependentCare.getText().toString());
        OutState.putString("etEarnedIncome", etEarnedIncome.getText().toString());
        OutState.putString("etMedicalExpenses", etMedicalExpenses.getText().toString());
        OutState.putString("etPropertyInsurance", etPropertyInsurance.getText().toString());
        OutState.putString("etPropertyTaxes", etPropertyTaxes.getText().toString());
        OutState.putString("etRent", etRent.getText().toString());

        OutState.putBoolean("AGaged", cbAGAged.isChecked());
        OutState.putBoolean("AGSSI", cbAGSSI.isChecked());

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
        double monthlyIncome = Double.parseDouble(annualIncome);
        monthlyIncome = (double)Math.round((monthlyIncome / 12.0) * 1000 / 1000);
        requestingET.setText("" + monthlyIncome);
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
