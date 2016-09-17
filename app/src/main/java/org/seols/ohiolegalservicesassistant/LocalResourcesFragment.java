package org.seols.ohiolegalservicesassistant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.seols.ohiolegalservicesassistant.ResourceInformation;
import org.seols.ohiolegalservicesassistant.CountyNames;

import java.util.ArrayList;


/**
 * Created by Goodwin on 9/11/2016.
 */

public class LocalResourcesFragment extends Fragment {

    private DatabaseReference mRootRef, mResourceRef;
    private Spinner spinnerCounty, spinnerCategory;
    private TableLayout tl;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle instanceState) {
        Bundle savedInstanceState = instanceState;
        View rootView = inflater.inflate(R.layout.local_resources_layout, container, false);
        getViews(rootView);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mResourceRef = mRootRef.child("entities");
        setSpinners();
        return rootView;
    }

    private void getViews (View rootView) {
        spinnerCounty = (Spinner) rootView.findViewById(R.id.county_spinner);
        spinnerCategory = (Spinner) rootView.findViewById(R.id.category_spinner);
        tl = (TableLayout) rootView.findViewById(R.id.resource_table_layout);
        Button buttonShow = (Button) rootView.findViewById(R.id.show_button);
        buttonShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setSpinners() {
        ArrayList<ResourceInformation> counties = getLocalResources();
    }

    private ArrayList<ResourceInformation> getLocalResources() {

        final ArrayList<ResourceInformation> information = new ArrayList<>();

        Query query = mResourceRef.orderByChild("county").equalTo("Fairfield");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("DATABASE", "number of children: " + Double.toString(dataSnapshot.getChildrenCount()));
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    ResourceInformation ri = child.getValue(ResourceInformation.class);
                    information.add(ri);
                    Log.d("DATABASE", "category: " + ri.category);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return information;
    }

}
