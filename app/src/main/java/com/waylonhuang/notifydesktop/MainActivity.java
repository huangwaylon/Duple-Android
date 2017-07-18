package com.waylonhuang.notifydesktop;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.waylonhuang.notifydesktop.applist.AppListFragment;
import com.waylonhuang.notifydesktop.history.HistoryFragment;
import com.waylonhuang.notifydesktop.setupwizard.WizardFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.waylonhuang.notifydesktop.setupwizard.DoneSetupFragment.FINISH_INTENT;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String REAL_AD_ID = "ca-app-pub-1189993122998448~5476081412";
    public static final String TEST_AD_ID = "ca-app-pub-3940256099942544~3347511713";

    public static final String DELETE_INTENT = "DELETE_INTENT";
    public static final String PREFS_FILE = "PREFS_FILE";
    private static final String NAV_DRAWER_SELECT_KEY = "NAV_DRAWER_SELECT_KEY";
    private int navDrawerSelectedIndex;

    private FloatingActionButton fab;

    private BroadcastReceiver finishReceiver;
    private IntentFilter finishFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTestNotification();
                Snackbar.make(view, "Test notification created", Snackbar.LENGTH_SHORT)
                        .setAction("Okay", null).show();
            }
        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Default fragment to select.
        navDrawerSelectedIndex = R.id.nav_apps;
        SharedPreferences settings = getSharedPreferences(PREFS_FILE, 0);
        boolean signedIn = settings.getBoolean("signedIn", false);
        if (!signedIn) {
            navDrawerSelectedIndex = R.id.nav_setup;
        }
        if (savedInstanceState != null) {
            navDrawerSelectedIndex = savedInstanceState.getInt(NAV_DRAWER_SELECT_KEY);
        }

        navigationView.setCheckedItem(navDrawerSelectedIndex);
        switchToFragment(navDrawerSelectedIndex);

        // Setup filter and receiver.
        finishFilter = new IntentFilter(FINISH_INTENT);
        finishReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                navDrawerSelectedIndex = R.id.nav_apps;
                navigationView.setCheckedItem(navDrawerSelectedIndex);
                switchToFragment(navDrawerSelectedIndex);
            }
        };

        MobileAds.initialize(this, REAL_AD_ID);
        final AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.wtf("Ads", "onAdLoaded");
                FrameLayout frameLayout = (FrameLayout)findViewById(R.id.flContent);
                setMargins(frameLayout, 0, 0, 0, mAdView.getHeight());
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.wtf("Ads", "onAdFailedToLoad");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.wtf("Ads", "onAdOpened");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.wtf("Ads", "onAdLeftApplication");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
                Log.wtf("Ads", "onAdClosed");
            }
        });
    }

    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (finishReceiver != null) {
            unregisterReceiver(finishReceiver);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(finishReceiver, finishFilter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_DRAWER_SELECT_KEY, navDrawerSelectedIndex);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (navDrawerSelectedIndex != R.id.nav_apps) {
                navDrawerSelectedIndex = R.id.nav_apps;
                switchToFragment(navDrawerSelectedIndex);

                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                navigationView.setCheckedItem(navDrawerSelectedIndex);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        navDrawerSelectedIndex = item.getItemId();
        return switchToFragment(navDrawerSelectedIndex);
    }

    private boolean switchToFragment(int id) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment fragment;
        if (id == R.id.nav_apps) {
            fragment = AppListFragment.newInstance();
            fab.hide();
        } else if (id == R.id.nav_setup) {
            fragment = WizardFragment.newInstance();
            fab.hide();
        } else if (id == R.id.nav_history) {
            fragment = HistoryFragment.newInstance();
            fab.hide();
        } else {
            // Settings.
            fragment = SettingsFragment.newInstance();
            fab.hide();
        }
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "TAG").commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        View view = findViewById(R.id.flContent);

        if (item.getItemId() == R.id.launch_menu) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://notify-desktop-d1548.firebaseapp.com"));
            startActivity(browserIntent);
        } else if (item.getItemId() == R.id.test_menu) {
            createTestNotification();
            Snackbar.make(view, "Created test notification", Snackbar.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.clear_menu) {
            final NotificationSQLiteHelper helper = NotificationSQLiteHelper.getInstance(this);
            helper.deleteAllItems();

            Intent intent = new Intent(DELETE_INTENT);
            sendBroadcast(intent);

            Snackbar.make(view, "History cleared", Snackbar.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.sign_out_menu) {
            final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navDrawerSelectedIndex = R.id.nav_setup;
            navigationView.setCheckedItem(navDrawerSelectedIndex);
            switchToFragment(navDrawerSelectedIndex);
        }
        return true;
    }

    private void createTestNotification() {
        long time = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault());
        String timeStr = dateFormat.format(new Date(time));

        NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setContentTitle("Duple");
        notificationBuilder.setContentText("Notification sent at: " + timeStr);
        notificationBuilder.setTicker("Test notification ticker text");
        notificationBuilder.setSmallIcon(R.drawable.sb_small_icon);
        notificationBuilder.setAutoCancel(true);
        nManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
    }
}
