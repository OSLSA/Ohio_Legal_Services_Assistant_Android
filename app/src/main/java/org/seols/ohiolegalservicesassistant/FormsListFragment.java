package org.seols.ohiolegalservicesassistant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import java.util.Arrays;

/**
 * Created by joshuagoodwin on 3/2/16.
 */
public class FormsListFragment extends Fragment {

    private TableLayout tl;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.rules_all_books_layout, container, false);
        tl = (TableLayout)rootView.findViewById(R.id.rules_titles);
        createFormsList();
        fillTable();
        return rootView;
    }

    private void createFormsList() {
        //TODO creates a list of the forms needed for the table layout
    }

    private void fillTable() {
        // TODO adds rows to the table

    }

    private View.OnClickListener myClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO on clikc listener that opens the selected form
        }
    };

}
