package org.seols.ohiolegalservicesassistant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
//import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
//import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by joshuagoodwin on 3/2/16.
 */
public class FormsListFragment extends Fragment {

    private static final String AUTHORITY = "org.seols.ohiolegalservicesassistant";
    private TableLayout tl;
    SharedPreferences prefs;
    File localFile;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.forms_layout, container, false);
        tl = (TableLayout)rootView.findViewById(R.id.forms);

        addHotDocsForms(inflater);
        fillTable(inflater);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        //checkForUpdates();
        return rootView;
    }

    private String[] getFormNames() {
        List<String> forms = new ArrayList<String>();
        forms.add(getResources().getString(R.string.medicaid_help_sheet));
        forms.add(getResources().getString(R.string.benefits_standards));
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
            getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    };

    private void CopyAssets(final String fileName, String titleName, String prefName) throws IOException {

        final String finalPrefName = prefName;

        if (((MainActivity)getActivity()).isInternetAvailable()) {

            Log.d("Internet", "Available");
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference fileRef = storageRef.child(fileName);

            localFile = File.createTempFile(titleName, "pdf", getContext().getFilesDir());

            fileRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been create
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(finalPrefName, localFile.getName());
                    editor.commit();

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    Uri uri = FileProvider.getUriForFile(getContext(), AUTHORITY, localFile);
                    intent.setDataAndType(uri, "*/*");  // was application/pdf
                    grantAllUriPermissions(getContext(), intent, uri);
                    PackageManager pm = getActivity().getPackageManager();

                    if (intent.resolveActivity(pm) != null) {
                        startActivity(intent);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.d("NO INTERNET: ", "error");
                }
            });

        } else {
            if (prefs.getString(finalPrefName, "none").equals("none")) {
                Toast toast = Toast.makeText(getActivity(), "Sorry, we can't connect to the database. Try again later", Toast.LENGTH_LONG);
                toast.show();
            } else {
                Toast toast = Toast.makeText(getActivity(), "Offline" + finalPrefName, Toast.LENGTH_LONG);
                toast.show();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                File savedFile = new File(getContext().getFilesDir(), prefs.getString(finalPrefName, ""));

                Uri uri = FileProvider.getUriForFile(getContext(), AUTHORITY, savedFile);

                intent.setDataAndType(uri, "*/*");  // was application/pdf
                grantAllUriPermissions(getContext(), intent, uri);
                PackageManager pm = getActivity().getPackageManager();

                if (intent.resolveActivity(pm) != null) {
                    startActivity(intent);
                }
            }

            }
        }

    private void grantAllUriPermissions(Context context, Intent intent, Uri uri) {
        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    private View.OnClickListener myClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            Log.d("COPYASSETS", "top");
            logSearch("Form opened", v.getTag().toString());
            if (v.getTag().toString().equals(getResources().getString(R.string.medicaid_help_sheet))) {
                try {
                    Log.d("COPYASSETS", "medicaid");
                    CopyAssets("medicaid_help_sheet.pdf", "Medicaid Help Sheet", "medicaid");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (v.getTag().toString().equals(getResources().getString(R.string.benefits_standards))) {
                try {
                    CopyAssets("standards_help_sheet.pdf", "Standards Help Sheet", "standards");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void logSearch(String value, String detail) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, value);
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, detail);
        ((MainActivity)getActivity()).recordAnalytics(bundle);
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
