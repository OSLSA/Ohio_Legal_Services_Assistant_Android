package org.seols.ohiolegalservicesassistant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by joshuagoodwin on 10/1/15.
 */
public class CalculatorsFragment extends Fragment {

    private Bundle savedInstanceState;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle instanceState) {

        savedInstanceState = instanceState;
        View rootView = inflater.inflate(R.layout.calculator_layout, container, false);
        getViews(rootView);
        return rootView;
    }

    private void getViews(View rootView) {
        Button fpl = (Button) rootView.findViewById(R.id.fpl_button);
        fpl.setOnClickListener(myListener);
        Button foodStamps = (Button) rootView.findViewById(R.id.food_stamps_button);
        foodStamps.setOnClickListener(myListener);
        Button owf = (Button) rootView.findViewById(R.id.owf_button);
        owf.setOnClickListener(myListener);
        Button apr = (Button) rootView.findViewById(R.id.apr_button);
        apr.setOnClickListener(myListener);
        Button garn = (Button) rootView.findViewById(R.id.garnishment_button);
        garn.setOnClickListener(myListener);
    }

    private View.OnClickListener myListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fpl_button:
                    logAnalystics("Poverty Calculator");
                    ((MainActivity)getActivity()).setFragment(new FederalPovertyLevelController(), "FPL", "Federal Poverty Level", null);
                    break;
                case R.id.owf_button:
                    logAnalystics("OWF Calculator");
                    ((MainActivity)getActivity()).setFragment(new OwfCalculatorController(), "OWF", "OWF", null);
                    break;
                case R.id.food_stamps_button:
                    logAnalystics("Food Stamps Calculator");
                    ((MainActivity)getActivity()).setFragment(new FoodStampController(), "FOODSTAMPS", "Food Stamps", null);
                    break;
                case R.id.apr_button:
                    logAnalystics("APR Calculator");
                    ((MainActivity)getActivity()).setFragment(new APRFragment(), "APR", "APR", null);
                    break;
                case R.id.garnishment_button:
                    logAnalystics("Garnishment Calculator");
                    ((MainActivity)getActivity()).setFragment(new GarnishmentFragment(), "garnishment", "Garnishability", null);
                    break;
                default:
                    break;
            }
        }
    };

    private void logAnalystics(String buttonClicked) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, buttonClicked);
        ((MainActivity)getActivity()).recordAnalytics(bundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title
        String title = getArguments().getString("title");
        ((AppCompatActivity)getActivity()).getSupportActionBar()
                .setTitle(title);
    }
}
