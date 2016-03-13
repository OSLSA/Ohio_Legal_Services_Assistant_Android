package org.seols.ohiolegalservicesassistant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by Goodwin on 3/12/2016.
 */
public class AddFormFragment extends Fragment {

    EditText etFormName;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle onSavedInstantState) {
        View rootView = inflater.inflate(R.layout.add_forms_detail_layout, container, false);
        populateViews(rootView);
        return rootView;
    }

    private void populateViews(View v) {
        etFormName = (EditText)v.findViewById(R.id.form_name);
        Bundle args = getArguments();
        if (args != null) etFormName.setText(args.getString("formName"));
    }

}
