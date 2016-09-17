package org.seols.ohiolegalservicesassistant;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Goodwin on 9/17/2016.
 */

@IgnoreExtraProperties
public class ResourceInformation {

    public String name, category, address, city, state, zip, county, website;

    public ResourceInformation() {
        // Default constructor required for calls to DataSnapshot.getValue(ResourceInformation.class)
    }

    public ResourceInformation(String name, String category, String address, String city, String state, String zip, String county, String website) {
        this.name = name;
        this.category = category;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.county = county;
        this.website = website;
    }

}
