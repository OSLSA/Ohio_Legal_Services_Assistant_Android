import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.seols.ohiolegalservicesassistant.R;

/**
 * Created by Goodwin on 9/11/2016.
 */
public class LocalResourcesFragment extends FragmentActivity {

    private DatabaseReference mRootRef, mCountyRef;
    private Spinner spinnerCounty, spinnerCategory;
    private TableLayout tl;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle instanceState) {
        Bundle savedInstanceState = instanceState;
        View rootView = inflater.inflate(R.layout.local_resources_layout, container, false);
        getViews(rootView);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mCountyRef = mRootRef.child("counties");
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
        String[] counties = getCounties();
    }

    private String[] getCounties() {

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mCountyRef.addChildEventListener(listener);
        return results;
    }

}
