package org.seols.ohiolegalservicesassistant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by Joshua Goodwin on 2/16/16.
 * <p/>
 * License information
 */
public class RulesDetailFragment extends Fragment {

    private LayoutInflater inflater;
    private Spinner spinner;
    private String bookName;
    String[] rulesTOC;
    private TableLayout tl;

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.rules_layout, container, false);
        bookName = getArguments().getString("bookName");
        tl = (TableLayout) rootView.findViewById(R.id.rule_table);
        setSpinner(rootView);
        return rootView;
    }

    private void setSpinner(View v) {
        spinner = (Spinner) v.findViewById(R.id.rules_spinner);
        rulesTOC = getResources().getStringArray(getResources().getIdentifier(bookName + "_toc", "array", "org.seols.ohiolegalservicesassistant"));

        // create array adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, rulesTOC);

        // set layout for when dropdown shown
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // apply adapter to spinner
        spinner.setAdapter(adapter);

        // set rule to current rule
        int startingPosition = getArguments().getInt("rulePosition");
        spinner.setSelection(startingPosition);

        // set selection listener
        spinner.setOnItemSelectedListener(mySpinnerListener);
    }

    /**
     * Displays the text of the rule given.
     * @param ruleNumber Should be the number of the rule in string format
     */
    private void setRule(String ruleNumber) {

        // remove current views
        tl.removeAllViews();

        // get the rule we will be using
        String[] titles = getResources().getStringArray(getResources().getIdentifier(bookName + "_" + ruleNumber, "array", "org.seols.ohiolegalservicesassistant"));

        // iterate through each member of the rule array
        // first character is level of indent and should be removed
        // then format in html
        for (int i = 0; i < titles.length; i++){
            View tr = inflater.inflate(R.layout.rules_detail_row, null);
            TextView tv = (TextView) tr.findViewById(R.id.row_text);

            // get indent amount and set it
            int paddingMultiplier = Integer.parseInt(titles[i].substring(0, 1));
            tv.setPadding(8 * (paddingMultiplier * 5), 8, 8, 8);

            // get the actual rule text without indent amount
            String rule = titles[i].substring(1);

            // set text on textview
            tv.setText(Html.fromHtml(rule));

            // add view to table
            tl.addView(tr);
        }
    }

    private Spinner.OnItemSelectedListener mySpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // get the rule number in String format to display
            Double test = null;
            try {
                test = NumberFormat.getInstance().parse(rulesTOC[position]).doubleValue();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // if this is an int, should convert then to string otherwise string will end in .0
            String ruleNumber = (test % 1 == 0) ? Integer.toString(test.intValue()) : Double.toString(test);

            // display selected rule
            setRule(ruleNumber);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

}
