package org.seols.ohiolegalservicesassistant;

import android.os.Bundle;

public class FoodStampCalculator {
	
	private boolean isAged, isDisabled, isHomeless, AGSSI;
	
	private Bundle bundle;
	
	private double totalGrossIncome;
	
	private int[] FA_ALLOTMENT, STANDARD_DEDUCTION, NET_STANDARD, GROSS_INCOME_LIMIT, GROSS_INCOME_165, GROSS_INCOME_200;
	
	private int STANDARD_SHELTER_HOMELESS, EXCESS_INCOME_DEDUCTION, EXCESS_MEDICAL_DEDUCTION, DEPENDENT_CARE_DEDUCTION, MINNIMUM_MONTHLY_ALLOTMENT, STANDARD_UTILITY_ALLOWANCE, LIMITED_UTILITY_ALLOWANCE, SINGLE_UTILITY_ALLOWANCE, STANDARD_TELEPHONE_ALLOWANCE, LIMIT_ON_SHELTER_DEDUCTION;
	
	private int AGSize, earnedIncome, unearnedIncome, medicalExpenses,finalNetIncome, dependentCare, childSupport;
	
	private String dialogTitle, dialogResults;
	
	/**
	 * Public constructor for the food stamp calculator class.
	 * @param bundle The bundle of all of the information from the controller needed to calculate food stamps. must contain the following:
	 **/
	public FoodStampCalculator(Bundle bundle) {
		this.bundle = bundle;
		String version = this.bundle.getString("version");
		setConstants(version);
		setVariables();
	}
	
	/**
	 * Method that pulls all of the relevant arrays and sets the constants for calculators based on the version of the calculator selected.
	 * @param version Version of the calculator to use as pulled from the version spinner
	 **/
	private void setConstants(String version) {
		// arrays
		FA_ALLOTMENT = getResources.getStringArray(getResources().getIdentifier("fa_allotment_" + version, "array", getResources.getPackage()));
		STANDARD_DEDUCTION = getResources.getStringArray(getResources().getIdentifier("standard_deduction_" + version, "array", getResources.getPackage()));
		NET_STANDARD = getResources.getStringArray(getResources().getIdentifier("net_standard_" + version, "array", getResources.getPackage()));
		GROSS_INCOME_LIMIT = getResources.getStringArray(getResources().getIdentifier("gross_income_limit_" + version, "array", getResources.getPackage()));
		GROSS_INCOME_165 = getResources.getStringArray(getResources().getIdentifier("gross_income_165_" + version, "array", getResources.getPackage()));
		GROSS_INCOME_200 = getResources.getStringArray(getResources().getIdentifier("gross_income_200_" + version, "array", getResources.getPackage()));
		
		// strings
		STANDARD_SHELTER_HOMELESS = getResources.getString(getResources().getIdentifier("standard_homeless_" + version, "string", getResources.getPackage()));
		EXCESS_INCOME_DEDUCTION = getResources.getString(getResources().getIdentifier("excess_income_deduction_" + version, "string", getResources.getPackage()));
		EXCESS_MEDICAL_DEDUCTION = getResources.getString(getResources().getIdentifier("excess_medical_" + version, "string", getResources.getPackage()));
		DEPENDENT_CARE_DEDUCTION = getResources.getString(getResources().getIdentifier("dependent_care_" + version, "string", getResources.getPackage()));
		MINNIMUM_MONTHLY_ALLOTMENT = getResources.getString(getResources().getIdentifier("minnimum_allotment_" + version, "string", getResources.getPackage()));
		STANDARD_UTILITY_ALLOWANCE = getResources.getString(getResources().getIdentifier("standard_utility_" + version, "string", getResources.getPackage()));
		LIMITED_UTILITY_ALLOWANCE = getResources.getString(getResources().getIdentifier("limited_utility_" + version, "string", getResources.getPackage()));
		SINGLE_UTILITY_ALLOWANCE = getResources.getString(getResources().getIdentifier("single_utility_" + version, "string", getResources.getPackage()));
		STANDARD_TELEPHONE_ALLOWANCE = getResources.getString(getResources().getIdentifier("telephone_" + version, "string", getResources.getPackage()));
		LIMIT_ON_SHELTER_DEDUCTION = getResources.getString(getResources().getIdentifier("shelter_limit_" + version, "string", getResources.getPackage()));
	}

	private void setVariables(Bundle bundle) {
		AGSize = bundle.getInt("AGSize");
		earnedIncome = bundle.getDouble("earnedIncome");
		unearnedIncome = bundle.getDouble("unearnedIncome");
		isAged = bundle.getBoolean("isAged");
		isDisabled = bundle.getBoolean("isDisabled");
		medicalExpenses = bundle.getInt("medicalExpenses");
		dependentCare = bundle.getInt("dependentCare");
		childSupport = bundle.getInt("childSupport");
		isHomeless = bundle.getBoolean("isHomeless");
		AGSSI = bundle.getBoolean("AGSSI");
	}
	
	private void calculateFoodStamps() {
		
		int grossIncomeLimit = GROSS_INCOME_LIMIT[AGSize - 1];
		
		// check to see if the gross income test is needed (ag not aged or disabled) and then see if it is passed
		if (!isAged || !isDisabled) {
			if (!checkTotalGrossIncome()) return;
		}
		
		// those eligible for Benefits bank and also aged or disabled don't do net income test
		boolean noNeedToCheckNetIncome = (totalGrossIncome <= GROSS_INCOME_200[AGSize - 1] && (isAged || isDisabled))
		if (!noNeedToCheckNetIncome) {
			if (!checkNetIncome) return;
		}
	}
	
	/**
	 * Checks whether the income given passes the gross income test is passed
	 * @return boolean True if the test is passed, false if it fails
	 **/
	private boolean checkTotalGrossIncomeAmount() {
        /* OAC 5101:4-4-31
		(R) Method of calculating gross monthly income
		Except for AGs containing at least one member who is elderly or disabled as defined in rule 5101:4-1-03 of the Administrative Code, or considered categorically eligible, all AGs shall be subject to the gross income eligibility standard for the appropriate AG size. To determine the AG's total gross income, add the gross monthly income earned by all AG members and the total monthly unearned income of all AG members, minus income exclusions. If an AG has income from a farming operation (with gross proceeds of more than one thousand dollars per year) which operates at a loss, see rule 5101:4-6-11 of the Administrative Code. The total gross income is compared to the gross income eligibility standard for the appropriate AG size. If the total gross income is less than the standard, proceed with calculating the adjusted net income as described in paragraph (S) of this rule. If the total gross income is more than the standard, the AG is ineligible for program benefits and the case is either denied or terminated at this point. */
        int grossIncomeLimit = GROSS_INCOME_LIMIT[AGSize - 1];
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

        finalNetIncome = totalGrossIncome - (int)Math.floor(earnedIncome * 0.2);

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

        if (isHomeless) finalNetIncome -= STANDARD_SHELTER_HOMELESS;

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

        result = AGSSI || result;

		if (!result) {
			dialogResults = "The total net income of $" + finalNetIncome + " exceeds the net income limit of $" + NET_STANDARD[AGSize - 1] + " by $" + (finalNetIncome - NET_STANDARD[AGSize -1]);
            dialogTitle = "Ineligible";
		}
			
        return result;

    }
	
	// TODO this needs fixed up and the variables imported
	private void calculateShelterDeduction() {
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
	
	/**
	 * Returns a string containing the results of the calculation. 
	 * @return [String] [title, results]
	 **/
	public [String] getResults() {
		calculateFoodStamps();
		return [dialogTitle, dialogResults];
	}
	
}