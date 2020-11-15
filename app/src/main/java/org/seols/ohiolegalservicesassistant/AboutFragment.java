package org.seols.ohiolegalservicesassistant;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by joshuagoodwin on 6/19/16.
 */

public class AboutFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle instanceState) {

        View rootView = inflater.inflate(R.layout.about_layout, container, false);
        TextView about = (TextView) rootView.findViewById(R.id.about);
        Linkify.addLinks(about, Linkify.ALL);
        return rootView;
    }

}
