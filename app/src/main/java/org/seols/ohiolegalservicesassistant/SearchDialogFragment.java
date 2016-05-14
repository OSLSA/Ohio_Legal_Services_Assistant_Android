package org.seols.ohiolegalservicesassistant;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Joshua Goodwin on 5/14/16.
 * <p/>
 * License information
 */
public class SearchDialogFragment extends DialogFragment {

    private OnUpdateSearchListener callback;

    public interface OnUpdateSearchListener {
        public void onSearchSubmit(String result);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            callback = (OnUpdateSearchListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement OnUpdateSearchListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View v = inflater.inflate(R.layout.dialog_search, null);
        final EditText etSearchTerm = (EditText) v.findViewById(R.id.search_term);
        Bundle bundle = getArguments();
        builder.setView(v)
                .setTitle(bundle.getString("title"))
                .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Grab the text from the input
                        String result = etSearchTerm.getText().toString();
                        callback.onSearchSubmit(result);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        callback.onSearchSubmit("-1");
                    }
                });
        return builder.create();

    }

}
