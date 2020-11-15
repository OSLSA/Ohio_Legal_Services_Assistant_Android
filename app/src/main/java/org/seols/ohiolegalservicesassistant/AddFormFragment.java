package org.seols.ohiolegalservicesassistant;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Goodwin on 3/12/2016.
 */
public class AddFormFragment extends Fragment {

    boolean newForm = true;
    Button addButton;
    EditText etFormName;
    String formName, fileName, filePath, id;
    TextView tv, formSelected;
    private static final int PICKFILE_RESULT_CODE = 100;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle onSavedInstantState) {
        View rootView = inflater.inflate(R.layout.add_forms_detail_layout, container, false);
        populateViews(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title
        String title = getArguments().getString("title");
        ((AppCompatActivity)getActivity()).getSupportActionBar()
                .setTitle(title);
    }

    private void populateViews(View v) {
        etFormName = (EditText)v.findViewById(R.id.form_name);
        tv = (TextView) v.findViewById(R.id.title);
        formSelected = (TextView) v.findViewById(R.id.tv_file_selected);
        addButton = (Button) v.findViewById(R.id.add_form);
        // check to see if editing existing link or adding new link
        Bundle args = getArguments();
        String title = args.getString("title");
        if (!title.equals("Add Form")) {
            // will not be null if editing form
            formName = args.getString("formName");
            etFormName.setText(formName);
            tv.setText("Form Name");
            FormsDAO dao = new FormsDAO(getContext());
            filePath = dao.addressFromName(formName);
            id = dao.IDFromName(formName);
            Log.d("ID", "populateViews: id = " + id);
            newForm = false;
            formSelected.setText(dao.fileNameFromFormName(formName));
            addButton.setText(getString(R.string.save_changes));
        }

        addButton.setOnClickListener(addListener);
        Button pickFormButton = (Button) v.findViewById(R.id.pick_form_button);
        pickFormButton.setOnClickListener(pickListener);

        /*Bundle bundle = getArguments();
        newForm = bundle.getBoolean("newForm");
        String id = bundle.getString("id");
        String filePath = bundle.getString("path");
        int extensionPosition = filePath.lastIndexOf("/");
        */
    }

    private View.OnClickListener pickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getFile();
        }
    };

    private void getFile() {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("file/");
            startActivityForResult(intent, PICKFILE_RESULT_CODE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "You do not have a file explorer installed on your phone", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode== Activity.RESULT_OK) {
                    filePath = data.getData().getPath();
                    fileName = data.getData().getLastPathSegment();
                }
                formSelected.setText(filePath);
                break;
            default:
                break;
        }
    }

    /**
     * Checks to see if there is a form name and file selected.
     * @return True if form name missing or file not selected, false if ready to proceed
     */
    private boolean notReadyToAddForm() {
        if (etFormName.getText().toString().equals("")) {
            Toast toast = Toast.makeText(getContext(), "You need to give the form a name", Toast.LENGTH_LONG);
            toast.show();
            return true;
        }

        if (formSelected.getText().toString().equals(getString(R.string.no_form))) {
            Toast toast = Toast.makeText(getContext(), "You need to pick the form to add", Toast.LENGTH_LONG);
            toast.show();
            return true;
        }
        return false;
    }

    private View.OnClickListener addListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check to see if there is a valid name and file selected
                if (notReadyToAddForm()) return;
                FormsDAO dao = new FormsDAO(getActivity());
                int extensionPosition = filePath.lastIndexOf(".");
                String ext = filePath.substring(extensionPosition + 1);
                String name = etFormName.getText().toString();
                String result;
                if (newForm) {
                    dao.addNewForm(name, filePath, ext);
                    result = name + " was successfully added!";
                } else {
                    dao.editForm(name, id, filePath, ext);
                    result = "Your change was successfully made!";
                }
                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                ((MainActivity)getActivity()).setFragment(new FormsListFragment(), "Forms", "Forms List", null);
            }
    };

}
