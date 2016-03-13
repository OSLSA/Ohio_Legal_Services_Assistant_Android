package org.seols.ohiolegalservicesassistant;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by joshuagoodwin on 3/6/16.
 */
public class EditFormsList extends Fragment {

    Button editButton, deleteButton;
    ListView lv;
    List<String> formNames;
    int positionSelected = AdapterView.INVALID_POSITION;
    View previouslySelectedItem = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle onSavedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_forms_list_layout, container, false);
        getViews(rootView);
        lv = (ListView) rootView.findViewById(R.id.forms_list);
        fillList(inflater);
        return rootView;
    }

    private void getViews(View v) {
        Button addButton = (Button) v.findViewById(R.id.add);
        addButton.setOnClickListener(myButtonListener);
        editButton = (Button) v.findViewById(R.id.edit_form);
        editButton.setOnClickListener(myButtonListener);
        deleteButton = (Button) v.findViewById(R.id.delete_form);
        deleteButton.setOnClickListener(myButtonListener);
    }

    private void fillList(LayoutInflater inflater) {
        formNames = getFormNames();

        // check to see if there are user forms
        if (formNames.size() == 0) {
            // means there are no existing user forms, so display that message
            formNames.add(getResources().getString(R.string.no_user_forms));
            // disable edit and delete buttons
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
            // disabled list selection
            lv.setEnabled(false);
        }

        // populate list
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, formNames);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // see if user clicked already selected position
                if (positionSelected == position) {
                    positionSelected = AdapterView.INVALID_POSITION;
                    previouslySelectedItem = null;
                    view.setBackgroundColor(0);

                } else {
                    // new position selected
                    positionSelected = position;
                    // set background color of selected item
                    view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.accent_highlight));
                    // change prior selected item back
                    if (previouslySelectedItem != null) {
                        previouslySelectedItem.setBackgroundColor(0);
                    }
                    previouslySelectedItem = view;
                }
            }
        });

    }

    /**
     * Gets the list of all of the user forms the user has added
     * @return list containing the name of the forms the user's added
     */
    private List<String> getFormNames() {
        FormsDAO formsDao = new FormsDAO(getContext());
        List<String> forms = formsDao.formNamesList();
        return forms;
    }


    /**
     * custom click listener for the add, edit, delete buttons
     */
    private View.OnClickListener myButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.add:
                    ((MainActivity)getActivity()).setFragment(new AddFormFragment(), "ADD FORM", "Add Form", null);
                    break;
                case R.id.edit_form:
                    // ensure item is actaully selected
                    if (positionSelected == AdapterView.INVALID_POSITION) {
                        Toast toast = Toast.makeText(getContext(), "You must select a form before choosing edit", Toast.LENGTH_LONG);
                        toast.show();
                        break;
                    }
                    Bundle args = new Bundle();
                    args.putString("formName", formNames.get(positionSelected));
                    ((MainActivity)getActivity()).setFragment(new AddFormFragment(), "ADD FORM", "Edit Form", args);
                    break;
                case R.id.delete_form:
                    break;
            }
        }
    };
}
