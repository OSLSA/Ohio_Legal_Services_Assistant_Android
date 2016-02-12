package org.seols.ohiolegalservicesassistant;

import android.content.Context;

/**
 * Created by joshuagoodwin on 10/1/15.
 */
public class FederalPovertyLevel {

    double annualIncome, results;
    int size;
    String year;
    String[] constants;
    Context context;


    /**
     * Calculates the federal poverty level and returns the result.
     * @return Federal Poverty Level
     */
    public double getResults() {

        constants = context.getResources().getStringArray(context.getResources().getIdentifier("fpl" + year, "array", "org.seols.ohiolegalservicesassistant"));
        int povertyStart = Integer.parseInt(constants[0]);
        int povertyIncrement = Integer.parseInt(constants[1]);

        double fpl = ((size - 1) * povertyIncrement) + povertyStart;

        results = Math.floor(((annualIncome / fpl) * 100) * 100) / 100;

        return results;

    }

    /**
     * Class constructor that sets all essential variables. NOTE: data validation
     * must happen in the view controller
     * @param size Size of the assistance group
     * @param year Year for which to calculate FPL
     * @param annualIncome Annual income
     * @param context Activity context
     */
    public FederalPovertyLevel(int size, String year, double annualIncome, Context context) {
        this.size = size;
        this.year = year;
        this.annualIncome = annualIncome;
        this.context = context;
    }

}