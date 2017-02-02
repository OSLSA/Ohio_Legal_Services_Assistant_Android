package org.seols.ohiolegalservicesassistant;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;


/**
 * Created by joshuagoodwin on 10/2/15.
 */
public class HomeFragment extends Fragment {

    private Bundle savedInstanceState;

    private Button viewRules;

    private EditText income, agSize, rule_number;

    SharedPreferences prefs;

    private Spinner rulesSpinner;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle instanceState) {

        savedInstanceState = instanceState;
        View rootView = inflater.inflate(R.layout.home_layout, container, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        getViews(rootView);
        setRulesSpinner();
        checkPushStatus();
        return rootView;
    }

    private void getViews(View rootView) {
        income = (EditText) rootView.findViewById(R.id.annual_income);
        agSize = (EditText) rootView.findViewById(R.id.ag_size);
        rule_number = (EditText) rootView.findViewById(R.id.rule_number);
        Button calculateFPL = (Button) rootView.findViewById(R.id.calculateFPL);
        calculateFPL.setOnClickListener(calculateFPLListener);
        Button ruleSelected = (Button) rootView.findViewById(R.id.view_rules);
        ruleSelected.setOnClickListener(ruleSelectedListener);
        viewRules = (Button) rootView.findViewById(R.id.view_rules);
        rulesSpinner = (Spinner) rootView.findViewById(R.id.rules_spinner);
        ImageView lscLogo = (ImageView) rootView.findViewById(R.id.lsc_logo);
        ImageView oslsaLogo = (ImageView) rootView.findViewById(R.id.oslsa_logo);
        lscLogo.setOnClickListener(logoClickListener);
        oslsaLogo.setOnClickListener(logoClickListener);
    }

    private void setRulesSpinner() {

        // create array adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.Rules));

        // set layout for when dropdown shown
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // apply adapter to spinner
        rulesSpinner.setAdapter(adapter);

    }

    private View.OnClickListener calculateFPLListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // check to see if AGSize is filled in
            if (AGSizeMissing()) return;

            // calculate percentage of poverty
            double annualIncome = income.getText().toString().equals("") ? 0.0 :
                    Double.parseDouble(income.getText().toString());

            FederalPovertyLevel calc = new FederalPovertyLevel(
                    Integer.parseInt(agSize.getText().toString()),
                    "2017",
                    annualIncome,
                    getContext()
            );
            logSearch("FPL Calculated from Home");

            showFPLResults(calc.getResults());
        }
    };

    private void logSearch(String value) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, value);
        ((MainActivity)getActivity()).recordAnalytics(bundle);
    }

    private void showFPLResults(Double results) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("This household is at " + results + "% of the Federal Poverty Level")
                .setPositiveButton("OK", null)
                .setTitle("Percentage of Poverty");
        builder.create().show();

    }

    private boolean AGSizeMissing() {

        // see if AGSize is greater than 0.
        // if it is, return false
        // if it is less than 1, show toast and return true

        String test = agSize.getText().toString();

        if (test.equals("") || test.equals("0")) {
            String text = "The assistance group size must be 1 or larger";
            errorToast(text);
            return true;
        } else {
            return false;
        }

    }

    private void errorToast(String text) {

        Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_LONG);
        toast.show();

    }

    private View.OnClickListener ruleSelectedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // figure out which book to go to
            int ruleBook = rulesSpinner.getSelectedItemPosition();
            String[] books = getResources().getStringArray(R.array.rules_tags);
            String bookName = books[ruleBook];

            // get that books TOC
            String[] toc = getResources().getStringArray(getResources().getIdentifier(bookName + "_toc", "array", "org.seols.ohiolegalservicesassistant"));

            // see if rule typed in exists
            // get rule
            String rule = rule_number.getText().toString();
            int rulePosition = -1;
            // check all rules
            for (int i = 0; i < toc.length; i++) {
                if (toc[i].startsWith(rule)) {
                    rulePosition = i;
                    break;
                }
            }
            // display error if rule doesn't exist
            if (rulePosition < 0) {
                String text = "Sorry, that rule does not exist";
                errorToast(text);
                return;
            }

            logSearch("Rule Searched from Home");

            // create bundle to push
            Bundle args = new Bundle();
            args.putString("ruleNumber", rule);
            args.putString("bookName", bookName);
            args.putInt("rulePosition", rulePosition);

            // change framents
            Fragment newFragment = new RulesDetailFragment();
            ((MainActivity)getActivity()).setFragment(newFragment, rule, "Rule " + rule, args);

        }
    };

    /**
     * See if this is the first time run. If so, ask users if they
     * want to enable push notifications.
     */
    private void checkPushStatus() {
        if (prefs.getBoolean("firstRun", true)) {
            // this is the first run, ask user
            editSharedPref("firstRun", false);
            pushPrompt();
        } else {
            // not first run, see whether push enabled and set accordingly
            boolean pushStatus = prefs.getBoolean("boolPushStatus", false);
            SettingsFragment sa = new SettingsFragment();
            sa.enablePush(pushStatus, getContext());
        }
    }

    /**
     * Method to edit the shared preferences
     * @param prefName name of the preference to edit
     * @param bool status to give the preference
     */
    private void editSharedPref(String prefName, boolean bool) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(prefName, bool);
        editor.commit();
    }



    /**
     * asks the user whether to enable push notifications
     */
    private void pushPrompt() {
        final SettingsFragment sa = new SettingsFragment();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        // Add the buttons
        builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked Enable button
                // enable message receiver
                sa.enablePush(true, getContext());
                editSharedPref("boolPushStatus", true);
                dialog.dismiss();
                // TODO show county settings so user can subscribe to user tier

            }
        });
        builder.setNegativeButton("Don't Enable", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked Don't Enable button
                sa.enablePush(false, getContext());
                editSharedPref("boolPushStatus", false);
                dialog.dismiss();
                canChangeChoiceDialog("enable");
            }
        });
        builder.setTitle("Push Notifications");
        builder.setMessage("This app would like to occasionally provide you with notifications about important changes in the law and other news. Would you like to enable these notifications?");
        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Method that shows an alertDialog reminding the user that they can either enable
     * or disable push notifications in the future through the settings options.
     *
     * @param decision either "enable" or "disable", the opposite of the user's current
     *                 push notification preference
     */
    private void canChangeChoiceDialog(String decision) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("You can change your mind and " + decision + " push notifications at any time through the settings menu.")
                .setTitle("Changing Notification Settings")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title
        String title = getArguments().getString("title", "Default");
        ((AppCompatActivity)getActivity()).getSupportActionBar()
                .setTitle(title);


    }

    private View.OnClickListener logoClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String url;
            switch (v.getId()) {
                case R.id.lsc_logo:
                    url = "http://www.lsc.gov/grants-grantee-resources/our-grant-programs/tig";
                    break;
                case R.id.oslsa_logo:
                    url = "http://www.seols.org";
                    break;
                default:
                    url = "http://www.seols.org";
                    break;
            }
            Uri uri = Uri.parse(url);
            Intent intent = new Intent();
            intent.setData(uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        }
    };
}
