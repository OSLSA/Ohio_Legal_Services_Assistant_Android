package org.seols.ohiolegalservicesassistant;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by joshuagoodwin on 3/2/16.
 */
public class FormsListFragment extends Fragment {

    private TableLayout tl;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.rules_all_books_layout, container, false);
        tl = (TableLayout)rootView.findViewById(R.id.rules_titles);
        addHotDocsForms(inflater);
        fillTable(inflater);
        return rootView;
    }

    private void createFormsList() {
        //TODO creates a list of the forms needed for the table layout

    }

    private String[] getFormNames() {
        FormsDAO formsDao = new FormsDAO(getContext());
        List<String> forms = formsDao.formNamesList();
        forms.add(getResources().getString(R.string.medicaid_help_sheet));
        forms.add(getResources().getString(R.string.benefits_standards));
        forms.add(getResources().getString(R.string.add_forms));
        return forms.toArray(new String[0]);
    }

    private void fillTable(LayoutInflater inflater) {
        String[] titles = getFormNames();
        for (int i = 0; i < titles.length; i++){
            View tr = inflater.inflate(R.layout.rules_row, null);
            tr.setTag(titles[i]);
            ((TextView)tr.findViewById(R.id.row_text)).setText(titles[i]);
            tr.setOnClickListener(myClickListener);
            tl.addView(tr);
        }
    }

    private void addHotDocsForms(LayoutInflater inflater) {
        String[] titles = getResources().getStringArray(R.array.hotdoc_names);
        for (int i = 0; i < titles.length; i++) {
            View tr = inflater.inflate(R.layout.rules_row, null);
            tr.setTag(i);
            ((TextView)tr.findViewById(R.id.row_text)).setText(titles[i]);
            tr.setOnClickListener(hotDocsClicker);
            tl.addView(tr);
        }
    }

    private View.OnClickListener hotDocsClicker = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = Integer.parseInt(v.getTag().toString());
            String url = (getResources().getStringArray(R.array.hotdoc_urls))[i];
            Uri uri = Uri.parse(url);
            Intent intent = new Intent();
            intent.setData(uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        }
    };

    private void CopyAssets(String fileName) {
        AssetManager am = getActivity().getAssets();
        InputStream in = null;
        OutputStream out = null;
        File file = new File(getActivity().getFilesDir(), fileName);
        try {
            in = am.open(fileName);
            out = getActivity().openFileOutput(file.getName(), Context.MODE_WORLD_READABLE);
            copyFile(in, out);
            in.close();
            in = null;
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + getActivity().getFilesDir() + "/" + fileName), "application/pdf");
        startActivity(intent);
    }

    private void openUserForm(String name) {
        FormsDAO formsDao = new FormsDAO(getContext());
        String fileName = formsDao.addressFromName(name);
        File file = new File(fileName);
        AssetManager am = getActivity().getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = am.open(fileName);
            out = getActivity().openFileOutput(file.getName(), Context.MODE_WORLD_READABLE);
            copyFile(in, out);
            in.close();
            in = null;
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        String ext = formsDao.extensionFromName(name);
        String type;
        switch (ext) {
            case "doc":
            case "docx":
                // Word document
                type = "application/msword";
                break;
            case "pdf":
                // PDF file
                type = "application/pdf";
                break;
            case "ppt":
            case "pptx":
                // Powerpoint file
                type = "application/vnd.ms-powerpoint";
                break;
            case "xls":
            case "xlsx":
                // Excel file
                type = "application/vnd.ms-excel";
                break;
            case "zip":
            case "rar":
                // WAV audio file
                type = "application/x-wav";
                break;
            case "rtf":
                // RTF file
                type = "application/rtf";
                break;
            case "wav":
            case "mp3":
                // WAV audio file
                type = "audio/x-wav";
                break;
            case "gif":
                // GIF file
                type = "image/gif";
                break;
            case "jpg":
            case "jpeg":
            case "png":
                // JPG file
                type = "image/jpeg";
                break;
            case "txt":
                // Text file
                type = "text/plain";
                break;
            case "3gp":
            case "mpg":
            case "mpeg":
            case "mpe":
            case "mp4":
            case "avi":
                // Video files
                type = "video/*";
                break;
            default:
                //if you want you can also define the intent type for any other file

                //additionally use else clause below, to manage other unknown extensions
                //in this case, Android will show all applications installed on the device
                //so you can choose which application to use
                type = "*/*";
                break;
        }

        intent.setDataAndType(Uri.parse("file://" + fileName), type);
        startActivity(intent);
    }


    private View.OnClickListener myClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            logSearch("Form opened", v.getTag().toString());
            if (v.getTag().toString().equals(getResources().getString(R.string.medicaid_help_sheet))) {
                CopyAssets("medicaid_help_sheet.pdf");
            } else if (v.getTag().toString().equals(getResources().getString(R.string.benefits_standards))) {
                CopyAssets("standards_help_sheet.pdf");
            } else if (v.getTag().toString().equals(getResources().getString(R.string.add_forms))) {
                ((MainActivity)getActivity()).setFragment(new EditFormsList(), "EDIT", "Add/Edit Forms", null);
            } else {
                // user added form
                openUserForm(v.getTag().toString());
            }

        }
    };

    private void logSearch(String value, String detail) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, value);
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, detail);
        ((MainActivity)getActivity()).recordAnalytics(bundle);
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
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
