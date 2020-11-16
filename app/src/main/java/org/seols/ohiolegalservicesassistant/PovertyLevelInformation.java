package org.seols.ohiolegalservicesassistant;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;


@IgnoreExtraProperties

public class PovertyLevelInformation {

    public ArrayList<Integer> povertyInformation;

    public ArrayList<Integer> getPovertyInformation() {
        return povertyInformation;
    }

    public void setPovertyInformation(ArrayList<Integer> povertyInformation) {
        this.povertyInformation = povertyInformation;
    }

}
