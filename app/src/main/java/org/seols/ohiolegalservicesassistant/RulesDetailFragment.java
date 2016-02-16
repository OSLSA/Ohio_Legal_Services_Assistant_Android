package org.seols.ohiolegalservicesassistant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by Joshua Goodwin on 2/16/16.
 * <p/>
 * License information
 */
public class RulesDetailFragment extends Fragment {

    private Spinner spinner;
    private String bookName;
    private TableLayout tl;

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View rootView = inflater.inflate(R.layout.rules_layout, container, false);
        bookName = getArguments().getString("bookName");
        tl = (TableLayout) rootView.findViewById(R.id.rule_table);
        setSpinner(rootView);
        setRule(inflater);
        return rootView;
    }

    private void setSpinner(View v) {
        spinner = (Spinner) v.findViewById(R.id.rules_spinner);
        String[] rulesTOC = getResources().getStringArray(getResources().getIdentifier(bookName + "_toc", "array", "org.seols.ohiolegalservicesassistant"));

        // create array adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, rulesTOC);

        // set layout for when dropdown shown
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // apply adapter to spinner
        spinner.setAdapter(adapter);

        // TODO set on click listener to change rule shown to ruyle selected
    }

    private void setRule(LayoutInflater inflater) {

        //TODO this needs to be attributed text and adjust the padding for indentation
        String ruleNumber = getArguments().getString("ruleNumber");
        String[] titles = getResources().getStringArray(getResources().getIdentifier(bookName + "_" + ruleNumber, "array", "org.seols.ohiolegalservicesassistant"));
        for (int i = 0; i < titles.length; i++){
            View tr = inflater.inflate(R.layout.rules_row, null);
            ((TextView)tr.findViewById(R.id.row_text)).setText(titles[i]);
            tl.addView(tr);
        }
    }


}
