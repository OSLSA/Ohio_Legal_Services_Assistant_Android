package org.seols.ohiolegalservicesassistant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by Joshua Goodwin on 5/13/16.
 * <p/>
 * License information
 */
public class RulesSearchFragment extends Fragment {

    private String bookName, searchTerm;
    private TextView tvSearchTerm;
    private TableLayout tl;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.rule_search, container, false);
        bookName = getArguments().getString("bookName");
        searchTerm = getArguments().getString("searchTerm");
        getViews(rootView);
        showResults(inflater);
        logSearch();
        return rootView;
    }

    private void getViews(View v) {
        tl = (TableLayout)v.findViewById(R.id.search_table);
        tvSearchTerm = (TextView)v.findViewById(R.id.search_term);
    }

    private void showResults(LayoutInflater inflater) {

        ArrayList<String[]> searchResults = getSearchResults();
        if (searchResults.size() < 1) {
            // no results found
            tvSearchTerm.setText("No results were found for the term: " + searchTerm);
        } else {
            // results were found
            for (int i = 0; i < searchResults.size(); i++) {
                View tr = inflater.inflate(R.layout.rule_search_cell, null);
                // tag of the row is the rule number
                Book tag = new Book(searchResults.get(i)[2], Integer.parseInt(searchResults.get(i)[3]));
                tr.setTag(tag);
                ((TextView)tr.findViewById(R.id.rule_name)).setText(Html.fromHtml(searchResults.get(i)[0]));
                if (!searchResults.get(i)[0].equals(searchResults.get(i)[1])) {
                    ((TextView) tr.findViewById(R.id.rule_detail)).setText(Html.fromHtml(searchResults.get(i)[1]));
                } else {
                    ((TextView) tr.findViewById(R.id.rule_detail)).setVisibility(View.GONE);
                }
                tr.setOnClickListener(myClickListener);
                tr.setPadding(8, 8, 8, 8);
                tl.addView(tr);
            }
        }

    }

    private ArrayList<String[]> getSearchResults() {
        ArrayList<String[]> results = new ArrayList<>();
        String modSearch = searchTerm.toLowerCase();
        String[] toc = getTOC();
        for (int i = 0; i < toc.length; i++) {
            String[] ruleDetail = getResources().getStringArray(getResources().getIdentifier(bookName + "_" + toc[i], "array", "org.seols.ohiolegalservicesassistant"));
            for (int j = 0; j < ruleDetail.length; j++) {
                String modRule = ruleDetail[j].toLowerCase();
                if (modRule.contains(modSearch)) {
                    // search term is in this line
                    // order of array is rule title, rule detail, rule number for lookup later, rule position
                    String[] termFound = {ruleDetail[0].substring(1), ruleDetail[j].substring(1), toc[i], Integer.toString(i)};
                    results.add(termFound);
                    break;
                }
            }
        }
        return results;
    }

    private String[] getTOC() {
        String[] titles = getResources().getStringArray(getResources().getIdentifier(bookName + "_toc", "array", "org.seols.ohiolegalservicesassistant"));
        for (int i = 0; i < titles.length; i++) {
            try {
                Double test = NumberFormat.getInstance().parse(titles[i]).doubleValue();
                // if this is an int, should convert then to string otherwise string will end in .0
                String ruleNumber = (test % 1 == 0) ? Integer.toString(test.intValue()) : Double.toString(test);
                titles[i] = ruleNumber;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return titles;
    }

    private View.OnClickListener myClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // create book object to get tag
            Book tag = (Book)v.getTag();

            // get information to pass
            String ruleNumber = tag.ruleNumber;
            int position = tag.rulePosition;

            // create bundle to push
            Bundle args = new Bundle();
            args.putString("ruleNumber", ruleNumber);
            args.putString("bookName", bookName);
            args.putInt("rulePosition", position);

            // change framents
            Fragment newFragment = new RulesDetailFragment();
            ((MainActivity)getActivity()).setFragment(newFragment, "RULE DETAIL", "Rule " + ruleNumber, args);
        }
    };

    /**
     * internal class just to set tag on text views
     */
    private class Book {
        String ruleNumber;
        int rulePosition;

        public Book (String ruleNumber, int rulePosition) {
            this.ruleNumber = ruleNumber;
            this.rulePosition = rulePosition;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        // Set title
        String title = getArguments().getString("title");
        ((AppCompatActivity)getActivity()).getSupportActionBar()
                .setTitle(title);
    }

    private void logSearch() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, searchTerm);
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, bookName);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Rule Search Used");
        ((MainActivity)getActivity()).recordAnalytics(bundle);
    }

}
