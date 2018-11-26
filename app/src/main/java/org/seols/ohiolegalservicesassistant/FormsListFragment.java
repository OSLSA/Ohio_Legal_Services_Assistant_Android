package org.seols.ohiolegalservicesassistant;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by joshuagoodwin on 3/2/16.
 */
public class FormsListFragment extends Fragment {

    private static final String AUTHORITY = "org.seols.ohiolegalservicesassistant";
    private TableLayout tl;
    SharedPreferences prefs;


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

    private void createFormsList() {
        //TODO creates a list of the forms needed for the table layout

    }

    private String[] getFormNames() {
        FormsDAO formsDao = new FormsDAO(getContext());
        List<String> forms = formsDao.formNamesList();
        forms.add(getResources().getString(R.string.medicaid_help_sheet));
        forms.add(getResources().getString(R.string.benefits_standards));
        //forms.add(getResources().getString(R.string.add_forms));
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

    private void CopyAssets(final String fileName, String titleName) throws IOException {

        /* TODO
        1. store copies of pdfs on firebase
        2. create firebase database entry with date of most recent update for medicaid and help sheet
        3. create persistent data entry for those dates
        4. on form open, check to see if the internal date matches the external date
        5. if they do match, open form saved already on internal storage
        6. if they don't match, download latest copy from firebase and store locally, toast saying forms have been updated
        7. open newly downloaded form
         */

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference fileRef = storageRef.child(fileName);
        final File localFile = File.createTempFile(titleName, "pdf");


        fileRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been create
                //File file = new File(getActivity().getFilesDir(), fileName);
                Log.d("FormName Downloaded: ", String.valueOf(fileName));

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
                Log.d("ERROR: ", "error");
            }
        });




    }

    private void grantAllUriPermissions(Context context, Intent intent, Uri uri) {
        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
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

            Log.d("COPYASSETS", "top");
            logSearch("Form opened", v.getTag().toString());
            if (v.getTag().toString().equals(getResources().getString(R.string.medicaid_help_sheet))) {
                try {
                    Log.d("COPYASSETS", "medicaid");
                    CopyAssets("medicaid_help_sheet.pdf", "Medicaid Help Sheet");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (v.getTag().toString().equals(getResources().getString(R.string.benefits_standards))) {
                try {
                    CopyAssets("standards_help_sheet.pdf", "Standards Help Sheet");
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    static private void copy(InputStream in, File dst) throws IOException {
        FileOutputStream out=new FileOutputStream(dst);
        byte[] buf=new byte[1024];
        int len;

        while ((len=in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }

        in.close();
        out.close();
    }


}
