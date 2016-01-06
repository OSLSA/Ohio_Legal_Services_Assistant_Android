package org.seols.ohiolegalservicesassistant;

/**
 * Created by joshuagoodwin on 10/2/15.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.os.Bundle;
import android.app.AlertDialog;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.CheckBox;

public class FoodStampsCalculatorController extends Fragment implements IncomeDialogFragment.OnUpdateIncomeListener {

    private static final int[] FA_ALLOTMENT = { 194, 357, 511, 649, 771, 925, 1022, 1169, 1315, 1461, 1607, 1753 };
    private static final int[] STANDARD_DEDUCTION = { 155, 155, 155, 165, 193, 221, 221, 221, 221, 221, 221, 221 };
    private static final int[] NET_STANDARD = { 973, 1311, 1650, 1988, 2326, 2665, 3003, 3341, 3680, 4019, 4358, 4697 };
    private static final int[] GROSS_INCOME_LIMIT = { 1265, 1705, 2144, 2584, 3024, 3464, 3904, 4344, 4784, 5224, 5664, 6104 };
    private static final int[] GROSS_INCOME_165 = { 1605, 2163, 2722, 3280, 3838, 4396, 4955, 5513, 6072, 6631, 7190, 7749 };
    private static final int[] GROSS_INCOME_200 = { 1945, 2622, 3299, 3975, 4652, 5329, 6005, 6682, 7359, 8035, 8712, 9389 };
    private static final int STANDARD_SHELTER_HOMELESS = 143;
    private static final int EXCESS_INCOME_DEDUCTION = 20;
    private static final int EXCESS_MEDICAL_DEDUCTION = 35;
    private static final int DEPENDENT_CARE_DEDUCTION = 0;
    private static final int MINNIMUM_MONTHLY_ALLOTMENT = 16;
    private static final int STANDARD_UTILITY_ALLOWANCE = 498;
    private static final int LIMITED_UTILITY_ALLOWANCE = 330;
    private static final int SINGLE_UTILITY_ALLOWANCE = 73;
    private static final int STANDARD_TELEPHONE_ALLOWANCE = 39;
    private static final int LIMIT_ON_SHELTER_DEDUCTION = 490;

    private boolean isAged, isDisabled;

    private CheckBox cbElectricGasOil, cbGarbageTrash, cbHeatingCooling, cbHomeless, cbPhone, cbWaterSewer, cbAGSSI, cbAGAged;

    private double earnedIncome, earnedHoursPerWeek, unearnedHoursPerWeek, unearnedIncome;

    private EditText etAGsize;
    private EditText etChildSupport;
    private EditText etDependentCare;
    private EditText etEarnedIncome;
    private EditText etMedicalExpenses;
    private EditText etPropertyInsurance;
    private EditText etPropertyTaxes;
    private EditText etRent;
    private EditText etUnearnedIncome;
    private EditText requestingET;

    private int AGSize;
    private int childSupport;
    private int dependentCare;
    private int finalEarnedIncome;
    private int finalNetIncome;
    private int finalUnearnedIncome;
    private int grossIncomeAmount;
    private int medicalExpenses;
    private int propertyInsurance;
    private int propertyTaxes;
    private int rent;
    private int totalGrossIncome;
    private int utilityAllowance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.food_stamp_layout, container, false);
        initializeViews(rootView);
        initializeHomelessCheck();
        initializeClearButton(rootView);
        initializeSubmitButton(rootView);
        // if (savedInstanceState != null) restoreState(savedInstanceState);
        return rootView;
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

        Button button = (Button) rootView.findViewById(R.id.submit);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                submitPressed();
            }

        });
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

        // check for valid AG size
        boolean validAGSize = isValidAGSize();
        // toast and finish if invalid AG size
        if (!validAGSize) {
            Toast toast = Toast.makeText(getActivity(), "AG Size must be 1 or larger", Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        // fill all other variables
        getVariables();

        // figure utility allowance
        calculateUtilityAllowance();

        // set net income and other standards
        grossIncomeAmount = GROSS_INCOME_LIMIT[AGSize - 1];

        if (!checkTotalGrossIncome()) {
            String results = "The total gross income of $" + totalGrossIncome + " exceeds the gross income limit of $" + grossIncomeAmount + " by $" + (totalGrossIncome - grossIncomeAmount);
            ineligibleDialog("Ineligible", results);
            return;
        }

        if (!checkNetIncome()) {
            String results = "The total net income of $" + finalNetIncome + " exceeds the net income limit of $" + NET_STANDARD[AGSize - 1] + " by $" + (finalNetIncome - NET_STANDARD[AGSize -1]);
            ineligibleDialog("Ineligible", results);
            return;
        }

		/* 	OAC 5101:4-4-27
		*	(c) If the assistance group is subject to the net income standard, compare
		*	the assistance group's net monthly income to the maximum net monthly income
		*	standard. If the assistance group's net income is greater than the net
		*	monthly income standard, the assistance group is ineligible. If the
		*	assistance group's net income is equal to or less than the net monthly income
		*	standard, the assistance group is eligible. Multiply the net monthly income by
		*	thirty per cent.
		*	(d) Round the product up to the next whole dollar if it ends in one cent
		*	through ninety-nine cents */

        finalNetIncome = Math.max(0, finalNetIncome);

        int benefitAmount = FA_ALLOTMENT[AGSize - 1] - (int)Math.ceil(finalNetIncome * 0.3);

		/* 	OAC 5101:4-4-27
		*	(f) If the benefit is for a one or two person assistance group and the
		*	computation results in a benefit of less than the minimum benefit
		*	allotment, round up to the minimum benefit amount. */

        if (isDisabled || isAged || AGSize <= 3) benefitAmount = Math.max(benefitAmount,MINNIMUM_MONTHLY_ALLOTMENT);
        String results = "Eligible for food stamps in the amount of $" + benefitAmount + " per month";
        ineligibleDialog("Eligible", results);

    }

    private boolean checkNetIncome() {

        // 5101:4-4-31(S)

		/*	5101:4-4-31(S)(2) Earned income deduction: Multiply the total gross
		*	monthly earned income by twenty per cent and subtract that amount
		*	from the total gross income. */

        finalNetIncome = totalGrossIncome - (int)Math.floor(finalEarnedIncome * 0.2);

		/* (3) Standard deduction: Subtract the standard deduction. */

        finalNetIncome -= STANDARD_DEDUCTION[AGSize - 1];

		/* 	(4) Excess medical deduction: If the AG is entitled to an excess
		*	medical deduction, determine if total medical expenses exceed
		*	thirty-five dollars. If so, subtract that portion which exceeds
		*	thirty-five dollars. */
        medicalExpenses = Math.max(0, medicalExpenses - EXCESS_MEDICAL_DEDUCTION);
        finalNetIncome -= medicalExpenses;

		/* (5) Dependent care deduction: Subtract monthly dependent care expenses, if any. */

        finalNetIncome -= dependentCare;

		/* 	(6) Legally obligated child support deduction: Subtract the allowable
		*	monthly child support payments in accordance with rule 5101:4-4-23 of
		*	the Administrative Code. */

        finalNetIncome -= childSupport;

		/* 	(7) Standard homeless shelter deduction: Subtract the standard homeless
		*	shelter deduction amount if any, up to the maximum of one hundred
		*	forty-three dollars if the AG is homeless and it incurs shelter costs during the month. */

        if (cbHomeless.isChecked()) finalNetIncome -= STANDARD_SHELTER_HOMELESS;

		/*	(8) Determining any excess shelter cost: Total the allowable shelter
		*	expenses to determine shelter costs, unless a deduction has been
		*	subtracted in accordance with paragraph (S)(7) of this rule. Subtract
		*	from total shelter costs fifty per cent of the AG's monthly income after
		*	all the above deductions have been subtracted. The remaining amount, if any,
		*	is the excess shelter cost. If there is no excess shelter cost, go to the next step.
		*	(9) Applying any excess shelter cost :Subtract the excess shelter cost
		*	up to the maximum amount allowed (unless the AG is entitled to the full
		*	amount of its excess shelter expenses) from the AG's monthly income
		*	after all other applicable deductions. AGs not subject to the shelter
		*	limitation shall have the full amount exceeding fifty per cent of their
		*	adjusted income subtracted. The AG's net monthly income has been determined. */

        finalNetIncome -= calculateShelterDeduction();

        boolean result = finalNetIncome <= NET_STANDARD[AGSize - 1];

        result = cbAGSSI.isChecked() || result;

        if (totalGrossIncome <= GROSS_INCOME_200[AGSize - 1] && (isAged || isDisabled)) return true;

        return result;

    }

    private int calculateShelterDeduction() {

		/*	OAC 5101:4-4-23 Food assistance: deductions from income.
		*	(E) Shelter costs: monthly shelter costs over fifty per cent of the assistance group's income
		*	after all other deductions contained in this rule have been allowed. If the assistance group
		*	does not contain an elderly or disabled member, as defined in rule 5101:4-1-03 of the
		*	Administrative Code, the shelter deduction cannot exceed the maximum shelter deduction provided.
		*	These assistance groups shall receive an excess shelter deduction for the entire monthly cost
		*	that exceeds fifty per cent of the assistance group income after all other deductions
		*	contained in this rule have been allowed. */

        int shelterExpenses = utilityAllowance + rent + propertyInsurance + propertyTaxes;
        shelterExpenses -= (finalNetIncome / 2);
        shelterExpenses = Math.max(0, shelterExpenses);

        if (!isAged) {
            shelterExpenses = Math.min(shelterExpenses, LIMIT_ON_SHELTER_DEDUCTION);
        }

        return shelterExpenses;

    }

    private void ineligibleDialog(String title, String text){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(text)
                .setPositiveButton("OK", null)
                .setTitle(title);
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
        isAged = cbAGAged.isChecked() ? true : false;
        isDisabled = cbAGSSI.isChecked() || isAged ? true : false;


        // get incomes as doubles or processing later
        unearnedIncome = etUnearnedIncome.getText().toString().equals("") ? 0 : Double.parseDouble(etUnearnedIncome.getText().toString());
        earnedIncome = etEarnedIncome.getText().toString().equals("") ? 0 : Double.parseDouble(etEarnedIncome.getText().toString());

        // figure utility allowance
        calculateUtilityAllowance();

    }

    private boolean checkTotalGrossIncome() {

		/* OAC 5101:4-4-31
		(R) Method of calculating gross monthly income
		Except for AGs containing at least one member who is elderly or disabled as defined in rule 5101:4-1-03 of the Administrative Code, or considered categorically eligible, all AGs shall be subject to the gross income eligibility standard for the appropriate AG size. To determine the AG's total gross income, add the gross monthly income earned by all AG members and the total monthly unearned income of all AG members, minus income exclusions. If an AG has income from a farming operation (with gross proceeds of more than one thousand dollars per year) which operates at a loss, see rule 5101:4-6-11 of the Administrative Code. The total gross income is compared to the gross income eligibility standard for the appropriate AG size. If the total gross income is less than the standard, proceed with calculating the adjusted net income as described in paragraph (S) of this rule. If the total gross income is more than the standard, the AG is ineligible for program benefits and the case is either denied or terminated at this point. */

        totalGrossIncome = finalEarnedIncome + finalUnearnedIncome;
        boolean results = totalGrossIncome <= grossIncomeAmount;
        if (isAged || isDisabled) results = true;
        return results;

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

    private void calculateUtilityAllowance() {

        /* int homeless = cbHomeless.isChecked() ? 1 : 0; */
        int phone = cbPhone.isChecked() ? 2 : 0;
        int heating = cbHeatingCooling.isChecked() ? 4 : 0;
        int electric = cbElectricGasOil.isChecked() ? 8 : 0;
        int water = cbWaterSewer.isChecked() ? 8 : 0;
        int garbage = cbGarbageTrash.isChecked() ? 8 : 0;

        int test = phone + heating + electric + water + garbage;

        switch (test) {

            case 0:
                utilityAllowance = 0;
                break;
            /* case 1:
                utilityAllowance = STANDARD_SHELTER_HOMELESS;
                break; */
            case 2:
                utilityAllowance = STANDARD_TELEPHONE_ALLOWANCE;
                break;
            case 4:
            case 6:
            case 12:
            case 14:
            case 20:
            case 22:
            case 28:
            case 30:
                utilityAllowance = STANDARD_UTILITY_ALLOWANCE;
                break;
            case 8:
                utilityAllowance = SINGLE_UTILITY_ALLOWANCE;
                break;
            default:
                utilityAllowance = LIMITED_UTILITY_ALLOWANCE;
                break;

        }


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
        Double monthlyIncome = Double.parseDouble(annualIncome);
        monthlyIncome = (double)Math.round((monthlyIncome / 12.0) * 1000 / 1000);
        requestingET.setText("" + monthlyIncome);
    }
}
