package org.seols.ohiolegalservicesassistant;

import android.content.Context;

/**
 * Created by joshuagoodwin on 2/21/16.
 */

public class GarnishmentCalculator {

    Context context;
    String hours, netIncome, frequencyResult;
    int frequency;

    public GarnishmentCalculator(String income, int frequency, String hours, Context context) {
        this.hours = hours;
        this.netIncome = income;
        this.frequency = frequency;
        this.context = context;
    }

    public String getGarnishability() {
        int multiplier = getMultiplier();
        // income from the controller
        double income = (Double.parseDouble(String.valueOf(netIncome)));

        // multiplier to send
        double minWageFrequency = ((double)getMultiplier());

        // federal hourly minnimum wage
        double minWage = Double.parseDouble(context.getResources().getString(R.string.FEDERAL_HOURLY_MINNIMUM_WAGE));

        // garnishable amount based upon minnimum wage
        // R.C. 2923.66(A)(13)
        double minWageAmount = minWage * minWageFrequency;

        double countableIncome = getIncomeMultiplier();

        double exemptPercent = income * 0.75;

        double exempt = Math.max(minWageAmount, exemptPercent);
        double garnishable = Math.max(countableIncome - exempt, 0);
        return getResults(exempt, garnishable);
    }

    private String getResults(double exempt, double garnishable) {
        if (garnishable <= 0) {
            // client is not garnishable
            return "None of the income is garnishable.";
        } else {
            // client is garnishable
            return "$" + exempt + " of the income is exempt, and $" + garnishable + " of the income " + frequencyResult + " is garnishable.";
        }
    }

    private double getIncomeMultiplier() {
        switch (frequency) {
            case 0:
                return (Double.parseDouble(netIncome) * Double.parseDouble(hours));
            case 5:
                return (Double.parseDouble(netIncome) / 12);
            default:
                return Double.parseDouble(netIncome);
        }
    }

    private int getMultiplier() {

        switch (frequency) {
            // multiplier based on RC 2923.66(A)(13)
            case 0:
            case 1:
                // hourly and weekly
                frequencyResult = "per week";
                return 30;
            case 2:
                // every other week
                frequencyResult = "every other week";
                return 60;
            case 3:
                // 2x per month
                frequencyResult = "twice per month";
                return 65;
            case 4:
            case 5:
                //monthly and yearly
                frequencyResult = "per month";
                return 130;
            default:
                return 60;
        }
    }

}
