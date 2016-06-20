package com.gabrielezanelli.schoolendar;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.data.Freezable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.lang.WordUtils;

public class FirebaseUser {
    private static Context context;
    private static boolean logged;
    private static boolean anonymous;
    private static String userID;
    private static String username;
    private static String email;
    private static Uri photoUrl;
    private static DatabaseReference currentUserRef;

    public static void updateUser(com.google.firebase.auth.FirebaseUser currentUser) {
        FirebaseUser.logged = true;
        FirebaseUser.anonymous = currentUser.isAnonymous();
        FirebaseUser.userID = currentUser.getUid();
        setUsername(currentUser.getDisplayName());
        if (!anonymous) {
            FirebaseUser.email = currentUser.getEmail();
            FirebaseUser.photoUrl = currentUser.getPhotoUrl();
            FirebaseUser.currentUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(userID);
        } else {
            FirebaseUser.email = null;
            FirebaseUser.photoUrl = null;
            FirebaseUser.currentUserRef = null;
        }
    }

    public static void setContext(Context context) {
        FirebaseUser.context=context;
    }

    public static String getUserID() {
        return userID;
    }

    public static boolean isAnonymous() {
        return anonymous;
    }

    public static boolean isLogged() {
        return logged;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(final String username) {
        FirebaseAuth.getInstance().getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder()
                .setDisplayName(adjustUsername(username))
                .build()
        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FirebaseUser.username = username;
                    Log.d("User profile", "Username update succeeded: "+username);
                }
                else
                    Log.d("User profile", "Username update failed" + task.getException().toString());
            }
        });
    }

    public static String getEmail() {
        return email;
    }


    public static Uri getPhotoUrl() {
        return photoUrl;
    }

    public static void setPhotoUrl(final Uri photoUrl) {
        FirebaseAuth.getInstance().getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder()
                .setPhotoUri(photoUrl)
                .build()
        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FirebaseUser.photoUrl= photoUrl;
                    Log.d("User profile update", "Photo update succeeded");
                }
            }
        });
    }

    public static void clearInfo() {
        FirebaseUser.userID = null;
        FirebaseUser.username = null;
        FirebaseUser.email = null;
        FirebaseUser.photoUrl = null;
        FirebaseUser.currentUserRef = null;
        FirebaseUser.logged = false;
        FirebaseUser.anonymous = false;
    }

    public static void saveEvent(Event newEvent) {
        // Use push() in order to get unique ID
        // But for now it's better using the ID of the event
        currentUserRef.child("events").child("" + newEvent.getId()).setValue(newEvent);
        Log.d("Firebase Database", "Event Saved with ID: " + newEvent.getId());
    }

    public static void saveSubject(String subject) {
        subject = adjustSubject(subject);
        currentUserRef.child("subjects").child(subject).setValue(subject);
    }

    public static DatabaseReference getSubjectsRef(){
        return currentUserRef.child("subjects");
    }

    public static void removeSubject(String subject) {
        getSubjectsRef().child(subject).removeValue();
    }

    public static void updateSubject(String oldSubject,String newSubject) {
        getSubjectsRef().child(oldSubject).removeValue();
        getSubjectsRef().child(newSubject).setValue(adjustSubject(newSubject));
    }

    private static String adjustSubject(String subject) {
        return WordUtils.capitalize(subject.trim());
    }

    private static String adjustUsername(String username) {
        if (username == null)
            return context.getString(R.string.pref_default_username);
        if (username.equals(""))
            return context.getString(R.string.pref_default_username);
        return username;
    }
}
