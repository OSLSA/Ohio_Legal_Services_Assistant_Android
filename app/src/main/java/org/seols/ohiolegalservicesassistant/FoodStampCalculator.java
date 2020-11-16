package org.seols.ohiolegalservicesassistant;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class FoodStampCalculator {
	
	private boolean isAged, isDisabled, isHomeless, AGSSI;
	
	private Bundle bundle;

	private double totalGrossIncome;
	
	private String[] FA_ALLOTMENT;
	private String[] STANDARD_DEDUCTION;
	private String[] NET_STANDARD;
	private String[] GROSS_INCOME_LIMIT;
	private String[] GROSS_INCOME_200;
	
	private String STANDARD_SHELTER_HOMELESS;
	private String EXCESS_MEDICAL_DEDUCTION;
	private String MINNIMUM_MONTHLY_ALLOTMENT;
	private String STANDARD_UTILITY_ALLOWANCE;
	private String LIMITED_UTILITY_ALLOWANCE;
	private String SINGLE_UTILITY_ALLOWANCE;
	private String STANDARD_TELEPHONE_ALLOWANCE;
	private String LIMIT_ON_SHELTER_DEDUCTION;
	
	private int AGSize, earnedIncome, unearnedIncome, medicalExpenses,finalNetIncome, dependentCare, childSupport, utilityAllowance, rent, propertyInsurance, propertyTaxes;
	
	private String dialogTitle, dialogResults;
	
	/**
	 * Public constructor for the food stamp calculator class.
	 * @param bundle The bundle of all of the information from the controller needed to calculate food stamps. must contain the following:
     *               
     * @param context Need to pass in the context so that .xml resources can be accessed
	 **/
	public FoodStampCalculator(Bundle bundle, Context context) {
		this.bundle = bundle;
		//String version = this.bundle.getString("version");
		//setConstants(version);
		setConstants(bundle);
		setVariables(bundle);
	}
	
	/**
	 * Method that pulls all of the relevant arrays and sets the constants for calculators based on the version of the calculator selected.
	 * @param bundle bundle with Firebase JFS data calculator to use as pulled from the version spinner
	 **/
	private void setConstants(Bundle bundle) {

		FA_ALLOTMENT = bundle.getStringArray("allotment");
//		FA_ALLOTMENT = context.getResources().getStringArray(context.getResources().getIdentifier("fa_allotment_" + version, "array", MainActivity.PACKAGE_NAME));
		STANDARD_DEDUCTION = bundle.getStringArray("standardDeduction");
//		STANDARD_DEDUCTION = context.getResources().getStringArray(context.getResources().getIdentifier("standard_deduction_" + version, "array", packageName));
		NET_STANDARD = bundle.getStringArray("netStandard");
//		NET_STANDARD = context.getResources().getStringArray(context.getResources().getIdentifier("net_standard_" + version, "array", packageName));
		GROSS_INCOME_LIMIT = bundle.getStringArray("grossIncomeLimit");
//		GROSS_INCOME_LIMIT = context.getResources().getStringArray(context.getResources().getIdentifier("gross_income_limit_" + version, "array", packageName));
		String[] GROSS_INCOME_165 = bundle.getStringArray("gross165");
//		GROSS_INCOME_165 = context.getResources().getStringArray(context.getResources().getIdentifier("gross_income_165_" + version, "array", packageName));
		GROSS_INCOME_200 = bundle.getStringArray("gross200");
//		GROSS_INCOME_200 = context.getResources().getStringArray(context.getResources().getIdentifier("gross_income_200_" + version, "array", packageName));
		
		// strings
		STANDARD_SHELTER_HOMELESS = bundle.getString("standardHomeless");
//		STANDARD_SHELTER_HOMELESS = context.getResources().getString(context.getResources().getIdentifier("standard_homeless_" + version, "string", packageName));
		String EXCESS_INCOME_DEDUCTION = bundle.getString("excessIncome");
//		EXCESS_INCOME_DEDUCTION = context.getResources().getString(context.getResources().getIdentifier("excess_income_deduction_" + version, "string", packageName
//        ));
		EXCESS_MEDICAL_DEDUCTION = bundle.getString("excessMedical");
//		EXCESS_MEDICAL_DEDUCTION = context.getResources().getString(context.getResources().getIdentifier("excess_medical_" + version, "string", packageName));
		String DEPENDENT_CARE_DEDUCTION = bundle.getString("dependent");
//		DEPENDENT_CARE_DEDUCTION = context.getResources().getString(context.getResources().getIdentifier("dependent_care_" + version, "string", packageName));
		MINNIMUM_MONTHLY_ALLOTMENT = bundle.getString("minnimumAllotment");
//		MINNIMUM_MONTHLY_ALLOTMENT = context.getResources().getString(context.getResources().getIdentifier("minnimum_allotment_" + version, "string", packageName));
		STANDARD_UTILITY_ALLOWANCE = bundle.getString("standardUtility");
//		STANDARD_UTILITY_ALLOWANCE = context.getResources().getString(context.getResources().getIdentifier("standard_utility_" + version, "string", packageName));
		LIMITED_UTILITY_ALLOWANCE = bundle.getString("limitedUtility");
//		LIMITED_UTILITY_ALLOWANCE = context.getResources().getString(context.getResources().getIdentifier("limited_utility_" + version, "string", packageName));
		SINGLE_UTILITY_ALLOWANCE = bundle.getString("singleUtility");
//		SINGLE_UTILITY_ALLOWANCE = context.getResources().getString(context.getResources().getIdentifier("single_utility_" + version, "string", packageName));
		STANDARD_TELEPHONE_ALLOWANCE = bundle.getString("standardTelephone");
//		STANDARD_TELEPHONE_ALLOWANCE = context.getResources().getString(context.getResources().getIdentifier("telephone_" + version, "string", packageName));
		LIMIT_ON_SHELTER_DEDUCTION = bundle.getString("shelterDeduction");
//		LIMIT_ON_SHELTER_DEDUCTION = context.getResources().getString(context.getResources().getIdentifier("shelter_limit_" + version, "string", packageName));
	}

	private void setVariables(Bundle bundle) {
		AGSize = bundle.getInt("AGSize");
		earnedIncome = (int)bundle.getDouble("earnedIncome");
		unearnedIncome = (int)bundle.getDouble("unearnedIncome");
		isAged = bundle.getBoolean("isAged");
		isDisabled = bundle.getBoolean("isDisabled");
		medicalExpenses = bundle.getInt("medicalExpenses");
		dependentCare = bundle.getInt("dependentCare");
		childSupport = bundle.getInt("childSupport");
		isHomeless = bundle.getBoolean("isHomeless");
		AGSSI = bundle.getBoolean("AGSSI");
        utilityAllowance = bundle.getInt("utilityAllowance");
        rent = bundle.getInt("rent");
        propertyInsurance = bundle.getInt("propertyInsurance");
        propertyTaxes = bundle.getInt("propertyTaxes");
	}

    private int calculateUtilityAllowance() {
        switch (utilityAllowance) {

            case 0:
                return 0;
            /* case 1:
                utilityAllowance = STANDARD_SHELTER_HOMELESS;
                break; */
            case 2:
                return Integer.parseInt(STANDARD_TELEPHONE_ALLOWANCE);
            case 4:
            case 6:
            case 12:
            case 14:
            case 20:
            case 22:
            case 28:
            case 30:
                return Integer.parseInt(STANDARD_UTILITY_ALLOWANCE);
            case 8:
                return Integer.parseInt(SINGLE_UTILITY_ALLOWANCE);
            default:
                return Integer.parseInt(LIMITED_UTILITY_ALLOWANCE);
        }
    }
	
	private void calculateFoodStamps() {
		
		// check to see if the gross income test is needed (ag not aged or disabled) and then see if it is passed
		boolean passedGrossIncomeTest = checkTotalGrossIncome();
		if (!isAged || !isDisabled) {
            // if gross income test fails, kick out
			if (!passedGrossIncomeTest) return;
		}
		
		// those eligible for Benefits bank and also aged or disabled don't do net income test
		boolean noNeedToCheckNetIncome = (totalGrossIncome <= Integer.parseInt(GROSS_INCOME_200[AGSize - 1]) && (isAged || isDisabled));
		boolean passedNetIncome = checkNetIncome();

		if (!noNeedToCheckNetIncome) {
			if (!passedNetIncome) return;
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
		*	through ninety-nine cents
		*/

        finalNetIncome = Math.max(0, finalNetIncome);

        int benefitAmount = Integer.parseInt(FA_ALLOTMENT[AGSize - 1]) - (int)Math.ceil(finalNetIncome * 0.3);

		/* 	OAC 5101:4-4-27
		*	(f) If the benefit is for a one or two person assistance group and the
		*	computation results in a benefit of less than the minimum benefit
		*	allotment, round up to the minimum benefit amount. */

        if (isDisabled || isAged || AGSize <= 3) benefitAmount = Math.max(benefitAmount, Integer.parseInt(MINNIMUM_MONTHLY_ALLOTMENT));
        dialogResults = "Eligible for food stamps in the amount of $" + benefitAmount + " per month";
        dialogTitle = "Eligible";
	}

    /**
     * Checks whether the income given passes the gross income test is passed
     * @return boolean True if the test is passed, false if it fails
     **/
	private boolean checkTotalGrossIncome() {
        /* OAC 5101:4-4-31
		(R) Method of calculating gross monthly income
		Except for AGs containing at least one member who is elderly or disabled as defined in rule 5101:4-1-03 of the Administrative Code, or considered categorically eligible, all AGs shall be subject to the gross income eligibility standard for the appropriate AG size. To determine the AG's total gross income, add the gross monthly income earned by all AG members and the total monthly unearned income of all AG members, minus income exclusions. If an AG has income from a farming operation (with gross proceeds of more than one thousand dollars per year) which operates at a loss, see rule 5101:4-6-11 of the Administrative Code. The total gross income is compared to the gross income eligibility standard for the appropriate AG size. If the total gross income is less than the standard, proceed with calculating the adjusted net income as described in paragraph (S) of this rule. If the total gross income is more than the standard, the AG is ineligible for program benefits and the case is either denied or terminated at this point. */
        int grossIncomeLimit = Integer.parseInt(GROSS_INCOME_LIMIT[AGSize - 1]);
		totalGrossIncome = earnedIncome + unearnedIncome;
        boolean results = (int)Math.floor(totalGrossIncome) <= grossIncomeLimit;
        
		// if income test fails, set results
		if (!results) {
			dialogResults = "The total gross income of $" + totalGrossIncome + " exceeds the gross income limit of $" + grossIncomeLimit + " by $" + (totalGrossIncome - grossIncomeLimit);
            dialogTitle = "Ineligible";
		}
		
		// return whether the gross income test was passed
		return results;
			
	}
	
	/**
	 * Checks whether the income given passes the net income test is passed
	 * @return boolean True if the test is passed, false if it fails
	 **/
	private boolean checkNetIncome() {

        // 5101:4-4-31(S)

		/*	5101:4-4-31(S)(2) Earned income deduction: Multiply the total gross
		*	monthly earned income by twenty per cent and subtract that amount
		*	from the total gross income. */


        finalNetIncome = (int)totalGrossIncome - (int)Math.floor(earnedIncome * 0.2);

		/* (3) Standard deduction: Subtract the standard deduction. */

        finalNetIncome -= Integer.parseInt(STANDARD_DEDUCTION[AGSize - 1]);

		/* 	(4) Excess medical deduction: If the AG is entitled to an excess
		*	medical deduction, determine if total medical expenses exceed
		*	thirty-five dollars. If so, subtract that portion which exceeds
		*	thirty-five dollars. */
        medicalExpenses = Math.max(0, medicalExpenses - Integer.parseInt(EXCESS_MEDICAL_DEDUCTION));
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

        if (isHomeless) finalNetIncome -= Integer.parseInt(STANDARD_SHELTER_HOMELESS);

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
		Log.d("Final Net: ", Integer.toString(finalNetIncome));
        finalNetIncome -= calculateShelterDeduction();

        boolean result = finalNetIncome <= Integer.parseInt(NET_STANDARD[AGSize - 1]);

        result = AGSSI || result;

		if (!result) {
			dialogResults = "The total net income of $" + finalNetIncome + " exceeds the net income limit of $" + NET_STANDARD[AGSize - 1] + " by $" + (finalNetIncome - Integer.parseInt(NET_STANDARD[AGSize -1]));
            dialogTitle = "Ineligible";
		}
			
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

		int utility = calculateUtilityAllowance();
        int shelterExpenses = utility + rent + propertyInsurance + propertyTaxes;


        shelterExpenses -= (finalNetIncome / 2);
        shelterExpenses = Math.max(0, shelterExpenses);

        if (!isAged) {
            shelterExpenses = Math.min(shelterExpenses, Integer.parseInt(LIMIT_ON_SHELTER_DEDUCTION));
        }
		Log.d("Shelter Expense: ", Integer.toString(shelterExpenses));

        return shelterExpenses;
	}
	
	/**
	 * Returns a string containing the results of the calculation.
	 * @return [String] [title, results]
	 **/
	public Bundle getResults() {
		calculateFoodStamps();
		Bundle resultsBundle = new Bundle();
        resultsBundle.putString("results", dialogResults);
        resultsBundle.putString("title", dialogTitle);
        return resultsBundle;
	}
	
}