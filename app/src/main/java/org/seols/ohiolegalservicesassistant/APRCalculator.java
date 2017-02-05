package org.seols.ohiolegalservicesassistant;

import android.renderscript.Double2;
import android.util.Log;

/**
 * Created by joshuagoodwin on 1/12/2016.
 */
public class APRCalculator {

    private double APR, principalBorrowed, amountBorrowed, costs, monthlyRate, monthlyPayment;
    private int numberOfPayments;

    /**
     * APR Class initializer.
     * @param principalBorrowed
     * @param baseRate
     * @param costs
     * @param numberOfPayments
     */
    public APRCalculator (double principalBorrowed, double baseRate, double costs, int numberOfPayments) {
        setBaseRate(baseRate);
        this.principalBorrowed = principalBorrowed;
        this.costs = costs;
        this.numberOfPayments = numberOfPayments;
        this.amountBorrowed = this.principalBorrowed + costs;
        monthlyPayment = setMonthlyPayment();
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

        double error = 0.000001;
        double approx = 0.05/12;
        double prev_approx;

        for (int i = 0; i < 100; i++) {
            prev_approx = approx;
            approx = prev_approx - F(prev_approx)/F_1(prev_approx);
            double diff = Math.abs(approx-prev_approx);
            if (diff < error) {break;}
        }

        APR = (double)Math.round((approx * 12 * 100) * 10000) / 10000;

    }

    private Double F(Double x) {

        //              amountBorrowed * x * pow(1+x,numberOfPayments) / (pow(1+x,numberOfPayments) - 1) - getMonthlyPayment()
        double result = principalBorrowed * x * Math.pow(1+x,numberOfPayments) / (Math.pow(1+x,numberOfPayments) - 1) - monthlyPayment;
        Log.d("F: ", Double.toString(result));
        return result;
    }

    private Double F_1(Double x) {
        double results = principalBorrowed * ( Math.pow(1 + x,numberOfPayments) / (-1 + Math.pow(1 + x,numberOfPayments)) - numberOfPayments * x * Math.pow(1 + x,-1 + 2*numberOfPayments)/Math.pow(-1 + Math.pow(1 + x,numberOfPayments),2) + numberOfPayments * x * Math.pow(1 + x,-1 + numberOfPayments)/(-1 + Math.pow(1 + x,numberOfPayments)));
        Log.d("F_1: ", Double.toString(results));
        return results;
    }



    /**returns the monthly payment
     *
     * @return monthly payment
     */
    public double getMonthlyPayment() {
        // calculate monthly payment and return as double
        //double monthlyPayment = ((amountBorrowed + costs) * monthlyRate * Math.pow(1 + monthlyRate, numberOfPayments)) / (Math.pow(1 + monthlyRate, numberOfPayments)-1);
        return (double)Math.round(monthlyPayment * 100) / 100;
    }

    private double setMonthlyPayment() {
        double pvif = Math.pow((1+monthlyRate),numberOfPayments);
        double result = amountBorrowed * ((monthlyRate * pvif) / (pvif-1));
        monthlyPayment = result;
        return(result);
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