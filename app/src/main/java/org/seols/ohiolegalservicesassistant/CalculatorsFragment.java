package org.seols.ohiolegalservicesassistant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
    }

    private View.OnClickListener myListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fpl_button:
                    ((MainActivity)getActivity()).setFragment(new FederalPovertyLevelController(), "FPL", "Federal Poverty Level", null);
                    break;
                case R.id.owf_button:
                    ((MainActivity)getActivity()).setFragment(new OwfCalculatorController(), "OWF", "OWF", null);
                    break;
                case R.id.food_stamps_button:
                    ((MainActivity)getActivity()).setFragment(new FoodStampController(), "FOODSTAMPS", "Food Stamps", null);
                    break;
                case R.id.apr_button:
                    ((MainActivity)getActivity()).setFragment(new APRFragment(), "APR", "APR", null);
                    break;
                case R.id.garnishment_button:
                    ((MainActivity)getActivity()).setFragment(new GarnishmentFragment(), "garnishment", "Garnishability", null);
                    break;
                default:
                    break;
            }
        }
    };
}
