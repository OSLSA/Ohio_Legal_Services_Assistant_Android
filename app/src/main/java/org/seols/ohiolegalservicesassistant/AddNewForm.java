package org.seols.ohiolegalservicesassistant;

/**
 * Created by joshuagoodwin on 3/9/16.
 */
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;
import android.content.ActivityNotFoundException;

public class AddNewForm extends Fragment {

    private boolean newForm;

    private Button add_button;

    private EditText new_form_name;

    private ImageButton add, clear;

    private String id;
    private static final int PICKFILE_RESULT_CODE = 100;

    private String filePath;
    private String fileName;
    private String extension;

    private TextView file_name_text;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_forms_detail_layout, container, false);
        getViews(rootView);
        InitializeGetFileButton();
        InitializeAddButton();
        return rootView;
    }

    private void getViews(View v) {
        new_form_name = (EditText) v.findViewById(R.id.new_form_name);
        file_name_text = (TextView) v.findViewById(R.id.file_name_text);
        Button doneButton = (Button) v.findViewById(R.id.done);
        doneButton.setOnClickListener(myClickListener);
        Bundle bundle = getArguments();
        newForm = bundle.getBoolean("newForm");
        id = bundle.getString("id");
        filePath = bundle.getString("path");
        int extensionPosition = filePath.lastIndexOf("/");
        String fileName = filePath.substring(extensionPosition + 1);
        file_name_text.setText(fileName);
        new_form_name.setText(bundle.getString("name"));
    }

    private void InitializeGetFileButton() {
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFile();
            }
        });
    }

    private View.OnClickListener myClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.done:
                    ((MainActivity)getActivity()).setFragment(new FormsListFragment(), "FORMS", "Forms", null);
            }
        }
    }

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
                    file_name_text.setText("File: " + fileName);
                }
                break;
            default:
                break;
        }
    }

    private void InitializeAddButton() {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormsDAO dao = new FormsDAO(getActivity());
                int extensionPosition = filePath.lastIndexOf(".");
                String ext = filePath.substring(extensionPosition + 1);
                String name = new_form_name.getText().toString();
                String result;
                if (newForm) {
                    dao.addNewForm(name, filePath, ext);
                    result = name + " was successfully added!";
                } else {
                    dao.editForm(name, id, filePath, ext);
                    result = "Your change was successfully made!";
                }
                ((MainActivity) getActivity()).setDrawer();
                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            }
        });
    }
}