package com.gabrielezanelli.schoolendar.activities;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.gabrielezanelli.schoolendar.FirebaseUser;
import com.gabrielezanelli.schoolendar.R;
import com.gabrielezanelli.schoolendar.fragments.AccountPreferencesFragment;
import com.gabrielezanelli.schoolendar.fragments.AddEventFragment;
import com.gabrielezanelli.schoolendar.fragments.EventFragment;
import com.gabrielezanelli.schoolendar.fragments.ListViewFragment;
import com.gabrielezanelli.schoolendar.fragments.ManageSubjectsFragment;
import com.gabrielezanelli.schoolendar.fragments.MonthViewFragment;
import com.gabrielezanelli.schoolendar.fragments.SignInFragment;
import com.gabrielezanelli.schoolendar.services.AuthStateService;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    // TODO: Used in order to set image only one time, make it better
    private boolean hasImage;

    // UI References
    private DrawerLayout drawer;
    private NavigationView navView;
    private Toolbar toolbar;
    private TextView usernameText;
    private TextView emailText;
    private CircleImageView imageView;
    private Fragment nextFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // This is suppose to initialize some nice facebook feature
        FacebookSdk.sdkInitialize(getApplicationContext());

        // Initialize default values for preferences
        // The boolean "false" ensure that this is called just once ever
        PreferenceManager.setDefaultValues(this, R.xml.account_preferences, false);

        // Initialize FirebaseUser
        FirebaseUser.init(this);
        hasImage = false;

        initAuthenticationService();

        drawer = (DrawerLayout) findViewById(R.id.drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navView = (NavigationView) findViewById(R.id.navView);
        usernameText = (TextView) navView.getHeaderView(0).findViewById(R.id.username);
        emailText = (TextView) navView.getHeaderView(0).findViewById(R.id.email);
        imageView = (CircleImageView) navView.getHeaderView(0).findViewById(R.id.image);

        setSupportActionBar(toolbar);
        initNavDrawer();

        getExtraEventIdFromIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu adding item to the action bar
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    // Sets up the Option Menu behavior
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Called whenever an item is selected
        return super.onOptionsItemSelected(item);
    }

    private void initAuthenticationService() {
        startService(new Intent(this, AuthStateService.class));
    }

    private void getExtraEventIdFromIntent() {
        long eventID = getIntent().getLongExtra(getString(R.string.EXTRA_STRING_EVENT_ID), -1);
        if (eventID == -1)
            return;

        EventFragment event = new EventFragment();
        Bundle extras = new Bundle();
        extras.putLong(getString(R.string.EXTRA_STRING_EVENT_ID), eventID);
        event.setArguments(extras);
        fragmentTransaction(event, true, -666);
    }

    public void unsetUserImage() {
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.user_default_image));
    }

    // Nav item > -1 is valid
    // Nav item = -1 does nothing because it has already being set
    // Nav item < -1 un-check all items
    public void fragmentTransaction(Fragment fragment, boolean addToBackStack, int navItem) {
        nextFragment = fragment;

        // Retrieve the current fragment
        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.fragment_container);

        // Check if an item was selected
        if (nextFragment == null) {
            Log.d("Fragment update", "Any item was selected");
            return;
        }

        // Check if the user selected the current item
        if (currentFragment != null) {
            if (currentFragment.getClass().equals(nextFragment.getClass())) {
                Log.d("Fragment update", "This item is already selected");
                nextFragment = null;
                return;
            }
        }

        android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.slide_in_from_left, R.animator.slide_out_to_right,
                        R.animator.slide_out_to_right, R.animator.slide_in_from_left)
                .replace(R.id.fragment_container, nextFragment);
        if (addToBackStack)
            fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();

        // TODO: Add a listener somewhere in order to check the right item on nav view when popping fragment stack

        if (navItem > -1)
            navView.setCheckedItem(navItem);
        else if (navItem < -1) {
            // Deselect items
            navView.setCheckedItem(0);
            navView.getMenu().getItem(0).setChecked(false);
        }

        Log.d("Fragment update", "Updated");
        nextFragment = null;
    }

    private void initNavDrawer() {
        // Sets up the toggle which is the hamburger button
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.menu_drawer_account, R.string.menu_drawer_account) // Two useless strings inserted randomly
        {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (slideOffset > 0) {
                    // The drawer started opening
                   // Log.d("Drawer", "User logged: "+FirebaseUser.isLogged()+" anonym: "+FirebaseUser.isAnonymous());
                    if (FirebaseUser.isLogged() && !FirebaseUser.isAnonymous()) {
                        usernameText.setText(getString(R.string.message_welcome_user, FirebaseUser.getUsername()));
                        emailText.setText(FirebaseUser.getEmail());
                        if (!hasImage && FirebaseUser.getImage() != null) {
                            Log.d("Image", "Setting from URI");
                            hasImage = true;
                            imageView.setImageBitmap(FirebaseUser.getImage());
                        }
                    } else {
                        usernameText.setText(getString(R.string.message_welcome_user, getString(R.string.pref_default_username)));
                        emailText.setText(getString(R.string.message_user_not_signed_in));
                    }
                }
                super.onDrawerSlide(drawerView, slideOffset);
            }

            // Waits until the drawer is closed for replacing fragment
            @Override
            public void onDrawerClosed(View drawerView) {
                fragmentTransaction(nextFragment, true, -1);
                super.onDrawerClosed(drawerView);
            }
        };

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Sets up the Navigation Drawer behavior once an item is selected
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navAddEvent:
                        nextFragment = new AddEventFragment();
                        break;
                    case R.id.navManageSubjects:
                        nextFragment = new ManageSubjectsFragment();
                        break;
                    case R.id.navAllEvents:
                        nextFragment = new ListViewFragment();
                        break;
                    case R.id.navAccount:
                        if (FirebaseUser.isLogged() && !FirebaseUser.isAnonymous()) {
                            nextFragment = new AccountPreferencesFragment();
                        } else {
                            nextFragment = new SignInFragment();
                        }
                        break;
                    default:
                        nextFragment = new MonthViewFragment();
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        fragmentTransaction(new MonthViewFragment(), true, R.id.navMonthView);
    }

}
