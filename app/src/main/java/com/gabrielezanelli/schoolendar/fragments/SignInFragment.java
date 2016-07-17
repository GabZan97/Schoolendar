package com.gabrielezanelli.schoolendar.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.gabrielezanelli.schoolendar.FirebaseHelper;
import com.gabrielezanelli.schoolendar.R;
import com.gabrielezanelli.schoolendar.activities.MainActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

public class SignInFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    //Id to identity READ_CONTACTS permission request.
    private static final int REQUEST_READ_CONTACTS = 0;

    // UI references.
    private AutoCompleteTextView emailEdit;
    private TextInputEditText passwordEdit;
    private SignInButton googleSignInButton;
    private LoginButton facebookSignInButton;
    private Button signInButton;
    private View progressView;
    private View loginFormView;

    private FirebaseAuth firebaseAuth;
    private GoogleApiClient googleClient;
    private CallbackManager callbackManager;

    private int googleCode = 9001;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View thisFragment = inflater.inflate(R.layout.fragment_sign_in, container, false);

        getActivity().setTitle(getString(R.string.fragment_title_sign_in));

        emailEdit = (AutoCompleteTextView) thisFragment.findViewById(R.id.email);
        passwordEdit = (TextInputEditText) thisFragment.findViewById(R.id.password);
        googleSignInButton = (SignInButton) thisFragment.findViewById(R.id.google_sign_in_button);
        facebookSignInButton = (LoginButton) thisFragment.findViewById(R.id.facebook_sign_in_button);
        signInButton = (Button) thisFragment.findViewById(R.id.sign_in_button);
        loginFormView = thisFragment.findViewById(R.id.login_form);
        progressView = thisFragment.findViewById(R.id.login_progress);

        firebaseAuth = FirebaseAuth.getInstance();

        initGoogleSignIn();
        initFacebookSignIn();
        initLoginForm();

        // Inflate the layout for this fragment
        return thisFragment;
    }

    private void initGoogleSignIn() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the options specified by gso.
        googleClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        googleSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Starts google sign in intent
                showProgress(true);
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleClient);
                startActivityForResult(signInIntent, googleCode);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == googleCode) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                Log.d("Google SignIn", "firebaseAuthWithGoogle Succeeded: " + account.getIdToken());

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                signInWithCredential(credential);
            } else {
                // Google Sign In failed
                Log.d("Google SignIn", "firebaseAuthWithGoogle failed");
                showProgress(false);
                Toast.makeText(getActivity(), R.string.toast_authentication_failed, Toast.LENGTH_SHORT).show();
            }
        }

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    // TODO: Fix facebook login doesn't get response
    private void initFacebookSignIn() {

        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create();
        facebookSignInButton.setReadPermissions("email", "public_profile");
        facebookSignInButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Facebook Login", "facebook:onSuccess:" + loginResult);
                AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                signInWithCredential(credential);
            }

            @Override
            public void onCancel() {
                showProgress(false);
                Log.d("Facebook Login", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                showProgress(false);
                Toast.makeText(getActivity(), R.string.toast_authentication_failed, Toast.LENGTH_SHORT).show();
                Log.d("Facebook Login", "facebook:onError", error);
            }
        });
    }

    private void signInWithCredential(final AuthCredential credential) {

        if (FirebaseHelper.isAnonymous()) {
            FirebaseHelper.deleteUserAndSaveData();
            //FirebaseHelper.setPreviousUser(FirebaseAuth.getInstance().getCurrentUser());
        }

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Authentication State", "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            onLoginSuccessful();
                        } else {
                            showProgress(false);
                            try {
                                throw task.getException();
                            } catch (com.google.firebase.auth.FirebaseAuthInvalidCredentialsException ex) {
                                if (credential.getProvider().equals("password")) {
                                    Log.d("Login Failed: ", "Wrong password");
                                    passwordEdit.setError(getString(R.string.error_wrong_password));
                                    passwordEdit.requestFocus();
                                } else {
                                    Log.w("Authentication failed", "signInWithCredential", task.getException());
                                    Toast.makeText(getActivity(), R.string.toast_authentication_failed, Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Log.w("Authentication failed", "signInWithCredential", task.getException());
                                Toast.makeText(getActivity(), R.string.toast_authentication_failed, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void onLoginSuccessful() {
        showProgress(false);
        Toast.makeText(getActivity(), R.string.toast_login_successful, Toast.LENGTH_SHORT).show();
        AccountPreferencesFragment account = new AccountPreferencesFragment();
        ((MainActivity) getActivity()).performFragmentTransaction(account, false);
    }

    private void initLoginForm() {
        // Set up the login form.
        populateAutoComplete();

        /**
         passwordEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
        @Override public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
        if (id == R.id.login || id == EditorInfo.IME_NULL) {
        attemptLogin();
        return true;
        }
        return false;
        }
        });
         */

        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress(true);

                // Close the keyboard
                View focussedView = getActivity().getCurrentFocus();
                if (focussedView != null) {
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(focussedView.getWindowToken(), 0);
                }

                attemptRegistration();
            }
        });
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (getActivity().checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(emailEdit, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    private boolean checkFields(String email, String password) {
        // Reset errors.
        emailEdit.setError(null);
        passwordEdit.setError(null);
        boolean cancel = false;
        View focusView = null;

        // Ensure password is valid
        if (!isPasswordValid(password)) {
            passwordEdit.setError(getString(R.string.error_invalid_password));
            focusView = passwordEdit;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailEdit.setError(getString(R.string.error_field_required));
            focusView = emailEdit;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailEdit.setError(getString(R.string.error_invalid_email));
            focusView = emailEdit;
            cancel = true;
        }

        // If there was an error focus the first field with an error
        if (cancel) {
            showProgress(false);
            focusView.requestFocus();
            return false;
        } else
            return true;
    }

    private void attemptRegistration() {
        // Store values at the time of the login attempt.
        final String email = emailEdit.getText().toString();
        final String password = passwordEdit.getText().toString();

        if (checkFields(email, password)) {
            Log.d("Firebase", "Attempting registration");
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    showProgress(false);
                    if (task.isSuccessful()) {
                        // If the registration was successful, login the user
                        signInWithCredential(EmailAuthProvider.getCredential(email, password));
                    } else if (task.getException() != null)
                        try {
                            throw task.getException();
                        } catch (com.google.firebase.auth.FirebaseAuthUserCollisionException ex) {
                            // If the email is already registered, try to login
                            Log.d("Registration failed", "Email already registered");
                            signInWithCredential(EmailAuthProvider.getCredential(email, password));
                        } catch (com.google.firebase.auth.FirebaseAuthInvalidCredentialsException ex) {
                            Log.d("Registration failed", "Bad email format");
                            emailEdit.setError(getString(R.string.error_invalid_email));
                            emailEdit.requestFocus();
                            ex.printStackTrace();
                        } catch (Exception ex) {
                            Log.d("Registration Failed", task.getException().toString());
                            ex.printStackTrace();
                        }
                }
            });
        }
    }

    private boolean isEmailValid(String email) {
        if (email.contains(" ") || !email.contains("@"))
            return false;
        return email.split("@")[1].contains(".");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        loginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        emailEdit.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

}

