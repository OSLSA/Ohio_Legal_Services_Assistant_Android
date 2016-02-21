package org.seols.ohiolegalservicesassistant;

/**
 * Created by joshuagoodwin on 1/12/2016.
 */
public class APRCalculator {

    private double APR, baseRate, amountBorrowed, costs, monthlyRate;
    private int numberOfPayments;

    /**
     * APR Class initializer.
     * @param amountBorrowed
     * @param baseRate
     * @param costs
     * @param numberOfPayments
     */
    public APRCalculator (double amountBorrowed, double baseRate, double costs, int numberOfPayments) {
        setBaseRate(baseRate);
        this.amountBorrowed = amountBorrowed;
        this.costs = costs;
        this.numberOfPayments = numberOfPayments;
        setAPR();
    }

    private void setBaseRate(double baseRate) {
        //this.baseRate = baseRate;
        monthlyRate = baseRate / 100 / 12;
    }


    /**
     * Retuurns the APR
     * @return APR
     */
    public double getAPR() {
        return APR;
    }

    private void setAPR() {

        double testRate = monthlyRate;
        double testResult;
        double testDiff = monthlyRate;
        for (int i = 0; i <= 100; i++) {
            testResult = ((testRate * Math.pow(1 + testRate, numberOfPayments)) / (Math.pow(1 + testRate, numberOfPayments) - 1)) - (getMonthlyPayment() / amountBorrowed);
            if (Math.abs(testResult) < 0.0000001) break;
            if (testResult < 0) {
                testRate += testDiff;
            } else {
                testRate -= testDiff;
            }

            testDiff = testDiff / 2;
        }
        // round apr
        APR = (double)Math.round((testRate * 12 * 100) * 10000) / 10000;

    }

    /**returns the monthly payment
     *
     * @return monthly payment
     */
    public double getMonthlyPayment() {
        // calculate monthly payment and return as double
        double monthlyPayment = ((amountBorrowed + costs) * monthlyRate * Math.pow(1 + monthlyRate, numberOfPayments)) / (Math.pow(1 + monthlyRate, numberOfPayments)-1);
        return (double)Math.round(monthlyPayment * 100) / 100;
    }

    /**
     * returns the total payments made (interest + costs + principal)
     * @return total payment
     */
    public double getTotalPayments() {
        return (double)Math.round((getMonthlyPayment() * numberOfPayments) * 100) / 100;
    }

    /**
     * returns the total interest paid
     * @return total interest paid
     */
    public double getTotalInterest() {
        return (double)Math.round((getTotalPayments() - amountBorrowed) * 100) /100;
    }

}