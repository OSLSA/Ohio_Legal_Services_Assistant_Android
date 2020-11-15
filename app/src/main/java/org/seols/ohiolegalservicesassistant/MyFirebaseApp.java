package org.seols.ohiolegalservicesassistant;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Joshua Goodwin on 11/15/20.
 * <p>
 * License information
 */
public class MyFirebaseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}