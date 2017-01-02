package org.seols.ohiolegalservicesassistant;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by Goodwin on 6/9/2016.
 */
public class SettingsFragment extends Fragment {

    SharedPreferences prefs;
    Button saveButton;
    Switch pushSwitch;
    EditText pikaURL;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle onSavedInstantState) {
        View rootView = inflater.inflate(R.layout.settings_layout, container, false);

        getViews(rootView);
        return rootView;
    }

    private void getViews(View v) {
        pushSwitch = (Switch) v.findViewById(R.id.push_switch);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        pushSwitch.setChecked(prefs.getBoolean("boolPushStatus", false));
        pushSwitch.setOnCheckedChangeListener(myCheckedChangeListener);
        pikaURL = (EditText)v.findViewById(R.id.pika_mobile_address);
        saveButton = (Button)v.findViewById(R.id.save_pika);
        saveButton.setOnClickListener(pikaSaveListener);

        Switch swAttorney = (Switch) v.findViewById(R.id.switch_attorney);
        Switch swLegalAid = (Switch) v.findViewById(R.id.switch_legal_aid);
        Switch swServiceProvider = (Switch) v.findViewById(R.id.switch_service_provider);

        pikaURL.setText(prefs.getString("pikaURL", ""));
        swAttorney.setChecked(prefs.getBoolean("pushAttorney", false));
        swLegalAid.setChecked(prefs.getBoolean("pushLegalAid", false));
        swServiceProvider.setChecked(prefs.getBoolean("pushServiceProvider", false));

        swAttorney.setOnCheckedChangeListener(myTopicsCheckedChangeListener);
        swLegalAid.setOnCheckedChangeListener(myTopicsCheckedChangeListener);
        swServiceProvider.setOnCheckedChangeListener(myTopicsCheckedChangeListener);
    }

    /**
     * Either enables or disables the push service in the manifest.
     * @param boolPushEnabled Whether push notification are enabled
     */
    public void enablePush(boolean boolPushEnabled, Context context) {

        int flag=(boolPushEnabled ?
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
        ComponentName componentMessg=new ComponentName(context, context.getPackageName() + ".MyFirebaseMessagingService");
        context.getPackageManager()
                .setComponentEnabledSetting(componentMessg, flag, PackageManager.DONT_KILL_APP);
        ComponentName componentIID=new ComponentName(context, context.getPackageName() + ".MyFirebaseInstanceIDService");
        context.getPackageManager()
                .setComponentEnabledSetting(componentIID, flag, PackageManager.DONT_KILL_APP);
        ComponentName component=new ComponentName(context, "com.google.firebase.iid.FirebaseInstanceIdReceiver");
        context.getPackageManager()
                .setComponentEnabledSetting(component, flag, PackageManager.DONT_KILL_APP);
    }

    /**
     * Method to edit the shared preferences
     * @param prefName name of the preference to edit
     * @param bool status to give the preference
     */
    private void editSharedPref(String prefName, boolean bool) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(prefName, bool);
        editor.commit();
    }

    private void changeSubscription(String topic, boolean subscribe) {
        if (subscribe) {
            FirebaseMessaging.getInstance().subscribeToTopic(topic);
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
        }
    }

    private View.OnClickListener pikaSaveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("pikaURL", pikaURL.getText().toString());
            editor.commit();
            Toast toast = Toast.makeText(getActivity(), "Pika address saved", Toast.LENGTH_LONG);
            toast.show();
        }
    };

    private CompoundButton.OnCheckedChangeListener myCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            editSharedPref("boolPushStatus", isChecked);
            enablePush(isChecked, getContext());
            pushSwitch.setChecked(isChecked);
        }
    };

    private CompoundButton.OnCheckedChangeListener myTopicsCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.switch_legal_aid:
                    editSharedPref("pushLegalAid", isChecked);
                    changeSubscription("Legal_Aid_Advocate", isChecked);
                    Log.d("PUSH NOTIFICATIONS: ", "legal aid");
                    break;
                case R.id.switch_attorney:
                    editSharedPref("pushAttorney", isChecked);
                    changeSubscription("Attorney", isChecked);
                    Log.d("PUSH NOTIFICATIONS: ", "attorney");
                    break;
                case R.id.switch_service_provider:
                    editSharedPref("pushServiceProvider", isChecked);
                    changeSubscription("Service_Provider", isChecked);
                    Log.d("PUSH NOTIFICATIONS: ", "service provider");
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    public void onResume() {
        super.onResume();
        // Set title
        String title = getArguments().getString("title");
        ((AppCompatActivity)getActivity()).getSupportActionBar()
                .setTitle(title);
    }
}
