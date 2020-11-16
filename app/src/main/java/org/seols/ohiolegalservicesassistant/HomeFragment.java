package org.seols.ohiolegalservicesassistant;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * Created by joshuagoodwin on 10/2/15.
 */
public class HomeFragment extends Fragment {

    private Bundle savedInstanceState;

    final Calendar myCalendar = Calendar.getInstance();

    private Button calculateFPL;

    private DatabaseReference mPovertyLevelRef,mFPLVersionRef;

    private EditText income, agSize, rule_number, etStartDate, etNumberOfDays;

    SharedPreferences prefs;

    private String version;

    private Spinner rulesSpinner;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle instanceState) {

        savedInstanceState = instanceState;
        View rootView = inflater.inflate(R.layout.home_layout, container, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        getViews(rootView);
        setRulesSpinner();
        checkPushStatus();
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        mPovertyLevelRef = mRootRef.child("povertyLevel");
        mFPLVersionRef = mPovertyLevelRef.child("fplVersion");
        return rootView;
    }

    private void getCurrentYear() {
        mFPLVersionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<Long> versions = (ArrayList<Long>) dataSnapshot.getValue();
                version = versions.get(0).toString();
                calculateFPL.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private void getViews(View rootView) {
        income = (EditText) rootView.findViewById(R.id.annual_income);
        agSize = (EditText) rootView.findViewById(R.id.ag_size);
        etStartDate = (EditText) rootView.findViewById(R.id.startingDate);
        etStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog( getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        etNumberOfDays = (EditText) rootView.findViewById(R.id.numberOfDays);
        rule_number = (EditText) rootView.findViewById(R.id.rule_number);
        calculateFPL = (Button) rootView.findViewById(R.id.calculateFPL);
        calculateFPL.setOnClickListener(calculateFPLListener);
        calculateFPL.setEnabled(false);
        Button calculateDate = (Button) rootView.findViewById(R.id.calculateDate);
        calculateDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getResults();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        Button ruleSelected = (Button) rootView.findViewById(R.id.view_rules);
        ruleSelected.setOnClickListener(ruleSelectedListener);

        rulesSpinner = (Spinner) rootView.findViewById(R.id.rules_spinner);
        ImageView lscLogo = (ImageView) rootView.findViewById(R.id.lsc_logo);
        ImageView oslsaLogo = (ImageView) rootView.findViewById(R.id.oslsa_logo);
        lscLogo.setOnClickListener(logoClickListener);
        oslsaLogo.setOnClickListener(logoClickListener);
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        etStartDate.setText(sdf.format(myCalendar.getTime()));
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
            final double annualIncome = income.getText().toString().equals("") ? 0.0 :
                    Double.parseDouble(income.getText().toString());

            DatabaseReference mYearRef = mPovertyLevelRef.child("fpl" +
                    version);
            mYearRef.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<Long> fplInfo = (ArrayList<Long>) dataSnapshot.getValue();
                    Long povertyStart = fplInfo.get(0);
                    Long povertyIncrement = fplInfo.get(1);
                    double fpl = ((Integer.parseInt(agSize.getText().toString()) - 1) * povertyIncrement) + povertyStart;
                    double results = Math.floor(((annualIncome / fpl) * 100) * 100) / 100;
                    showFPLResults(results);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });

            logSearch("FPL Calculated from Home");

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

            // change fragments
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
                canChangeChoiceDialog();
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
     */
    private void canChangeChoiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("You can change your mind and enable push notifications at any time through the settings menu.")
                .setTitle("Changing Notification Settings")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getResults() throws ParseException {
        // check that everything is properly entered
        String start = etStartDate.getText().toString();

        // check for start date
        if (start.equals("")) {
            Toast toast = Toast.makeText(getActivity(), "You must enter a starting date!", Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        //check for days in the future
        String numberDays = etNumberOfDays.getText().toString();
        if (numberDays.equals("")) {
            Toast toast = Toast.makeText(getActivity(), "You must put in a number of days larger than 0!", Toast.LENGTH_LONG);
            toast.show();
            return;
        } else if (Integer.parseInt(numberDays) <= 0) {
            Toast toast = Toast.makeText(getActivity(), "You must put in a number of days larger than 0!", Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date startDate = formatter.parse(start);
        DateCalculator dateCalc = new DateCalculator(Integer.parseInt(numberDays), false, startDate);
        showResults(dateCalc.getNewDate());

    }

    private void showResults(String results) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // This should be coming from the string xml file with variables in it
        builder.setMessage(results)
                .setPositiveButton("OK", null)
                .setTitle("Date Calculator");
        builder.create().show();

    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title
        String title = getArguments().getString("title", "Default");
        ((AppCompatActivity)getActivity()).getSupportActionBar()
                .setTitle(title);
        getCurrentYear();


    }

    private View.OnClickListener logoClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String url;
            switch (v.getId()) {
                case R.id.lsc_logo:
                    url = "http://www.lsc.gov/grants-grantee-resources/our-grant-programs/tig";
                    break;
                default:
                    url = "http://www.seols.org";
                    break;
            }
            getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    };
}
