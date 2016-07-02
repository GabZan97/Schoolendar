package com.gabrielezanelli.schoolendar.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
    private int UNCHECK_ITEMS = -10;
    private int FIRST_VALID_MENU_ITEM = 0;

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
        initFragmentManager();
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
        performFragmentTransaction(event, true);
    }

    public void unsetUserImage() {
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.user_default_image));
    }

    public int getNavIdFromFragment(Fragment fragment) {
        Class fragmentClass = fragment.getClass();
        if(fragmentClass.equals(AddEventFragment.class))
            return R.id.navAddEvent;
        if(fragmentClass.equals(ListViewFragment.class))
            return R.id.navListView;
        if(fragmentClass.equals(MonthViewFragment.class))
            return R.id.navMonthView;
        if(fragmentClass.equals(ManageSubjectsFragment.class))
            return R.id.navManageSubjects;
        if(fragmentClass.equals(SignInFragment.class) || fragmentClass.equals(AccountPreferencesFragment.class))
            return R.id.navAccount;
        return UNCHECK_ITEMS;
    }

    public void performFragmentTransaction(Fragment fragment, boolean addToBackStack) {
        nextFragment = fragment;

        // Retrieve the current fragment
        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.fragment_container);

        // Check if an item was selected
        if (nextFragment == null) {
            Log.d("Fragment Manager", "Any item was selected");
            return;
        }

        // Check if the user selected the current item
        if (currentFragment != null) {
            if (currentFragment.getClass().equals(nextFragment.getClass())) {
                Log.d("Fragment Manager", "This item is already selected");
                nextFragment = null;
                return;
            }
        }

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.slide_in_from_left, R.animator.slide_out_to_right,
                        R.animator.slide_out_to_right, R.animator.slide_in_from_left)
                .replace(R.id.fragment_container, nextFragment);
        if (addToBackStack)
            fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
        getFragmentManager().executePendingTransactions();
        Log.d("Fragment Manager", "Updated");

        nextFragment = null;

        refreshCheckedMenuItem();
    }

    private void refreshCheckedMenuItem() {
        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.fragment_container);
        int navItem = getNavIdFromFragment(currentFragment);

        if (navItem >= FIRST_VALID_MENU_ITEM)
            navView.setCheckedItem(navItem);
        else if (navItem == UNCHECK_ITEMS) {
            // Deselect items
            navView.setCheckedItem(0);
            navView.getMenu().getItem(0).setChecked(false);
        }

    }


    private void initFragmentManager() {
        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                refreshCheckedMenuItem();
            }
        });
    }

    private void initNavDrawer() {
        // Sets up the toggle which is the hamburger button
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.menu_drawer_account, R.string.menu_drawer_account) // Two useless strings inserted randomly
        {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (slideOffset > 0) {
                    // TODO: Set this changes just when the user gets updated, not every time the drawer opens
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
                performFragmentTransaction(nextFragment, true);
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
                    case R.id.navListView:
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

        performFragmentTransaction(new MonthViewFragment(), true);
    }

}
