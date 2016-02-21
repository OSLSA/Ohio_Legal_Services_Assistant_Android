package org.seols.ohiolegalservicesassistant;

import android.content.res.Resources;

/**
 * Created by joshuagoodwin on 2/21/16.
 */

public class GarnishmentCalculator {

    String hours, netIncome;
    int frequency;

    public GarnishmentCalculator(String income, int frequency, String hours) {
        this.hours = hours;
        this.netIncome = income;
        this.frequency = frequency;
    }

    public String getGarnishability() {
        int multiplier = getMultiplier();
        // income from the controller
        double income = (Double.parseDouble(String.valueOf(netIncome)));

        // multiplier to send
        double minWageFrequency = ((double)getMultiplier());

        // federal hourly minnimum wage
        double minWage = Double.parseDouble(Resources.getSystem().getString(R.string.FEDERAL_HOURLY_MINNIMUM_WAGE));

        // garnishable amount based upon minnimum wage
        // R.C. 2923.66(A)(13)
        double minWageAmount = minWage * minWageFrequency;

        double countableIncome = income * getIncomeMultiplier();

        double exemptPercent = income * 0.75;

        double exempt = Math.max(minWageAmount, exemptPercent);
        double garnishable = Math.max(countableIncome - exempt, 0);
        return getResults(exempt, garnishable);
    }

    private double getIncomeMultiplier() {
        switch (frequency) {
            case 0:
                return (Double.parseDouble(netIncome) * Double.parseDouble(hours));
            default:
                return Double.parseDouble(netIncome);
        }
    }

    private int getMultiplier() {

        switch (frequency) {
            // multiplier based on RC 2923.66(A)(13)
            case 0:
                return 30;
            case 1:
                return 60;
            case 2:
                return 65;
            case 3:
                return 130;
            default:
                return 60;
        }
    }

}
