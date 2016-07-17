package com.gabrielezanelli.schoolendar.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gabrielezanelli.schoolendar.FirebaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthStateService extends Service {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        firebaseAuth = FirebaseAuth.getInstance();
        initAuthenticationStateListener();

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        firebaseAuth.addAuthStateListener(authStateListener);
        Log.d("Authentication Service","Created and Started");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        firebaseAuth.removeAuthStateListener(authStateListener);
        Log.d("Authentication Service","Detroyed");
        super.onDestroy();
    }

    private void initAuthenticationStateListener() {

        // Gets Firebase Authentication Listener in order to add it onStart
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public synchronized void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                com.google.firebase.auth.FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    // FirebaseHelper is signed in
                    Log.d("AuthState Service", "onAuthStateChanged: User signed in: " + currentUser.getUid());
                    FirebaseHelper.updateUser(currentUser);

                } else {
                    // FirebaseHelper is signed out
                    Log.d("AuthState Service", "onAuthStateChanged: User signed out");
                    FirebaseHelper.clearUser();

                    // Sign in Anonymously
                    firebaseAuth.signInAnonymously()
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("AuthState Service", "signInAnonymously:onComplete:" + task.isSuccessful());

                            if (!task.isSuccessful()) {
                                Log.w("Anonymous Auth", "signInAnonymously failed for: "+task.getException());
                                task.getException().printStackTrace();
                            }
                        }
                    });

                }
            }
        };
    }
}
