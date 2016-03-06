package org.seols.ohiolegalservicesassistant;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by joshuagoodwin on 3/6/16.
 */
public class EditFormsList extends ListFragment {

    ListView lv;
    int positionSelected;
    String address;
    FormsDAO dao;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle onSavedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_forms_list_layout, container, false);
        getViews(rootView);
        fillList(inflater);
        return rootView;
    }

    private void getViews(View v) {
        lv = (ListView)v.findViewById(R.id.lv);
        Button addButton = (Button) v.findViewById(R.id.add);
        addButton.setOnClickListener(myButtonListener);
        Button editButton = (Button) v.findViewById(R.id.edit_form);
        editButton.setOnClickListener(myButtonListener);
        Button deleteButton = (Button) v.findViewById(R.id.delete_form);
        deleteButton.setOnClickListener(myButtonListener);
    }

    private void fillList(LayoutInflater inflater) {
        List<String> formNames = getFormNames();

        // check to see if there are user forms
        if (formNames.size() == 0) {
            // means there are no existing user forms, so display that message
            formNames.add(getResources().getString(R.string.no_user_forms));
        }

        // populate list


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

    private View.OnClickListener myButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.add:
                    break;
                case R.id.edit_form:
                    break;
                case R.id.delete_form:
                    break;
            }
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        dao = new FormsDAO(getActivity());

        setListAdapter(new FormListAdapter(getActivity(), dao.getFormsForEdit()));

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        v.setSelected(true);
        positionSelected = position;

    }

    public class FormListAdapter extends ArrayAdapter<Forms> {

        // List context
        private final Context context;
        // List values
        private final List<Forms> formsList;

        public FormListAdapter(Context context, List<Forms> formsList) {
            super(context, R.layout.rules_row, formsList);
            this.context = context;
            this.formsList = formsList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.rules_row, parent, false);

            TextView formName = (TextView) rowView.findViewById(R.id.row_text);
            formName.setText(formsList.get(position).getFormName());


            return rowView;
        }
    }

}
