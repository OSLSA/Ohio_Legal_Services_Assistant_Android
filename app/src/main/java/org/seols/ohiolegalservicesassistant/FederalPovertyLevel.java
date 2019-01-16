package org.seols.ohiolegalservicesassistant;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by joshuagoodwin on 10/1/15.
 */
public class FederalPovertyLevel {

    double annualIncome, results;
    int size;
    String year;
    String[] constants;
    Context context;
    ArrayList<Long> fplInfo;


    /**
     * Calculates the federal poverty level and returns the result.
     * @return Federal Poverty Level
     */
    public double getResults() {

        setData();
        Long povertyStart = fplInfo.get(0);
        Long povertyIncrement = fplInfo.get(1);
        double fpl = ((size - 1) * povertyIncrement) + povertyStart;
        results = Math.floor(((annualIncome / fpl) * 100) * 100) / 100;
        return results;

        //constants = context.getResources().getStringArray(context.getResources().getIdentifier("fpl" + year, "array", "org.seols.ohiolegalservicesassistant"));

//
//        int povertyStart = fplInfo.getPovertyInformation().get(0);
//        int povertyIncrement = fplInfo.getPovertyInformation().get(1);
//
//        double fpl = ((size - 1) * povertyIncrement) + povertyStart;
//
//        results = Math.floor(((annualIncome / fpl) * 100) * 100) / 100;

    }

    private void setData() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        String version = "fpl" + year;
        DatabaseReference mRootRef;
        mRootRef = FirebaseDatabase.getInstance().getReference().child("povertyLevel").child(version);
        mRootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fplInfo = (ArrayList<Long>) dataSnapshot.getValue();
                Long povertyStart = fplInfo.get(0);
                Long povertyIncrement = fplInfo.get(1);
                double fpl = ((size - 1) * povertyIncrement) + povertyStart;
                results = Math.floor(((annualIncome / fpl) * 100) * 100) / 100;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private Double calculateIt() {
        Long povertyStart = fplInfo.get(0);
        Long povertyIncrement = fplInfo.get(1);

        double fpl = ((size - 1) * povertyIncrement) + povertyStart;

        Double solution = Math.floor(((annualIncome / fpl) * 100) * 100) / 100;

        return solution;
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