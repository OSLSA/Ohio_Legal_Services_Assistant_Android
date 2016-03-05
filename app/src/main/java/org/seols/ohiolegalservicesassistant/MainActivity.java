package org.seols.ohiolegalservicesassistant;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static String PACKAGE_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        PACKAGE_NAME = getApplicationContext().getPackageName();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        setLogoClick(navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        setFragment(new HomeFragment(), "home", getResources().getString(R.string.app_name), null);
    }

    private void setLogoClick(NavigationView navigationView) {
        View header = navigationView.getHeaderView(0);
        ImageView logo = (ImageView) header.findViewById(R.id.nav_logo);
        header.setOnClickListener(headerListener);
    }

    private View.OnClickListener headerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setFragment(new HomeFragment(), "home", getResources().getString(R.string.app_name), null);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_forms) {
            setFragment(new FormsListFragment(), "FORMS", "Forms", null);
        } else if (id == R.id.nav_calculators) {
            setFragment(new CalculatorsFragment(), "CALCULATORS", "Calculators", null);
        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_resources) {

        } else if (id == R.id.nav_rules) {
            setFragment(new RulesAllTitlesFragment(), "RULES", "Rules", null);
        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Sets the main content area to the chosen fragment
     * @param name name of the fragment
     * @param tag log tag
     * @param title Title to set the activity bar to
     * @param args Arguments to be passed to new fragment, can be null
     */
    public void setFragment(Fragment name, String tag, String title, Bundle args) {
        getSupportActionBar().setTitle(title);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Log.d("args: ", "args = " + args);
        if (args != null) {
            name.setArguments(args);
        }
        ft.replace(R.id.nav_content_frame, name, tag);
        ft.commit();
    }
}
