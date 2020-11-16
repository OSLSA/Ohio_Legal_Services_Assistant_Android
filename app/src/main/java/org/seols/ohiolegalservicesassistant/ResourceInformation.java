package org.seols.ohiolegalservicesassistant;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Goodwin on 9/17/2016.
 */

@IgnoreExtraProperties
public class ResourceInformation {

    public String name, category, address, city, state, zip, county, website, phone, notes;

    public ResourceInformation() {
        // Default constructor required for calls to DataSnapshot.getValue(ResourceInformation.class)
    }

    public ResourceInformation(String name) {
        this.name = name;
        this.category = "";
        this.address = "";
        this.city = "";
        this.state = "";
        this.zip = "";
        this.county = "";
        this.website = "";
        this.phone = "";
        this.notes = "";
    }

}
