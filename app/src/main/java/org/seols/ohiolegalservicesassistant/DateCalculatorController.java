package org.seols.ohiolegalservicesassistant;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateCalculatorController extends Fragment {

    private Bundle savedInstanceState;
    private Button button;
    private EditText etNumberOfDays, etStartDate;
    final Calendar myCalendar = Calendar.getInstance();
    private CheckBox check;

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

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle instanceState) {

        savedInstanceState = instanceState;
        // get view
        View rootView = inflater.inflate(R.layout.date_calculator, container, false);

        submitButton(rootView);

        // set up the edittexts
        initializeViews(rootView);

        // set up the clear button
        clearButton(rootView);

        //return view
        return rootView;
    }

    private void submitButton(View rootView) {
        button = (Button) rootView.findViewById(R.id.submit);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    getResults();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initializeViews(View rootView) {
        etStartDate = (EditText) rootView.findViewById(R.id.etStartDate);
        etStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog( getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        etNumberOfDays = (EditText) rootView.findViewById(R.id.etNumberOfDays);
        check = (CheckBox) rootView.findViewById(R.id.exclude_weekends);
    }

    private void clearButton(View rootView) {

        Button button = (Button) rootView.findViewById(R.id.clear);

        // set button's onClickListener
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetAll();
            }
        });
    }

    private void resetAll() {

        // reset the EditTexts
        etStartDate.setText("");
        etNumberOfDays.setText("");
        check.setChecked(false);

    }

    private void updateLabel() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        etStartDate.setText(sdf.format(myCalendar.getTime()));
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
        DateCalculator dateCalc = new DateCalculator(Integer.parseInt(numberDays), check.isChecked(), startDate, getContext());
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

}
