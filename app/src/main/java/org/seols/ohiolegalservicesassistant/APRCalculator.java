package org.seols.ohiolegalservicesassistant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by joshuagoodwin on 1/12/2016.
 */
public class APRCalculator {

    private double APR, baseRate, amountBorrowed, costs, monthlyRate;
    private int numberOfPayments;

    // TODO javadoc
    // this should be the class initializer
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


    // TODO add javadoc
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

    // TODO add javadoc comment
    public double getMonthlyPayment() {
        // calculate monthly payment and return as double
        double monthlyPayment = ((amountBorrowed + costs) * monthlyRate * Math.pow(1 + monthlyRate, numberOfPayments)) / (Math.pow(1 + monthlyRate, numberOfPayments)-1);
        return (double)Math.round(monthlyPayment * 100) / 100;
    }

    // TODO add javadoc comment
    public double getTotalPayments() {
        return (double)Math.round((getMonthlyPayment() * numberOfPayments) * 100) / 100;
    }

    // TODO add javadoc comment
    public double getTotalInterest() {
        return (double)Math.round((getTotalPayments() - amountBorrowed) * 100) /100;
    }

}