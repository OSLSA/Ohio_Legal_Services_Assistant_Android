package org.seols.ohiolegalservicesassistant;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by joshuagoodwin on 6/19/16.
 */

public class LocalResourcesFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle instanceState) {

        View rootView = inflater.inflate(R.layout.local_resources_layout, container, false);
        return rootView;
    }
}
